package modelo;

import excecoes.ProjetoException;
import java.util.ArrayList;
import java.util.List;


public class Professor extends Usuario {

    private String departamento;
    private String titulacao;
    private List<Projeto> projetosCriados;

    public Professor(String nome, String email, String senha, String departamento, String titulacao) {
        super(nome, email, senha);
        this.departamento = departamento;
        this.titulacao = titulacao;
        this.projetosCriados = new ArrayList<>();
    }

    @Override
    public String getTipoUsuario() {
        return "Professor";
    }

    @Override
    public String getMenuOpcoes() {
        return "1 - Criar novo projeto\n" +
               "2 - Editar projeto existente\n" +
               "3 - Encerrar projeto\n" +
               "4 - Visualizar inscritos\n" +
               "5 - Validar relatórios\n" +
               "6 - Enviar notificações\n" +
               "7 - Meus projetos\n" +
               "8 - Notificações\n" +
               "9 - Sair";
    }

    public Projeto criarProjeto(String titulo, String area, String descricao,
                                 String dataInicio, String prazo, int vagas) throws ProjetoException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ProjetoException("Título do projeto não pode ser vazio.");
        }
        if (vagas <= 0) {
            throw new ProjetoException("Número de vagas deve ser maior que zero.");
        }
        Projeto projeto = new Projeto(titulo, area, descricao, this, dataInicio, prazo, vagas);
        projetosCriados.add(projeto);
        return projeto;
    }

    public void editarProjeto(Projeto projeto, String novoTitulo, String novaArea,
                               String novaDescricao, int novasVagas) throws ProjetoException {
        if (!projetosCriados.contains(projeto) && projeto.getOrientador().getId() != this.getId()) {
            throw new ProjetoException("Você não tem permissão para editar este projeto.");
        }
        if (novoTitulo != null && !novoTitulo.trim().isEmpty()) projeto.setTitulo(novoTitulo);
        if (novaArea != null && !novaArea.trim().isEmpty()) projeto.setArea(novaArea);
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) projeto.setDescricao(novaDescricao);
        if (novasVagas > 0) projeto.setTotalVagas(novasVagas);
    }

    public void validarRelatorio(Relatorio relatorio, boolean aprovado, String feedback) throws ProjetoException {
        if (relatorio.getProjeto().getOrientador().getId() != this.getId()) {
            throw new ProjetoException("Você não é o orientador deste projeto.");
        }
        relatorio.setAprovado(aprovado);
        relatorio.setFeedback(feedback);
        relatorio.setValidado(true);
    }

    public List<Projeto> getProjetosCriados() {
        return new ArrayList<>(projetosCriados);
    }

    public void adicionarProjeto(Projeto projeto) {
        projetosCriados.add(projeto);
    }

    // Getters e Setters
    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    public String getTitulacao() {
        return titulacao;
    }
    public void setTitulacao(String titulacao) {
        this.titulacao = titulacao;
    }

    @Override
    public String toString() {
        return String.format("[%d] Prof. %s | %s | %s", getId(), getNome(), titulacao, departamento);
    }
}
