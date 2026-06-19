package modelo;

import excecoes.ProjetoException;
import interfaces.Relatoravel;
import modelo.enums.StatusProjeto;

import java.util.ArrayList;
import java.util.List;


public class Projeto implements Relatoravel {

    //Constante estatica
    public static final int MAX_PARTICIPANTES_POR_PROJETO = 50;

    //Atributo estatico
    private static int contadorId = 1;

    private final int id;
    private String titulo;
    private String area;
    private String descricao;
    private Professor orientador;
    private String dataInicio;
    private String prazo;
    private int totalVagas;
    private StatusProjeto status;
    private List<Aluno> participantes;
    private List<Relatorio> relatorios;

    //Construtor
    public Projeto(String titulo, String area, String descricao, Professor orientador,
                   String dataInicio, String prazo, int totalVagas) {
        this.id = contadorId++;
        this.titulo = titulo;
        this.area = area;
        this.descricao = descricao;
        this.orientador = orientador;
        this.dataInicio = dataInicio;
        this.prazo = prazo;
        this.totalVagas = totalVagas;
        this.status = StatusProjeto.ABERTO;
        this.participantes = new ArrayList<>();
        this.relatorios = new ArrayList<>();
    }

    //Metodo adcionarParticipante
    public void adicionarParticipante(Aluno aluno) throws ProjetoException {
        if (participantes.size() >= totalVagas) {
            throw new ProjetoException("Projeto sem vagas disponíveis.");
        }
        if (participantes.size() >= MAX_PARTICIPANTES_POR_PROJETO) {
            throw new ProjetoException("Limite máximo de participantes atingido.");
        }
        participantes.add(aluno);
        if (participantes.size() == totalVagas) {
            this.status = StatusProjeto.ATIVO;
        }
    }

    //Metodo removerParticipante
    public void removerParticipante(Aluno aluno) {
        participantes.removeIf(a -> a.getId() == aluno.getId());
        if (status == StatusProjeto.ATIVO && participantes.size() < totalVagas) {
            this.status = StatusProjeto.ABERTO;
        }
    }

    public void adicionarRelatorio(Relatorio relatorio) {
        relatorios.add(relatorio);
    }

    public int getVagasDisponiveis() {
        return totalVagas - participantes.size();
    }

    //Metodo estatico
    public static int getTotalProjetosCriados() {
        return contadorId - 1;
    }

    @Override
    public String gerarRelatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("     RELATÓRIO DO PROJETO\n");
        sb.append("========================================\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Título: ").append(titulo).append("\n");
        sb.append("Área: ").append(area).append("\n");
        sb.append("Orientador: Prof. ").append(orientador.getNome()).append("\n");
        sb.append("Início: ").append(dataInicio).append("\n");
        sb.append("Prazo: ").append(prazo).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Vagas: ").append(participantes.size()).append("/").append(totalVagas).append("\n");
        sb.append("Participantes: ").append(participantes.size()).append("\n");
        sb.append("Relatórios recebidos: ").append(relatorios.size()).append("\n");
        sb.append("Descrição: ").append(descricao).append("\n");
        return sb.toString();
    }

    //Metodo sobrecrito
    @Override
    public String getTipoRelatorio() {
        return "Relatório de Projeto";
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Professor getOrientador() {
        return orientador;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public int getTotalVagas() {
        return totalVagas;
    }
    public void setTotalVagas(int totalVagas) {
        this.totalVagas = totalVagas;
    }

    public StatusProjeto getStatus() {
        return status;
    }

    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public List<Aluno> getParticipantes() {
        return new ArrayList<>(participantes);
    }
    public List<Relatorio> getRelatorios() {
        return new ArrayList<>(relatorios);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | Área: %s | Orientador: Prof. %s | Vagas: %d/%d | Status: %s",
                id, titulo, area, orientador.getNome(), participantes.size(), totalVagas, status);
    }

    public String toDetalhes() {
        return "========================================\n" +
               "         DETALHES DO PROJETO\n" +
               "========================================\n" +
               "Título: " + titulo + "\n" +
               "Área: " + area + "\n" +
               "Orientador: Prof. " + orientador.getNome() + "\n" +
               "Descrição: " + descricao + "\n" +
               "Data de Início: " + dataInicio + "\n" +
               "Prazo: " + prazo + "\n" +
               "Status: " + status + "\n" +
               "Vagas disponíveis: " + getVagasDisponiveis() + "/" + totalVagas + "\n" +
               "Participantes: " + participantes.size();
    }
}
