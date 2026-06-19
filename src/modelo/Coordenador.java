package modelo;

import excecoes.ProjetoException;
import excecoes.UsuarioException;
import modelo.enums.StatusProjeto;

import java.util.List;

public class Coordenador extends Usuario {

    private String cargo;

    public Coordenador(String nome, String email, String senha, String cargo) {
        super(nome, email, senha);
        this.cargo = cargo;
    }

    @Override
    public String getTipoUsuario() {
        return "Coordenador";
    }

    @Override
    public String getMenuOpcoes() {
        return "1 - Gerenciar projetos\n" +
               "2 - Gerenciar usuários\n" +
               "3 - Gerar relatórios\n" +
               "4 - Estatísticas gerais\n" +
               "5 - Notificações\n" +
               "6 - Sair";
    }

    public void ativarUsuario(Usuario usuario) throws UsuarioException {
        if (usuario.isAtivo()) {
            throw new UsuarioException("Usuário já está ativo.");
        }
        usuario.setAtivo(true);
    }

    public void desativarUsuario(Usuario usuario) throws UsuarioException {
        if (!usuario.isAtivo()) {
            throw new UsuarioException("Usuário já está inativo.");
        }
        if (usuario instanceof Coordenador && usuario.getId() == this.getId()) {
            throw new UsuarioException("Coordenador não pode desativar a si mesmo.");
        }
        usuario.setAtivo(false);
    }

    public void encerrarProjeto(Projeto projeto, List<Aluno> alunos) throws ProjetoException {
        if (projeto.getStatus() == StatusProjeto.ENCERRADO) {
            throw new ProjetoException("Projeto já está encerrado.");
        }
        projeto.setStatus(StatusProjeto.ENCERRADO);
        // Mover alunos para histórico
        for (Aluno aluno : projeto.getParticipantes()) {
            aluno.concluirProjeto(projeto);
        }
    }

    public void removerProjeto(Projeto projeto) throws ProjetoException {
        if (projeto.getStatus() == StatusProjeto.ATIVO && !projeto.getParticipantes().isEmpty()) {
            throw new ProjetoException("Não é possível remover um projeto ativo com participantes. Encerre-o primeiro.");
        }
        projeto.setStatus(StatusProjeto.REMOVIDO);
    }

    // Getters e Setters
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public String toString() {
        return String.format("[%d] Coord. %s | %s", getId(), getNome(), cargo);
    }
}
