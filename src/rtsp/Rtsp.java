package rtsp;

import video.VideoMetadata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
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

    @Override
    public String getDescribe(VideoMetadata meta, int RTP_dest_port) {
        StringWriter rtspHeader = new StringWriter();
        StringWriter rtspBody = new StringWriter();

        // Write the body first, so we can get the size later

        // Session description
        // v=  (protocol version)
        // o=  (owner/creator and session identifier).
        // s=  (session name)
        rtspBody.write("v=0" + CRLF);
        rtspBody.write("o=- 0 0 IN IP4 0.0.0.0" + CRLF);
        rtspBody.write("s=RTSP-Streaming" + CRLF);
        rtspBody.write("c=IN IP4 0.0.0.0=" + CRLF);

        // Time description
        // t= (time the session is active)
        rtspBody.write("t=0 0" + CRLF);
        rtspBody.write("a=control:*" + CRLF);
        // Stream control
        //

        // Media description
        // m=  (media name and transport address)
        rtspBody.write("m=video " + RTP_dest_port + " RTP/AVP 26"  + CRLF);         // 26 = MJPEG  RTP_dest_port Vorgeschlagener Port von S-seite aus
            rtspBody.write("a=control:trackID=0" + CRLF);
            rtspBody.write("a=rtpmap:" + MJPEG_TYPE + " JPEG/90000" + CRLF);
        // rtspBody.write("a=mimetype:string;\"video/mjpeg\"" + CRLF);



        rtspBody.write("m=video " + RTP_dest_port + " RTP/AVP 96" + CRLF);          // 96 =  H264
            rtspBody.write("a=control:trackID=1" + CRLF);
            rtspBody.write("a=rtpmap:96 H264/90000" + CRLF);


        // Audio ist not supported yet
        rtspBody.write("m=audio " + RTP_dest_port + " RTP/AVP " + "0" + CRLF);
            rtspBody.write("a=control:trackID=2" + CRLF);
            rtspBody.write("a=rtpmap:" + "0" + " G711/8000" + CRLF);


        rtspBody.write("a=framerate:" + meta.getFramerate() + CRLF);
        rtspBody.write("a=range:npt=0-");
        if (meta.getDuration() > 0.0) {
            rtspBody.write(Double.toString(meta.getDuration()));
        }
        rtspBody.write(CRLF);

        rtspHeader.write("Cseq: " + RTSPSeqNb + CRLF);
        rtspHeader.write("Content-Base: " + VideoFileName + CRLF);
        rtspHeader.write("Content-Type: " + "application/sdp" + CRLF);
        rtspHeader.write("Content-Length: " + rtspBody.toString().length() + CRLF);
        rtspHeader.write(CRLF);

        return rtspHeader + rtspBody.toString();
    }
}


