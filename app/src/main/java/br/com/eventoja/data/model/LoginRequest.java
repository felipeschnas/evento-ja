package br.com.eventoja.data.model;

/** Corpo enviado no POST /api/login. */
public class LoginRequest {

    private final String login;
    private final String senha;

    public LoginRequest(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }
}
