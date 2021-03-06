package market;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by joshuaPro on 2015-11-25.
 */
public interface ClientInterface extends Remote {
  String getID(String id) throws RemoteException;
  void reciveMsg( String msg)throws RemoteException;
  void notifySold(String msg) throws RemoteException;
  void lackMoney() throws RemoteException;
  void notifyWish(String itemName,float price) throws RemoteException;

}
