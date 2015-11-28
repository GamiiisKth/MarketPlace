package market;

import bankrmi.RejectedException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


/**
 * Created by joshuaPro on 2015-11-25.
 */
public interface MarketInterface extends Remote {
 void registerMarketClient(String name,ClientInterface clientInterface) throws RemoteException, RejectedException;
 ClientInterface getRegisteredAcc(String name)throws RemoteException;
 boolean unregisterMarketClient(String name) throws RemoteException;
 String addItemToSell(String itemName,float price,String owner) throws RemoteException;
 String [] getClientBoughtItems(String clientId) throws RemoteException;
 void wishItemToBuy(String itemName,float price,String wisher) throws RemoteException;
 void buyItem(String item,String buyer) throws RemoteException;
 ArrayList<Item> getListOfItemsInMarket() throws RemoteException;
 String[] listAccounts() throws RemoteException;

}
