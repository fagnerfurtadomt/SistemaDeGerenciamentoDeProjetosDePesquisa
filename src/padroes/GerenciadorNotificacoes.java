package padroes;

import modelo.Notificacao;
import modelo.Projeto;
import modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Padrão de Projeto: SINGLETON
 * Garante uma única instância do gerenciador de notificações.
 * Demonstra: Padrões de Projeto, Atributos/Métodos Estáticos
 */
public class GerenciadorNotificacoes {

    // Única instância — Singleton
    private static GerenciadorNotificacoes instancia;

    private List<String> historicoNotificacoes;

    // Construtor privado — impede instanciação externa
    private GerenciadorNotificacoes() {
        this.historicoNotificacoes = new ArrayList<>();
    }

    // Método estático de acesso à instância única
    public static GerenciadorNotificacoes getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorNotificacoes();
        }
        return instancia;
    }

    public void notificarUsuario(Usuario usuario, String mensagem, String tipo) {
        Notificacao notificacao = new Notificacao(mensagem, tipo);
        usuario.receberNotificacao(notificacao);
        historicoNotificacoes.add("[" + usuario.getNome() + "] " + mensagem);
    }

    public void notificarTodos(List<Usuario> usuarios, String mensagem, String tipo) {
        for (Usuario u : usuarios) {
            notificarUsuario(u, mensagem, tipo);
        }
    }

    public void notificarSobreNovoProjeto(List<Usuario> usuarios, Projeto projeto) {
        String mensagem = "Novo projeto disponível: \"" + projeto.getTitulo() +
                "\" | Área: " + projeto.getArea() +
                " | Vagas: " + projeto.getVagasDisponiveis();
        notificarTodos(usuarios, mensagem, Notificacao.TIPO_NOVO_PROJETO);
    }

    public void notificarSobreAtualizacao(List<Usuario> usuarios, Projeto projeto) {
        String mensagem = "Projeto \"" + projeto.getTitulo() + "\" foi atualizado.";
        notificarTodos(usuarios, mensagem, Notificacao.TIPO_ATUALIZACAO);
    }

    public void notificarSobreEncerramento(List<Usuario> participantes, Projeto projeto) {
        String mensagem = "Projeto \"" + projeto.getTitulo() + "\" foi encerrado.";
        notificarTodos(participantes, mensagem, Notificacao.TIPO_ENCERRAMENTO);
    }

    public void notificarValidacaoRelatorio(Usuario aluno, boolean aprovado, String tituloRelatorio) {
        String status = aprovado ? "aprovado" : "reprovado";
        String mensagem = "Seu relatório \"" + tituloRelatorio + "\" foi " + status + " pelo orientador.";
        notificarUsuario(aluno, mensagem, Notificacao.TIPO_VALIDACAO);
    }

    public void notificarInscricao(Usuario professor, String nomeAluno, String tituloProjeto) {
        String mensagem = nomeAluno + " solicitou participação no projeto \"" + tituloProjeto + "\".";
        notificarUsuario(professor, mensagem, Notificacao.TIPO_INSCRICAO);
    }

    public List<String> getHistoricoNotificacoes() {
        return new ArrayList<>(historicoNotificacoes);
    }

    public int getTotalNotificacoesEnviadas() {
        return historicoNotificacoes.size();
    }
}
