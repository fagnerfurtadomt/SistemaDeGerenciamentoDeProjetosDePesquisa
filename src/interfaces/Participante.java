package interfaces;

import modelo.Projeto;


public interface Participante {
    boolean solicitarParticipacao(Projeto projeto) throws excecoes.ProjetoException;
    boolean desistirParticipacao(Projeto projeto) throws excecoes.ProjetoException;
    java.util.List<Projeto> getProjetosAtivos();
    java.util.List<Projeto> getHistoricoProjetosConcluidos();
}
