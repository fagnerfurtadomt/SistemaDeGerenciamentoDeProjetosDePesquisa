package servicos;

import modelo.Aluno;
import modelo.Projeto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço de Recomendação de Projetos baseado em histórico e interesses do aluno.
 * Demonstra: Extra — Sistema de Recomendação, Encapsulamento, Pacotes
 */
public class ServicoRecomendacao {

    private SistemaRepositorio repositorio;

    public ServicoRecomendacao(SistemaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Recomenda projetos com base nas áreas de interesse e histórico do aluno.
     */
    public List<Projeto> recomendar(Aluno aluno, int limite) {
        List<Projeto> disponíveis = repositorio.listarProjetosDisponiveis();
        List<String> interesses = aluno.getAreasInteresse();
        List<Projeto> historico = aluno.getHistoricoProjetosConcluidos();
        Set<Integer> idsParticipando = aluno.getProjetosAtivos().stream()
                .map(Projeto::getId).collect(Collectors.toSet());

        // Pontuação para cada projeto
        Map<Projeto, Integer> pontuacao = new HashMap<>();

        for (Projeto p : disponíveis) {
            if (idsParticipando.contains(p.getId())) continue;

            int pontos = 0;

            // +3 pontos por área de interesse direta
            for (String interesse : interesses) {
                if (p.getArea().toLowerCase().contains(interesse.toLowerCase()) ||
                    interesse.toLowerCase().contains(p.getArea().toLowerCase())) {
                    pontos += 3;
                }
            }

            // +2 pontos se o aluno já participou de projeto na mesma área
            for (Projeto hist : historico) {
                if (hist.getArea().equalsIgnoreCase(p.getArea())) {
                    pontos += 2;
                }
            }

            // +1 ponto por vagas disponíveis (projetos com mais vagas têm leve prioridade)
            pontos += Math.min(p.getVagasDisponiveis(), 3);

            pontuacao.put(p, pontos);
        }

        return pontuacao.entrySet().stream()
                .sorted(Map.Entry.<Projeto, Integer>comparingByValue().reversed())
                .limit(limite)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public String gerarTextoRecomendacoes(Aluno aluno) {
        List<Projeto> recomendados = recomendar(aluno, 5);

        if (recomendados.isEmpty()) {
            return "Nenhum projeto recomendado no momento.\n" +
                   "Dica: adicione áreas de interesse ao seu perfil para receber recomendações!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("     PROJETOS RECOMENDADOS PARA VOCÊ\n");
        sb.append("========================================\n");
        sb.append("Baseado nas suas áreas de interesse: ")
          .append(aluno.getAreasInteresse().isEmpty() ? "não definidas" : String.join(", ", aluno.getAreasInteresse()))
          .append("\n\n");

        int i = 1;
        for (Projeto p : recomendados) {
            sb.append(i++).append(". ").append(p.getTitulo()).append("\n");
            sb.append("   Área: ").append(p.getArea()).append("\n");
            sb.append("   Orientador: Prof. ").append(p.getOrientador().getNome()).append("\n");
            sb.append("   Vagas disponíveis: ").append(p.getVagasDisponiveis()).append("\n\n");
        }
        return sb.toString();
    }
}
