package quadcopter.io;

import java.io.IOException;

public interface Serial {

    public void open() throws IOException;

    public void close() throws IOException;

    public boolean isOpen();

    public boolean write(byte[] buffer);

    public boolean write(String string);

    public void addListener(SerialListener listener);

}
