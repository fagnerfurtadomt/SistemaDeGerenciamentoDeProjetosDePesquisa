package excecoes;


 //Exceção personalizada para regras de negócio de Projetos

public class ProjetoException extends Exception {

    public ProjetoException(String mensagem) {
        super(mensagem);
    }

    public ProjetoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
