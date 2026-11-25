package br.com.eventoja.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;

import br.com.eventoja.data.local.SessaoUsuario;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Coloca o token do usuário logado no header "token" de todas as requisições,
 * assim cada chamada da API não precisa receber o token como parâmetro.
 */
public class TokenInterceptor implements Interceptor {

    private static final String HEADER_TOKEN = "token";

    private final SessaoUsuario sessao;

    public TokenInterceptor(SessaoUsuario sessao) {
        this.sessao = sessao;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request requisicao = chain.request();
        String token = sessao.getToken();

        if (token == null) {
            return chain.proceed(requisicao);
        }

        Request comToken = requisicao.newBuilder()
                .header(HEADER_TOKEN, token)
                .build();
        return chain.proceed(comToken);
    }
}
