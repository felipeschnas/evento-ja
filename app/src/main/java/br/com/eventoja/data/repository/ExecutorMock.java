package br.com.eventoja.data.repository;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simula uma chamada de rede para os repositórios de exemplo (mock):
 * roda a tarefa em outra thread, espera um pouco e entrega o resultado
 * na thread principal, igual acontece com o Retrofit.
 */
class ExecutorMock {

    private static final long ATRASO_MS = 600;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler threadPrincipal = new Handler(Looper.getMainLooper());

    /** Tarefa que devolve um resultado ou lança um erro com a mensagem. */
    interface Tarefa<T> {
        T executar() throws ErroMock;
    }

    static class ErroMock extends Exception {
        ErroMock(String mensagem) {
            super(mensagem);
        }
    }

    <T> void executar(Tarefa<T> tarefa, RepositorioCallback<T> callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(ATRASO_MS);
                T resultado = tarefa.executar();
                threadPrincipal.post(() -> callback.onSucesso(resultado));
            } catch (ErroMock e) {
                threadPrincipal.post(() -> callback.onErro(e.getMessage()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
