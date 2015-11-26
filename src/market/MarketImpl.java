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

/**
 * Created by joshuaPro on 2015-11-25.
 */
public class MarketImpl extends UnicastRemoteObject implements MarketInterface {
    private Map<String,ClientInterface>clientTable=new HashMap<>();
    private ArrayList<Item> itemsList=new ArrayList<>();
    private ArrayList<Item> wishList=new ArrayList<>();
    private Bank bankInterface;

    public MarketImpl( ) throws RemoteException, MalformedURLException, NotBoundException {
        super();
        try {
            LocateRegistry.getRegistry(7777).list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(7777);
        }
        bankInterface=(Bank) Naming.lookup("rmi://localhost:7777/Nordea");
    }


    @Override
    public synchronized void registerMarketClient(String name) throws RemoteException {
        ClientImpl account= (ClientImpl)clientTable.get(name);
        if(account!=null){
            System.out.print("Account ["+name+"] exists");
            try {
                throw new RejectedException(  " Account for: " + name + " already exists: " + account);
            } catch (RejectedException e) {
                e.printStackTrace();
            }
            account = new ClientImpl(name);
            clientTable.put(name,account);
            System.out.println(  " Account: " + account
                    + " has been created for " + name);
        }

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
    public synchronized void addItemToSell(Item item) throws RemoteException {
        itemsList.add(item);
        System.out.println("your item has been added"+ item.getName());

        checkWishList(item);
    }

    @Override
    public synchronized String[] getClientBoughtItems(String clientId) throws RemoteException {
        return new String[0];
    }

    @Override
    public synchronized void wishItemToBuy(String wisher, Item item) throws RemoteException {
        if(!checkMarket(wisher,item)){
            wishList.add(item);
        }
    }

    @Override
    public void buyItem(Item item, String buyer) throws RemoteException {
        Account buyerAcc= bankInterface.getAccount(buyer);
        if(buyerAcc !=null) {

            Account sellerAcc = bankInterface.getAccount(item.getOwner());
            if (sellerAcc !=null) {
                try {
                    buyerAcc.withdraw(item.getPrice());
                    sellerAcc.deposit(item.getPrice());

                    clientTable.get(item.getOwner()).notifySold(item);
                    removeItem(itemsList, item.getName());

                } catch (RejectedException e) {
                    e.printStackTrace();

                    clientTable.get(buyer).lackMoney();
                }
            }
            System.out.println(item.getOwner()+" please create a bank account so buyer can pay money");
        }
        System.out.println(buyer+" please create a bank account and deposit it");

    }

    @Override
    public synchronized String[] getListOfItemsInMarket() throws RemoteException {
        int listLenght=itemsList.size();
        String [] a= new String[listLenght];
        for (int i=0; i< listLenght;i++){
            a[i]=   itemsList.get(i).toString();
        }
        return a;
    }

    @Override
    public String[] listAccounts() throws RemoteException {
        return clientTable.keySet().toArray(new String[1]);
    }

    private void checkWishList(Item item) throws RemoteException{
        int i= wishList.size();
        System.out.println(i);
        for (int j=0;  j< i; j++){
            if(item.getName().equalsIgnoreCase(wishList.get(j).getName())){
                if(item.getPrice() <= wishList.get(j).getPrice()){
                    wishList.get(i).getOwnerInterface().notifyWish(item);
                    wishList.remove(j);
                    --j;
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
    public synchronized boolean checkMarket(String wisher,Item item) throws RemoteException {
        boolean check=false;
        System.out.println("size of list");
        for (int i=0; i< itemsList.size(); i++){
            if(itemsList.get(i).getName().equals(item.getName())){
                if(itemsList.get(i).getPrice() <= item.getPrice()){
                    clientTable.get(wisher).notifyWish(item);
                    check=true;
                }
            }
        }
        return check;
    }


}
