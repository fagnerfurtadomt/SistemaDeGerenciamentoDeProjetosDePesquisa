package telas;

import javax.swing.*;
import java.awt.*;

/**
 * Utilitários de UI para padronizar o estilo das janelas JOptionPane.
 * Demonstra: Encapsulamento, Métodos Estáticos, Pacotes
 */
public class TelasUtil {

    // Constantes de estilo
    public static final String TITULO_SISTEMA = "Sistema de Gerenciamento de Pesquisas Universitárias";
    private static final Color COR_FUNDO = new Color(30, 30, 46);
    private static final Color COR_TEXTO = new Color(205, 214, 244);

    // Método estático utilitário
    public static void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, TITULO_SISTEMA, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(null, "❌ " + mensagem, "Erro — " + TITULO_SISTEMA, JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(null, "✅ " + mensagem, "Sucesso — " + TITULO_SISTEMA, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(null, "⚠️ " + mensagem, "Aviso — " + TITULO_SISTEMA, JOptionPane.WARNING_MESSAGE);
    }

    public static String pedirTexto(String label) {
        String input = JOptionPane.showInputDialog(null, label, TITULO_SISTEMA, JOptionPane.QUESTION_MESSAGE);
        return (input != null) ? input.trim() : null;
    }

    public static String pedirSenha(String label) {
        JPasswordField campo = new JPasswordField(20);
        int ok = JOptionPane.showConfirmDialog(null, new Object[]{label, campo},
                TITULO_SISTEMA, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            return new String(campo.getPassword());
        }
        return null;
    }

    public static boolean confirmar(String mensagem) {
        int resultado = JOptionPane.showConfirmDialog(null, mensagem, TITULO_SISTEMA,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resultado == JOptionPane.YES_OPTION;
    }

    public static int escolherOpcao(String titulo, String[] opcoes) {
        return JOptionPane.showOptionDialog(null, titulo, TITULO_SISTEMA,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, opcoes, opcoes[0]);
    }

    public static String escolherOpcaoCombo(String label, String[] opcoes) {
        return (String) JOptionPane.showInputDialog(null, label, TITULO_SISTEMA,
                JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
    }

    public static int pedirInteiro(String label, int min, int max) {
        while (true) {
            String input = pedirTexto(label + " (entre " + min + " e " + max + ")");
            if (input == null) return -1;
            try {
                int valor = Integer.parseInt(input);
                if (valor >= min && valor <= max) return valor;
                mostrarErro("Valor deve estar entre " + min + " e " + max + ".");
            } catch (NumberFormatException e) {
                mostrarErro("Por favor, insira um número válido.");
            }
        }
    }

    public static void mostrarInfoFormatada(String titulo, String conteudo) {
        JTextArea area = new JTextArea(conteudo);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setRows(20);
        area.setColumns(60);
        JScrollPane scroll = new JScrollPane(area);
        JOptionPane.showMessageDialog(null, scroll, titulo + " — " + TITULO_SISTEMA,
                JOptionPane.PLAIN_MESSAGE);
    }

    // Impede instanciação
    private TelasUtil() {}
}
