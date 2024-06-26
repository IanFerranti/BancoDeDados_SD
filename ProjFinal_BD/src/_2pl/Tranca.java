package _2pl;

public class Tranca {
    private String tipo; // 'r' para leitura, 'w' para escrita
    private String recurso; // Tabela
    private String transacao; // Thread
    private String operacao; // CRUD

    public Tranca(String tipo, String recurso, String transacao, String operacao) {
        this.tipo = tipo;
        this.recurso = recurso;
        this.transacao = transacao;
        this.operacao = operacao;
    }

    public String getTipo() {
        return tipo;
    }

    public String getRecurso() {
        return recurso;
    }

    public String getTransacao() {
        return transacao;
    }
    public String getOperacao() {
        return operacao;
    }
}
