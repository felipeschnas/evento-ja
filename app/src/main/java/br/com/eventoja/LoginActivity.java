package br.com.eventoja;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import br.com.eventoja.data.model.Usuario;
import br.com.eventoja.viewmodel.LoginViewModel;
import br.com.eventoja.viewmodel.Resultado;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private TextInputEditText campoLogin;
    private TextInputEditText campoSenha;
    private ProgressBar progress;
    private MaterialButton botaoEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Se o usuario ja fez login antes, vai direto para a lista de eventos
        if (viewModel.isLogado()) {
            abrirEventos();
            return;
        }

        setContentView(R.layout.activity_login);
        // Tela de login sem barra de título, só o formulário
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        campoLogin = findViewById(R.id.campo_login);
        campoSenha = findViewById(R.id.campo_senha);
        progress = findViewById(R.id.progress_login);
        botaoEntrar = findViewById(R.id.botao_entrar);

        viewModel.getResultadoLogin().observe(this, this::mostrarResultado);

        botaoEntrar.setOnClickListener(v -> entrar());
        campoSenha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                entrar();
                return true;
            }
            return false;
        });
    }

    private void entrar() {
        viewModel.entrar(
                campoLogin.getText() == null ? "" : campoLogin.getText().toString(),
                campoSenha.getText() == null ? "" : campoSenha.getText().toString());
    }

    /** Abre a lista de eventos e tira o login da pilha (o "voltar" não retorna para cá). */
    private void abrirEventos() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarResultado(Resultado<Usuario> resultado) {
        progress.setVisibility(resultado.isCarregando() ? View.VISIBLE : View.GONE);
        botaoEntrar.setEnabled(!resultado.isCarregando());
        if (resultado.isErro()) {
            Toast.makeText(this, resultado.getMensagem(), Toast.LENGTH_LONG).show();
        } else if (resultado.isSucesso()) {
            Usuario usuario = resultado.getDados();
            String nome = usuario == null || usuario.getNome() == null ? "" : usuario.getNome();
            Toast.makeText(this, getString(R.string.login_welcome, nome), Toast.LENGTH_SHORT).show();
            abrirEventos();
        }
    }
}
