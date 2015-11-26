package market;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by joshuaPro on 2015-11-26.
 */
public class MarketServer  {
    private static final String USAGE = "java bankrmi.Server <bank_rmi_url>";
    private static final String BANK = "Nordea";
    public MarketServer( ) {
        try {
            MarketInterface markOnj = new MarketImpl();
            // Register the newly created object at rmiregistry.
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            Naming.rebind("rmi://localhost:1099/blocket", markOnj);
            System.out.println(markOnj + " is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if (args.length > 1 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
            System.out.println(USAGE);
            System.exit(1);
        }

        new MarketServer();
    }

}
