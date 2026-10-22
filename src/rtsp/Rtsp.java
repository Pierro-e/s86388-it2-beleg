package rtsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URI;

public class Rtsp extends RtspDemo{

    public Rtsp(URI url, int rtpRcvPort) {
        super(url, rtpRcvPort);
    }

    public Rtsp(BufferedReader RTSPBufferedReader, BufferedWriter RTSPBufferedWriter) {
        super(RTSPBufferedReader, RTSPBufferedWriter);
    }

    @Override
    public boolean play() {
        // TODO Aufgabe 1
        return false;
    }

    @Override
    public boolean pause() {
        // TODO Aufgabe 1
        return false;
    }

    @Override
    public boolean teardown() {
        // TODO Aufgabe 1
        return false;
    }

    @Override
    public void describe() {
        // TODO Aufgabe 1

    }

    @Override
    public void options() {
        // TODO Aufgabe 1

    }

    @Override
    public void send_RTSP_request(String request_type) {
        // TODO Aufgabe 1

    }
}
