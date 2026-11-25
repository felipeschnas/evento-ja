package br.com.eventoja.data.repository;

import android.content.Context;

import br.com.eventoja.BuildConfig;
import br.com.eventoja.data.local.SessaoUsuario;
import br.com.eventoja.data.remote.EventoApi;
import br.com.eventoja.data.remote.RetrofitClient;

/**
 * Ponto único para pegar os repositórios do app.
 * Se BuildConfig.USAR_MOCK for true, usa os dados de exemplo em vez da API.
 */
public final class Repositorios {

    private static EventoRepository eventoRepository;
    private static UsuarioRepository usuarioRepository;

    private Repositorios() {
    }

    public static synchronized EventoRepository eventos(Context context) {
        if (eventoRepository == null) {
            SessaoUsuario sessao = SessaoUsuario.getInstance(context);
            eventoRepository = BuildConfig.USAR_MOCK
                    ? new EventoMockRepository(sessao)
                    : new EventoRemoteRepository(api(context), sessao);
        }
        return eventoRepository;
    }

    public static synchronized UsuarioRepository usuarios(Context context) {
        if (usuarioRepository == null) {
            SessaoUsuario sessao = SessaoUsuario.getInstance(context);
            usuarioRepository = BuildConfig.USAR_MOCK
                    ? new UsuarioMockRepository(sessao)
                    : new UsuarioRemoteRepository(api(context), sessao);
        }
        return usuarioRepository;
    }

    private static EventoApi api(Context context) {
        return RetrofitClient.getApi(context);
    }
}
