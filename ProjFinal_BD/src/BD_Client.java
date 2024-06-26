import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BD_Client {

    private static ThreadLocal<String> threadName = new ThreadLocal<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao Banco de Dados!");

        int choice;
        do {
            System.out.println("\nEscolha uma opcao:");
            System.out.println("1 - Executar operacoes com threads");
            System.out.println("2 - Executar operacoes CRUD pela entrada do usuario");
            System.out.println("0 - Sair\n");

            choice = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer do scanner

            switch (choice) {
                case 1:
                    runThreads();
                    break;
                case 2:
                    runUsuario(scanner);
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opcao invalida. Tente novamente.");
                    break;
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void runThreads() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IBD_Service service = (IBD_Service) registry.lookup("BancoDeDados");

            String table = getTabelaOperacao();

            // Thread para Create
            Thread t1 = new Thread(() -> {
                try {
                    threadName.set("Thread-0");
                    Map<String, String> record1 = new HashMap<>();
                    record1.put("id", "1");
                    record1.put("name", "Ian");
                    System.out.println(threadName.get() + " - Criando registro: " + record1);
                    service.create(table, record1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Thread-0");

            // Thread para Read
            Thread t2 = new Thread(() -> {
                try {
                    threadName.set("Thread-1");
                    Map<String, String> condition = new HashMap<>();
                    condition.put("id", "1");
                    System.out.println(threadName.get() + " - Lendo registros com condicao: " + condition);
                    List<Map<String, String>> results = service.read(table, condition);
                    for (Map<String, String> result : results) {
                        System.out.println(threadName.get() + " - Resultado: " + result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Thread-1");

            // Thread para Update
            Thread t3 = new Thread(() -> {
                try {
                    threadName.set("Thread-2");
                    Map<String, String> condition = new HashMap<>();
                    condition.put("id", "1");
                    Map<String, String> newRecord = new HashMap<>();
                    newRecord.put("name", "Robinho");
                    System.out.println(threadName.get() + " - Atualizando registros com condicao: " + condition + " para: " + newRecord);
                    service.update(table, condition, newRecord);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Thread-2");

            // Thread para Delete
            Thread t4 = new Thread(() -> {
                try {
                    threadName.set("Thread-3");
                    Map<String, String> condition = new HashMap<>();
                    condition.put("id", "2");
                    System.out.println(threadName.get() + " - Excluindo registros com condicao: " + condition);
                    service.delete(table, condition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Thread-3");

            // Thread com multiplas acoes
            Thread t5 = new Thread(() -> {
                try {
                    threadName.set("Thread-4");
                    Map<String, String> record2 = new HashMap<>();
                    record2.put("id", "2");
                    record2.put("name", "Silvana");
                    System.out.println(threadName.get() + " - Criando registro: " + record2);
                    service.create(table, record2);

                    Map<String, String> condition = new HashMap<>();
                    condition.put("id", "2");
                    System.out.println(threadName.get() + " - Lendo registros com condicao: " + condition);
                    List<Map<String, String>> results = service.read(table, condition);
                    for (Map<String, String> result : results) {
                        System.out.println(threadName.get() + " - Resultado: " + result);
                    }

                    Map<String, String> newRecord = new HashMap<>();
                    newRecord.put("name", "Emilio");
                    System.out.println(threadName.get() + " - Atualizando registros com condicao: " + condition + " para: " + newRecord);
                    service.update(table, condition, newRecord);

                    System.out.println(threadName.get() + " - Excluindo registros com condicao: " + condition);
                    service.delete(table, condition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Thread-4");

            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();

            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runUsuario(Scanner scanner) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IBD_Service service = (IBD_Service) registry.lookup("BancoDeDados");

            String table = getTabelaOperacao();
            int operation;
            do {
                System.out.println("\nEscolha uma operacao:");
                System.out.println("1 - Criar registro");
                System.out.println("2 - Ler registro especifico");
                System.out.println("3 - Ler todos os registros");
                System.out.println("4 - Atualizar registro");
                System.out.println("5 - Excluir registro");
                System.out.println("0 - Voltar\n");

                operation = scanner.nextInt();
                scanner.nextLine();

                switch (operation) {
                    case 1:
                        // Create
                        System.out.println("Digite o ID do registro:");
                        String idCreate = scanner.nextLine();
                        System.out.println("Digite o nome do registro:");
                        String nameCreate = scanner.nextLine();

                        Map<String, String> recordCreate = new HashMap<>();
                        recordCreate.put("id", idCreate);
                        recordCreate.put("name", nameCreate);

                        System.out.println("Criando registro: " + recordCreate);
                        service.create(table, recordCreate);
                        break;
                    case 2:
                        // Read
                        System.out.println("Digite o ID para ler:");
                        String idRead = scanner.nextLine();

                        Map<String, String> conditionRead = new HashMap<>();
                        conditionRead.put("id", idRead);

                        System.out.println("Lendo registros com condicao: " + conditionRead);
                        List<Map<String, String>> resultsRead = service.read(table, conditionRead);
                        for (Map<String, String> result : resultsRead) {
                            System.out.println("Resultado: " + result);
                        }
                        break;
                    case 3:
                        // ReadAll
                        System.out.println("Listando todos os registros da tabela: " + table);
                        List<Map<String, String>> allRecords = service.read(table, null);
                        for (Map<String, String> record : allRecords) {
                            System.out.println(record);
                        }
                        break;
                    case 4:
                        // Update
                        System.out.println("Digite o ID do registro para atualizar:");
                        String idUpdate = scanner.nextLine();
                        System.out.println("Digite o novo nome:");
                        String newName = scanner.nextLine();

                        Map<String, String> conditionUpdate = new HashMap<>();
                        conditionUpdate.put("id", idUpdate);

                        Map<String, String> newRecordUpdate = new HashMap<>();
                        newRecordUpdate.put("name", newName);

                        System.out.println("Atualizando registros com condicao: " + conditionUpdate + " para: " + newRecordUpdate);
                        service.update(table, conditionUpdate, newRecordUpdate);
                        break;
                    case 5:
                        // Delete
                        System.out.println("Digite o ID do registro para excluir:");
                        String idDelete = scanner.nextLine();

                        Map<String, String> conditionDelete = new HashMap<>();
                        conditionDelete.put("id", idDelete);

                        System.out.println("Excluindo registros com condi??o: " + conditionDelete);
                        service.delete(table, conditionDelete);
                        break;
                    case 0:
                        System.out.println("Voltando ao menu principal...");
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                        break;
                }
            } while (operation != 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTabelaOperacao() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nDigite o nome da tabela com a qual voce deseja trabalhar:");
        String table = scanner.nextLine();
        return table;
    }
}
