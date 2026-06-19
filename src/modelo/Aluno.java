package modelo;

import excecoes.ProjetoException;
import interfaces.Participante;
import modelo.enums.StatusProjeto;

import java.util.ArrayList;
import java.util.List;


public class Aluno extends Usuario implements Participante {

    private String matricula;
    private String curso;
    private List<String> areasInteresse;
    private List<Projeto> projetosAtivos;
    private List<Projeto> historicoProjetosConcluidos;
    private List<Relatorio> relatoriosEnviados;

    public Aluno(String nome, String email, String senha, String matricula, String curso) {
        super(nome, email, senha);
        this.matricula = matricula;
        this.curso = curso;
        this.areasInteresse = new ArrayList<>();
        this.projetosAtivos = new ArrayList<>();
        this.historicoProjetosConcluidos = new ArrayList<>();
        this.relatoriosEnviados = new ArrayList<>();
    }

    @Override
    public String getTipoUsuario() {
        return "Aluno";
    }

    @Override
    public String getMenuOpcoes() {
        return "1 - Visualizar projetos disponíveis\n" +
               "2 - Inscrever-se em projeto\n" +
               "3 - Cancelar inscrição\n" +
               "4 - Enviar relatório parcial\n" +
               "5 - Ver histórico de projetos\n" +
               "6 - Notificações\n" +
               "7 - Meu perfil\n" +
               "8 - Projetos recomendados\n" +
               "9 - Sair";
    }

    @Override
    public boolean solicitarParticipacao(Projeto projeto) throws ProjetoException {
        if (!isAtivo()) {
            throw new ProjetoException("Usuário inativo não pode participar de projetos.");
        }
        if (projeto.getStatus() != StatusProjeto.ABERTO) {
            throw new ProjetoException("Projeto '" + projeto.getTitulo() + "' não está aberto para inscrições.");
        }
        if (projeto.getVagasDisponiveis() <= 0) {
            throw new ProjetoException("Projeto '" + projeto.getTitulo() + "' não possui vagas disponíveis.");
        }
        for (Projeto p : projetosAtivos) {
            if (p.getId() == projeto.getId()) {
                throw new ProjetoException("Você já está inscrito neste projeto.");
            }
        }
        projeto.adicionarParticipante(this);
        projetosAtivos.add(projeto);
        return true;
    }

    @Override
    public boolean desistirParticipacao(Projeto projeto) throws ProjetoException {
        boolean removido = projetosAtivos.removeIf(p -> p.getId() == projeto.getId());
        if (!removido) {
            throw new ProjetoException("Você não está inscrito neste projeto.");
        }
        projeto.removerParticipante(this);
        return true;
    }

    @Override
    public List<Projeto> getProjetosAtivos() {
        return new ArrayList<>(projetosAtivos);
    }

    @Override
    public List<Projeto> getHistoricoProjetosConcluidos() {
        return new ArrayList<>(historicoProjetosConcluidos);
    }

    public void concluirProjeto(Projeto projeto) {
        boolean removido = projetosAtivos.removeIf(p -> p.getId() == projeto.getId());
        if (removido) {
            historicoProjetosConcluidos.add(projeto);
        }
    }

    public void enviarRelatorio(Relatorio relatorio) throws ProjetoException {
        boolean participa = false;
        for (Projeto p : projetosAtivos) {
            if (p.getId() == relatorio.getProjeto().getId()) {
                participa = true;
                break;
            }
        }
        if (!participa) {
            throw new ProjetoException("Você não está inscrito neste projeto.");
        }
        relatoriosEnviados.add(relatorio);
        relatorio.getProjeto().adicionarRelatorio(relatorio);
    }

    public void adicionarAreaInteresse(String area) {
        if (!areasInteresse.contains(area)) {
            areasInteresse.add(area);
        }
    }

    // Getters e Setters
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    public String getCurso() {
        return curso;
    }
    public void setCurso(String curso) {
        this.curso = curso;
    }
    public List<String> getAreasInteresse() {
        return new ArrayList<>(areasInteresse);
    }
    public List<Relatorio> getRelatoriosEnviados() {
        return new ArrayList<>(relatoriosEnviados);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | Matrícula: %s | Curso: %s", getId(), getNome(), matricula, curso);
    }
}
