package me.jumper251.replay.replaysystem.data.types;

public class EntityItemData extends PacketData {

    /**
     *
     */
    private static final long serialVersionUID = 2309497657075148314L;


    private final int action;
    private final int id;

    private final ItemData itemData;

    private final LocationData location;
    private final LocationData velocity;

    public EntityItemData(int action, int id, ItemData itemData, LocationData location, LocationData velocity) {
        this.action = action;
        this.itemData = itemData;
        this.location = location;
        this.id = id;
        this.velocity = velocity;
    }


    public int getAction() {
        return action;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public LocationData getLocation() {
        return location;
    }

    public int getId() {
        return id;
    }

    public LocationData getVelocity() {
        return velocity;
    }

}
