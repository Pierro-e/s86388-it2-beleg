package rtsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URI;
import java.io.IOException;


public class Rtsp extends RtspDemo{

    public Rtsp(URI url, int rtpRcvPort) {
        super(url, rtpRcvPort);
    }

    public Rtsp(BufferedReader RTSPBufferedReader, BufferedWriter RTSPBufferedWriter) {
        super(RTSPBufferedReader, RTSPBufferedWriter);
    }

    @Override
    public boolean play() {
        if (state != State.READY) {
            log.warning("PLAY nicht erlaubt im RTSP-Zustand: " + state);
            return false;
        }

        RTSPSeqNb++;
        send_RTSP_request("PLAY");
        int status = parse_server_response();
        if (status != 200) {
            log.warning("PLAY fehlgeschlagen, RTSP-Statuscode: " + status);
            return false;
        }

        state = State.PLAYING;
        log.info("Neuer RTSP-Zustand: " + state);
        return true;
    }

    @Override
    public boolean pause() {
        if (state != State.PLAYING) {
            log.warning("PAUSE nicht erlaubt im RTSP-Zustand: " + state);
            return false;
        }

        RTSPSeqNb++;
        send_RTSP_request("PAUSE");
        int status = parse_server_response();
        if (status != 200) {
            log.warning("PAUSE fehlgeschlagen, RTSP-Statuscode: " + status);
            return false;
        }

        state = State.READY;
        log.info("Neuer RTSP-Zustand: " + state);
        return true;
    }

    @Override
    public boolean teardown() {
        if (state != State.READY && state != State.PLAYING) {
            log.warning("TEARDOWN nicht erlaubt im RTSP-Zustand: " + state);
            return false;
        }

        RTSPSeqNb++;
        send_RTSP_request("TEARDOWN");
        int status = parse_server_response();
        if (status != 200) {
            log.warning("TEARDOWN fehlgeschlagen, RTSP-Statuscode: " + status);
            return false;
        }

        state = State.INIT;
        RTSPid = "0";
        log.info("Neuer RTSP-Zustand: " + state);
        return true;
    }

    @Override
    public void describe() {
        RTSPSeqNb++;
        send_RTSP_request("DESCRIBE");
        int status = parse_server_response();
        if(status != 200)
            log.warning("falscher Server respons jonge: " + status);
    }

    @Override
    public void options() {
        RTSPSeqNb++;
        send_RTSP_request("OPTIONS");
        int status = parse_server_response();
        if(status != 200)
            log.warning("falscher Server respons jonge: " + status);

    }

    @Override
    public void send_RTSP_request(String request_type) {
        String target;
        String extra_header = "";

        switch(request_type) {
            case "OPTIONS":
                target = "*";
                break;

            case "DESCRIBE":
                target = url.toString();
                break;

            case "SETUP":
                target = url.toString() + "/trackID=0";
                extra_header = "Transport: RTP/AVP;unicast;client_port="
                                + RTP_RCV_PORT + "-" + (RTP_RCV_PORT + 1) + CRLF;
                break;

            case "PLAY":
            case "TEARDOWN":
            case "PAUSE":
                target = url.toString();
                extra_header = "Session: " + RTSPid + CRLF;
                break;

            default:
                throw new IllegalArgumentException("Kenn keine RTSP-Methode" +
                        request_type + "LUL was willste dagegen machen? Loser");
        }

        /************ Paket als String zusammenbauen *****************************************************/

        StringBuilder request = new StringBuilder();
        request.append(request_type).append(" ").append(target).append(" RTSP/1.0")
                .append(CRLF).append("CSeq: ").append(RTSPSeqNb).append(CRLF)
                .append(extra_header);
        request.append(CRLF);

        String message = request.toString();

        log.info("Isch hab RTSP Anfrage gemacht JUUnge" + CRLF + message);

        try {
            RTSPBufferedWriter.write(message);
            RTSPBufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "RTSP-Anfrage konnte nicht machen boyyyy: " + request_type, e);
        }

    }
}


