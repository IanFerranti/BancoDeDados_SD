import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IBD_Service extends Remote {
    boolean create(String table, Map<String, String> record) throws RemoteException;
    List<Map<String, String>> read(String table, Map<String, String> condition) throws RemoteException;
    boolean update(String table, Map<String, String> condition, Map<String, String> newRecord) throws RemoteException;
    boolean delete(String table, Map<String, String> condition) throws RemoteException;
}
