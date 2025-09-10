# Projektrahmen
Im Praktikum werden Sie einen Client und Server für Videostreaming unter Nutzung des Real-Time-Streaming-Protokolls [RTSP](http://www.ietf.org/rfc/rfc2326.txt) implementieren. 
Die eigentlichen Videodaten werden mittels Real-Time-Protokoll [RTP](http://www.ietf.org/rfc/rfc3550.txt) übertragen. 


Ein großer Teil der Funktionalität ist bereits als Quelltext vorhanden, so das RTSP-Protokoll für den Server, das RTP-Handling im Client sowie die Videoanzeige.
Ihre Aufgabe besteht im Wesentlichen aus der Ergänzung der Quellcodes in den Punkten:
* RTSP-Protokoll: Button-Handler + RTSP-Request für den Client vervollständigen
* RTP-Protokoll: Headerdaten konfigurieren
* FEC-Protokoll: Paketkorrektur implementieren
* JPEG-Anzeige: Fehlerverdeckung im Client implementieren

## 1. Java-Klassen
Das Projekt besteht aus mehreren Java-Klassen, die je nach Funktionsumfang für Client, Server oder auch beide Anwendungen eingesetzt werden.
Die bereitgestellten Kassen mit der Endung `Demo` sind abstrakte Klassen, die noch ausprogrammiert werden müssen und den Klassennamen ohne `Demo` erhalten.

### Server-seitige Klassen
* [Server](src/Server.java): Funktionalität des Servers zur Antwort auf die RTSP-Clientanfragen und Streaming des Videos
* [video.AviMetadataParser](src/video/AviMetadataParser.java): Extrahiert Metadaten aus AVI-Dateien
* [video.QuickTimeMetadataParser](src/video/QuickTimeMetadataParser.java): Extrahiert Metadaten aus Quicktime-Movie-Dateien
* [video.VideoReader](src/video/VideoReader.java): Einlesen einer MJPEG-Datei auf der Serverseite

### Client-seitige Klassen
* [Client](src/Client.java): Funktionalität des Clients mit Benutzerschnittstelle zum Senden der RTSP-Kommandos und Anzeige des Videos
* [ClientView](src/ClientView.java): Client-GUI
* [JpegDisplayDemo](src/JpegDisplayDemo.java): Funktionalität der Client-Videoanzeige (abstrakte Klasse)
* JpegDisplay: TODO
* [rtp.ReceptionStatistic](src/rtp/ReceptionStatistic.java): Bereitstellung von Empfangsstatistiken

### RTSP- und RTP-Klassen für Server und Client
* [rtsp.RtspDemo](src/rtsp/RtspDemo.java): Implementierung des RTSP-Protokolls für Client und Server (abstrakte Klasse)
* rtsp.Rtsp: TODO
* [rtp.RtpHandler](src/rtp/RtpHandler.java): Verarbeitung von RTP-Paketen
* [rtp.RtpPacketDemo](src/rtp/RtpPacketDemo.java): Funktionalität zur Unterstützung von RTP-Paketen (abstrakte Klasse)
* rtp.RtpPacket: TODO
* [rtp.FecHandlerDemo](src/rtp/FecHandlerDemo.java): Unterstützung der Fehlerkorrektur mittels FEC (abstrakte Klasse)
* rtp.FECHandler: TODO
* [rtp.FecPacket](src/rtp/FECpacket.java): Erweiterung der RTP-Klasse mit FEC-Funktionalität
* [rtp.JpegFrame](src/rtp/JpegFrame): Codierung/Decodierung von JPEG-Bildern gemäß RFC-2435
* [rtp.SrtpHandler](src/rtp/SrtpHandler.java): Verschlüsselung von RTP-Paketen
* [rtp.JpegEncryptionHandler](src/rtp/JpegEncryptionHandler.java): Verschlüsselung von JPEG-Bildern (Quantisierungstabellen)

  ### Utils
* [video.VideoMetadata](src/video/VideoMetadata.java): Video-Metadaten wie Framerate und Abspieldauer
* [utils.CustomLoggingHandler](src/utils/CustomLoggingHandler.java): Anpassung der Logger-Ausgaben für minimalen Overhead


## 2. Programmstart und -nutzung
### Server
Der Start des Servers erfolgt mittels `java Server RTSP-Port`. Der Standard-RTSP-Port ist 554, da Sie aber im Praktikum einen Port > 1024 nutzen müssen, bietet sich der alternative Port 8554 an. 
Sie können den Server auch mit den optionalen Parametern `java Server RTSP-Port lossRate groupSize` starten um die FEC-Optionen schon beim Progammstart zu konfigurieren.

Der Server besitzt zwei Schieberegler welche den Kanal beeinflussen und die FEC-Gruppengröße festlegen.
* oben: genutzte FEC-Gruppengröße (2-48)
* unten: erzeugte Paketverlustrate (0-100%) 

Das Auswahlfeld für die Verschlüsselung sollte auf keine gesetzt bleiben.
Angezeigt werden in der GUI weiterhin die aktuell gesendete Bildnummer (send frame) und der aktuelle RTSP-Zustand. 

![Server](/images/server.png)

### Client
Der Start des Clients erfolgt mittels Übergabe der URL: `java Client rtsp://server:port/video_file`. 
Der Server sucht automatisch nach dem angegebenen Video in seinen Ordner videos. Ein Verzeichnis ist nicht anzugeben!
Bei Angabe des Videos mit dem Namen webcam wird der Server eine angeschlossene Webcam anstatt einer Datei verwenden.

Die in der Kommandozeile angegebene URL werden in der GUI in einem Eingabefeld angezeigt und kann dort auch verändert werden.

Verschlüsselung:  
wird z.Z.  nicht verwendet und sollte somit auf keine gesetzt bleiben. 

Felder:
* FEC: FEC wird im Client ausgewertet oder nicht
* ECO: Error Concealment (Fehlerverdeckung) wird im Client genutzt oder nicht 
* Puffergröße: Größe des Jitter-Puffers gemessen in Bildern

Anzeige (Zeile):
1. RTSP-Zustand  
Pufferfüllstand des Clients (in Bildern und korrespondierenden RTPs)
2. aktuell empfangenes RTP-Paket  
Summe der empfangenen RTPs
3. aktuell bearbeitete RTP-Nummer und Anzahl der bisher verlorenen RTPs  
aktuelle Bildnummer und Anzahl der unvollständigen und fehlenden Bilder
4. Anzahl wiederhergestellter und nicht wiederhergestellter RTPs beim Einsatz von FEC  
Paketfehlerrate (fehlende Pakete / übertragene Pakete) vor Einsatz von FEC und nach Einsatz von FEC (Im Beispiel ergibt sich eine Verbesserung von 10% auf 2% durch FEC)

Es gelten folgende Zusammenhänge: Die aktuelle empfangene RTP-Nummer ergibt sich aus RTP-Puffergröße und dem aktuellen RTP-Index sowie aus der Summe der empfangenen RTPs plus der verlorenen RTPs.   
 
Progressbar:    
* Puffergröße
* Übertragungsfortschritt (Bildnummer)

Im Bild selbst werden in der oberen linken Ecke die aktuelle Bildnummer und eventuell fehlende Bildsegmente angezeigt (im Beispiel fehlt das Segment Nr. 18 und der Bereich wird grau dargestellt). 
Das HTW-Demovideo beinhaltet insgesamt 2808 Bilder welche aus 64607 RTPs bestehen. 

Serverantworten:  
Die Antworten des Servers auf Clientrequests werden in der Konsole angezeigt. Dazu ist es hilfreich, beim Einsatz einer IDE zwischen den Konsolenfenstern von Client und Server zu wechseln.

![Client](/images/client.png)

## 3. Client

Eine RTSP-Kommunikation wird vom Client gesteuert und läuft in der Regel folgendermaßen ab:
1. Client verbindet sich mit einem Server
2. Client fragt vorhandene RTSP-Servermethoden mittels OPTIONS ab.
3. Client sendet DESCRIBE für das gewünschte Video: Analyse der vorhandenen Streams und Parameter einer gewünschten Präsentation/Video
4. Client sendet SETUP für das gewünschte Video: Erzeugung der Session und der Transportparameter anhand der vorab ermittelten Parameter
5. Client sendet PLAY für Session
6. Client sendet u.U. PAUSE für Session
7. Client sendet TEARDOWN: Terminierung der Session.

Nach einer Terminierung der Session bleibt der Client mit dem Server verbunden und es kann eine neue Session gestartet werden.

In jeden Client-Request wird ein CSeq-Header (Kommando-Sequenznummer) eingefügt, bei jedem Request wird dieser Wert erhöht.
Der Server antwortet auf die Client-Requests mit einer dazu passenden Sequenznummer. Die Antwortcodes sind ähnlich zu HTTP. Der Code 200 bedeutet z.B. Erfolg. Die RTSP-Antwortcodes finden Sie in [RTSP](http://www.ietf.org/rfc/rfc2326.txt).

### OPTIONS
* Abfrage der implementierten Servermethoden
* URL kann unspezifisch sein (*)
* Einlesen der RTSP-Antwort

### DESCRIBE
* Abfrage der Parameter des gewünschten Videos
* Einlesen der RTSP-Antwort

### SETUP
* Erzeugen eines Sockets für den Empfang der RTP-Pakete und setzen des Timeouts (5 ms)
* Senden des SETUP-Requests an den Server, Ergänzung des Transportheaders mit dem geöffneten RTP-Port.
* Einlesen der RTSP-Antwort vom Server und parsen des Sessionheaders für die Session-ID

### PLAY
* Senden des PLAY-Requests mit Sessionheader und Session-ID (kein Transportheader)
* Einlesen der RTSP-Antwort

### PAUSE
* Senden des PAUSE-Requests mit Sessionheader und Session-ID
* Einlesen der RTSP-Antwort

### TEARDOWN
* Senden des TEARDOWN-Requests mit Sessionheader und Session-ID
* Einlesen der RTSP-Antwort

### Beispiel
Bitte beachten Sie, dass der im Praktikum verwendete RTSP-Parser im Client und Server nur eine Untermenge an möglichen Attributen unterstützt. Im Zweifelsfall schauen Sie bitte in die jeweilige Implementierung. 
Sie können sich an dem folgenden Beispiel orientieren (C-Client, S-Server). Je nach Konfiguration Ihres Rechners müssen Sie unter Umständen mit FQDN arbeiten (z.B. idefix.informatik.htw-dresden.de).
```
C: OPTIONS * RTSP/1.0
 : CSeq: 1

S: RTSP/1.0 200 OK
 : CSeq: 1
 : Public: DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE

 
C: DESCRIBE rtsp://idefix/htw.mjpeg RTSP/1.0
 : CSeq: 2

S: RTSP/1.0 200 OK
 : CSeq: 2
 : Content-Base: rtsp://idefix/htw.mjpeg
 : Content-Type: application/sdp
 : Content-Length: 460
 :
 : v=0
 : [...]
 : m=video 0 RTP/AVP 96
 : a=control:trackID=0
 : a=rtpmap:26 JPEG/90000
 : a=framerate:25
 : m=audio 0 RTP/AVP 0
 : a=control:trackID=1
 : a=rtpmap:0 PCMU/8000
 : [...]

   
C: SETUP rtsp://idefix/htw.mjpeg/trackID=0 RTSP/1.0
 : CSeq: 3
 : Transport: RTP/AVP;unicast;client_port=25000-25001

S: RTSP/1.0 200 OK
 : CSeq: 3
 : Transport: RTP/AVP;unicast;client_port=25000-25001;server_port=5004-5005
 : Session: 123456;timeout=30000

 
C: PLAY rtsp://idefix/htw.mjpeg RTSP/1.0
 : CSeq: 4
 : Session: 123456

S: RTSP/1.0 200 OK
 : CSeq: 4
 : Session: 123456

 
C: PAUSE rtsp://idefix/htw.mjpeg RTSP/1.0
 : CSeq: 5
 : Session: 123456

S: RTSP/1.0 200 OK
 : CSeq: 5
 : Session: 123456

 
C: TEARDOWN rtsp://idefix/htw.mjpeg RTSP/1.0
 : CSeq: 6
 : Session: 123456
 
S: RTSP/1.0 200 OK
 : CSeq: 6
```

### Zustände des Clients
Im RTSP-Protokoll hat jede Session einen bestimmten Zustand. Sie müssen den Zustand des Clients entsprechend aktualisieren.

![RTP-Zustände](images/rtp-state.gif)

## 4. Server
Auf Serverseite muss das Einbetten der Videodaten in die RTP-Pakete erfolgen. Die beinhaltet das Erzeugen des Paketes, Setzen der Headerfelder und setzen der Payload. Für Informationen zur Bitmanipulation in Java siehe **Vorlesungsfolien zu RTP**.
Im Beleg wird nur eine Quelle genutzt (CC=0), das Feld CSRC existiert also nicht. Die Länge des RTP-Headers beträgt demzufolge 12 Byte.

<!-- ![RTP-Header](images/rtp-header.png)   -->

```
    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |V=2|P|X|  CC   |M|     PT      |       sequence number         |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                           timestamp                           |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |           synchronization source (SSRC) identifier            |
   +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
   |            contributing source (CSRC) identifiers             |
   |                             ....                              |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

## 5. Literatur

[1]: Kurose, Ross "Computernetzwerke", Pearson  
[2]: www.ietf.org/rfc/rfc2326.txt (RTSP)

