package quadcopter.io;

import java.io.IOException;

public interface SerialListener {

    public void onOpen();

    public void onClose();

    public void onError(IOException error);

    public void onData(byte[] data);

}
