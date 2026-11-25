package br.com.eventoja.data.remote;

import java.util.List;

import br.com.eventoja.data.model.Evento;
import br.com.eventoja.data.model.LoginRequest;
import br.com.eventoja.data.model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Endpoints da API de eventos (back-end Spring Boot).
 * O header "token" é adicionado automaticamente pelo {@link TokenInterceptor}.
 */
public interface EventoApi {

    @POST("api/login")
    Call<Usuario> login(@Body LoginRequest dados);

    @GET("api/eventos")
    Call<List<Evento>> listarEventos();

    @GET("api/eventos/{id}")
    Call<Evento> buscarEvento(@Path("id") long id);

    @POST("api/eventos")
    Call<Evento> criarEvento(@Body Evento evento);

    @PUT("api/eventos/{id}")
    Call<Evento> atualizarEvento(@Path("id") long id, @Body Evento evento);

    @DELETE("api/eventos/{id}")
    Call<Void> excluirEvento(@Path("id") long id);
}
