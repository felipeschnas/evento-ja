package br.com.eventoja.data.model;

/**
 * Corpo de erro padrão do Spring Boot, ex.:
 * { "status": 400, "error": "Bad Request", "message": "Titulo obrigatorio" }
 */
public class ErroApi {

    private int status;
    private String error;
    private String message;

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
