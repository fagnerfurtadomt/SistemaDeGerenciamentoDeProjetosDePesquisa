package telas;

import excecoes.ProjetoException;
import excecoes.UsuarioException;
import interfaces.EstrategiaRelatorio;
import modelo.*;
import modelo.enums.StatusProjeto;
import padroes.*;
import servicos.SistemaRepositorio;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface JOptionPane para o menu do Coordenador.
 * Demonstra: Polimorfismo, Herança, Padrão Strategy
 */
public class MenuCoordenador {

    private Coordenador coordenador;
    private SistemaRepositorio repositorio;
    private GeradorRelatorio geradorRelatorio;

    public MenuCoordenador(Coordenador coordenador, SistemaRepositorio repositorio) {
        this.coordenador = coordenador;
        this.repositorio = repositorio;
        this.geradorRelatorio = new GeradorRelatorio(new RelatorioEstatisticasGerais());
    }

    public void exibir() {
        while (true) {
            String[] opcoes = {
                "1 - Gerenciar projetos",
                "2 - Gerenciar usuários",
                "3 - Gerar relatórios",
                "4 - Estatísticas gerais",
                "5 - Notificações (" + coordenador.getNotificacoesNaoLidas().size() + " não lidas)",
                "6 - Alterar senha",
                "7 - Sair"
            };

            String escolhida = TelasUtil.escolherOpcaoCombo(
                    "🛡️ Bem-vindo, Coord. " + coordenador.getNome() + "!\nEscolha uma opção:", opcoes);

            if (escolhida == null || escolhida.equals("7 - Sair")) break;

            switch (escolhida) {
                case "1 - Gerenciar projetos"  -> gerenciarProjetos();
                case "2 - Gerenciar usuários"  -> gerenciarUsuarios();
                case "3 - Gerar relatórios"    -> gerarRelatorios();
                case "4 - Estatísticas gerais" -> estatisticasGerais();
                case "6 - Alterar senha"       -> new telas.TelaSeguranca(repositorio).alterarSenha(coordenador);
                default -> { if (escolhida.startsWith("5")) verNotificacoes(); }
            }
        }
    }

    private void gerenciarProjetos() {
        String[] opcoes = {
            "Listar todos os projetos",
            "Criar projeto",
            "Encerrar projeto",
            "Remover projeto",
            "Buscar projeto",
            "Voltar"
        };

        while (true) {
            int op = TelasUtil.escolherOpcao("📁 GERENCIAR PROJETOS", opcoes);
            if (op < 0 || op == 5) break;

            switch (op) {
                case 0 -> listarTodosProjetos();
                case 1 -> criarProjetoAdmin();
                case 2 -> encerrarProjetoAdmin();
                case 3 -> removerProjetoAdmin();
                case 4 -> buscarProjeto();
            }
        }
    }

    private void listarTodosProjetos() {
        List<Projeto> projetos = repositorio.listarTodosProjetos();
        if (projetos.isEmpty()) {
            TelasUtil.mostrarAviso("Nenhum projeto cadastrado.");
            return;
        }

        String[] filtros = {"Todos", "Apenas abertos", "Apenas ativos", "Apenas encerrados"};
        int f = TelasUtil.escolherOpcao("Filtrar por status:", filtros);

        List<Projeto> lista = projetos;
        if (f == 1) lista = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.ABERTO).collect(Collectors.toList());
        else if (f == 2) lista = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.ATIVO).collect(Collectors.toList());
        else if (f == 3) lista = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.ENCERRADO).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           TODOS OS PROJETOS\n");
        sb.append("========================================\n\n");
        lista.forEach(p -> sb.append(p).append("\n"));
        sb.append("\nTotal: ").append(lista.size()).append(" projeto(s)");

        TelasUtil.mostrarInfoFormatada("Projetos", sb.toString());
    }

    private void criarProjetoAdmin() {
        List<Professor> professores = repositorio.listarProfessores();
        if (professores.isEmpty()) {
            TelasUtil.mostrarErro("Nenhum professor cadastrado para ser orientador.");
            return;
        }

        String[] nomesProfessores = professores.stream()
                .map(p -> p.getId() + " - Prof. " + p.getNome())
                .toArray(String[]::new);

        String profEscolhido = TelasUtil.escolherOpcaoCombo("Escolha o orientador:", nomesProfessores);
        if (profEscolhido == null) return;
        Professor orientador = professores.get(indexDe(nomesProfessores, profEscolhido));

        String titulo = TelasUtil.pedirTexto("Título do projeto:");
        if (titulo == null || titulo.isEmpty()) return;
        String area = TelasUtil.pedirTexto("Área:");
        if (area == null || area.isEmpty()) return;
        String descricao = TelasUtil.pedirTexto("Descrição:");
        if (descricao == null || descricao.isEmpty()) return;
        String inicio = TelasUtil.pedirTexto("Data de início:");
        String prazo = TelasUtil.pedirTexto("Prazo:");
        int vagas = TelasUtil.pedirInteiro("Vagas:", 1, 20);
        if (vagas == -1) return;

        try {
            Projeto novo = orientador.criarProjeto(titulo, area, descricao,
                    inicio != null ? inicio : "A definir",
                    prazo != null ? prazo : "A definir", vagas);
            repositorio.cadastrarProjeto(novo);

            List<Aluno> alunos = repositorio.listarAlunos();
            GerenciadorNotificacoes.getInstancia().notificarSobreNovoProjeto(
                    alunos.stream().map(a -> (Usuario)a).collect(Collectors.toList()), novo);

            TelasUtil.mostrarSucesso("Projeto criado com sucesso! " + alunos.size() + " aluno(s) notificado(s).");
        } catch (ProjetoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void encerrarProjetoAdmin() {
        List<Projeto> projetos = repositorio.listarTodosProjetos().stream()
                .filter(p -> p.getStatus() != StatusProjeto.ENCERRADO && p.getStatus() != StatusProjeto.REMOVIDO)
                .collect(Collectors.toList());

        if (projetos.isEmpty()) { TelasUtil.mostrarAviso("Nenhum projeto ativo para encerrar."); return; }

        Projeto projeto = selecionarProjeto(projetos, "Escolha o projeto para encerrar:");
        if (projeto == null) return;

        if (!TelasUtil.confirmar("Encerrar o projeto \"" + projeto.getTitulo() + "\"?")) return;

        try {
            coordenador.encerrarProjeto(projeto, repositorio.listarAlunos());
            GerenciadorNotificacoes.getInstancia().notificarSobreEncerramento(
                    projeto.getParticipantes().stream().map(a -> (Usuario)a).collect(Collectors.toList()), projeto);
            TelasUtil.mostrarSucesso("Projeto encerrado com sucesso.");
        } catch (ProjetoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void removerProjetoAdmin() {
        List<Projeto> projetos = repositorio.listarTodosProjetos().stream()
                .filter(p -> p.getStatus() != StatusProjeto.REMOVIDO)
                .collect(Collectors.toList());

        if (projetos.isEmpty()) { TelasUtil.mostrarAviso("Nenhum projeto disponível."); return; }

        Projeto projeto = selecionarProjeto(projetos, "Escolha o projeto para remover:");
        if (projeto == null) return;

        if (!TelasUtil.confirmar("⚠️ ATENÇÃO! Remover o projeto \"" + projeto.getTitulo() + "\" permanentemente?")) return;

        try {
            coordenador.removerProjeto(projeto);
            TelasUtil.mostrarSucesso("Projeto marcado como removido.");
        } catch (ProjetoException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void buscarProjeto() {
        String termo = TelasUtil.pedirTexto("Digite o termo de busca:");
        if (termo == null || termo.isEmpty()) return;

        List<Projeto> resultados = repositorio.buscarProjetosPorTermo(termo);
        if (resultados.isEmpty()) {
            TelasUtil.mostrarAviso("Nenhum projeto encontrado para \"" + termo + "\".");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Resultados para \"").append(termo).append("\" (").append(resultados.size()).append("):\n\n");
        resultados.forEach(p -> sb.append(p).append("\n"));
        TelasUtil.mostrarInfoFormatada("Busca de Projetos", sb.toString());
    }

    private void gerenciarUsuarios() {
        String[] opcoes = {
            "Listar todos os usuários",
            "Ativar usuário",
            "Desativar usuário",
            "Remover usuário",
            "Voltar"
        };

        while (true) {
            int op = TelasUtil.escolherOpcao("👥 GERENCIAR USUÁRIOS", opcoes);
            if (op < 0 || op == 4) break;

            switch (op) {
                case 0 -> listarUsuarios();
                case 1 -> ativarUsuario();
                case 2 -> desativarUsuario();
                case 3 -> removerUsuario();
            }
        }
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = repositorio.listarTodosUsuarios();
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           TODOS OS USUÁRIOS\n");
        sb.append("========================================\n\n");

        sb.append("👨‍🎓 ALUNOS\n");
        usuarios.stream().filter(u -> u instanceof Aluno).forEach(u ->
                sb.append("  ").append(u).append(u.isAtivo() ? "" : " [INATIVO]").append("\n"));

        sb.append("\n👨‍🏫 PROFESSORES\n");
        usuarios.stream().filter(u -> u instanceof Professor).forEach(u ->
                sb.append("  ").append(u).append(u.isAtivo() ? "" : " [INATIVO]").append("\n"));

        sb.append("\n🛡️ COORDENADORES\n");
        usuarios.stream().filter(u -> u instanceof Coordenador).forEach(u ->
                sb.append("  ").append(u).append("\n"));

        sb.append("\nTotal: ").append(usuarios.size()).append(" usuário(s)");
        TelasUtil.mostrarInfoFormatada("Usuários", sb.toString());
    }

    private void ativarUsuario() {
        List<Usuario> inativos = repositorio.listarTodosUsuarios().stream()
                .filter(u -> !u.isAtivo()).collect(Collectors.toList());
        if (inativos.isEmpty()) { TelasUtil.mostrarAviso("Nenhum usuário inativo."); return; }

        String[] nomes = inativos.stream().map(u -> u.getId() + " - " + u.getNome() + " (" + u.getTipoUsuario() + ")")
                .toArray(String[]::new);
        String escolhido = TelasUtil.escolherOpcaoCombo("Escolha o usuário para ativar:", nomes);
        if (escolhido == null) return;

        try {
            coordenador.ativarUsuario(inativos.get(indexDe(nomes, escolhido)));
            TelasUtil.mostrarSucesso("Usuário ativado com sucesso.");
        } catch (UsuarioException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void desativarUsuario() {
        List<Usuario> ativos = repositorio.listarTodosUsuarios().stream()
                .filter(u -> u.isAtivo() && !(u instanceof Coordenador)).collect(Collectors.toList());
        if (ativos.isEmpty()) { TelasUtil.mostrarAviso("Nenhum usuário disponível para desativar."); return; }

        String[] nomes = ativos.stream().map(u -> u.getId() + " - " + u.getNome() + " (" + u.getTipoUsuario() + ")")
                .toArray(String[]::new);
        String escolhido = TelasUtil.escolherOpcaoCombo("Escolha o usuário para desativar:", nomes);
        if (escolhido == null) return;

        if (!TelasUtil.confirmar("Desativar usuário " + escolhido + "?")) return;

        try {
            coordenador.desativarUsuario(ativos.get(indexDe(nomes, escolhido)));
            TelasUtil.mostrarSucesso("Usuário desativado.");
        } catch (UsuarioException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void removerUsuario() {
        List<Usuario> usuarios = repositorio.listarTodosUsuarios().stream()
                .filter(u -> !(u instanceof Coordenador)).collect(Collectors.toList());
        if (usuarios.isEmpty()) { TelasUtil.mostrarAviso("Nenhum usuário removível."); return; }

        String[] nomes = usuarios.stream().map(u -> u.getId() + " - " + u.getNome() + " (" + u.getTipoUsuario() + ")")
                .toArray(String[]::new);
        String escolhido = TelasUtil.escolherOpcaoCombo("Escolha o usuário para REMOVER:", nomes);
        if (escolhido == null) return;

        if (!TelasUtil.confirmar("⚠️ ATENÇÃO! Remover permanentemente " + escolhido + "?")) return;

        try {
            repositorio.removerUsuario(usuarios.get(indexDe(nomes, escolhido)).getId());
            TelasUtil.mostrarSucesso("Usuário removido.");
        } catch (UsuarioException e) {
            TelasUtil.mostrarErro(e.getMessage());
        }
    }

    private void gerarRelatorios() {
        String[] estrategias = {
            "Estatísticas gerais",
            "Projetos mais ativos",
            "Projetos por área"
        };

        int op = TelasUtil.escolherOpcao("📊 Escolha o tipo de relatório:", estrategias);
        if (op < 0) return;

        EstrategiaRelatorio estrategia = switch (op) {
            case 0 -> new RelatorioEstatisticasGerais();
            case 1 -> new RelatorioProjetosAtivos();
            case 2 -> new RelatorioProjetosPorArea();
            default -> new RelatorioEstatisticasGerais();
        };

        geradorRelatorio.setEstrategia(estrategia);
        String resultado = geradorRelatorio.executar(repositorio);
        TelasUtil.mostrarInfoFormatada(geradorRelatorio.getNomeEstrategia(), resultado);
    }

    private void estatisticasGerais() {
        geradorRelatorio.setEstrategia(new RelatorioEstatisticasGerais());
        String resultado = geradorRelatorio.executar(repositorio);
        TelasUtil.mostrarInfoFormatada("Estatísticas Gerais", resultado);
    }

    private void verNotificacoes() {
        List<Notificacao> notificacoes = coordenador.getNotificacoes();
        if (notificacoes.isEmpty()) { TelasUtil.mostrarAviso("Sem notificações."); return; }

        StringBuilder sb = new StringBuilder();
        notificacoes.forEach(n -> sb.append(n).append("\n"));
        TelasUtil.mostrarInfoFormatada("Notificações", sb.toString());

        if (TelasUtil.confirmar("Marcar todas como lidas?")) {
            coordenador.marcarTodasNotificacoesComoLidas();
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
