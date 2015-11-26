package market;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Created by joshuaPro on 2015-11-25.
 */
public interface MarketInterface extends Remote {
 void registerMarketClient(String name) throws RemoteException;
 boolean unregisterMarketClient(String name) throws RemoteException;
 void addItemToSell(Item item) throws RemoteException;
 String [] getClientBoughtItems(String clientId) throws RemoteException;
 void wishItemToBuy(String id,Item item) throws RemoteException;
 void buyItem(Item Item,String buyer) throws RemoteException;
 String [] getListOfItemsInMarket() throws RemoteException;
 String[] listAccounts() throws RemoteException;

}