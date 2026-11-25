package br.com.eventoja.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import br.com.eventoja.data.model.Usuario;
import br.com.eventoja.data.repository.RepositorioCallback;
import br.com.eventoja.data.repository.Repositorios;
import br.com.eventoja.data.repository.UsuarioRepository;

/** ViewModel da tela de login. */
public class LoginViewModel extends AndroidViewModel {

    private final UsuarioRepository usuarioRepository;
    private final MutableLiveData<Resultado<Usuario>> resultadoLogin = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        usuarioRepository = Repositorios.usuarios(application);
    }

    public LiveData<Resultado<Usuario>> getResultadoLogin() {
        return resultadoLogin;
    }

    public void entrar(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            resultadoLogin.setValue(Resultado.erro("Informe o login e a senha."));
            return;
        }

        resultadoLogin.setValue(Resultado.carregando());
        usuarioRepository.login(login.trim(), senha, new RepositorioCallback<Usuario>() {
            @Override
            public void onSucesso(Usuario usuario) {
                resultadoLogin.setValue(Resultado.sucesso(usuario));
            }

            @Override
            public void onErro(String mensagem) {
                resultadoLogin.setValue(Resultado.erro(mensagem));
            }
        });
    }

    public void sair() {
        usuarioRepository.logout();
    }

    public boolean isLogado() {
        return usuarioRepository.isLogado();
    }

    public boolean isAdmin() {
        return usuarioRepository.isAdmin();
    }

    @Nullable
    public Usuario getUsuarioLogado() {
        return usuarioRepository.getUsuarioLogado();
    }
}
