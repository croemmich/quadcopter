package quadcopter;

public class Contstants {

    /**
     * Earth's gravity in m/s^2
     */
    public static double GRAVITY_EARTH = 9.80665;

    /**
     * The moon's gravity in m/s^2
     */
    public static double GRAVITY_MOON = 1.6;

    /**
     * The sun's gravity in m/s^2
     */
    public static double GRAVITY_SUN = 275.0F;

    public static double GRAVITY = GRAVITY_EARTH;

    /**
     * Maximum magnetic field on Earth's surface
     */
    public static double MAGFIELD_EARTH_MAX = 60.0;

    /**
     * Minimum magnetic field on Earth's surface
     */
    public static double MAGFIELD_EARTH_MIN = 30.0;

    /**
     * Average sea level pressure is 1013.25 hPa
     */
    public static double PRESSURE_SEALEVEL = 1013.25F;

    /**
     * Degrees/s to rad/s multiplier
     */
    public static double DPS_TO_RADS = 0.017453293;

    /**
     * Gauss to micro-Tesla multiplier
     */
    public static double GAUSS_TO_MICROTESLA = 100;

}
