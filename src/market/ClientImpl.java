package market;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by joshuaPro on 2015-11-26.
 */
public class ClientImpl implements ClientInterface  {
    private String name;

    public ClientImpl(String name){
        this.name=name;
    }

    @Override
    public String getID(String id) throws RemoteException {
        return name;
    }

    @Override
    public void reciveMsg(String msg) {
     System.out.println(msg);
    }

    @Override
    public void notifySold(Item item) throws RemoteException {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " Your item has been sold: "+name+", "+String.valueOf(item.getPrice());
        System.out.println(temp);
    }

    @Override
    public void lackMoney() throws RemoteException {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " Your balance is not enough!: ";
        System.out.println(temp);
    }

    @Override
    public void notifyWish(Item item) throws RemoteException {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " An item meets your wish: ";

        System.out.println(item.getName() + "," + String.valueOf(item.getPrice()) + "," + item.getOwner());
    }
}
