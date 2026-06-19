package telas;

import excecoes.ProjetoException;
import modelo.Aluno;
import modelo.Notificacao;
import modelo.Projeto;
import modelo.Relatorio;
import modelo.enums.StatusProjeto;
import padroes.GerenciadorNotificacoes;
import servicos.ServicoRecomendacao;
import servicos.SistemaRepositorio;

import java.util.List;

/**
 * Interface JOptionPane para o menu do Aluno.
 * Demonstra: Polimorfismo, Interfaces
 */
public class MenuAluno {

    private Aluno aluno;
    private SistemaRepositorio repositorio;
    private ServicoRecomendacao recomendacao;

    public MenuAluno(Aluno aluno, SistemaRepositorio repositorio) {
        this.aluno = aluno;
        this.repositorio = repositorio;
        this.recomendacao = new ServicoRecomendacao(repositorio);
    }

    public void exibir() {
        while (true) {
            String[] opcoes = {
                "1 - Visualizar projetos disponíveis",
                "2 - Inscrever-se em projeto",
                "3 - Cancelar inscrição",
                "4 - Enviar relatório parcial",
                "5 - Ver histórico de projetos",
                "6 - Notificações (" + aluno.getNotificacoesNaoLidas().size() + " não lidas)",
                "7 - Meu perfil",
                "8 - Projetos recomendados",
                "9 - Alterar senha",
                "10 - Sair"
            };

            String escolhida = TelasUtil.escolherOpcaoCombo(
                    "👤 Bem-vindo, " + aluno.getNome() + "!\nEscolha uma opção:", opcoes);

            if (escolhida == null || escolhida.equals("10 - Sair")) break;

            switch (escolhida) {
                case "1 - Visualizar projetos disponíveis" -> visualizarProjetos();
                case "2 - Inscrever-se em projeto"        -> inscreverEmProjeto();
                case "3 - Cancelar inscrição"             -> cancelarInscricao();
                case "4 - Enviar relatório parcial"       -> enviarRelatorio();
                case "5 - Ver histórico de projetos"      -> verHistorico();
                case "9 - Alterar senha"                  -> new telas.TelaSeguranca(repositorio).alterarSenha(aluno);
                default -> {
                    if (escolhida.startsWith("6")) verNotificacoes();
                    else if (escolhida.equals("7 - Meu perfil"))            verPerfil();
                    else if (escolhida.equals("8 - Projetos recomendados")) verRecomendados();
                }
            }
        }
    }

    private void visualizarProjetos() {
        List<Projeto> projetos = repositorio.listarProjetosDisponiveis();
        if (projetos.isEmpty()) {
            TelasUtil.mostrarAviso("Nenhum projeto disponível no momento.");
            return;
        }

        String[] filtros = {"Ver todos", "Filtrar por área", "Filtrar por orientador", "Buscar por termo"};
        int filtro = TelasUtil.escolherOpcao("Como deseja visualizar?", filtros);

        List<Projeto> lista = projetos;

        if (filtro == 1) {
            String area = TelasUtil.pedirTexto("Digite a área de pesquisa:");
            if (area == null || area.isEmpty()) return;
            lista = repositorio.listarProjetosPorArea(area);
            lista.removeIf(p -> p.getStatus() != StatusProjeto.ABERTO);
        } else if (filtro == 2) {
            String orientador = TelasUtil.pedirTexto("Digite o nome do orientador:");
            if (orientador == null || orientador.isEmpty()) return;
            String finalOrientador = orientador.toLowerCase();
            lista = projetos;
            lista = lista.stream()
                    .filter(p -> p.getOrientador().getNome().toLowerCase().contains(finalOrientador))
                    .collect(java.util.stream.Collectors.toList());
        } else if (filtro == 3) {
            String termo = TelasUtil.pedirTexto("Digite o termo de busca:");
            if (termo == null || termo.isEmpty()) return;
            lista = repositorio.buscarProjetosPorTermo(termo);
            lista.removeIf(p -> p.getStatus() != StatusProjeto.ABERTO);
        }

        if (lista.isEmpty()) {
            TelasUtil.mostrarAviso("Nenhum projeto encontrado com o filtro aplicado.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("         PROJETOS DISPONÍVEIS\n");
        sb.append("========================================\n\n");
        int i = 1;
        for (Projeto p : lista) {
            sb.append(i++).append(". ").append(p.getTitulo()).append("\n");
            sb.append("   Orientador: Prof. ").append(p.getOrientador().getNome()).append("\n");
            sb.append("   Área: ").append(p.getArea()).append("\n");
            sb.append("   Vagas disponíveis: ").append(p.getVagasDisponiveis()).append("\n\n");
        }

        TelasUtil.mostrarInfoFormatada("Projetos Disponíveis", sb.toString());

        // Opção de ver detalhes
        if (TelasUtil.confirmar("Deseja ver os detalhes de algum projeto?")) {
            String idStr = TelasUtil.pedirTexto("Digite o número do projeto:");
            if (idStr == null) return;
            try {
                int idx = Integer.parseInt(idStr) - 1;
                if (idx >= 0 && idx < lista.size()) {
                    TelasUtil.mostrarInfoFormatada("Detalhes do Projeto", lista.get(idx).toDetalhes());
                } else {
                    TelasUtil.mostrarErro("Número inválido.");
                }
            } catch (NumberFormatException e) {
                TelasUtil.mostrarErro("Digite apenas números.");
            }
        }
    }

    private void inscreverEmProjeto() {
        List<Projeto> projetos = repositorio.listarProjetosDisponiveis();
        if (projetos.isEmpty()) {
            TelasUtil.mostrarAviso("Nenhum projeto disponível para inscrição.");
            return;
        }

        String[] nomes = projetos.stream()
                .map(p -> p.getId() + " - " + p.getTitulo() + " (Vagas: " + p.getVagasDisponiveis() + ")")
                .toArray(String[]::new);

        String escolhido = TelasUtil.escolherOpcaoCombo("Escolha o projeto para se inscrever:", nomes);
        if (escolhido == null) return;

        Projeto projetoEscolhido = projetos.get(indexDe(nomes, escolhido));
        TelasUtil.mostrarInfoFormatada("Detalhes", projetoEscolhido.toDetalhes());

        if (TelasUtil.confirmar("Deseja confirmar inscrição em \"" + projetoEscolhido.getTitulo() + "\"?")) {
            try {
                aluno.solicitarParticipacao(projetoEscolhido);
                GerenciadorNotificacoes.getInstancia().notificarInscricao(
                        projetoEscolhido.getOrientador(), aluno.getNome(), projetoEscolhido.getTitulo());
                TelasUtil.mostrarSucesso("Inscrição realizada com sucesso no projeto \"" + projetoEscolhido.getTitulo() + "\"!");
            } catch (ProjetoException e) {
                TelasUtil.mostrarErro(e.getMessage());
            }
        }
    }

    private void cancelarInscricao() {
        List<Projeto> ativos = aluno.getProjetosAtivos();
        if (ativos.isEmpty()) {
            TelasUtil.mostrarAviso("Você não está inscrito em nenhum projeto.");
            return;
        }

        String[] nomes = ativos.stream()
                .map(p -> p.getId() + " - " + p.getTitulo())
                .toArray(String[]::new);

        String escolhido = TelasUtil.escolherOpcaoCombo("Escolha o projeto para cancelar inscrição:", nomes);
        if (escolhido == null) return;

        Projeto projeto = ativos.get(indexDe(nomes, escolhido));

        if (TelasUtil.confirmar("Tem certeza que deseja cancelar inscrição em \"" + projeto.getTitulo() + "\"?")) {
            try {
                aluno.desistirParticipacao(projeto);
                TelasUtil.mostrarSucesso("Inscrição cancelada com sucesso.");
            } catch (ProjetoException e) {
                TelasUtil.mostrarErro(e.getMessage());
            }
        }
    }

    private void enviarRelatorio() {
        List<Projeto> ativos = aluno.getProjetosAtivos();
        if (ativos.isEmpty()) {
            TelasUtil.mostrarAviso("Você não está inscrito em nenhum projeto ativo.");
            return;
        }

        String[] nomes = ativos.stream()
                .map(p -> p.getId() + " - " + p.getTitulo())
                .toArray(String[]::new);

        String escolhido = TelasUtil.escolherOpcaoCombo("Escolha o projeto para enviar relatório:", nomes);
        if (escolhido == null) return;

        Projeto projeto = ativos.get(indexDe(nomes, escolhido));

        String titulo = TelasUtil.pedirTexto("Título do relatório:");
        if (titulo == null || titulo.isEmpty()) { TelasUtil.mostrarErro("Título não pode ser vazio."); return; }

        String conteudo = TelasUtil.pedirTexto("Conteúdo do relatório:");
        if (conteudo == null || conteudo.isEmpty()) { TelasUtil.mostrarErro("Conteúdo não pode ser vazio."); return; }

        Relatorio relatorio = new Relatorio(aluno, projeto, titulo, conteudo);
        try {
            aluno.enviarRelatorio(relatorio);
            GerenciadorNotificacoes.getInstancia().notificarUsuario(
                    projeto.getOrientador(),
                    "Novo relatório recebido de " + aluno.getNome() + " no projeto \"" + projeto.getTitulo() + "\".",
                    Notificacao.TIPO_ATUALIZACAO);
            TelasUtil.mostrarSucesso("Relatório \"" + titulo + "\" enviado com sucesso!");
        } catch (ProjetoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void verHistorico() {
        List<Projeto> ativos = aluno.getProjetosAtivos();
        List<Projeto> concluidos = aluno.getHistoricoProjetosConcluidos();

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("        HISTÓRICO DE PROJETOS\n");
        sb.append("========================================\n\n");

        sb.append("📌 PROJETOS ATIVOS (").append(ativos.size()).append(")\n");
        if (ativos.isEmpty()) sb.append("  Nenhum projeto ativo.\n");
        else ativos.forEach(p -> sb.append("  • ").append(p.getTitulo()).append(" | ").append(p.getArea()).append("\n"));

        sb.append("\n✅ PROJETOS CONCLUÍDOS (").append(concluidos.size()).append(")\n");
        if (concluidos.isEmpty()) sb.append("  Nenhum projeto concluído.\n");
        else concluidos.forEach(p -> sb.append("  • ").append(p.getTitulo()).append(" | ").append(p.getArea()).append("\n"));

        sb.append("\n📄 RELATÓRIOS ENVIADOS (").append(aluno.getRelatoriosEnviados().size()).append(")\n");
        if (aluno.getRelatoriosEnviados().isEmpty()) sb.append("  Nenhum relatório enviado.\n");
        else aluno.getRelatoriosEnviados().forEach(r -> sb.append("  • ").append(r.getTitulo())
                .append(" | ").append(r.getDataEnvio())
                .append(" | ").append(r.isValidado() ? (r.isAprovado() ? "✓ Aprovado" : "✗ Reprovado") : "Aguardando").append("\n"));

        TelasUtil.mostrarInfoFormatada("Histórico", sb.toString());
    }

    private void verNotificacoes() {
        List<Notificacao> notificacoes = aluno.getNotificacoes();
        if (notificacoes.isEmpty()) {
            TelasUtil.mostrarAviso("Você não tem notificações.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           NOTIFICAÇÕES\n");
        sb.append("========================================\n\n");
        notificacoes.forEach(n -> sb.append(n).append("\n"));

        TelasUtil.mostrarInfoFormatada("Notificações", sb.toString());

        if (TelasUtil.confirmar("Marcar todas as notificações como lidas?")) {
            aluno.marcarTodasNotificacoesComoLidas();
            TelasUtil.mostrarSucesso("Todas as notificações foram marcadas como lidas.");
        }
    }

    private void verPerfil() {
        String info = "========================================\n" +
                "            MEU PERFIL\n" +
                "========================================\n" +
                "Nome: " + aluno.getNome() + "\n" +
                "E-mail: " + aluno.getEmail() + "\n" +
                "Matrícula: " + aluno.getMatricula() + "\n" +
                "Curso: " + aluno.getCurso() + "\n" +
                "Áreas de interesse: " + (aluno.getAreasInteresse().isEmpty() ? "Não definidas" :
                        String.join(", ", aluno.getAreasInteresse())) + "\n" +
                "Projetos ativos: " + aluno.getProjetosAtivos().size() + "\n" +
                "Projetos concluídos: " + aluno.getHistoricoProjetosConcluidos().size();

        TelasUtil.mostrarInfoFormatada("Meu Perfil", info);

        String[] opcoesPerfil = {"Adicionar área de interesse", "Voltar"};
        int op = TelasUtil.escolherOpcao("O que deseja fazer?", opcoesPerfil);
        if (op == 0) {
            String area = TelasUtil.pedirTexto("Nova área de interesse:");
            if (area != null && !area.isEmpty()) {
                aluno.adicionarAreaInteresse(area);
                TelasUtil.mostrarSucesso("Área \"" + area + "\" adicionada com sucesso!");
            }
        }
    }

    private void verRecomendados() {
        String texto = recomendacao.gerarTextoRecomendacoes(aluno);
        TelasUtil.mostrarInfoFormatada("Projetos Recomendados", texto);
    }

    private int indexDe(String[] array, String valor) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(valor)) return i;
        }
        return 0;
    }
}
