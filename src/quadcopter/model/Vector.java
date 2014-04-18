package quadcopter.model;

public class Vector<T> {
    public final T x;
    public final T y;
    public final T z;

    public Vector(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "X: " + String.valueOf(x) + "    Y: " + String.valueOf(y) + "    Z:" + String.valueOf(z);
    }
}
