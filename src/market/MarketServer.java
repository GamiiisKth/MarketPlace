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
                LocateRegistry.getRegistry(6767).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(6767);
            }
            System.out.println(markOnj + " is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
