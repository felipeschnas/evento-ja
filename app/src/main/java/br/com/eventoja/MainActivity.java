package br.com.eventoja;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.com.eventoja.data.model.Evento;
import br.com.eventoja.viewmodel.EventoViewModel;
import br.com.eventoja.viewmodel.LoginViewModel;
import br.com.eventoja.viewmodel.Resultado;

public class MainActivity extends AppCompatActivity {

    private EventoViewModel eventoViewModel;
    private LoginViewModel loginViewModel;
    private EventoAdapter adapter;
    private ProgressBar progress;
    private TextView textoStatus;
    private FloatingActionButton botaoNovo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        progress = findViewById(R.id.progress);
        textoStatus = findViewById(R.id.texto_status);
        botaoNovo = findViewById(R.id.botao_novo);

        RecyclerView recyclerView = findViewById(R.id.lista_eventos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventoAdapter();
        recyclerView.setAdapter(adapter);

        eventoViewModel = new ViewModelProvider(this).get(EventoViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        eventoViewModel.getEventos().observe(this, this::mostrarEventos);

        botaoNovo.setOnClickListener(v ->
                startActivity(new Intent(this, FormularioEventoActivity.class)));

        toolbar.inflateMenu(R.menu.menu_principal);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.acao_login) {
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.acao_sair) {
                loginViewModel.sair();
                atualizarPermissoes();
                Toast.makeText(this, R.string.user_logged_out, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarPermissoes();
        if (eventoViewModel != null) {
            eventoViewModel.carregarEventos();
        }
    }

    private void atualizarPermissoes() {
        boolean logado = loginViewModel != null && loginViewModel.isLogado();
        boolean admin = loginViewModel != null && loginViewModel.isAdmin();
        botaoNovo.setVisibility(admin ? View.VISIBLE : View.GONE);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_principal);
        toolbar.getMenu().findItem(R.id.acao_login).setVisible(!logado);
        toolbar.getMenu().findItem(R.id.acao_sair).setVisible(logado);
    }

    private void mostrarEventos(Resultado<List<Evento>> resultado) {
        progress.setVisibility(resultado.isCarregando() ? View.VISIBLE : View.GONE);
        if (resultado.isSucesso()) {
            adapter.setEventos(resultado.getDados());
            if (resultado.getDados() == null || resultado.getDados().isEmpty()) {
                textoStatus.setVisibility(View.VISIBLE);
                textoStatus.setText(R.string.no_events);
            } else {
                textoStatus.setVisibility(View.GONE);
            }
        } else if (resultado.isErro()) {
            atualizarPermissoes();
            textoStatus.setVisibility(View.VISIBLE);
            textoStatus.setText(resultado.getMensagem());
        }
    }

    private class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

        private final List<Evento> eventos = new ArrayList<>();

        void setEventos(List<Evento> novosEventos) {
            eventos.clear();
            if (novosEventos != null) {
                eventos.addAll(novosEventos);
            }
            notifyDataSetChanged();
        }

        @Override
        public EventoViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_evento, parent, false);
            return new EventoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EventoViewHolder holder, int position) {
            holder.bind(eventos.get(position));
        }

        @Override
        public int getItemCount() {
            return eventos.size();
        }

        class EventoViewHolder extends RecyclerView.ViewHolder {
            private final TextView categoria;
            private final TextView titulo;
            private final TextView data;
            private final TextView local;

            EventoViewHolder(View itemView) {
                super(itemView);
                categoria = itemView.findViewById(R.id.evento_categoria);
                titulo = itemView.findViewById(R.id.evento_titulo);
                data = itemView.findViewById(R.id.evento_data);
                local = itemView.findViewById(R.id.evento_local);
                itemView.setOnClickListener(v -> {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(MainActivity.this, DetalheEventoActivity.class);
                        intent.putExtra("evento", eventos.get(pos));
                        startActivity(intent);
                    }
                });
            }

            void bind(Evento evento) {
                categoria.setText(evento.getCategoria());
                categoria.setVisibility(evento.getCategoria() == null
                        || evento.getCategoria().trim().isEmpty() ? View.GONE : View.VISIBLE);
                titulo.setText(evento.getTitulo());
                data.setText(getString(R.string.event_date_range,
                        evento.getDataInicio(), evento.getDataFim()));
                local.setText(evento.getLocal());
            }
        }
    }
}
