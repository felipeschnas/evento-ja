package br.com.eventoja.data.repository;

import java.util.ArrayList;
import java.util.List;

import br.com.eventoja.data.local.SessaoUsuario;
import br.com.eventoja.data.model.Evento;

/**
 * Eventos de exemplo guardados em memória, para testar as telas sem a API.
 * Segue as mesmas regras da API (precisa ser admin para alterar).
 */
public class EventoMockRepository implements EventoRepository {

    private final List<Evento> eventos = new ArrayList<>();
    private final ExecutorMock executor = new ExecutorMock();
    private final SessaoUsuario sessao;
    private long proximoId = 1;

    public EventoMockRepository(SessaoUsuario sessao) {
        this.sessao = sessao;
        adicionar(new Evento("Feira de Artesanato", "Feira com artesaos locais, comidas tipicas e musica ao vivo.", "Praca Central", "06/12/2026 09:00", "06/12/2026 18:00", "Cultura"));
        adicionar(new Evento("Cinema ao Ar Livre", "Sessao gratuita de filmes nacionais. Leve sua cadeira!", "Parque da Cidade", "12/12/2026 19:30", "12/12/2026 22:00", "Cultura"));
        adicionar(new Evento("Corrida de Rua 5K", "Corrida aberta ao publico com inscricao gratuita.", "Parque da Cidade", "13/12/2026 07:00", "13/12/2026 10:00", "Esporte"));
        adicionar(new Evento("Show de Natal", "Apresentacao do coral municipal e da orquestra da cidade.", "Teatro Municipal", "20/12/2026 20:00", "20/12/2026 22:30", "Musica"));
        adicionar(new Evento("Festival Gastronomico", "Food trucks e restaurantes da regiao reunidos em um so lugar.", "Centro de Eventos", "10/01/2027 11:00", "12/01/2027 23:00", "Gastronomia"));
    }

    @Override
    public void listar(RepositorioCallback<List<Evento>> callback) {
        executor.executar(() -> {
            verificarLogado();
            return copiarTodos();
        }, callback);
    }

    @Override
    public void buscar(long id, RepositorioCallback<Evento> callback) {
        executor.executar(() -> {
            verificarLogado();
            return copiar(encontrar(id));
        }, callback);
    }

    @Override
    public void criar(Evento evento, RepositorioCallback<Evento> callback) {
        executor.executar(() -> {
            verificarAdmin();
            return copiar(adicionar(copiar(evento)));
        }, callback);
    }

    @Override
    public void atualizar(long id, Evento evento, RepositorioCallback<Evento> callback) {
        executor.executar(() -> {
            verificarAdmin();
            return copiar(substituir(id, evento));
        }, callback);
    }

    @Override
    public void excluir(long id, RepositorioCallback<Void> callback) {
        executor.executar(() -> {
            verificarAdmin();
            remover(id);
            return null;
        }, callback);
    }

    // Os métodos abaixo são synchronized porque a lista é acessada pela thread do executor

    private synchronized Evento adicionar(Evento evento) {
        evento.setId(proximoId++);
        eventos.add(evento);
        return evento;
    }

    private synchronized Evento substituir(long id, Evento evento) throws ExecutorMock.ErroMock {
        Evento existente = encontrar(id);
        Evento novo = copiar(evento);
        novo.setId(id);
        eventos.set(eventos.indexOf(existente), novo);
        return novo;
    }

    private synchronized void remover(long id) throws ExecutorMock.ErroMock {
        eventos.remove(encontrar(id));
    }

    private synchronized Evento encontrar(long id) throws ExecutorMock.ErroMock {
        for (Evento evento : eventos) {
            if (evento.getId() == id) {
                return evento;
            }
        }
        throw new ExecutorMock.ErroMock("Evento não encontrado.");
    }

    private synchronized List<Evento> copiarTodos() {
        List<Evento> copia = new ArrayList<>();
        for (Evento evento : eventos) {
            copia.add(copiar(evento));
        }
        return copia;
    }

    private void verificarLogado() throws ExecutorMock.ErroMock {
        if (!sessao.isLogado()) {
            throw new ExecutorMock.ErroMock("Você precisa fazer login para continuar.");
        }
    }

    private void verificarAdmin() throws ExecutorMock.ErroMock {
        verificarLogado();
        if (!sessao.isAdmin()) {
            throw new ExecutorMock.ErroMock("Apenas administradores podem fazer isso.");
        }
    }

    /** Devolve cópias para a tela não alterar a lista "do servidor" sem querer. */
    private static Evento copiar(Evento original) {
        Evento copia = new Evento(original.getTitulo(), original.getDescricao(), original.getLocal(),
                original.getDataInicio(), original.getDataFim(), original.getCategoria());
        copia.setId(original.getId());
        return copia;
    }
}
