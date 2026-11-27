package br.com.eventoja;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);

        campoLogin = findViewById(R.id.campo_login);
        campoSenha = findViewById(R.id.campo_senha);
        progress = findViewById(R.id.progress_login);
        botaoEntrar = findViewById(R.id.botao_entrar);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getResultadoLogin().observe(this, this::mostrarResultado);

        botaoEntrar.setOnClickListener(v -> viewModel.entrar(
                campoLogin.getText() == null ? "" : campoLogin.getText().toString(),
                campoSenha.getText() == null ? "" : campoSenha.getText().toString()));
    }

    private void mostrarResultado(Resultado<br.com.eventoja.data.model.Usuario> resultado) {
        progress.setVisibility(resultado.isCarregando() ? View.VISIBLE : View.GONE);
        botaoEntrar.setEnabled(!resultado.isCarregando());
        if (resultado.isErro()) {
            Toast.makeText(this, resultado.getMensagem(), Toast.LENGTH_LONG).show();
        } else if (resultado.isSucesso()) {
            Toast.makeText(this, R.string.login_welcome, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
