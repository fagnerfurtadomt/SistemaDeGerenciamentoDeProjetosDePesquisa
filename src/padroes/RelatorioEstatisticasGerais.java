package padroes;

import interfaces.EstrategiaRelatorio;
import modelo.*;
import modelo.enums.StatusProjeto;
import servicos.SistemaRepositorio;

import java.util.List;

/**
 * Estratégia: Relatório de estatísticas gerais da plataforma.
 * Demonstra: Padrão Strategy, Polimorfismo
 */
public class RelatorioEstatisticasGerais implements EstrategiaRelatorio {

    @Override
    public String gerarRelatorio(SistemaRepositorio repositorio) {
        List<Projeto> projetos = repositorio.listarTodosProjetos();
        List<Usuario> usuarios = repositorio.listarTodosUsuarios();

        long totalAlunos = usuarios.stream().filter(u -> u instanceof Aluno).count();
        long totalProfessores = usuarios.stream().filter(u -> u instanceof Professor).count();
        long totalCoordenadores = usuarios.stream().filter(u -> u instanceof Coordenador).count();
        long projetosAbertos = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.ABERTO).count();
        long projetosAtivos = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.ATIVO).count();
        long projetosEncerrados = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.ENCERRADO).count();
        int totalParticipacoes = projetos.stream().mapToInt(p -> p.getParticipantes().size()).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("      ESTATÍSTICAS GERAIS\n");
        sb.append("========================================\n");
        sb.append("👥 USUÁRIOS\n");
        sb.append("  • Total: ").append(usuarios.size()).append("\n");
        sb.append("  • Alunos: ").append(totalAlunos).append("\n");
        sb.append("  • Professores: ").append(totalProfessores).append("\n");
        sb.append("  • Coordenadores: ").append(totalCoordenadores).append("\n\n");
        sb.append("📁 PROJETOS\n");
        sb.append("  • Total: ").append(projetos.size()).append("\n");
        sb.append("  • Abertos: ").append(projetosAbertos).append("\n");
        sb.append("  • Em andamento: ").append(projetosAtivos).append("\n");
        sb.append("  • Encerrados: ").append(projetosEncerrados).append("\n\n");
        sb.append("📊 PARTICIPAÇÕES\n");
        sb.append("  • Total de participações ativas: ").append(totalParticipacoes).append("\n");
        sb.append("  • Notificações enviadas: ")
          .append(GerenciadorNotificacoes.getInstancia().getTotalNotificacoesEnviadas()).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    @Override
    public String getNomeRelatorio() {
        return "Estatísticas Gerais";
    }
}
