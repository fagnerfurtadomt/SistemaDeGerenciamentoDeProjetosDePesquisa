package padroes;

import interfaces.EstrategiaRelatorio;
import modelo.Projeto;
import modelo.enums.StatusProjeto;
import servicos.SistemaRepositorio;

import java.util.Comparator;
import java.util.List;

/**
 * Estratégia: Relatório dos projetos mais ativos.
 * Demonstra: Padrão Strategy, Polimorfismo
 */
public class RelatorioProjetosAtivos implements EstrategiaRelatorio {

    @Override
    public String gerarRelatorio(SistemaRepositorio repositorio) {
        List<Projeto> projetos = repositorio.listarTodosProjetos();

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("   RELATÓRIO: PROJETOS MAIS ATIVOS\n");
        sb.append("========================================\n");

        if (projetos.isEmpty()) {
            sb.append("Nenhum projeto cadastrado.\n");
        } else {
            projetos.stream()
                    .filter(p -> p.getStatus() != StatusProjeto.REMOVIDO)
                    .sorted(Comparator.comparingInt(p -> -p.getParticipantes().size()))
                    .limit(10)
                    .forEach(p -> sb.append(String.format("  [%d] %s\n      Participantes: %d/%d | Status: %s | Área: %s\n",
                            p.getId(), p.getTitulo(),
                            p.getParticipantes().size(), p.getTotalVagas(),
                            p.getStatus(), p.getArea())));
        }
        sb.append("----------------------------------------\n");
        sb.append("Total exibido: ").append(Math.min(projetos.size(), 10)).append("\n");
        return sb.toString();
    }

    @Override
    public String getNomeRelatorio() {
        return "Projetos Mais Ativos";
    }
}
