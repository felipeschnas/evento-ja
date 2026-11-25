package br.com.eventoja.data.repository;

import androidx.annotation.Nullable;

import br.com.eventoja.data.local.SessaoUsuario;
import br.com.eventoja.data.model.LoginRequest;
import br.com.eventoja.data.model.Usuario;
import br.com.eventoja.data.remote.EventoApi;

/** Faz o login na API e guarda o token na sessão. */
public class UsuarioRemoteRepository implements UsuarioRepository {

    private final EventoApi api;
    private final SessaoUsuario sessao;

    public UsuarioRemoteRepository(EventoApi api, SessaoUsuario sessao) {
        this.api = api;
        this.sessao = sessao;
    }

    @Override
    public void login(String login, String senha, RepositorioCallback<Usuario> callback) {
        api.login(new LoginRequest(login, senha)).enqueue(new CallbackApi<>(new RepositorioCallback<Usuario>() {
            @Override
            public void onSucesso(Usuario usuario) {
                sessao.salvar(usuario);
                callback.onSucesso(usuario);
            }

            @Override
            public void onErro(String mensagem) {
                callback.onErro(mensagem);
            }
        }, sessao));
    }

    @Override
    public void logout() {
        sessao.limpar();
    }

    @Nullable
    @Override
    public Usuario getUsuarioLogado() {
        return sessao.getUsuario();
    }

    @Override
    public boolean isLogado() {
        return sessao.isLogado();
    }

    @Override
    public boolean isAdmin() {
        return sessao.isAdmin();
    }
}
