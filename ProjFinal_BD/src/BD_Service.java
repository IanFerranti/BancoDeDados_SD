
import _2pl.TwoPL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BD_Service extends UnicastRemoteObject implements IBD_Service {

    private Map<String, List<Map<String, String>>> database;
    private TwoPL twoPL;

    protected BD_Service() throws RemoteException {
        super();
        this.database = new HashMap<>();
        this.twoPL = new TwoPL();
    }

    @Override
    public boolean create(String table, Map<String, String> record) throws RemoteException {
        String transaction = Thread.currentThread().getName();
        System.out.println(transaction + " - Na fila a tranca de escrita para a tabela: [" + table + "][CREATE]");
        if (twoPL.adquirirTrancaEscrita(table, transaction, "CREATE")) {
            try {
                database.putIfAbsent(table, new ArrayList<>());
                boolean result = database.get(table).add(record);
                System.out.println(transaction + " - Operação de CREATE executada: " + record);
                return result;
            } finally {
                twoPL.liberarTranca(table, transaction, "CREATE");
            }
        }
        System.out.println(transaction + " - Falha ao adquirir tranca de escrita para a tabela: [" + table + "][CREATE]");
        return false;
    }

    @Override
    public List<Map<String, String>> read(String table, Map<String, String> condition) throws RemoteException {
        String transaction = Thread.currentThread().getName();
        System.out.println(transaction + " - Na fila a tranca de leitura para a tabela: [" + table + "][READ]");
        if (twoPL.adquirirTrancaLeitura(table, transaction, "READ")) {
            try {
                List<Map<String, String>> records = database.getOrDefault(table, Collections.emptyList());
                List<Map<String, String>> result = new ArrayList<>();
                for (Map<String, String> record : records) {
                    if (condition == null || matchesCondition(record, condition)) {
                        result.add(record);
                    }
                }
                System.out.println(transaction + " - Operação de READ executada com condição: " + condition);
                return result;
            } finally {
                twoPL.liberarTranca(table, transaction, "READ");
            }
        }
        System.out.println(transaction + " - Falha ao adquirir tranca de leitura para a tabela: [" + table + "][READ]");
        return Collections.emptyList();
    }

    @Override
    public boolean update(String table, Map<String, String> condition, Map<String, String> newRecord) throws RemoteException {
        String transaction = Thread.currentThread().getName();
        System.out.println(transaction + " - Na fila a tranca de escrita para a tabela: [" + table + "][UPDATE]");
        if (twoPL.adquirirTrancaEscrita(table, transaction, "UPDATE")) {
            try {
                List<Map<String, String>> records = database.get(table);
                if (records == null) {
                    return false;
                }
                boolean updated = false;
                for (Map<String, String> record : records) {
                    if (matchesCondition(record, condition)) {
                        record.putAll(newRecord);
                        updated = true;
                    }
                }
                System.out.println(transaction + " - Operação de UPDATE executada com condição: " + condition + " para: " + newRecord);
                return updated;
            } finally {
                twoPL.liberarTranca(table, transaction, "UPDATE");
            }
        }
        System.out.println(transaction + " - Falha ao adquirir tranca de escrita para a tabela: [" + table + "][UPDATE]");
        return false;
    }

    @Override
    public boolean delete(String table, Map<String, String> condition) throws RemoteException {
        String transaction = Thread.currentThread().getName();
        System.out.println(transaction + " - Na fila a tranca de escrita para a tabela: [" + table + "][DELETE]");
        if (twoPL.adquirirTrancaEscrita(table, transaction, "DELETE")) {
            try {
                List<Map<String, String>> records = database.get(table);
                if (records == null) {
                    return false;
                }
                boolean result = records.removeIf(record -> matchesCondition(record, condition));
                System.out.println(transaction + " - Operação de DELETE executada com condição: " + condition);
                return result;
            } finally {
                twoPL.liberarTranca(table, transaction, "DELETE");
            }
        }
        System.out.println(transaction + " - Falha ao adquirir tranca de escrita para a tabela: [" + table + "][DELETE]");
        return false;
    }

    private boolean matchesCondition(Map<String, String> record, Map<String, String> condition) {
        return condition.entrySet().stream().allMatch(entry -> entry.getValue().equals(record.get(entry.getKey())));
    }
}
