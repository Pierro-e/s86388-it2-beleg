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

public class ServerView extends JFrame {
  JLabel label;
  JLabel stateLabel;
  JSlider dropRate;
  JSlider groupSize;
  ButtonGroup encryptionButtons = null;
  Server.ButtonListener server;

  public ServerView(Server.ButtonListener server) {
    this.server = server;
    initView();
  }

  private void initView() {
    setTitle("Server");
    setSize(500, 200);
    setLocationRelativeTo(null);  // position in the middle of the screen
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // GUI:
    label = new JLabel("Send frame #        ", JLabel.CENTER);
    stateLabel = new JLabel("State:         ", JLabel.CENTER);
    getContentPane().add(label, BorderLayout.NORTH);
    getContentPane().add(stateLabel, BorderLayout.SOUTH);
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    dropRate = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    dropRate.addChangeListener(server);
    dropRate.setMajorTickSpacing(10);
    dropRate.setMinorTickSpacing(5);
    dropRate.setPaintTicks(true);
    dropRate.setPaintLabels(true);
    dropRate.setName("p");
    groupSize = new JSlider(JSlider.HORIZONTAL, 2, 48, 2);
    groupSize.addChangeListener(server);
    groupSize.setMajorTickSpacing(4);
    groupSize.setMinorTickSpacing(1);
    groupSize.setPaintLabels(true);
    groupSize.setPaintTicks(true);
    groupSize.setName("k");
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 4;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    mainPanel.add(groupSize, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 4;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    mainPanel.add(dropRate, gbc);

    initGuiEncryption(mainPanel);
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    setVisible(true);
  }


  private void initGuiEncryption(JPanel panel) {
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel encryptionLabel = new JLabel("Verschlüsselung:");
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 10, 0, 0);
    panel.add(encryptionLabel, gbc);

    encryptionButtons = new ButtonGroup();
    JRadioButton e_none = new JRadioButton("keine");
    e_none.addItemListener(server);
    encryptionButtons.add(e_none);
    e_none.setSelected(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(e_none, gbc);

    JRadioButton e_srtp = new JRadioButton("SRTP");
    e_srtp.addItemListener(server);
    encryptionButtons.add(e_srtp);
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(e_srtp, gbc);

    JRadioButton e_jpeg = new JRadioButton("JPEG");
    e_jpeg.addItemListener(server);
    encryptionButtons.add(e_jpeg);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(e_jpeg, gbc);
  }

}
