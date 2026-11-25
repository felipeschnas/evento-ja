package br.com.eventoja.data.repository;

import androidx.annotation.Nullable;

import java.util.UUID;

import br.com.eventoja.data.local.SessaoUsuario;
import br.com.eventoja.data.model.Usuario;

/** Login de exemplo com os mesmos usuários da API (admin/admin123 e joao/123). */
public class UsuarioMockRepository implements UsuarioRepository {

    private final ExecutorMock executor = new ExecutorMock();
    private final SessaoUsuario sessao;

    public UsuarioMockRepository(SessaoUsuario sessao) {
        this.sessao = sessao;
    }

    @Override
    public void login(String login, String senha, RepositorioCallback<Usuario> callback) {
        executor.executar(() -> {
            Usuario usuario = autenticar(login, senha);
            sessao.salvar(usuario);
            return usuario;
        }, callback);
    }

    private Usuario autenticar(String login, String senha) throws ExecutorMock.ErroMock {
        String token = UUID.randomUUID().toString();
        if ("admin".equals(login) && "admin123".equals(senha)) {
            return new Usuario(1L, "Administrador", "admin", true, token);
        }
        if ("joao".equals(login) && "123".equals(senha)) {
            return new Usuario(2L, "Joao", "joao", false, token);
        }
        throw new ExecutorMock.ErroMock("Login ou senha invalidos");
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
