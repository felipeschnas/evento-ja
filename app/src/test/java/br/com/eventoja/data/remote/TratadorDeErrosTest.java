package br.com.eventoja.data.remote;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonSyntaxException;

import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class TratadorDeErrosTest {

    private static final MediaType JSON = MediaType.get("application/json");

    @Test
    public void usaMensagemEnviadaPelaApi() {
        Response<Object> resposta = Response.error(400, ResponseBody.create(
                "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Titulo obrigatorio\"}", JSON));

        assertEquals("Titulo obrigatorio", TratadorDeErros.mensagemDeResposta(resposta));
    }

    @Test
    public void usaMensagemPadraoQuandoCorpoNaoTemMensagem() {
        Response<Object> resposta = Response.error(403, ResponseBody.create("{}", JSON));

        assertEquals(TratadorDeErros.mensagemPorStatus(403), TratadorDeErros.mensagemDeResposta(resposta));
    }

    @Test
    public void usaMensagemPadraoQuandoCorpoNaoEJson() {
        Response<Object> resposta = Response.error(500, ResponseBody.create("<html>erro</html>", JSON));

        assertEquals(TratadorDeErros.mensagemPorStatus(500), TratadorDeErros.mensagemDeResposta(resposta));
    }

    @Test
    public void erroDeServidorTemMensagemGenerica() {
        assertEquals("Erro no servidor. Tente novamente mais tarde.", TratadorDeErros.mensagemPorStatus(503));
    }

    @Test
    public void semConexaoComOServidor() {
        assertEquals(TratadorDeErros.SEM_CONEXAO,
                TratadorDeErros.mensagemDeFalha(new ConnectException("Connection refused")));
    }

    @Test
    public void tempoEsgotado() {
        assertEquals(TratadorDeErros.TEMPO_ESGOTADO,
                TratadorDeErros.mensagemDeFalha(new SocketTimeoutException()));
    }

    @Test
    public void respostaComJsonInvalido() {
        assertEquals(TratadorDeErros.RESPOSTA_INVALIDA,
                TratadorDeErros.mensagemDeFalha(new JsonSyntaxException("json quebrado")));
    }

    @Test
    public void outroErroDeRede() {
        assertEquals(TratadorDeErros.SEM_CONEXAO, TratadorDeErros.mensagemDeFalha(new IOException()));
    }
}
