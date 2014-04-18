package quadcopter.model;

public class Gravity {

    /**
     * Earth's gravity in m/s^2
     */
    public static final double GRAVITY_EARTH = 9.80665;

    /**
     * The moon's gravity in m/s^2
     */
    public static final double GRAVITY_MOON = 1.6;

    /**
     * The sun's gravity in m/s^2
     */
    public static final double GRAVITY_SUN = 275.0F;

    private double gravity;

    public Gravity(double gravity) {
        this.gravity = gravity;
    }

    /**
     * @return the acceleration on earth in m/s^2
     */
    public double getAcceleration() {
        return getAccleration(GRAVITY_EARTH);
    }

    /**
     * @param constant a gravity to m/s^2 constant
     * @return the acceleration in m/s^2
     */
    public double getAccleration(double constant) {
        return this.gravity * constant;
    }

    /**
     * @return the gravity in Gs
     */
    public double getGravity() {
        return this.gravity;
    }

    @Override
    public String toString() {
        return String.valueOf(getGravity());
    }

}
