package telas;

import excecoes.UsuarioException;
import modelo.Aluno;
import modelo.Professor;
import servicos.SistemaRepositorio;

/**
 * Tela de cadastro de novos usuários.
 * Demonstra: Polimorfismo, Herança, Tratamento de Exceções
 */
public class TelaCadastro {

    private SistemaRepositorio repositorio;

    public TelaCadastro(SistemaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void exibir() {
        String[] tiposUsuario = {"Aluno", "Professor", "Cancelar"};
        int tipo = TelasUtil.escolherOpcao("Escolha o tipo de usuário para cadastro:", tiposUsuario);

        if (tipo < 0 || tipo == 2) return;

        String nome = TelasUtil.pedirTexto("Nome completo:");
        if (nome == null || nome.trim().isEmpty()) { TelasUtil.mostrarErro("Nome não pode ser vazio."); return; }

        String email = TelasUtil.pedirTexto("E-mail:");
        if (email == null || email.trim().isEmpty()) { TelasUtil.mostrarErro("E-mail não pode ser vazio."); return; }
        if (!email.contains("@")) { TelasUtil.mostrarErro("E-mail inválido."); return; }

        String senha = TelasUtil.pedirSenha("Senha (mínimo 6 caracteres):");
        if (senha == null || senha.length() < 6) { TelasUtil.mostrarErro("Senha deve ter pelo menos 6 caracteres."); return; }

        String confirmacao = TelasUtil.pedirSenha("Confirme a senha:");
        if (!senha.equals(confirmacao)) { TelasUtil.mostrarErro("As senhas não coincidem."); return; }

        try {
            if (tipo == 0) {
                cadastrarAluno(nome, email, senha);
            } else {
                cadastrarProfessor(nome, email, senha);
            }
        } catch (UsuarioException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void cadastrarAluno(String nome, String email, String senha) throws UsuarioException {
        String matricula = TelasUtil.pedirTexto("Matrícula:");
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new UsuarioException("Matrícula não pode ser vazia.");
        }

        String curso = TelasUtil.pedirTexto("Curso:");
        if (curso == null || curso.trim().isEmpty()) {
            throw new UsuarioException("Curso não pode ser vazio.");
        }

        Aluno aluno = new Aluno(nome, email, senha, matricula, curso);
        repositorio.cadastrarUsuario(aluno);
        TelasUtil.mostrarSucesso("Aluno cadastrado com sucesso!\n" +
                "Nome: " + nome + "\nE-mail: " + email + "\nMatrícula: " + matricula);
    }

    private void cadastrarProfessor(String nome, String email, String senha) throws UsuarioException {
        String departamento = TelasUtil.pedirTexto("Departamento:");
        if (departamento == null || departamento.trim().isEmpty()) {
            throw new UsuarioException("Departamento não pode ser vazio.");
        }

        String[] titulacoes = {"Doutor", "Mestre", "Especialista", "Graduado"};
        String titulacao = TelasUtil.escolherOpcaoCombo("Titulação:", titulacoes);
        if (titulacao == null) return;

        Professor professor = new Professor(nome, email, senha, departamento, titulacao);
        repositorio.cadastrarUsuario(professor);
        TelasUtil.mostrarSucesso("Professor cadastrado com sucesso!\n" +
                "Nome: " + nome + "\nE-mail: " + email + "\nDepartamento: " + departamento);
    }
}
