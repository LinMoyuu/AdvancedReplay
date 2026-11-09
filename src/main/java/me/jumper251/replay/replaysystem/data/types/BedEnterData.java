package me.jumper251.replay.replaysystem.data.types;

public class BedEnterData extends PacketData {

    /**
     *
     */
    private static final long serialVersionUID = 6849586468365004854L;


    private final LocationData location;

    public BedEnterData(LocationData location) {
        this.location = location;
    }


    public LocationData getLocation() {
        return location;
    }
}
