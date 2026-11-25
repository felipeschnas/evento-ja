package br.com.eventoja.data.repository;

import androidx.annotation.Nullable;

import br.com.eventoja.data.model.Usuario;

/** Login e dados do usuário logado. */
public interface UsuarioRepository {

    /** Faz login e, se der certo, guarda o usuário e o token na sessão. */
    void login(String login, String senha, RepositorioCallback<Usuario> callback);

    void logout();

    @Nullable
    Usuario getUsuarioLogado();

    boolean isLogado();

    boolean isAdmin();
}
