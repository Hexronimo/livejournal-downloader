import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
        JRadioButton secondButton = new JRadioButton("Only images");
        firstButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        secondButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        firstButton.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(firstButton);
        group.add(secondButton);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

        panel2.add(lQuantity);
        panel2.add(jQuantity);
        panel2.add(lTooltip2);
        panel2.add(lDownload);
        panel2.add(firstButton);
        panel2.add(secondButton);
        panel3.add(panel2);

        JButton start = new JButton("Start!");
        start.setBorder(BorderFactory.createCompoundBorder(
                start.getBorder(),
                BorderFactory.createEmptyBorder(4, 0, 4, 0))
        );
        start.setBackground(new Color(149, 238, 105, 255));

        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        JTextArea done = new JTextArea();
        done.setBackground(this.getBackground());
        JLabel size = new JLabel();
        result.add(done);
        result.add(size);

        start.addActionListener(e -> {
            done.setText("");
            // validation
            String e1 = normalizeJournalName(jName.getText());
            e1 = normalizeJournalName(e1);
            boolean hasErrors = false;
            if (validateJournalName(e1) != null) {
                done.setText(done.getText() + "\n" + validateJournalName(e1));
                done.setForeground(Color.RED);
                hasErrors = true;
            }
            if (saveDir == null) {
                done.setText(done.getText() + "\nChoose download location first.");
                done.setForeground(Color.RED);
                hasErrors = true;
            }
            String e3 = jQuantity.getText();
            if (validateQuantity(e3) != null) {
                done.setText(done.getText() + "\n" + validateQuantity(e3));
                done.setForeground(Color.RED);
                hasErrors = true;
            }

            if (!hasErrors) {
                // end of the validation
                if (e3 == null || e3.trim().length() == 0) e3 = "-1";
                int q = Integer.parseInt(e3);
                done.setForeground(Color.darkGray);

                ParserConfig.setJournal(e1); // replace with lj url without last slash!
                ParserConfig.setSaveDir(saveDir.toPath()); // replace with dir where you want to save data

                ParserConfig.setQuantity(q); // how much posts you want to save, -1 for all posts
                if (firstButton.isSelected()) {
                    ParserConfig.setWantedResult(0);
                } else if (secondButton.isSelected()) {
                    ParserConfig.setWantedResult(1);
                }
                ParserConfig.requestParse();
                done.setText("Done!");
                size.setText(ParserConfig.requestSize() + " posts successfully downloaded to " + saveDir.getAbsolutePath());
            }
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
        System.out.println("Welcome to Livejournal downloader!");
        System.out.println("Enter 1 - to use GUI, 2 - to use command line");
        System.out.print("> ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String e = br.readLine();
            if (e.equals("1")) {
                MainWindow mainWindow = new MainWindow();
                System.out.println("GUI is loading...");
            } else if (e.equals("2")){
                noGui();
            }
        } catch (IOException e) {}


    }

    public static String normalizeJournalName(String name){
        if (name == null || name.trim().length() == 0) {
            return name; // it will be dropped at validation level
        }
        if (name.endsWith("/")) name = name.substring(0, name.length()-1);
        name.replace("www.", "");
        if(!name.startsWith("https://") && !name.startsWith("http://")){
            name = "https://" + name;
        }
        if (!name.contains(".")) { // because there are premium users with custom url address
            name = name + ".livejournal.com";
        }
        return name;
    }

    public static  String validateJournalName(String name){
        if (name == null || name.trim().length() == 0) {
            return "Journal name can't be empty.";
        }


        try {
            final URL url = new URL(name);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            int responseCode = huc.getResponseCode();

            if (responseCode != 200) {
                return "Unable to find a journal with this name.";
            }
        } catch (Exception e) {
            System.out.println(name);
            e.printStackTrace();
            return "Unable to find a journal with this name.";
        }

        return null;
    }

    public static String validateSaveDir(String dir){
        if (dir == null || dir.trim().length() == 0) {
            return "Directory name can't be empty";
        }
        Path path = Paths.get(dir);
        if (path.toFile().isDirectory()) return null;
        try {
            if (!path.toFile().exists()) Files.createDirectories(path);
        } catch (IOException e) {
            return "Wrong save location.";
        }
        return null;
    }

    public static String validateQuantity(String q){
        if (q ==  null || q.equals("-1") || q.trim().length() == 0) return null;
        int num = 0;
        try {
            num = Integer.parseInt(q);
        } catch (Exception e){
            return "Quantity must be a number.";
        }

        if (num < -1 || num == 0) return "Quantity can't match zero or below.";
        return null;
    }
    public static void noGui () {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            String validation = "";
            String e1 = "";
            while (validation != null) {
                System.out.println("Enter the link to a journal");
                System.out.print("> ");
                e1 = br.readLine();
                e1 = normalizeJournalName(e1);
                validation = validateJournalName(e1);
                if (validation != null) System.out.println(validation);
            }

            validation = "";
            String e2 = "";
            while (validation != null) {
                System.out.println("Enter the full path to the directory where you want to save a journal");
                System.out.print("> ");
                e2 = br.readLine();
                validation = validateSaveDir(e2);
                if (validation != null) System.out.println(validation);
            }

            validation = "";
            String e3 = "";
            while (validation != null) {
                System.out.println("How much entries you want to download? (enter -1 for all)");
                System.out.print("> ");
                e3 = br.readLine();
                validation = validateQuantity(e3);
                if(validation != null) System.out.println(e3);
            }
            System.out.println("Do you want to choose more options?");
            System.out.println("1 - yes, 2 - no (just download full posts as html)");
            String e4 = null;
            while (e4 == null || (!"1".equals(e4.trim()) && !"2".equals(e4.trim()))) {
                System.out.print("> ");
                e4 = br.readLine();
            }

            ParserConfig.setJournal(e1);
            ParserConfig.setSaveDir(Paths.get(e2));
            ParserConfig.setQuantity(Integer.parseInt(e3)); // how much posts you want to save, -1 for all posts

            String e5 = null;
            if (e4.equals("1")) {
                System.out.println("What do you want to download?");
                System.out.println("1 - full posts without <head></head>");
                System.out.println("2 - only images");

                while (e5 == null || (!"1".equals(e5) && !"2".equals(e5))) {
                    System.out.print("> ");
                    e5 = br.readLine();
                }

                if(e5.equals("1")) ParserConfig.setWantedResult(0);
                if(e5.equals("2")) ParserConfig.setWantedResult(1);

            }

            System.out.println("Parsing...");

            ParserConfig.requestParse();
            System.out.println("Done!");
            System.out.println(ParserConfig.requestSize() + " posts successfully downloaded to " + e2);
        } catch (IOException e){}
    }
}
