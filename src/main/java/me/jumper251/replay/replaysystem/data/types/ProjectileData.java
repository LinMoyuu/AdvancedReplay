package me.jumper251.replay.replaysystem.data.types;

import org.bukkit.entity.EntityType;

public class ProjectileData extends PacketData {

    /**
     *
     */
    private static final long serialVersionUID = 8320803760880960739L;

    private final LocationData spawn;
    private final LocationData velocity;

    private final EntityType type;


    public ProjectileData(LocationData spawn, LocationData velocity, EntityType type) {
        this.spawn = spawn;
        this.velocity = velocity;
        this.type = type;
    }

    public LocationData getSpawn() {
        return spawn;
    }

    public LocationData getVelocity() {
        return velocity;
    }

    public EntityType getType() {
        return type;
    }
}
