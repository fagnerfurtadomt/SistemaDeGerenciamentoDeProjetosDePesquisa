package padroes;

import interfaces.EstrategiaRelatorio;
import modelo.Projeto;
import servicos.SistemaRepositorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Estratégia: Relatório de projetos por área.
 * Demonstra: Padrão Strategy, Polimorfismo
 */
public class RelatorioProjetosPorArea implements EstrategiaRelatorio {

    @Override
    public String gerarRelatorio(SistemaRepositorio repositorio) {
        List<Projeto> projetos = repositorio.listarTodosProjetos();
        Map<String, Integer> contagem = new HashMap<>();

        for (Projeto p : projetos) {
            contagem.merge(p.getArea(), 1, Integer::sum);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("     RELATÓRIO: PROJETOS POR ÁREA\n");
        sb.append("========================================\n");

        if (contagem.isEmpty()) {
            sb.append("Nenhum projeto cadastrado.\n");
        } else {
            contagem.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> sb.append("  • ").append(e.getKey())
                            .append(": ").append(e.getValue())
                            .append(" projeto(s)\n"));
        }
        sb.append("----------------------------------------\n");
        sb.append("Total de projetos: ").append(projetos.size()).append("\n");
        return sb.toString();
    }

    @Override
    public String getNomeRelatorio() {
        return "Projetos por Área";
    }
}
