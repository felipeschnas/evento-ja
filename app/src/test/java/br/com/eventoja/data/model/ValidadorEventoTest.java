package br.com.eventoja.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ValidadorEventoTest {

    private Evento eventoValido() {
        return new Evento("Feira", "Descricao", "Praca Central",
                "06/12/2026 09:00", "06/12/2026 18:00", "Cultura");
    }

    @Test
    public void eventoValidoNaoTemErro() {
        assertNull(ValidadorEvento.validar(eventoValido()));
    }

    @Test
    public void descricaoECategoriaSaoOpcionais() {
        Evento evento = eventoValido();
        evento.setDescricao(null);
        evento.setCategoria("");

        assertNull(ValidadorEvento.validar(evento));
    }

    @Test
    public void tituloObrigatorio() {
        Evento evento = eventoValido();
        evento.setTitulo("   ");

        assertEquals("Informe o título do evento.", ValidadorEvento.validar(evento));
    }

    @Test
    public void localObrigatorio() {
        Evento evento = eventoValido();
        evento.setLocal(null);

        assertEquals("Informe o local do evento.", ValidadorEvento.validar(evento));
    }

    @Test
    public void dataComFormatoErrado() {
        Evento evento = eventoValido();
        evento.setDataInicio("2026-12-06 09:00");

        assertEquals("Data de início inválida. Use o formato dd/MM/yyyy HH:mm.", ValidadorEvento.validar(evento));
    }

    @Test
    public void dataQueNaoExiste() {
        Evento evento = eventoValido();
        evento.setDataFim("31/02/2026 10:00");

        assertEquals("Data de fim inválida. Use o formato dd/MM/yyyy HH:mm.", ValidadorEvento.validar(evento));
    }

    @Test
    public void fimAntesDoInicio() {
        Evento evento = eventoValido();
        evento.setDataFim("05/12/2026 18:00");

        assertEquals("A data de fim não pode ser antes da data de início.", ValidadorEvento.validar(evento));
    }

    @Test
    public void fimIgualAoInicioEhPermitido() {
        Evento evento = eventoValido();
        evento.setDataFim(evento.getDataInicio());

        assertNull(ValidadorEvento.validar(evento));
    }
}
