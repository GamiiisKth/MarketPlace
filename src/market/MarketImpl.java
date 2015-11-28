package market;


import bankrmi.Account;
import bankrmi.Bank;
import bankrmi.RejectedException;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by joshuaPro on 2015-11-25.
 */
public class MarketImpl extends UnicastRemoteObject implements MarketInterface {
    private Map<String,ClientInterface>clientTable=new HashMap<>();
    private ArrayList<Item> itemsList=new ArrayList<>();
    private ArrayList<Item> wishList=new ArrayList<>();
    private static Bank bankInterface;
    private String owner;
    private float price;
    public MarketImpl( ) throws RemoteException, MalformedURLException, NotBoundException {
        super();
    }


    @Override
    public synchronized void registerMarketClient(String name,ClientInterface clientInterface) throws RemoteException, RejectedException {
        if((clientTable.containsKey(name))){
            throw new RejectedException("Rejected: market.marketImpl: " + "blocket"
                    + " Account for: " + name + " already exists:");
        }else if (bankInterface.getAccount(name)!=null){
            throw new RejectedException("Rejected: market.marketImpl: " + "bank"
                    + " Account for: " + name + " already exists:");
        }else {
            clientTable.put(name,clientInterface);
        }


    }

    @Override
    public ClientInterface getRegisteredAcc(String name) throws RemoteException {
        return clientTable.get(name);
    }

    @Override
    public synchronized boolean unregisterMarketClient(String name) throws RemoteException {
        if(!hasAccount(name)){
            return false;
        }
        clientTable.remove(name);
        removeItem(wishList,name);
        removeItem(itemsList,name);
        System.out.println(" Account for " + name
                + " has been deleted");
        return true;
    }
    private boolean hasAccount(String name) {
        return clientTable.get(name) != null;
    }

    @Override
    public synchronized String addItemToSell(String itemName,float price,String owner) throws RemoteException {
        Item item= new Item(itemName,price,owner);
        itemsList.add(item);
        checkWishList(itemName,price,owner);
        return  "your item has been added "+ item.getName();


    }

    @Override
    public synchronized String[] getClientBoughtItems(String clientId) throws RemoteException {
        return new String[0];
    }

    @Override
    public synchronized void wishItemToBuy(String item,float price,String wisher) throws RemoteException {
        if(!checkMarket(wisher,item,price)){
            Item i= new Item(item,price,wisher);
            wishList.add(i);
        }
    }

    @Override
    public void buyItem(String itemName, String buyer) throws RemoteException {
        Account buyerAcc = bankInterface.getAccount(buyer);
        if (buyerAcc != null) {

            for (Item i : itemsList) {
                if (i.getName().equalsIgnoreCase(itemName)) {
                    owner = i.getOwner();
                    price = i.getPrice();
                    break;

                }
            }
            Account sellerAcc = bankInterface.getAccount(owner);
            if (sellerAcc != null) {
                try {
                    buyerAcc.withdraw(price);
                    sellerAcc.deposit(price);

                    clientTable.get(owner).notifySold(itemName);
                    clientTable.get(buyer).reciveMsg("Coungratulation for: "+itemName);
                    removeItem(itemsList, itemName);

                } catch (RejectedException e) {

                   // clientTable.get(buyer).lackMoney();
                    clientTable.get(buyer).reciveMsg(e.toString());
                }
            } else {
                clientTable.get(owner).reciveMsg(" please create a bank account so buyer can pay money");
            }


        } else{
            clientTable.get(buyer).reciveMsg("please create a bank account so buyer can pay money");
        }
    }
    @Override
    public synchronized ArrayList getListOfItemsInMarket() throws RemoteException {
        return itemsList;
    }


    @Override
    public String[] listAccounts() throws RemoteException {
        return clientTable.keySet().toArray(new String[1]);
    }

    private void checkWishList(String item,float price,String owner) throws RemoteException{


        for (Item j : wishList){
            if(item.equalsIgnoreCase(j.getName())){
                if(price <= j.getPrice()){
                    // TODO add ro notifywish nameOfItem and the price
                    clientTable.get(j.getOwner()).notifyWish(item,price);
                    clientTable.get(owner).reciveMsg("Some one is intressted in your advertisement:" +item);
                    wishList.remove(j);
                    break;
                }
            }
        }

    }

    public void removeItem(ArrayList<Item> list,String name){
        for (int i=0; i< list.size(); i++){
            if(list.get(i).getName().equals(name)){
                list.remove(i);
                i--;
            }
        }
    }
    public synchronized boolean checkMarket(String wisher,String itemName,float price) throws RemoteException {
        boolean check=false;

        for (Item i: itemsList){
            if(i.getName().equalsIgnoreCase(itemName)){
                if(i.getPrice() <= price){
                    //TODO notifyWish must be done with itemName and price
                    clientTable.get(wisher).notifyWish(itemName,price);
                    clientTable.get(i.getOwner()).reciveMsg("Some one is intressted in your advertisement: "+itemName);
                    check=true;
                }
            }
        }
        return check;
    }

    public static void main(String [] arg) throws RemoteException, NotBoundException, MalformedURLException {
        try {
            LocateRegistry.getRegistry(9090).list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(9090);
        }
        MarketImpl marketObj= new MarketImpl();
        Naming.rebind("//:9090/Market",marketObj);
        bankInterface=(Bank) Naming.lookup("rmi://localhost:7777/Nordea");
    }
}
