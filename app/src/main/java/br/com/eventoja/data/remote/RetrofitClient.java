package br.com.eventoja.data.remote;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import br.com.eventoja.BuildConfig;
import br.com.eventoja.data.local.SessaoUsuario;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cria uma única instância do Retrofit para o app inteiro.
 * O endereço da API vem do BuildConfig (ver app/build.gradle).
 */
public final class RetrofitClient {

    private static final int TIMEOUT_SEGUNDOS = 15;

    private static EventoApi api;

    private RetrofitClient() {
    }

    public static synchronized EventoApi getApi(Context context) {
        if (api == null) {
            api = criarRetrofit(SessaoUsuario.getInstance(context)).create(EventoApi.class);
        }
        return api;
    }

    private static Retrofit criarRetrofit(SessaoUsuario sessao) {
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        // Mostra as requisições e respostas no Logcat só na versão de debug
        log.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient cliente = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                .addInterceptor(new TokenInterceptor(sessao))
                .addInterceptor(log)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
