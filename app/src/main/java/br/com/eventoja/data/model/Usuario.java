package br.com.eventoja.data.model;

/**
 * Usuário retornado pelo login (/api/login).
 * O token precisa ser enviado no header "token" para criar, alterar ou excluir eventos.
 */
public class Usuario {

    private Long id;
    private String nome;
    private String login;
    private boolean admin;
    private String token;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String login, boolean admin, String token) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.admin = admin;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getToken() {
        return token;
    }
}
