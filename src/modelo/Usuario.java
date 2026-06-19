package modelo;

import seguranca.SenhaUtil;
import java.util.ArrayList;
import java.util.List;


public abstract class Usuario {

    // Atributo estático para controle de IDs únicos
    private static int contadorId = 1;

    private final int id;
    private String nome;
    private String email;
    private String senhaHash; // senha armazenada como hash SHA-256
    private boolean ativo;
    private List<Notificacao> notificacoes;

    public Usuario(String nome, String email, String senha) {
        this.id = contadorId++;
        this.nome = nome;
        this.email = email;
        this.senhaHash = SenhaUtil.criptografarSenha(senha); // criptografa na criacao da senha
        this.ativo = true;
        this.notificacoes = new ArrayList<>();
    }

    //Metodo abstrato — polimorfismo
    public abstract String getTipoUsuario();

    //Metodo abstrato pois  cada tipo de usuário tem menu diferente
    public abstract String getMenuOpcoes();

    //Metodo estatico pois pertence a classe
    public static int getTotalUsuariosCriados() {
        return contadorId - 1;
    }

    public void receberNotificacao(Notificacao notificacao) {
        this.notificacoes.add(notificacao);
    }

    public List<Notificacao> getNotificacoes() {
        return new ArrayList<>(notificacoes);
    }

    public List<Notificacao> getNotificacoesNaoLidas() {
        List<Notificacao> naoLidas = new ArrayList<>();
        for (Notificacao n : notificacoes) {
            if (!n.isLida()) {
                naoLidas.add(n);
            }
        }
        return naoLidas;
    }

    public void marcarTodasNotificacoesComoLidas() {
        for (Notificacao n : notificacoes) {
            n.marcarComoLida();
        }
    }

    //Verifica se a senha informada corresponde ao hash armazenado.
    public boolean verificarSenha(String senhaDigitada) {
        return SenhaUtil.verificarSenha(senhaDigitada, this.senhaHash);
    }

    // Atualiza a senha  recebe senha de forma pura e armazena como hash.
    public void setSenha(String novaSenha) {
        this.senhaHash = SenhaUtil.criptografarSenha(novaSenha);
    }

    //Retorna o hash da senha e nunca retornca a senha em forma pura
    public String getSenhaHash() {
        return senhaHash;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s", id, nome, email, getTipoUsuario());
    }
}
