package br.com.eventoja.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import br.com.eventoja.data.model.Usuario;

/**
 * Guarda o usuário logado (e o token dele) nas SharedPreferences,
 * para o login continuar valendo mesmo depois de fechar o app.
 */
public class SessaoUsuario {

    private static final String ARQUIVO = "sessao_usuario";
    private static final String CHAVE_ID = "id";
    private static final String CHAVE_NOME = "nome";
    private static final String CHAVE_LOGIN = "login";
    private static final String CHAVE_ADMIN = "admin";
    private static final String CHAVE_TOKEN = "token";

    private static SessaoUsuario instancia;

    private final SharedPreferences preferencias;

    private SessaoUsuario(Context context) {
        preferencias = context.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
    }

    public static synchronized SessaoUsuario getInstance(Context context) {
        if (instancia == null) {
            instancia = new SessaoUsuario(context.getApplicationContext());
        }
        return instancia;
    }

    public void salvar(Usuario usuario) {
        preferencias.edit()
                .putLong(CHAVE_ID, usuario.getId() == null ? -1 : usuario.getId())
                .putString(CHAVE_NOME, usuario.getNome())
                .putString(CHAVE_LOGIN, usuario.getLogin())
                .putBoolean(CHAVE_ADMIN, usuario.isAdmin())
                .putString(CHAVE_TOKEN, usuario.getToken())
                .apply();
    }

    public void limpar() {
        preferencias.edit().clear().apply();
    }

    @Nullable
    public String getToken() {
        return preferencias.getString(CHAVE_TOKEN, null);
    }

    public boolean isLogado() {
        return getToken() != null;
    }

    public boolean isAdmin() {
        return isLogado() && preferencias.getBoolean(CHAVE_ADMIN, false);
    }

    /** Retorna o usuário logado ou null se ninguém fez login. */
    @Nullable
    public Usuario getUsuario() {
        if (!isLogado()) {
            return null;
        }
        return new Usuario(
                preferencias.getLong(CHAVE_ID, -1),
                preferencias.getString(CHAVE_NOME, null),
                preferencias.getString(CHAVE_LOGIN, null),
                preferencias.getBoolean(CHAVE_ADMIN, false),
                getToken());
    }
}
