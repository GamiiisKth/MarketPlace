package market;

import java.util.UUID;

/**
 * Created by joshuaPro on 2015-11-25.
 */
public class Item {

    private String name;
    private float price;
    private String owner;

    private ClientInterface ownerInterface;
    public Item(String ItemName, float price, String owner  ,ClientInterface ownerInterface){
        this.name=ItemName;
        this.price=price;
        this.owner=owner;

        this.ownerInterface=ownerInterface;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

   public ClientInterface getOwnerInterface() {
        return ownerInterface;
    }

    public void setOwnerInterface(ClientInterface ownerInterface) {
        this.ownerInterface = ownerInterface;
    }

    @Override
    public String toString() {
        return String.format("[ ("+ getName()+"), " + getPrice()+ ")]");
    }
}



