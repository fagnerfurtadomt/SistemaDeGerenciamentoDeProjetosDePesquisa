package telas;

import excecoes.AutenticacaoException;
import excecoes.UsuarioException;
import modelo.Usuario;
import seguranca.GerenciadorDeToken;
import servicos.SistemaRepositorio;

/**
 * Tela de recuperação e alteração de senha.
 * Integra GerenciadorDeToken (Singleton) e SenhaUtil (SHA-256).
 * Demonstra: Tratamento de Exceções, Padrão Singleton, Segurança
 */
public class TelaSeguranca {

    private SistemaRepositorio repositorio;

    public TelaSeguranca(SistemaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Fluxo completo de recuperação de senha via token.
     */
    public void recuperarSenha() {
        String email = TelasUtil.pedirTexto("Digite seu e-mail cadastrado:");
        if (email == null || email.trim().isEmpty()) return;

        try {
            String token = repositorio.gerarTokenRecuperacao(email);

            // Em um sistema real o token seria enviado por e-mail.
            // Aqui exibimos na tela para fins acadêmicos/demonstração.
            TelasUtil.mostrarMensagem(
                "Token de recuperação gerado!\n\n" +
                "Seu token (válido por 5 minutos):\n\n" +
                "         [ " + token + " ]\n\n" +
                "Guarde este código e use na próxima tela."
            );

            // Solicita o token digitado pelo usuário
            String tokenDigitado = TelasUtil.pedirTexto("Digite o token de 6 dígitos:");
            if (tokenDigitado == null || tokenDigitado.trim().isEmpty()) return;

            GerenciadorDeToken gdt = GerenciadorDeToken.getInstance();

            if (!gdt.validarToken(email, tokenDigitado.trim())) {
                long segundos = gdt.segundosRestantes(email);
                if (segundos == 0) {
                    TelasUtil.mostrarErro("Token expirado. Solicite um novo.");
                } else {
                    TelasUtil.mostrarErro("Token inválido. Verifique e tente novamente.");
                }
                return;
            }

            // Token válido — solicita nova senha
            String novaSenha = TelasUtil.pedirSenha("Digite a nova senha (mínimo 6 caracteres):");
            if (novaSenha == null || novaSenha.length() < 6) {
                TelasUtil.mostrarErro("Senha deve ter pelo menos 6 caracteres.");
                return;
            }

            String confirmacao = TelasUtil.pedirSenha("Confirme a nova senha:");
            if (!novaSenha.equals(confirmacao)) {
                TelasUtil.mostrarErro("As senhas não coincidem.");
                return;
            }

            repositorio.redefinirSenha(email, tokenDigitado.trim(), novaSenha);
            TelasUtil.mostrarSucesso("Senha redefinida com sucesso!\nFaça login com a nova senha.");

        } catch (UsuarioException e) {
            TelasUtil.mostrarErro(e.getMessage());
        } catch (AutenticacaoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    /**
     * Permite ao usuário logado alterar sua própria senha.
     */
    public void alterarSenha(Usuario usuarioLogado) {
        String senhaAtual = TelasUtil.pedirSenha("Digite sua senha atual:");
        if (senhaAtual == null) return;

        String novaSenha = TelasUtil.pedirSenha("Digite a nova senha (mínimo 6 caracteres):");
        if (novaSenha == null || novaSenha.length() < 6) {
            TelasUtil.mostrarErro("Nova senha deve ter pelo menos 6 caracteres.");
            return;
        }

        String confirmacao = TelasUtil.pedirSenha("Confirme a nova senha:");
        if (!novaSenha.equals(confirmacao)) {
            TelasUtil.mostrarErro("As senhas não coincidem.");
            return;
        }

        try {
            repositorio.alterarSenha(usuarioLogado, senhaAtual, novaSenha);
            TelasUtil.mostrarSucesso("Senha alterada com sucesso!");
        } catch (UsuarioException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }
}
