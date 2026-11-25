package br.com.eventoja.data.model;

import java.io.Serializable;

/**
 * Evento retornado pela API (/api/eventos).
 * Os nomes dos campos são iguais aos do JSON para o Gson converter direto.
 * Implementa Serializable para poder ser enviado entre telas via Intent.
 */
public class Evento implements Serializable {

    /** Formato usado pela API nas datas, ex.: 06/12/2026 09:00 */
    public static final String FORMATO_DATA = "dd/MM/yyyy HH:mm";

    private Long id;
    private String titulo;
    private String descricao;
    private String local;
    private String dataInicio;
    private String dataFim;
    private String categoria;

    public Evento() {
    }

    public Evento(String titulo, String descricao, String local,
                  String dataInicio, String dataFim, String categoria) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.local = local;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
