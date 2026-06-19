package excecoes;


 //Exceção personalizada para falhas de autenticação

public class AutenticacaoException extends Exception {

    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
