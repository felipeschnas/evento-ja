package br.com.eventoja.data.repository;

import java.util.List;

import br.com.eventoja.data.model.Evento;

/**
 * Operações com eventos. Quem usa não precisa saber se os dados
 * vêm da API ou dos dados de exemplo (mock).
 */
public interface EventoRepository {

    void listar(RepositorioCallback<List<Evento>> callback);

    void buscar(long id, RepositorioCallback<Evento> callback);

    /** Precisa estar logado como admin. */
    void criar(Evento evento, RepositorioCallback<Evento> callback);

    /** Precisa estar logado como admin. */
    void atualizar(long id, Evento evento, RepositorioCallback<Evento> callback);

    /** Precisa estar logado como admin. */
    void excluir(long id, RepositorioCallback<Void> callback);
}
