package excecoes;

//Exceção personalizada para regras de negócio de Usuários.

public class UsuarioException extends Exception {

    public UsuarioException(String mensagem) {
        super(mensagem);
    }

    public UsuarioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
