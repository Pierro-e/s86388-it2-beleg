># 02 – RTSP: Was ist vorhanden, was fehlt?

Zur [Dokumentübersicht](README.md) · [Codeüberblick](01-Code-Ueberblick.md) · [Klassen für Aufgabe 0.3](03-Aufgabe-0.3-Klassen-anlegen.md)

## Einordnung für deinen aktuellen Stand

Die zentrale Vorlage ist [src/rtsp/RtspDemo.java](../src/rtsp/RtspDemo.java). Sie enthält RTSP-Funktionen für Client und Server. Die davon abzuleitende konkrete Klasse **`src/rtsp/Rtsp.java` fehlt noch**.

Für **Aufgabe 0.3** erstellst du diese Klasse zunächst mit Konstruktoren und Methodenrümpfen. **Aufgabe 1** ergänzt die Clientlogik und den Versand. **Aufgabe 2** vervollständigt die SDP-Antwort des Servers. Dazu kommen später die anderen Protokollaufgaben; eine fertige RTSP-Steuerung allein erzeugt noch keinen korrekten RTP-Header.

## Was bereits im Code steckt

| Funktion | Vorhandene Umsetzung |
| --- | --- |
| Client mit Server verbinden | Der Clientkonstruktor `RtspDemo(URI, int)` setzt `INIT` und ruft `connectServer()` auf. Dort entstehen TCP-Socket, `BufferedReader` und `BufferedWriter`. |
| Client auf Serverseite annehmen | `Server.connectClient()` wartet am RTSP-Port und übergibt die Textstreams an `new Rtsp(reader, writer)`. |
| GUI-Buttons verknüpfen | `Client.ButtonListener` ruft `setup()`, `play()`, `pause()`, `teardown()`, `options()` und `describe()` bereits auf. |
| Clientseitiges SETUP | `RtspDemo.setup()` prüft `INIT`, erhöht `RTSPSeqNb`, ruft den noch fehlenden Anfragenversand auf und liest die Antwort. |
| Serverantworten einlesen | `parse_server_response()` liest Statuscode und Header, übernimmt beim Aufbau die Session-ID und ruft bei einem Inhalt `parseSDP()` auf. |
| SDP auswerten | `parseSDP()` liest die Bildrate aus `framerate` und die Dauer aus `range:npt`. Andere Medienattribute werden nicht zur automatischen Streamauswahl verwendet. |
| Clientanfragen auf dem Server lesen | `parse_RTSP_request()` erkennt die sechs Methoden sowie Dateiname, `CSeq` und Client-RTP-Port. |
| Serverantworten schreiben | `send_RTSP_response()` erzeugt Statuszeile, `CSeq` und je nach Methode weitere Header. `send_RTSP_error_response()` wird beispielsweise für eine fehlende Videodatei genutzt. |
| OPTIONS beantworten | `getOptions()` nennt die angebotenen Methoden. |
| Wiedergabe auf dem Server steuern | `Server.handleRequest()` behandelt die Anfragen und startet beziehungsweise stoppt den Sendetimer. |

**Wichtig:** Die Formulierung „Buttonhandler ausprogrammieren“ in Aufgabe 1 bezieht sich auf die Protokollmethoden in deiner neuen `Rtsp`-Klasse. Der Anschluss der sichtbaren Buttons ist in `Client.java` bereits vorhanden.

Auch das Öffnen des **UDP-Empfangssockets** ist schon umgesetzt: Nach erfolgreichem `setup()` ruft `Client` den `RtpHandler.startReceiver(...)` auf. Dafür brauchst du in `Rtsp` keinen weiteren Empfangssocket anzulegen.

## Was du in Aufgabe 1 ergänzen musst

Diese sechs Methoden sind in `RtspDemo` abstrakt und müssen in `Rtsp` überschrieben werden:

| Methode | Spätere Aufgabe |
| --- | --- |
| `public boolean play()` | Nur aus `READY` eine PLAY-Anfrage schicken. Antwort lesen und bei Erfolg auf `PLAYING` wechseln. |
| `public boolean pause()` | Nur aus `PLAYING` PAUSE schicken. Bei Erfolg auf `READY` wechseln. |
| `public boolean teardown()` | Aus `READY` oder `PLAYING` die Session beenden. Bei Erfolg auf `INIT` wechseln und den Sessionzustand zurücksetzen. |
| `public void options()` | Methoden abfragen und Antwort lesen. Kein Wechsel des Wiedergabezustands. |
| `public void describe()` | Beschreibung für das ausgewählte Video abfragen und Antwort einschließlich SDP lesen. Kein Wechsel des Wiedergabezustands. |
| `public void send_RTSP_request(String request_type)` | Die vollständige Anfrage passend zur Methode zusammensetzen, schreiben, protokollieren und mit `flush()` versenden. |

Orientiere dich beim Ablauf an `setup()`: Zustand prüfen, `RTSPSeqNb` erhöhen, Anfrage senden, Antwort auswerten. Bei den neuen booleschen Methoden sollte nur eine erfolgreiche Antwort (`200`) zum Zustandswechsel und zu `true` führen. Bei ungültigem Zustand wird keine Anfrage verschickt und `false` zurückgegeben.

**CSeq genau einmal pro versendeter Anfrage erhöhen.** Das vorhandene `setup()` erledigt dies schon vor dem Aufruf von `send_RTSP_request()`. Wenn die anderen Handler es genauso machen, darf der gemeinsame Sender nicht nochmals erhöhen.

### Was gehört in die Anfrage?

Die benötigten Variablen liegen bereits in der Basisklasse und sind aus demselben Package erreichbar: `url`, `RTSPSeqNb`, `RTSPid`, `RTP_RCV_PORT`, `RTSPBufferedWriter`, `CRLF` und `log`.

| Methode | Ziel der Anfrage im Projektbeispiel | Zusätzliche Angaben |
| --- | --- | --- |
| OPTIONS | `*` | `CSeq` |
| DESCRIBE | `rtsp://localhost:8554/htw.mjpeg` | `CSeq` |
| SETUP | `rtsp://localhost:8554/htw.mjpeg/trackID=0` | `CSeq` und `Transport: RTP/AVP;unicast;client_port=25000-25001` |
| PLAY / PAUSE / TEARDOWN | `rtsp://localhost:8554/htw.mjpeg` | `CSeq` und `Session: <gespeicherte RTSPid>` |

Eine Anfrage beginnt mit `METHODE ZIEL RTSP/1.0`. Jede Zeile endet mit **`CRLF`**, also `\r\n`. Nach dem letzten Header folgt eine **leere Zeile**, danach `flush()`. Ohne diese Trennung kann der Server weiter auf Header warten. Der Transportheader gehört beim hier vorgesehenen Ablauf zu SETUP; PLAY, PAUSE und TEARDOWN verwenden den Sessionheader.

Die vollständigen Nachrichtenbeispiele stehen in der [Projektbeschreibung](Projektbeschreibung.md#beispiel). Diese Beispiele dienen als Formatvorlage, die tatsächlichen Werte stammen aus deiner URL und den gespeicherten Feldern.

### Zustände des Clients

```text
INIT ── SETUP erfolgreich ──→ READY ── PLAY erfolgreich ──→ PLAYING
                              ↑                              │
                              └──── PAUSE erfolgreich ───────┘

READY oder PLAYING ── TEARDOWN erfolgreich ──→ INIT
```

`OPTIONS` und `DESCRIBE` ändern diese Zustände nicht. „Connect“ öffnet zunächst nur die TCP-Verbindung. Nach TEARDOWN soll die Verbindung gemäß Projektbeschreibung für eine neue Session bestehen bleiben.

Der Server verwaltet seinen Zustand separat in `Server.java`; die Clientzustände stehen im Enum `RtspDemo.State`. Der Client muss seinen Zustand anhand der Antworten selbst aktualisieren.

## Was du in Aufgabe 2 ergänzen musst

`getDescribe(...)` ist bereits vorhanden, liefert aber noch **keine vollständige SDP-Beschreibung**. Sie enthält unter anderem diese Platzhalter:

```text
m=video ...
a=control ...
```

Vorbereitet sind SDP-Grundzeilen, die RTP-Zuordnung `26 JPEG/90000`, die Bildrate sowie der Zeitbereich. Zusätzliche Audiobeschreibungen sind nur auskommentiert; ein H.264-Angebot fehlt.

Du überschreibst in `Rtsp` die tatsächlich vorhandene Signatur:

```java
@Override
String getDescribe(VideoMetadata meta, int RTP_dest_port) {
    // Aufgabe 2: SDP-Header und SDP-Inhalt erzeugen.
}
```

Das ist nur die Signatur zur Orientierung, noch kein vollständiger Methodenrumpf: Die Methode muss später einen `String` zurückgeben. Dafür wird `video.VideoMetadata` importiert.

Laut [Aufgabe 2](Aufgabenstellung.md#2-sdp-protokoll) muss die Antwort Folgendes beschreiben:

- Einen MJPEG-Stream mit Payloadtyp **26** und **`trackID=0`**.
- Zusätzlich einen H.264-Videostream und einen G.711-Audiostream mit passenden Medien- und RTP-Zuordnungen.
- Bildrate und Zeitbereich, die der vorhandene Client auswerten kann.
- Später auch die FEC-Informationen, sobald FEC implementiert ist.

Für das Beispielvideo darf die Beschreibung statisch hinterlegt werden. Das zusätzliche Angebot von H.264 und G.711 demonstriert SDP; der vorhandene Medienpfad verarbeitet weiterhin MJPEG.

**Rückgabewert beachten:** `getDescribe(...)` liefert die SDP-bezogenen RTSP-Header (`Content-Type`, `Content-Length` und die trennende Leerzeile) **zusammen mit dem SDP-Inhalt** zurück. Statuszeile und `CSeq` schreibt `send_RTSP_response()` schon davor. Sie dürfen nicht doppelt erscheinen. `Content-Length` muss zum übertragenen Inhalt passen.

## Besonderheiten der Vorlage

Die folgenden Punkte helfen dir, den vorhandenen Code richtig einzuordnen. Sie gehören nicht alle zu Aufgabe 0.3:

- **SETUP ist als Beispiel vereinfacht:** Es behandelt nur `404` ausdrücklich als Fehler. Bei anderen Statuscodes würde es derzeit ebenfalls nach `READY` wechseln. Übernimm diese Fehlerbehandlung nicht ungeprüft für die neuen Methoden.
- **Server reagiert bei falschem PLAY-/PAUSE-Zustand nicht mit einer Fehlerantwort.** Die Zustandsprüfung im Client verhindert deshalb auch unnötiges Warten auf eine Antwort.
- **Einfaches URL-Format verwenden:** Der Server ermittelt den Dateinamen mit `split("/")[3]`. Verwende für den Einstieg `/htw.mjpeg`, keine verschachtelten Videopfade.
- **Transportheader wie im Beispiel formatieren:** Der Parser sucht nach `CSeq` und `Transport` in genau dieser Schreibweise und liest den Clientport hinter dem ersten `=` aus. Die Vorlage ist kein allgemeiner RTSP-Parser.
- **Session und CSeq sind nur teilweise geprüft:** Die Session-ID ist serverseitig fest `123456`. Der Server validiert die mitgesendete Session nicht; der Client protokolliert die Antwort-CSeq, vergleicht sie aber nicht mit seiner Anfrage.
- **SDP-Auswertung ist eingeschränkt:** Der Parser erwartet ganzzahlige Bildraten und einfache `range:npt`-Angaben. Der Gerüstcode arbeitet beim Inhalt mit Zeichen statt durchgehend mit Bytes und liest den Inhalt nur mit einem `read()`-Aufruf.
- **RTP und FEC nutzen im aktuellen Versand denselben UDP-Zielport.** Der standardmäßige Client-Empfangsport ist `25000`, der RTSP-Port für den Einstieg `8554`.

## Woran erkennst du später den Fortschritt?

Für **0.3** genügt zunächst ein vollständiges Klassengerüst, sodass nach eingerichteten Abhängigkeiten gebaut und die Programme gestartet werden können. Es ist noch keine funktionierende RTSP-Sitzung zu erwarten.

Nach **Aufgabe 1** solltest du im Client und Server passende Anfragen, Antworten, fortlaufende CSeq-Werte und die Zustandsfolge prüfen können. Die vorgesehene Bedienfolge ist `Connect → Options → Describe → Setup → Play → Pause → Play → Teardown`.

Nach **Aufgabe 2** enthält die DESCRIBE-Antwort deine vollständige SDP-Beschreibung. Für die eigentliche Videoübertragung fehlt zu diesem Zeitpunkt weiterhin der RTP-Header aus **Aufgabe 3**.
