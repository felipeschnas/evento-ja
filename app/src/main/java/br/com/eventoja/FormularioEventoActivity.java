package br.com.eventoja;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import br.com.eventoja.data.model.Evento;
import br.com.eventoja.viewmodel.EventoViewModel;

public class FormularioEventoActivity extends AppCompatActivity {

    private EventoViewModel viewModel;
    private Evento eventoExistente;
    private TextInputEditText titulo;
    private TextInputEditText descricao;
    private TextInputEditText local;
    private TextInputEditText categoria;
    private TextInputEditText inicio;
    private TextInputEditText fim;
    private ProgressBar progress;
    private MaterialButton botaoSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_evento);

        titulo = findViewById(R.id.campo_titulo);
        descricao = findViewById(R.id.campo_descricao);
        local = findViewById(R.id.campo_local);
        categoria = findViewById(R.id.campo_categoria);
        inicio = findViewById(R.id.campo_inicio);
        fim = findViewById(R.id.campo_fim);
        progress = findViewById(R.id.progress_formulario);
        botaoSalvar = findViewById(R.id.botao_salvar);

        eventoExistente = (Evento) getIntent().getSerializableExtra("evento");
        boolean editando = eventoExistente != null;
        int tituloTela = editando ? R.string.event_edit_title : R.string.event_create_title;
        setTitle(tituloTela);
        android.widget.TextView tituloFormulario = findViewById(R.id.formulario_titulo);
        tituloFormulario.setText(tituloTela);

        viewModel = new ViewModelProvider(this).get(EventoViewModel.class);
        if (!viewModel.podeEditar()) {
            Toast.makeText(this, R.string.admin_only_edit, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (eventoExistente != null) {
            preencher(eventoExistente);
        }

        viewModel.getResultadoSalvar().observe(this, resultado -> {
            progress.setVisibility(resultado.isCarregando() ? View.VISIBLE : View.GONE);
            botaoSalvar.setEnabled(!resultado.isCarregando());
            if (resultado.isSucesso()) {
                Toast.makeText(this, R.string.event_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else if (resultado.isErro()) {
                Toast.makeText(this, resultado.getMensagem(), Toast.LENGTH_LONG).show();
                if (!viewModel.podeEditar()) {
                    finish();
                }
            }
        });

        botaoSalvar.setOnClickListener(v -> salvar());
    }

    private void preencher(Evento evento) {
        titulo.setText(evento.getTitulo());
        descricao.setText(evento.getDescricao());
        local.setText(evento.getLocal());
        categoria.setText(evento.getCategoria());
        inicio.setText(evento.getDataInicio());
        fim.setText(evento.getDataFim());
    }

    private void salvar() {
        Evento evento = eventoExistente == null ? new Evento() : eventoExistente;
        evento.setTitulo(texto(titulo));
        evento.setDescricao(texto(descricao));
        evento.setLocal(texto(local));
        evento.setCategoria(texto(categoria));
        evento.setDataInicio(texto(inicio));
        evento.setDataFim(texto(fim));
        viewModel.salvarEvento(evento);
    }

    private String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
