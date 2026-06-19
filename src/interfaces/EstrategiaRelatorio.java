package interfaces;


public interface EstrategiaRelatorio {
    String gerarRelatorio(servicos.SistemaRepositorio repositorio);
    String getNomeRelatorio();
}
