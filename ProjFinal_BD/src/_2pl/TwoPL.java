package _2pl;
import java.util.ArrayList;
import java.util.List;

public class TwoPL {
    private final List<Tranca> trancas;

    public TwoPL() {
        this.trancas = new ArrayList<>();
    }

    public synchronized boolean adquirirTrancaLeitura(String recurso, String transacao, String operacao) {
        for (Tranca t : trancas) {
            if (t.getRecurso().equals(recurso) && t.getTipo().equals("w") && !t.getTransacao().equals(transacao)) {
                return false; 
            }
        }
        trancas.add(new Tranca("r", recurso, transacao, operacao));
        System.out.println(transacao + " - Tranca de leitura adquirida: " + listarTrancas(transacao));
        return true;
    }

    public synchronized boolean adquirirTrancaEscrita(String recurso, String transacao, String operacao) {
        for (Tranca t : trancas) {
            if (t.getRecurso().equals(recurso) && !t.getTransacao().equals(transacao)) {
                return false;
            }
        }
        trancas.add(new Tranca("w", recurso, transacao, operacao));
        System.out.println(transacao + " - Tranca de escrita adquirida: " + listarTrancas(transacao));
        return true;
    }

    public synchronized void liberarTranca(String recurso, String transacao, String operacao) {
        trancas.removeIf(t -> t.getRecurso().equals(recurso) && t.getTransacao().equals(transacao));
        System.out.println(transacao + " - Tranca liberada após " + operacao + ":");
    }

    public synchronized boolean verificarTranca(String recurso, String transacao) {
        return trancas.stream().anyMatch(t -> t.getRecurso().equals(recurso) && t.getTransacao().equals(transacao));
    }

    public synchronized String listarTrancas(String transacao) {
        StringBuilder trancasStr = new StringBuilder();
        for (Tranca t : trancas) {
            if (t.getTransacao().equals(transacao)) {
                trancasStr.append(t.getTransacao())
                          .append("-[")
                          .append(t.getTipo())
                          .append("][")
                          .append(t.getRecurso())
                          .append("][")
                          .append(t.getOperacao())
                          .append("]");
            }
        }
        return trancasStr.toString();
    }
}
