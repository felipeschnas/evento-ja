package br.com.eventoja.data.repository;

import androidx.annotation.NonNull;

import br.com.eventoja.data.local.SessaoUsuario;
import br.com.eventoja.data.remote.TratadorDeErros;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Converte a resposta do Retrofit para o {@link RepositorioCallback},
 * já tratando os erros e transformando em mensagem.
 * No Android o Retrofit chama este callback na thread principal.
 */
class CallbackApi<T> implements Callback<T> {

    private static final int NAO_AUTORIZADO = 401;

    private final RepositorioCallback<T> callback;
    private final SessaoUsuario sessao;

    CallbackApi(RepositorioCallback<T> callback, SessaoUsuario sessao) {
        this.callback = callback;
        this.sessao = sessao;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> resposta) {
        if (resposta.isSuccessful()) {
            callback.onSucesso(resposta.body());
            return;
        }
        if (resposta.code() == NAO_AUTORIZADO) {
            // Token inválido ou expirado: desloga para o usuário entrar de novo
            sessao.limpar();
        }
        callback.onErro(TratadorDeErros.mensagemDeResposta(resposta));
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable erro) {
        if (call.isCanceled()) {
            return;
        }
        callback.onErro(TratadorDeErros.mensagemDeFalha(erro));
    }
}
