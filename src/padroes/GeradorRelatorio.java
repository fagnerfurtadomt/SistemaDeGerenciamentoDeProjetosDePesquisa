package padroes;

import interfaces.EstrategiaRelatorio;
import servicos.SistemaRepositorio;

//Usa o padrão Strategy para geração de relatórios.

public class GeradorRelatorio {

    private EstrategiaRelatorio estrategia;

    public GeradorRelatorio(EstrategiaRelatorio estrategia) {

        this.estrategia = estrategia;
    }

    public void setEstrategia(EstrategiaRelatorio estrategia) {
        this.estrategia = estrategia;
    }

    public String executar(SistemaRepositorio repositorio) {
        return estrategia.gerarRelatorio(repositorio);
    }

    public String getNomeEstrategia() {
        return estrategia.getNomeRelatorio();
    }
}
