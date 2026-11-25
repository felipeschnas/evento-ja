package br.com.eventoja.data.remote;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.eventoja.data.model.ErroApi;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Transforma os erros das chamadas da API em mensagens para mostrar ao usuário.
 *
 * Existem dois tipos de erro:
 * - a requisição nem chegou na API (sem internet, API desligada, demorou demais);
 * - a API respondeu com erro (400, 401, 403, 404, 500...).
 */
public final class TratadorDeErros {

    static final String SEM_CONEXAO =
            "Não foi possível conectar ao servidor. Verifique a internet e se a API está rodando.";
    static final String TEMPO_ESGOTADO =
            "O servidor demorou demais para responder. Tente novamente.";
    static final String RESPOSTA_INVALIDA =
            "O servidor enviou uma resposta inesperada.";
    static final String ERRO_DESCONHECIDO =
            "Ocorreu um erro inesperado. Tente novamente.";

    private static final Gson GSON = new Gson();

    private TratadorDeErros() {
    }

    /** Erro quando a requisição falhou antes de ter uma resposta (onFailure do Retrofit). */
    public static String mensagemDeFalha(Throwable erro) {
        if (erro instanceof SocketTimeoutException) {
            return TEMPO_ESGOTADO;
        }
        if (erro instanceof ConnectException || erro instanceof UnknownHostException) {
            return SEM_CONEXAO;
        }
        if (erro instanceof JsonParseException) {
            return RESPOSTA_INVALIDA;
        }
        if (erro instanceof IOException) {
            return SEM_CONEXAO;
        }
        return ERRO_DESCONHECIDO;
    }

    /**
     * Erro quando a API respondeu com status de erro.
     * Usa a mensagem que a própria API mandou (ex.: "Titulo obrigatorio");
     * se não tiver, usa uma mensagem padrão para o status.
     */
    public static String mensagemDeResposta(Response<?> resposta) {
        String mensagemDaApi = lerMensagemDaApi(resposta.errorBody());
        if (mensagemDaApi != null) {
            return mensagemDaApi;
        }
        return mensagemPorStatus(resposta.code());
    }

    static String mensagemPorStatus(int status) {
        switch (status) {
            case 400:
                return "Dados inválidos. Confira os campos e tente novamente.";
            case 401:
                return "Você precisa fazer login para continuar.";
            case 403:
                return "Apenas administradores podem fazer isso.";
            case 404:
                return "Evento não encontrado.";
            default:
                if (status >= 500) {
                    return "Erro no servidor. Tente novamente mais tarde.";
                }
                return ERRO_DESCONHECIDO;
        }
    }

    private static String lerMensagemDaApi(ResponseBody corpo) {
        if (corpo == null) {
            return null;
        }
        try {
            ErroApi erro = GSON.fromJson(corpo.string(), ErroApi.class);
            if (erro == null || erro.getMessage() == null || erro.getMessage().trim().isEmpty()) {
                return null;
            }
            return erro.getMessage();
        } catch (IOException | JsonParseException e) {
            return null;
        }
    }
}
