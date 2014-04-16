package quadcopter.model;

public class XYZ {
    public final double x;
    public final double y;
    public final double z;

    public XYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public XYZ(byte xLsb, byte xMsb, byte yLsb, byte yMsb, byte zLsb, byte zMsb) {
        this.x = (short) (((xLsb & 0xff) << 8) + (xMsb & 0xff));
        this.y = (short) (((yLsb & 0xff) << 8) + (yMsb & 0xff));
        this.z = (short) (((zLsb & 0xff) << 8) + (zMsb & 0xff));
    }

    @Override
    public String toString() {
        return "X: " + x + "    Y: " + y + "    Z:" + z;
    }
}
