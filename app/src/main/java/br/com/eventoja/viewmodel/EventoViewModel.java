package br.com.eventoja.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.eventoja.data.model.Evento;
import br.com.eventoja.data.model.ValidadorEvento;
import br.com.eventoja.data.repository.EventoRepository;
import br.com.eventoja.data.repository.RepositorioCallback;
import br.com.eventoja.data.repository.Repositorios;
import br.com.eventoja.data.repository.UsuarioRepository;

/**
 * ViewModel das telas de eventos (lista, detalhe e formulário).
 *
 * Uso na Activity:
 * <pre>
 * EventoViewModel viewModel = new ViewModelProvider(this).get(EventoViewModel.class);
 * viewModel.getEventos().observe(this, resultado -> { ... });
 * viewModel.carregarEventos();
 * </pre>
 */
public class EventoViewModel extends AndroidViewModel {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    private final MutableLiveData<Resultado<List<Evento>>> eventos = new MutableLiveData<>();
    private final MutableLiveData<Resultado<Evento>> eventoSelecionado = new MutableLiveData<>();
    private final MutableLiveData<Resultado<Evento>> resultadoSalvar = new MutableLiveData<>();
    private final MutableLiveData<Resultado<Long>> resultadoExcluir = new MutableLiveData<>();

    public EventoViewModel(@NonNull Application application) {
        super(application);
        eventoRepository = Repositorios.eventos(application);
        usuarioRepository = Repositorios.usuarios(application);
    }

    /** Lista de eventos (tela de lista). */
    public LiveData<Resultado<List<Evento>>> getEventos() {
        return eventos;
    }

    /** Evento buscado por id (tela de detalhe ou formulário de edição). */
    public LiveData<Resultado<Evento>> getEventoSelecionado() {
        return eventoSelecionado;
    }

    /** Resultado de criar ou editar um evento. */
    public LiveData<Resultado<Evento>> getResultadoSalvar() {
        return resultadoSalvar;
    }

    /** Resultado da exclusão. Em caso de sucesso, os dados são o id do evento excluído. */
    public LiveData<Resultado<Long>> getResultadoExcluir() {
        return resultadoExcluir;
    }

    /** Só administradores podem criar, editar e excluir. Use para mostrar ou esconder os botões. */
    public boolean podeEditar() {
        return usuarioRepository.isAdmin();
    }

    public void carregarEventos() {
        eventos.setValue(Resultado.carregando());
        eventoRepository.listar(new RepositorioCallback<List<Evento>>() {
            @Override
            public void onSucesso(List<Evento> dados) {
                eventos.setValue(Resultado.sucesso(dados));
            }

            @Override
            public void onErro(String mensagem) {
                eventos.setValue(Resultado.erro(mensagem));
            }
        });
    }

    public void buscarEvento(long id) {
        eventoSelecionado.setValue(Resultado.carregando());
        eventoRepository.buscar(id, new CallbackPara<>(eventoSelecionado));
    }

    /** Cria o evento se ele ainda não tiver id, senão atualiza. */
    public void salvarEvento(Evento evento) {
        String erro = ValidadorEvento.validar(evento);
        if (erro != null) {
            resultadoSalvar.setValue(Resultado.erro(erro));
            return;
        }

        resultadoSalvar.setValue(Resultado.carregando());
        if (evento.getId() == null) {
            eventoRepository.criar(evento, new CallbackPara<>(resultadoSalvar));
        } else {
            eventoRepository.atualizar(evento.getId(), evento, new CallbackPara<>(resultadoSalvar));
        }
    }

    public void excluirEvento(long id) {
        resultadoExcluir.setValue(Resultado.carregando());
        eventoRepository.excluir(id, new RepositorioCallback<Void>() {
            @Override
            public void onSucesso(Void dados) {
                resultadoExcluir.setValue(Resultado.sucesso(id));
                carregarEventos();
            }

            @Override
            public void onErro(String mensagem) {
                resultadoExcluir.setValue(Resultado.erro(mensagem));
            }
        });
    }

    /** Callback que só repassa o resultado para um LiveData. */
    private static class CallbackPara<T> implements RepositorioCallback<T> {

        private final MutableLiveData<Resultado<T>> destino;

        CallbackPara(MutableLiveData<Resultado<T>> destino) {
            this.destino = destino;
        }

        @Override
        public void onSucesso(T dados) {
            destino.setValue(Resultado.sucesso(dados));
        }

        @Override
        public void onErro(String mensagem) {
            destino.setValue(Resultado.erro(mensagem));
        }
    }
}
