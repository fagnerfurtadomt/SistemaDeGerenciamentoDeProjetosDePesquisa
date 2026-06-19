package seguranca;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Padrão SINGLETON — gerencia tokens de recuperação de senha.
 * Tokens expiram em 5 minutos após geração.
 * Demonstra: Singleton, Atributos/Métodos Estáticos, Encapsulamento
 */
public class GerenciadorDeToken {

    private static GerenciadorDeToken instance;

    private final Map<String, String> tokens;
    private final Map<String, Long> expiracao;

    // Constante estática — 5 minutos em milissegundos
    private static final long TEMPO_EXPIRACAO = 5 * 60 * 1000;

    private GerenciadorDeToken() {
        this.tokens = new HashMap<>();
        this.expiracao = new HashMap<>();
    }

    public static GerenciadorDeToken getInstance() {
        if (instance == null) {
            instance = new GerenciadorDeToken();
        }
        return instance;
    }

    /**
     * Gera um token numérico de 6 dígitos para o e-mail informado.
     */
    public String gerarToken(String email) {
        String codigo = String.format("%06d", new Random().nextInt(999999));
        tokens.put(email, codigo);
        expiracao.put(email, System.currentTimeMillis() + TEMPO_EXPIRACAO);
        return codigo;
    }

    /**
     * Valida se o token informado é correto e ainda não expirou.
     */
    public boolean validarToken(String email, String codigo) {
        if (!tokens.containsKey(email)) return false;
        if (System.currentTimeMillis() > expiracao.get(email)) {
            tokens.remove(email);
            expiracao.remove(email);
            return false;
        }
        return tokens.get(email).equals(codigo);
    }

    /**
     * Remove o token após uso bem-sucedido.
     */
    public void removerToken(String email) {
        tokens.remove(email);
        expiracao.remove(email);
    }

    /**
     * Verifica se existe token ativo para o e-mail.
     */
    public boolean tokenExiste(String email) {
        if (!tokens.containsKey(email)) return false;
        if (System.currentTimeMillis() > expiracao.get(email)) {
            tokens.remove(email);
            expiracao.remove(email);
            return false;
        }
        return true;
    }

    /**
     * Retorna quantos segundos restam para o token expirar.
     */
    public long segundosRestantes(String email) {
        if (!tokenExiste(email)) return 0;
        long restante = expiracao.get(email) - System.currentTimeMillis();
        return restante / 1000;
    }
}
