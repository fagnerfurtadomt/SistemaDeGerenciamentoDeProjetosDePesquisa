package seguranca;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitário para criptografia de senhas usando SHA-256.
 * Demonstra: Métodos Estáticos, Encapsulamento, Pacotes
 */
public class SenhaUtil {

    // Impede instanciação — classe utilitária
    private SenhaUtil() {}

    /**
     * Criptografa a senha usando SHA-256 e retorna o hash em hexadecimal.
     */
    public static String criptografarSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes());
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha: algoritmo SHA-256 não disponível.", e);
        }
    }

    /**
     * Compara a senha digitada com o hash armazenado.
     */
    public static boolean verificarSenha(String senhaDigitada, String senhaCriptografada) {
        return criptografarSenha(senhaDigitada).equals(senhaCriptografada);
    }
}
