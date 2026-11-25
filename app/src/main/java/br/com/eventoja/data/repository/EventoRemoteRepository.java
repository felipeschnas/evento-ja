package br.com.eventoja.data.repository;

import java.util.List;

import br.com.eventoja.data.local.SessaoUsuario;
import br.com.eventoja.data.model.Evento;
import br.com.eventoja.data.remote.EventoApi;

/** Busca e altera os eventos na API REST usando o Retrofit. */
public class EventoRemoteRepository implements EventoRepository {

    private final EventoApi api;
    private final SessaoUsuario sessao;

    public EventoRemoteRepository(EventoApi api, SessaoUsuario sessao) {
        this.api = api;
        this.sessao = sessao;
    }

    @Override
    public void listar(RepositorioCallback<List<Evento>> callback) {
        api.listarEventos().enqueue(new CallbackApi<>(callback, sessao));
    }

    @Override
    public void buscar(long id, RepositorioCallback<Evento> callback) {
        api.buscarEvento(id).enqueue(new CallbackApi<>(callback, sessao));
    }

    @Override
    public void criar(Evento evento, RepositorioCallback<Evento> callback) {
        api.criarEvento(evento).enqueue(new CallbackApi<>(callback, sessao));
    }

    @Override
    public void atualizar(long id, Evento evento, RepositorioCallback<Evento> callback) {
        api.atualizarEvento(id, evento).enqueue(new CallbackApi<>(callback, sessao));
    }

    @Override
    public void excluir(long id, RepositorioCallback<Void> callback) {
        api.excluirEvento(id).enqueue(new CallbackApi<>(callback, sessao));
    }
}
