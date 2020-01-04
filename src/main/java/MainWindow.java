import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

public class MainWindow extends JFrame {
    private File saveDir;

    public MainWindow() {
        super("Livejournal blog downloader");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);



        JLabel lName = new JLabel("Journal name");
        JTextField jName = new JTextField(25);
        jName.setBorder(BorderFactory.createCompoundBorder(
                jName.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JLabel lTooltip = new JLabel("e.g., https://some-name.livejournal.com");
        lTooltip.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));


        JLabel lSaveDir = new JLabel("Save location");
        JButton bSaveDir = new JButton("Choose");
        bSaveDir.setBackground(new Color(149, 238, 105, 255));
        JLabel lTooltip3 = new JLabel();
        lTooltip3.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        bSaveDir.addActionListener(actionEvent -> {
            JFileChooser jSaveDir = new JFileChooser();
            jSaveDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = jSaveDir.showDialog(null, "Choose Directory");
            if (ret == JFileChooser.APPROVE_OPTION) {
                saveDir = jSaveDir.getSelectedFile();
                lTooltip3.setText(saveDir.getAbsolutePath());
            }
        });

        JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayout(3,2,15,0));
        panel4.add(lName);
        panel4.add(lSaveDir);
        panel4.add(jName);
        JPanel panel5 = new JPanel(new GridLayout(1,3));
        panel5.add(bSaveDir);
        panel5.add(new JLabel());
        panel5.add(new JLabel());
        panel4.add(panel5);
        panel4.add(lTooltip);
        panel4.add(lTooltip3);


        JPanel panel3 = new JPanel(new FlowLayout(3,15,15));
        TitledBorder side;
        side = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(125, 135, 150, 255)),
                "Options",
                0,
                0,
                new Font(Font.SANS_SERIF, Font.BOLD,12));
        panel3.setBorder(side);

        JLabel lQuantity = new JLabel("Posts quantity");
        lQuantity.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        JTextField jQuantity = new JTextField();
        jQuantity.setBorder(BorderFactory.createCompoundBorder(
                jQuantity.getBorder(),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
        JLabel lTooltip2 = new JLabel("leave empty for <All>");
        lTooltip2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        lTooltip2.setBorder(BorderFactory.createEmptyBorder(5,0,15,0));
        JLabel lDownload = new JLabel("Download");
        lQuantity.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        JRadioButton firstButton = new JRadioButton("Full post without <head>");
        firstButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        firstButton.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(firstButton);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

        panel2.add(lQuantity);
        panel2.add(jQuantity);
        panel2.add(lTooltip2);
        panel2.add(lDownload);
        panel2.add(firstButton);
        panel3.add(panel2);

        JButton start = new JButton("Start!");
        start.setBorder(BorderFactory.createCompoundBorder(
                start.getBorder(),
                BorderFactory.createEmptyBorder(4, 0, 4, 0))
        );
        start.setBackground(new Color(149, 238, 105, 255));

        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        JLabel done = new JLabel();
        JLabel size = new JLabel();
        result.add(done);
        result.add(size);

        start.addActionListener(e -> {
            done.setText("");
            ParserConfig.setJournal(jName.getText()); // replace with lj url without last slash!
            ParserConfig.setSaveDir(saveDir.toPath()); // replace with dir where you want to save data
            int q = -1;
            if (jQuantity.getText() != null && !jQuantity.getText().equals("")) {
                q = Integer.parseInt(jQuantity.getText());
            }
            ParserConfig.setQuantity(q); // how much posts you want to save, -1 for all posts
            ParserConfig.setWantedResult(0); // just don't touch it while now
            ParserConfig.requestParse();
            done.setText("Done!");
            size.setText(ParserConfig.requestSize() + " posts successfully downloaded to " + saveDir.getAbsolutePath());
        });

        JPanel panel1 = new JPanel(new FlowLayout(3,2,0));
        panel1.add(start);

        JPanel pForm = new JPanel();
        pForm.setLayout(new BorderLayout(15,10));
        pForm.add(panel4, BorderLayout.PAGE_START);
        pForm.add(panel1, BorderLayout.PAGE_END);
        pForm.add(panel3, BorderLayout.WEST);
        pForm.add(result, BorderLayout.CENTER);
        pForm.setBorder(BorderFactory.createCompoundBorder(
                pForm.getBorder(),
                BorderFactory.createEmptyBorder(15, 25, 25, 25)));
        add(pForm);
        setVisible(true);
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }
}
