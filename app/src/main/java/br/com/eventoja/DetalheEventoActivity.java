package br.com.eventoja;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import br.com.eventoja.data.model.Evento;
import br.com.eventoja.viewmodel.EventoViewModel;
import br.com.eventoja.viewmodel.Resultado;

public class DetalheEventoActivity extends AppCompatActivity {

    private EventoViewModel viewModel;
    private Evento evento;
    private View acoesAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_evento);
        setTitle(R.string.event_details_title);

        evento = (Evento) getIntent().getSerializableExtra("evento");
        acoesAdmin = findViewById(R.id.acoes_admin);
        viewModel = new ViewModelProvider(this).get(EventoViewModel.class);

        if (evento != null) {
            preencher(evento);
        }

        viewModel.getEventoSelecionado().observe(this, resultado -> {
            if (resultado.isCarregando()) {
                findViewById(R.id.progress_detalhe).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.progress_detalhe).setVisibility(View.GONE);
            }
            if (resultado.isSucesso() && resultado.getDados() != null) {
                evento = resultado.getDados();
                preencher(evento);
            } else if (resultado.isErro()) {
                atualizarAcoesAdmin();
                Toast.makeText(this, resultado.getMensagem(), Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getResultadoExcluir().observe(this, resultado -> {
            if (resultado.isSucesso()) {
                Toast.makeText(this, R.string.event_deleted, Toast.LENGTH_SHORT).show();
                finish();
            } else if (resultado.isErro()) {
                atualizarAcoesAdmin();
                Toast.makeText(this, resultado.getMensagem(), Toast.LENGTH_LONG).show();
            }
        });

        MaterialButton botaoEditar = findViewById(R.id.botao_editar);
        MaterialButton botaoExcluir = findViewById(R.id.botao_excluir);
        botaoEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, FormularioEventoActivity.class);
            intent.putExtra("evento", evento);
            startActivity(intent);
        });
        botaoExcluir.setOnClickListener(v -> confirmarExclusao());
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarAcoesAdmin();
        if (evento != null && evento.getId() != null) {
            viewModel.buscarEvento(evento.getId());
        }
    }

    private void atualizarAcoesAdmin() {
        acoesAdmin.setVisibility(viewModel.podeEditar() ? View.VISIBLE : View.GONE);
    }

    private void preencher(Evento evento) {
        ((TextView) findViewById(R.id.detalhe_titulo)).setText(evento.getTitulo());
        TextView categoria = findViewById(R.id.detalhe_categoria);
        categoria.setText(evento.getCategoria());
        categoria.setVisibility(evento.getCategoria() == null
                || evento.getCategoria().trim().isEmpty() ? View.GONE : View.VISIBLE);
        ((TextView) findViewById(R.id.detalhe_data)).setText(getString(
                R.string.event_date_range, evento.getDataInicio(), evento.getDataFim()));
        ((TextView) findViewById(R.id.detalhe_local)).setText(
                getString(R.string.event_location_value, evento.getLocal()));
        ((TextView) findViewById(R.id.detalhe_descricao)).setText(evento.getDescricao());
    }

    private void confirmarExclusao() {
        if (evento == null || evento.getId() == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.event_delete_title)
                .setMessage(getString(R.string.event_delete_confirmation, evento.getTitulo()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm_delete, (dialog, which) ->
                        viewModel.excluirEvento(evento.getId()))
                .show();
    }
}
