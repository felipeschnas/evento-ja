package br.com.eventoja.data.model;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Confere os dados do evento antes de enviar para a API,
 * com as mesmas regras do back-end. Assim o usuário vê o erro na hora,
 * sem precisar esperar a resposta do servidor.
 */
public final class ValidadorEvento {

    private ValidadorEvento() {
    }

    /** Retorna a mensagem do primeiro problema encontrado, ou null se estiver tudo certo. */
    @Nullable
    public static String validar(Evento evento) {
        if (vazio(evento.getTitulo())) {
            return "Informe o título do evento.";
        }
        if (vazio(evento.getLocal())) {
            return "Informe o local do evento.";
        }
        if (vazio(evento.getDataInicio())) {
            return "Informe a data de início.";
        }
        if (vazio(evento.getDataFim())) {
            return "Informe a data de fim.";
        }

        Date inicio = converterData(evento.getDataInicio());
        if (inicio == null) {
            return "Data de início inválida. Use o formato " + Evento.FORMATO_DATA + ".";
        }
        Date fim = converterData(evento.getDataFim());
        if (fim == null) {
            return "Data de fim inválida. Use o formato " + Evento.FORMATO_DATA + ".";
        }
        if (fim.before(inicio)) {
            return "A data de fim não pode ser antes da data de início.";
        }
        return null;
    }

    @Nullable
    private static Date converterData(String texto) {
        SimpleDateFormat formato = new SimpleDateFormat(Evento.FORMATO_DATA, Locale.getDefault());
        formato.setLenient(false); // não aceita datas como 32/13/2026
        try {
            return formato.parse(texto.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    private static boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
