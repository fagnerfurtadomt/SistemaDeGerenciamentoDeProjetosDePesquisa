package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Relatorio {

    //Atributos
    private static int contadorId = 1;

    private final int id;
    private Aluno autor;
    private Projeto projeto;
    private String titulo;
    private String conteudo;
    private String dataEnvio;
    private boolean validado;
    private boolean aprovado;
    private String feedback;

    public Relatorio(Aluno autor, Projeto projeto, String titulo, String conteudo) {
        this.id = contadorId++;
        this.autor = autor;
        this.projeto = projeto;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.dataEnvio = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.validado = false;
        this.aprovado = false;
        this.feedback = "";
    }


    //Getters e Setters
    public int getId() {
        return id;
    }

    public Aluno getAutor() {
        return autor;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public boolean isValidado() {
        return validado;
    }

    public void setValidado(boolean validado) {
        this.validado = validado;
    }

    public boolean isAprovado() {
        return aprovado;
    }

    public void setAprovado(boolean aprovado) {
        this.aprovado = aprovado;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    //Metodos sobrecritos
    @Override
    public String toString() {
        String statusValidacao = validado ? (aprovado ? "✓ Aprovado" : "✗ Reprovado") : "Aguardando validação";
        return String.format("[%d] %s | Autor: %s | Projeto: %s | %s | %s",
                id, titulo, autor.getNome(), projeto.getTitulo(), dataEnvio, statusValidacao);
    }

    public String toDetalhes() {
        String statusValidacao = validado ? (aprovado ? "✓ Aprovado" : "✗ Reprovado") : "Aguardando validação";
        return "========================================\n" +
               "         DETALHES DO RELATÓRIO\n" +
               "========================================\n" +
               "ID: " + id + "\n" +
               "Título: " + titulo + "\n" +
               "Autor: " + autor.getNome() + "\n" +
               "Projeto: " + projeto.getTitulo() + "\n" +
               "Enviado em: " + dataEnvio + "\n" +
               "Status: " + statusValidacao + "\n" +
               (feedback != null && !feedback.isEmpty() ? "Feedback: " + feedback + "\n" : "") +
               "----------------------------------------\n" +
               "Conteúdo:\n" + conteudo;
    }
}
