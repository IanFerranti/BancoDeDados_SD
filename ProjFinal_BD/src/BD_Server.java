import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BD_Server {
    public static void main(String[] args) {
        try {
            IBD_Service service = new BD_Service();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("BancoDeDados", service);
            System.out.println("Servidor de banco de dados pronto.\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
