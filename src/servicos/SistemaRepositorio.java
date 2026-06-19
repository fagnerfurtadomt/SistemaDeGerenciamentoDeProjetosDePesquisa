package servicos;

import excecoes.AutenticacaoException;
import excecoes.ProjetoException;
import excecoes.UsuarioException;
import modelo.*;
import modelo.enums.StatusProjeto;
import seguranca.GerenciadorDeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositório central do sistema (armazenamento em memória).
 * Demonstra: Encapsulamento, Atributos Estáticos, Pacotes
 */
public class SistemaRepositorio {

    private List<Usuario> usuarios;
    private List<Projeto> projetos;

    public SistemaRepositorio() {
        this.usuarios = new ArrayList<>();
        this.projetos = new ArrayList<>();
    }

    // ===== USUÁRIOS =====

    public void cadastrarUsuario(Usuario usuario) throws UsuarioException {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                throw new UsuarioException("Já existe um usuário cadastrado com o e-mail: " + usuario.getEmail());
            }
        }
        usuarios.add(usuario);
    }

    /**
     * Autentica o usuário comparando senha digitada com o hash armazenado via SenhaUtil.
     */
    public Usuario autenticar(String email, String senha) throws AutenticacaoException {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.verificarSenha(senha)) {
                if (!u.isAtivo()) {
                    throw new AutenticacaoException("Usuário inativo. Contate o coordenador.");
                }
                return u;
            }
        }
        throw new AutenticacaoException("E-mail ou senha incorretos.");
    }

    /**
     * Altera a senha do usuário após validar a senha atual.
     */
    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha) throws UsuarioException {
        if (!usuario.verificarSenha(senhaAtual)) {
            throw new UsuarioException("Senha atual incorreta.");
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new UsuarioException("Nova senha deve ter pelo menos 6 caracteres.");
        }
        usuario.setSenha(novaSenha);
    }

    /**
     * Gera e retorna um token de recuperação de senha para o e-mail informado.
     */
    public String gerarTokenRecuperacao(String email) throws UsuarioException {
        buscarUsuarioPorEmail(email); // valida que o e-mail existe
        return GerenciadorDeToken.getInstance().gerarToken(email);
    }

    /**
     * Redefine a senha usando o token de recuperação gerado.
     */
    public void redefinirSenha(String email, String token, String novaSenha)
            throws UsuarioException, AutenticacaoException {
        if (!GerenciadorDeToken.getInstance().validarToken(email, token)) {
            throw new AutenticacaoException("Token inválido ou expirado. Solicite um novo.");
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new UsuarioException("Nova senha deve ter pelo menos 6 caracteres.");
        }
        Usuario usuario = buscarUsuarioPorEmail(email);
        usuario.setSenha(novaSenha);
        GerenciadorDeToken.getInstance().removerToken(email);
    }

    public List<Usuario> listarTodosUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Aluno> listarAlunos() {
        return usuarios.stream()
                .filter(u -> u instanceof Aluno)
                .map(u -> (Aluno) u)
                .collect(Collectors.toList());
    }

    public List<Professor> listarProfessores() {
        return usuarios.stream()
                .filter(u -> u instanceof Professor)
                .map(u -> (Professor) u)
                .collect(Collectors.toList());
    }

    public Usuario buscarUsuarioPorId(int id) throws UsuarioException {
        return usuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new UsuarioException("Usuário com ID " + id + " não encontrado."));
    }

    public Usuario buscarUsuarioPorEmail(String email) throws UsuarioException {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new UsuarioException("Nenhum usuário encontrado com o e-mail: " + email));
    }

    public void removerUsuario(int id) throws UsuarioException {
        Usuario usuario = buscarUsuarioPorId(id);
        if (usuario instanceof Coordenador) {
            throw new UsuarioException("Não é possível remover um Coordenador pelo sistema.");
        }
        usuarios.remove(usuario);
    }

    // ===== PROJETOS =====

    public void cadastrarProjeto(Projeto projeto) {
        projetos.add(projeto);
    }

    public List<Projeto> listarTodosProjetos() {
        return new ArrayList<>(projetos);
    }

    public List<Projeto> listarProjetosDisponiveis() {
        return projetos.stream()
                .filter(p -> p.getStatus() == StatusProjeto.ABERTO && p.getVagasDisponiveis() > 0)
                .collect(Collectors.toList());
    }

    public List<Projeto> listarProjetosPorArea(String area) {
        return projetos.stream()
                .filter(p -> p.getArea().equalsIgnoreCase(area) && p.getStatus() != StatusProjeto.REMOVIDO)
                .collect(Collectors.toList());
    }

    public List<Projeto> listarProjetosPorOrientador(Professor professor) {
        return projetos.stream()
                .filter(p -> p.getOrientador().getId() == professor.getId() && p.getStatus() != StatusProjeto.REMOVIDO)
                .collect(Collectors.toList());
    }

    public List<Projeto> listarProjetosPorStatus(StatusProjeto status) {
        return projetos.stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Projeto> buscarProjetosPorTermo(String termo) {
        String termoLower = termo.toLowerCase();
        return projetos.stream()
                .filter(p -> p.getStatus() != StatusProjeto.REMOVIDO &&
                        (p.getTitulo().toLowerCase().contains(termoLower) ||
                         p.getArea().toLowerCase().contains(termoLower) ||
                         p.getDescricao().toLowerCase().contains(termoLower) ||
                         p.getOrientador().getNome().toLowerCase().contains(termoLower)))
                .collect(Collectors.toList());
    }

    public Projeto buscarProjetoPorId(int id) throws ProjetoException {
        return projetos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ProjetoException("Projeto com ID " + id + " não encontrado."));
    }

    public void removerProjeto(int id) throws ProjetoException {
        Projeto projeto = buscarProjetoPorId(id);
        projetos.remove(projeto);
    }

    // ===== INICIALIZAÇÃO DO SISTEMA =====

    public void inicializar() throws Exception {
        // Apenas o coordenador padrão do sistema
        Coordenador coord = new Coordenador("Coordenador", "coordenador@ufc.com", "123456", "Coordenador de Pesquisa");
        cadastrarUsuario(coord);
    }
}
