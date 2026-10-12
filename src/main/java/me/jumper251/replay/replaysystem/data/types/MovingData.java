package me.jumper251.replay.replaysystem.data.types;


public class MovingData extends PacketData {


    /**
     *
     */
    private static final long serialVersionUID = -3792160902735306458L;

    private final double x;
    private final double y;
    private final double z;

    private final float pitch;
    private final float yaw;

    public MovingData(double x, double y, double z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public double getY() {
        return y;
    }

    public float getPitch() {
        return pitch;
    }

    public double getX() {
        return x;
    }

    public float getYaw() {
        return yaw;
    }

    public double getZ() {
        return z;
    }


}
