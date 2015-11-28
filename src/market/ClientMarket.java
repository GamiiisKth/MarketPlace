package market;


import bankrmi.Account;
import bankrmi.Bank;
import bankrmi.RejectedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.Format;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by joshuaPro on 2015-11-25.
 */
public class ClientMarket extends UnicastRemoteObject   implements  ClientInterface{

    Bank bankobjc;
    MarketInterface marketObj;
    private String clientName;
    private String itemName=null;
    private float price=0;
    Account account;
    private String owner=null;

    static enum CommandName1 {
        registerAccount, unRegisterAccount, wish, buy, listOfIem, help, quit, newItem,list
        ,balance,deposit;
    };

    public ClientMarket() throws RemoteException, MalformedURLException, NotBoundException {
        try {

            marketObj = (MarketInterface) Naming.lookup("rmi://localhost:9090/Market");
            bankobjc=(Bank)Naming.lookup("rmi://localhost:7777/Nordea");
        } catch (Exception e) {
            System.out.println("The runtime failed: " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Connected to market: " + "blocket" );
    }

    // denna metoden anropas i main metoden
    public void run() {

        BufferedReader consolIn = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(clientName + "@" + "Blocket" + ">");
            try {

                String userInput = consolIn.readLine();
                execute(parser(userInput));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Command2 parser(String userInput) {
        if (userInput == null) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(userInput);
        if (tokenizer.countTokens() == 0) {
            return null;
        }
        CommandName1 commandName1 = null;
        String userName=null;


        int userInputTOkenNo = 1;
        while (tokenizer.hasMoreTokens()) {
            switch (userInputTOkenNo) {
                case 1:
                    try {
                        String commandoString = tokenizer.nextToken();

                        commandName1 = CommandName1.valueOf(CommandName1.class, commandoString);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Illegal command");
                        return null;
                    }
                    break;
                case 2:
                    userName = tokenizer.nextToken();
                    break;
                case 3:
                    try {
                        price = Float.parseFloat(tokenizer.nextToken());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                case 4:
                    itemName = tokenizer.nextToken();
                    break;
                default:
                    System.out.println("Illegal command1");
                    return null;
            }
            userInputTOkenNo++;
        }


        return new Command2(commandName1, userName);
    }

    void execute(Command2 command2) throws RemoteException {
        if (command2 == null) {
            return;
        }
        switch (command2.getCommandName()) {
            case list:
                try {
                    for (String a: marketObj.listAccounts()){
                        System.out.println(a);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                return;
            case quit:
                System.exit(0);
            case help:
                for (CommandName1 commandName : CommandName1.values()) {
                    System.out.println(commandName);
                }
                return;
        }

        // all further commands require a name to be specified
        String username = command2.getUserName();
        if (username == null) {
            username = clientName;
        }
        if (username == null) {
            System.out.println("name is not specified");
            return;
        }
        switch (command2.getCommandName()) {
            case registerAccount:
                clientName = username;
                try {
                    marketObj.registerMarketClient(username,this);
                } catch (RejectedException e) {
                    account=bankobjc.getAccount(clientName);
                    System.out.println(e.toString());
                    return;
                }
                try {
                    account=bankobjc.newAccount(username);
                    System.out.println(account.toString());
                } catch (RejectedException e) {
                    e.printStackTrace();
                }
                return;
            case unRegisterAccount:
                clientName = username;
                marketObj.unregisterMarketClient(username);
                bankobjc.deleteAccount(username);
                clientName="";
                return;

            case deposit:
                try {
                    account.deposit(price);
                } catch (RejectedException e) {
                    e.printStackTrace();
                }
                return;
        }
        // all further require a client reference
        switch (command2.getCommandName()) {
            case listOfIem:
                marketObj.getListOfItemsInMarket().forEach(System.out::println);
                break;
            case newItem:
                System.out.println(marketObj.addItemToSell(itemName,price,clientName));
                return;
            case buy:
               marketObj.buyItem(itemName,clientName);
                return;
            case wish:
                marketObj.wishItemToBuy(itemName,price,clientName);
                return;
            default:System.out.println("Illegal");

        }
    }


    private class Command2 {
        private String userName;
        private CommandName1 commandName1;

        public CommandName1 getCommandName() {
            return commandName1;
        }

        public String getUserName() {
            return userName;
        }

        private Command2(ClientMarket.CommandName1 commandName1, String userName) {
            this.commandName1 = commandName1;
            this.userName = userName;

        }
    }

    public static void main(String[] args)  {

        try {
            new ClientMarket().run();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getID(String id) throws RemoteException {
        return clientName;
    }

    @Override
    public void reciveMsg(String msg) {
        System.out.println(msg);
    }

    @Override
    public void notifySold(String msg) throws RemoteException {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " Your "+msg+" has been sold";
        System.out.print(temp);
    }

    @Override
    public void lackMoney() throws RemoteException {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " Your balance is not enough!: ";
        System.out.print(temp);
    }

    @Override
    public void notifyWish(String itemName,float price) throws RemoteException {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " An item meets your wish: ";

        System.out.println(temp + itemName +" for: "+ price);
    }

}
