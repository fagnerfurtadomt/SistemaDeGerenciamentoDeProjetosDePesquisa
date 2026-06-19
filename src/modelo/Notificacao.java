package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma Notificação no sistema.
 * Demonstra: Classes e Objetos, Encapsulamento
 */
public class Notificacao {

    private static int contadorId = 1;

    public static final String TIPO_NOVO_PROJETO = "NOVO_PROJETO";
    public static final String TIPO_PRAZO = "PRAZO";
    public static final String TIPO_ATUALIZACAO = "ATUALIZACAO";
    public static final String TIPO_ENCERRAMENTO = "ENCERRAMENTO";
    public static final String TIPO_VALIDACAO = "VALIDACAO";
    public static final String TIPO_INSCRICAO = "INSCRICAO";

    private final int id;
    private String mensagem;
    private String tipo;
    private boolean lida;
    private String dataHora;

    public Notificacao(String mensagem, String tipo) {
        this.id = contadorId++;
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.lida = false;
        this.dataHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public int getId() { return id; }
    public String getMensagem() { return mensagem; }
    public String getTipo() { return tipo; }
    public boolean isLida() { return lida; }
    public String getDataHora() { return dataHora; }

    @Override
    public String toString() {
        String status = lida ? "[LIDA]" : "[NOVA]";
        return status + " [" + dataHora + "] " + mensagem;
    }
}
