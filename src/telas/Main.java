package telas;

import excecoes.AutenticacaoException;
import modelo.Aluno;
import modelo.Coordenador;
import modelo.Professor;
import modelo.Usuario;
import servicos.SistemaRepositorio;

import javax.swing.*;

/**
 * Classe principal do sistema — ponto de entrada.
 * Demonstra: Integração de todos os conceitos de POO
 */
public class Main {

    private static SistemaRepositorio repositorio = new SistemaRepositorio();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        try {
            repositorio.inicializar();
        } catch (Exception e) {
            TelasUtil.mostrarErro("Erro ao inicializar o sistema: " + e.getMessage());
        }

        exibirMenuPrincipal();
    }

    private static void exibirMenuPrincipal() {
        while (true) {
            String[] opcoes = {"Login", "Cadastro", "Esqueci minha senha", "Sair"};
            int escolha = TelasUtil.escolherOpcao(
                    "========================================\n" +
                    "  SISTEMA DE GERENCIAMENTO DE PESQUISAS\n" +
                    "         UNIVERSITÁRIAS\n" +
                    "========================================\n" +
                    "Selecione uma opção:", opcoes);

            if (escolha < 0 || escolha == 3) {
                if (TelasUtil.confirmar("Deseja realmente sair do sistema?")) {
                    TelasUtil.mostrarMensagem("Obrigado por utilizar o Sistema de Pesquisas Universitárias!\nEncerrando sessão...");
                    System.exit(0);
                }
            } else if (escolha == 0) {
                efetuarLogin();
            } else if (escolha == 1) {
                new TelaCadastro(repositorio).exibir();
            } else if (escolha == 2) {
                new TelaSeguranca(repositorio).recuperarSenha();
            }
        }
    }

    private static void efetuarLogin() {
        String email = TelasUtil.pedirTexto("E-mail:");
        if (email == null || email.isEmpty()) return;

        String senha = TelasUtil.pedirSenha("Senha:");
        if (senha == null) return;

        try {
            Usuario usuario = repositorio.autenticar(email, senha);
            TelasUtil.mostrarSucesso("Login realizado com sucesso!\nBem-vindo(a), " + usuario.getNome() + "!");
            redirecionarMenu(usuario);
        } catch (AutenticacaoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    /**
     * Demonstra POLIMORFISMO ao verificar o tipo real do usuário em tempo de execução.
     */
    private static void redirecionarMenu(Usuario usuario) {
        if (usuario instanceof Aluno aluno) {
            new MenuAluno(aluno, repositorio).exibir();
        } else if (usuario instanceof Professor professor) {
            new MenuProfessor(professor, repositorio).exibir();
        } else if (usuario instanceof Coordenador coordenador) {
            new MenuCoordenador(coordenador, repositorio).exibir();
        } else {
            TelasUtil.mostrarErro("Tipo de usuário desconhecido.");
        }
    }

    public static SistemaRepositorio getRepositorio() {
        return repositorio;
    }
}
