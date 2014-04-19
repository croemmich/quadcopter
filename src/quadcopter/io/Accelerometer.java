package quadcopter.io;

import javax.measure.Measure;
import javax.measure.VectorMeasure;
import javax.measure.quantity.Acceleration;
import java.io.IOException;

public interface Accelerometer extends Device {

    public Measure<Double, Acceleration> readX() throws IOException;

    public Measure<Double, Acceleration> readY() throws IOException;

    public Measure<Double, Acceleration> readZ() throws IOException;

    public VectorMeasure<Acceleration> readVector() throws IOException;

}
