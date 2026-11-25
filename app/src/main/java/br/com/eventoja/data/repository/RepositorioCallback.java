package br.com.eventoja.data.repository;

/**
 * Resposta assíncrona de um repositório.
 * Os dois métodos são sempre chamados na thread principal (pode mexer na tela direto).
 */
public interface RepositorioCallback<T> {

    void onSucesso(T dados);

    void onErro(String mensagem);
}
