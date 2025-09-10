import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

public class ServerView extends JFrame {
  JLabel frameLabel;
  JLabel stateLabel;
  JLabel fileLabel;
  JSlider dropRate;
  JSlider groupSize;
  ButtonGroup encryptionButtons = null;
  Server.ButtonListener server;

  // GUI constants
  private static final String WINDOW_TITLE = "Server";
  private static final int WINDOW_WIDTH = 500;
  private static final int WINDOW_HEIGHT = 300;

  private static final String LABEL_SEND_FRAME_TEXT = "Send frame #        ";
  private static final String LABEL_STATE_TEXT = "State:         ";
  private static final String LABEL_FILE_TEXT = "No file selected";
  private static final String LABEL_STATE2_TEXT = ":------------------:";
  private static final String SLIDERS_DESC_TEXT1 = "FEC Group Size:";
  private static final String SLIDERS_DESC_TEXT2 = "Packet Drop Rate:";
  private static final String ENCRYPTION_LABEL_TEXT = "Encryption:";
  private static final String ENCRYPTION_NONE_TEXT = "keine";
  private static final String ENCRYPTION_SRTP_TEXT = "SRTP";
  private static final String ENCRYPTION_JPEG_TEXT = "JPEG";

  public ServerView(Server.ButtonListener server) {
    this.server = server;
    initView();
  }

  private void initView() {
    setTitle(WINDOW_TITLE);
    setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    setLocationRelativeTo(null);  // position in the middle of the screen
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    // State labels
    frameLabel = new JLabel(LABEL_SEND_FRAME_TEXT, JLabel.LEFT);
    stateLabel = new JLabel(LABEL_STATE_TEXT, JLabel.LEFT);
    fileLabel = new JLabel(LABEL_FILE_TEXT, JLabel.LEFT);
    mainPanel.add(stateLabel, gbc(0, 0, 1));
    mainPanel.add(fileLabel, gbc(1, 0, 1));
    mainPanel.add(new JLabel(LABEL_STATE2_TEXT), gbc(2, 0, 1));
    mainPanel.add(frameLabel, gbc(3, 0, 1));

    // Description label left from sliders
    JLabel slidersDescLabel1 = new JLabel(SLIDERS_DESC_TEXT1);
    mainPanel.add(slidersDescLabel1, gbc(0, 1, 1));
    JLabel slidersDescLabel2 = new JLabel(SLIDERS_DESC_TEXT2);
    mainPanel.add(slidersDescLabel2, gbc(0, 2, 1));

    // Sliders for FEC group size and packet drop rate
    dropRate  = createSlider(0, 100, 0, 10, 5, "p", server);
    groupSize = createSlider(2, 48, 2, 4, 1, "k", server);
    mainPanel.add(groupSize, gbc(1, 1, 3));
    mainPanel.add(dropRate, gbc(1, 2, 3));

    // Radio button for encryption
    JLabel encryptionLabel = new JLabel(ENCRYPTION_LABEL_TEXT);
    mainPanel.add(encryptionLabel, gbc(0, 3, 1));
    encryptionButtons = new ButtonGroup();
    JRadioButton noneRadioButton = createEncryptionRadioButton(ENCRYPTION_NONE_TEXT, true);
    mainPanel.add(noneRadioButton, gbc(1, 3, 1));

    JRadioButton srtpRadioButton = createEncryptionRadioButton(ENCRYPTION_SRTP_TEXT, false);
    mainPanel.add(srtpRadioButton, gbc(2, 3, 1));

    JRadioButton jpegRadioButton = createEncryptionRadioButton(ENCRYPTION_JPEG_TEXT, false);
    mainPanel.add(jpegRadioButton, gbc(3, 3, 1));

    setContentPane(mainPanel);
    setVisible(true);
  }


  private GridBagConstraints gbc(int gridx, int gridy, int gridwidth ) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = gridx;
    gbc.gridy = gridy;
    gbc.gridwidth = gridwidth;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    //gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    return gbc;
  }


  private JSlider createSlider(int min, int max, int value,
      int majorTick, int minorTick,
      String name,
      ChangeListener listener) {
    JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
    slider.addChangeListener(listener);
    slider.setMajorTickSpacing(majorTick);
    slider.setMinorTickSpacing(minorTick);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.setName(name);
    return slider;
  }


  private JRadioButton createEncryptionRadioButton(String text, boolean selected) {
    JRadioButton radioButton = new JRadioButton(text);
    radioButton.addItemListener(server);
    radioButton.setSelected(selected);
    encryptionButtons.add(radioButton);
    return radioButton;
  }

}
