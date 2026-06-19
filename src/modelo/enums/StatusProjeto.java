package modelo.enums;


 // Enum para os status possíveis de um projeto.

public enum StatusProjeto {
    ABERTO("Aberto"),
    ATIVO("Em andamento"),
    ENCERRADO("Encerrado"),
    REMOVIDO("Removido");

    private final String descricao;

    StatusProjeto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
