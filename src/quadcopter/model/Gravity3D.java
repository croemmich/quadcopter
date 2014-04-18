package quadcopter.model;

public class Gravity3D extends Vector<Gravity> {

    public Gravity3D(double x, double y, double z) {
        super(new Gravity(x), new Gravity(y), new Gravity(z));
    }

    public double getTotalGravity() {
        return (Math.sqrt(x.getGravity() * x.getGravity() + y.getGravity() * y.getGravity() + z.getGravity() * z.getGravity()));
    }

    public double getTotalAcceleration() {
        return (Math.sqrt(x.getAcceleration() * x.getAcceleration() + y.getAcceleration() * y.getAcceleration() + z.getAcceleration() * z.getAcceleration()));
    }

    @Override
    public String toString() {
        return "X: " + x.toString() + " Y: " + y.toString() + " Z: " + z.toString();
    }

}
