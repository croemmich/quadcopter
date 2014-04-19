package quadcopter.io;

import javax.measure.Measure;
import javax.measure.VectorMeasure;
import javax.measure.quantity.MagneticFluxDensity;
import java.io.IOException;

public interface Magnetometer extends Device {

    public VectorMeasure<MagneticFluxDensity> readVector() throws IOException;

    public Measure<Double, MagneticFluxDensity> readX() throws IOException;

    public Measure<Double, MagneticFluxDensity> readY() throws IOException;

    public Measure<Double, MagneticFluxDensity> readZ() throws IOException;

}
