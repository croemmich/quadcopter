package quadcopter.filter;

public class LowPassFilter implements Filter {

    private final double b0;
    private final double b1;
    private final double b2;
    private final double a1;
    private final double a2;

    private double delayElement1 = 0;
    private double delayElement2 = 0;

    public LowPassFilter(double sampleFrequency, double cutoffFrequency) {
        final double fr = sampleFrequency / cutoffFrequency;
        final double ohm = Math.tan(Math.PI / fr);
        final double c = 1.0 + 2.0 * Math.cos(Math.PI / 4.0) * ohm + ohm * ohm;

        b0 = ohm * ohm / c;
        b1 = 2.0 * b0;
        b2 = b0;
        a1 = 2.0 * (ohm * ohm - 1.0) / c;
        a2 = (1.0 - 2.0 * Math.cos(Math.PI / 4.0) * ohm + ohm * ohm) / c;
    }

    public double filter(double sample) {
        double delayElement0 = sample - delayElement1 * a1 - delayElement2 * a2;
        if (delayElement0 == Double.NEGATIVE_INFINITY || delayElement0 == Double.POSITIVE_INFINITY) {
            delayElement0 = sample;
        }
        final double output = delayElement0 * b0 + delayElement1 * b1 + delayElement2 * b2;
        delayElement2 = delayElement1;
        delayElement1 = delayElement0;
        return output;
    }

}
