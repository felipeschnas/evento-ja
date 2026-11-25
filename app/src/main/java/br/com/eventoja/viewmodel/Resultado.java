package br.com.eventoja.viewmodel;

import androidx.annotation.Nullable;

/**
 * Estado de uma operação para a tela mostrar:
 * CARREGANDO (mostrar progresso), SUCESSO (usar os dados) ou ERRO (mostrar a mensagem).
 */
public class Resultado<T> {

    public enum Status {
        CARREGANDO,
        SUCESSO,
        ERRO
    }

    private final Status status;
    private final T dados;
    private final String mensagem;

    private Resultado(Status status, @Nullable T dados, @Nullable String mensagem) {
        this.status = status;
        this.dados = dados;
        this.mensagem = mensagem;
    }

    public static <T> Resultado<T> carregando() {
        return new Resultado<>(Status.CARREGANDO, null, null);
    }

    public static <T> Resultado<T> sucesso(@Nullable T dados) {
        return new Resultado<>(Status.SUCESSO, dados, null);
    }

    public static <T> Resultado<T> erro(String mensagem) {
        return new Resultado<>(Status.ERRO, null, mensagem);
    }

    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getDados() {
        return dados;
    }

    @Nullable
    public String getMensagem() {
        return mensagem;
    }

    public boolean isCarregando() {
        return status == Status.CARREGANDO;
    }

    public boolean isSucesso() {
        return status == Status.SUCESSO;
    }

    public boolean isErro() {
        return status == Status.ERRO;
    }
}
