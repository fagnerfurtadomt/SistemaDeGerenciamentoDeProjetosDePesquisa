package telas;

import excecoes.ProjetoException;
import modelo.*;
import modelo.enums.StatusProjeto;
import padroes.GerenciadorNotificacoes;
import servicos.SistemaRepositorio;

import java.util.List;

/**
 * Interface JOptionPane para o menu do Professor.
 * Demonstra: Polimorfismo, Herança
 */
public class MenuProfessor {

    private Professor professor;
    private SistemaRepositorio repositorio;

    public MenuProfessor(Professor professor, SistemaRepositorio repositorio) {
        this.professor = professor;
        this.repositorio = repositorio;
    }

    public void exibir() {
        while (true) {
            String[] opcoes = {
                "1 - Criar novo projeto",
                "2 - Editar projeto existente",
                "3 - Encerrar projeto",
                "4 - Visualizar inscritos",
                "5 - Validar relatórios",
                "6 - Enviar notificação",
                "7 - Meus projetos",
                "8 - Notificações (" + professor.getNotificacoesNaoLidas().size() + " não lidas)",
                "9 - Alterar senha",
                "10 - Sair"
            };

            String escolhida = TelasUtil.escolherOpcaoCombo(
                    "🎓 Bem-vindo, Prof. " + professor.getNome() + "!\nEscolha uma opção:", opcoes);

            if (escolhida == null || escolhida.equals("10 - Sair")) break;

            switch (escolhida) {
                case "1 - Criar novo projeto"       -> criarProjeto();
                case "2 - Editar projeto existente" -> editarProjeto();
                case "3 - Encerrar projeto"         -> encerrarProjeto();
                case "4 - Visualizar inscritos"     -> visualizarInscritos();
                case "5 - Validar relatórios"       -> validarRelatorios();
                case "6 - Enviar notificação"       -> enviarNotificacao();
                case "7 - Meus projetos"            -> meusProjetos();
                case "9 - Alterar senha"            -> new telas.TelaSeguranca(repositorio).alterarSenha(professor);
                default -> { if (escolhida.startsWith("8")) verNotificacoes(); }
            }
        }
    }

    private void criarProjeto() {
        String titulo = TelasUtil.pedirTexto("Título do projeto:");
        if (titulo == null || titulo.isEmpty()) { TelasUtil.mostrarErro("Título não pode ser vazio."); return; }

        String area = TelasUtil.pedirTexto("Área de pesquisa:");
        if (area == null || area.isEmpty()) { TelasUtil.mostrarErro("Área não pode ser vazia."); return; }

        String descricao = TelasUtil.pedirTexto("Descrição do projeto:");
        if (descricao == null || descricao.isEmpty()) { TelasUtil.mostrarErro("Descrição não pode ser vazia."); return; }

        String dataInicio = TelasUtil.pedirTexto("Data de início (DD/MM/AAAA):");
        if (dataInicio == null || dataInicio.isEmpty()) dataInicio = "A definir";

        String prazo = TelasUtil.pedirTexto("Prazo (DD/MM/AAAA):");
        if (prazo == null || prazo.isEmpty()) prazo = "A definir";

        int vagas = TelasUtil.pedirInteiro("Número de vagas:", 1, Projeto.MAX_PARTICIPANTES_POR_PROJETO);
        if (vagas == -1) return;

        try {
            Projeto novo = professor.criarProjeto(titulo, area, descricao, dataInicio, prazo, vagas);
            repositorio.cadastrarProjeto(novo);

            // Notificar todos os alunos sobre novo projeto
            List<Aluno> alunos = repositorio.listarAlunos();
            GerenciadorNotificacoes.getInstancia().notificarSobreNovoProjeto(
                    java.util.Collections.unmodifiableList(alunos.stream()
                            .map(a -> (Usuario)a).collect(java.util.stream.Collectors.toList())), novo);

            TelasUtil.mostrarSucesso("Projeto \"" + titulo + "\" criado com sucesso!\n" +
                    alunos.size() + " aluno(s) notificado(s).");
        } catch (ProjetoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void editarProjeto() {
        List<Projeto> meusProjetos = repositorio.listarProjetosPorOrientador(professor);
        if (meusProjetos.isEmpty()) {
            TelasUtil.mostrarAviso("Você não possui projetos para editar.");
            return;
        }

        Projeto projeto = selecionarProjeto(meusProjetos, "Escolha o projeto para editar:");
        if (projeto == null) return;

        if (projeto.getStatus() == StatusProjeto.ENCERRADO || projeto.getStatus() == StatusProjeto.REMOVIDO) {
            TelasUtil.mostrarErro("Não é possível editar um projeto encerrado ou removido.");
            return;
        }

        String novoTitulo = TelasUtil.pedirTexto("Novo título (deixe vazio para manter \"" + projeto.getTitulo() + "\"):");
        String novaArea = TelasUtil.pedirTexto("Nova área (deixe vazio para manter \"" + projeto.getArea() + "\"):");
        String novaDescricao = TelasUtil.pedirTexto("Nova descrição (deixe vazio para manter atual):");
        String vagasStr = TelasUtil.pedirTexto("Novas vagas (0 para manter " + projeto.getTotalVagas() + "):");
        int novasVagas = 0;
        try {
            if (vagasStr != null && !vagasStr.isEmpty()) novasVagas = Integer.parseInt(vagasStr);
        } catch (NumberFormatException ignored) {}

        try {
            professor.editarProjeto(projeto,
                    (novoTitulo != null && !novoTitulo.isEmpty()) ? novoTitulo : null,
                    (novaArea != null && !novaArea.isEmpty()) ? novaArea : null,
                    (novaDescricao != null && !novaDescricao.isEmpty()) ? novaDescricao : null,
                    novasVagas);

            // Notificar participantes
            List<Aluno> participantes = projeto.getParticipantes();
            GerenciadorNotificacoes.getInstancia().notificarSobreAtualizacao(
                    participantes.stream().map(a -> (Usuario) a).collect(java.util.stream.Collectors.toList()), projeto);

            TelasUtil.mostrarSucesso("Projeto atualizado com sucesso! " + participantes.size() + " participante(s) notificado(s).");
        } catch (ProjetoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void encerrarProjeto() {
        List<Projeto> meusProjetos = repositorio.listarProjetosPorOrientador(professor);
        meusProjetos.removeIf(p -> p.getStatus() == StatusProjeto.ENCERRADO || p.getStatus() == StatusProjeto.REMOVIDO);
        if (meusProjetos.isEmpty()) {
            TelasUtil.mostrarAviso("Nenhum projeto ativo para encerrar.");
            return;
        }

        Projeto projeto = selecionarProjeto(meusProjetos, "Escolha o projeto para encerrar:");
        if (projeto == null) return;

        if (!TelasUtil.confirmar("Tem certeza que deseja encerrar o projeto \"" + projeto.getTitulo() + "\"?\n" +
                "Isso moverá " + projeto.getParticipantes().size() + " aluno(s) para o histórico.")) return;

        List<Aluno> participantes = projeto.getParticipantes();
        projeto.setStatus(StatusProjeto.ENCERRADO);
        for (Aluno aluno : participantes) {
            aluno.concluirProjeto(projeto);
        }

        GerenciadorNotificacoes.getInstancia().notificarSobreEncerramento(
                participantes.stream().map(a -> (Usuario) a).collect(java.util.stream.Collectors.toList()), projeto);

        TelasUtil.mostrarSucesso("Projeto \"" + projeto.getTitulo() + "\" encerrado com sucesso.\n" +
                participantes.size() + " aluno(s) notificado(s).");
    }

    private void visualizarInscritos() {
        List<Projeto> meusProjetos = repositorio.listarProjetosPorOrientador(professor);
        if (meusProjetos.isEmpty()) {
            TelasUtil.mostrarAviso("Você não possui projetos.");
            return;
        }

        Projeto projeto = selecionarProjeto(meusProjetos, "Escolha o projeto:");
        if (projeto == null) return;

        List<Aluno> participantes = projeto.getParticipantes();
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("   INSCRITOS: ").append(projeto.getTitulo()).append("\n");
        sb.append("========================================\n");
        sb.append("Vagas: ").append(participantes.size()).append("/").append(projeto.getTotalVagas()).append("\n\n");

        if (participantes.isEmpty()) {
            sb.append("Nenhum aluno inscrito.\n");
        } else {
            int i = 1;
            for (Aluno a : participantes) {
                sb.append(i++).append(". ").append(a.getNome()).append("\n");
                sb.append("   Matrícula: ").append(a.getMatricula())
                  .append(" | Curso: ").append(a.getCurso()).append("\n");
            }
        }

        TelasUtil.mostrarInfoFormatada("Inscritos no Projeto", sb.toString());
    }

    private void validarRelatorios() {
        List<Projeto> meusProjetos = repositorio.listarProjetosPorOrientador(professor);
        if (meusProjetos.isEmpty()) {
            TelasUtil.mostrarAviso("Você não possui projetos.");
            return;
        }

        Projeto projeto = selecionarProjeto(meusProjetos, "Escolha o projeto:");
        if (projeto == null) return;

        List<Relatorio> relatorios = projeto.getRelatorios();
        List<Relatorio> pendentes = relatorios.stream()
                .filter(r -> !r.isValidado())
                .collect(java.util.stream.Collectors.toList());

        if (pendentes.isEmpty()) {
            TelasUtil.mostrarAviso("Nenhum relatório pendente de validação neste projeto.");
            return;
        }

        String[] nomes = pendentes.stream()
                .map(r -> r.getId() + " - " + r.getTitulo() + " | " + r.getAutor().getNome())
                .toArray(String[]::new);

        String escolhido = TelasUtil.escolherOpcaoCombo("Escolha o relatório para validar:", nomes);
        if (escolhido == null) return;

        Relatorio rel = pendentes.get(indexDe(nomes, escolhido));
        TelasUtil.mostrarInfoFormatada("Relatório", rel.toDetalhes());

        boolean aprovado = TelasUtil.confirmar("Deseja APROVAR este relatório?\n(Não = Reprovar)");
        String feedback = TelasUtil.pedirTexto("Feedback para o aluno:");

        try {
            professor.validarRelatorio(rel, aprovado, feedback != null ? feedback : "");
            GerenciadorNotificacoes.getInstancia().notificarValidacaoRelatorio(rel.getAutor(), aprovado, rel.getTitulo());
            TelasUtil.mostrarSucesso("Relatório " + (aprovado ? "aprovado" : "reprovado") + " com sucesso!\nAluno notificado.");
        } catch (ProjetoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void enviarNotificacao() {
        List<Projeto> meusProjetos = repositorio.listarProjetosPorOrientador(professor);
        if (meusProjetos.isEmpty()) {
            TelasUtil.mostrarAviso("Você não possui projetos.");
            return;
        }

        String[] opcoes = {"Enviar para todos os participantes de um projeto", "Enviar para todos os alunos"};
        int op = TelasUtil.escolherOpcao("Para quem enviar?", opcoes);

        String mensagem = TelasUtil.pedirTexto("Digite a mensagem da notificação:");
        if (mensagem == null || mensagem.isEmpty()) { TelasUtil.mostrarErro("Mensagem não pode ser vazia."); return; }

        if (op == 0) {
            Projeto projeto = selecionarProjeto(meusProjetos, "Escolha o projeto:");
            if (projeto == null) return;
            List<Aluno> participantes = projeto.getParticipantes();
            GerenciadorNotificacoes.getInstancia().notificarTodos(
                    participantes.stream().map(a -> (Usuario)a).collect(java.util.stream.Collectors.toList()),
                    "📢 Prof. " + professor.getNome() + ": " + mensagem, Notificacao.TIPO_ATUALIZACAO);
            TelasUtil.mostrarSucesso(participantes.size() + " participante(s) notificado(s).");
        } else {
            List<Aluno> alunos = repositorio.listarAlunos();
            GerenciadorNotificacoes.getInstancia().notificarTodos(
                    alunos.stream().map(a -> (Usuario)a).collect(java.util.stream.Collectors.toList()),
                    "📢 Prof. " + professor.getNome() + ": " + mensagem, Notificacao.TIPO_ATUALIZACAO);
            TelasUtil.mostrarSucesso(alunos.size() + " aluno(s) notificado(s).");
        }
    }

    private void meusProjetos() {
        List<Projeto> meusProjetos = repositorio.listarProjetosPorOrientador(professor);
        if (meusProjetos.isEmpty()) {
            TelasUtil.mostrarAviso("Você não possui projetos cadastrados.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("          MEUS PROJETOS\n");
        sb.append("========================================\n\n");
        meusProjetos.forEach(p -> sb.append(p).append("\n"));

        TelasUtil.mostrarInfoFormatada("Meus Projetos", sb.toString());
    }

    private void verNotificacoes() {
        List<Notificacao> notificacoes = professor.getNotificacoes();
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

        if (TelasUtil.confirmar("Marcar todas como lidas?")) {
            professor.marcarTodasNotificacoesComoLidas();
            TelasUtil.mostrarSucesso("Todas marcadas como lidas.");
        }
    }

    private Projeto selecionarProjeto(List<Projeto> projetos, String label) {
        String[] nomes = projetos.stream()
                .map(p -> p.getId() + " - " + p.getTitulo() + " [" + p.getStatus() + "]")
                .toArray(String[]::new);
        String escolhido = TelasUtil.escolherOpcaoCombo(label, nomes);
        if (escolhido == null) return null;
        return projetos.get(indexDe(nomes, escolhido));
    }

    private int indexDe(String[] array, String valor) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(valor)) return i;
        }
        return 0;
    }
}
