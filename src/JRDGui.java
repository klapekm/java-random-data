import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;
import java.lang.Math;

public class JRDGui extends JFrame implements ActionListener{
    private final JButton generateButton;
    private final JButton clearButton;
    private final JTextField fileSize;
    private final JTextField localization;
    private final JComboBox<String> generators;
    private final JComboBox<String> data_type;
    private final long UPPERBOUND = (long) Math.pow(2, 32);
    private final JFrame helpFrame;
    private final JButton helpButton;
    private JFrame aboutFrame;
    private JButton aboutButton;
    JFrame mainFrame;

    public JRDGui() {
        //Creating the main window
        mainFrame = new JFrame();
        mainFrame.setTitle("Java Random Data");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(515, 305);
        mainFrame.setLocation(50, 50);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.setResizable(false);

        //Creating the toolbar
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy=0;
        g.ipadx = 515;
        JToolBar toolbar = new JToolBar();
        mainFrame.add(toolbar, g);
        toolbar.setFloatable(false);

        //Creating the help window
        helpFrame = new JFrame();
        helpFrame.setTitle("JRD Help");
        helpFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        helpFrame.setSize(500, 100);
        helpFrame.setResizable(false);
        helpFrame.setLayout(new GridBagLayout());

        //Adding the help window text
        JLabel help_label = new JLabel("<html>For information about the program please refer to the README file.</html>");
        helpFrame.add(help_label, c);

        //Creating the about window
        aboutFrame = new JFrame();
        aboutFrame.setTitle("JRD About");
        aboutFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        aboutFrame.setSize(400, 100);
        aboutFrame.setResizable(false);
        aboutFrame.setLayout(new GridBagLayout());

        //Creating the about window text
        JLabel about_label = new JLabel("<html>Simple Random Data Generator<br>© 2026 Stanislaw Miszczak. All Rights Reserved.</html>");
        aboutFrame.add(about_label, c);

        //Creating the field for user input, generate button and such
        g.ipadx = 90;
        g.gridy=1;
        JPanel generateField = new JPanel();
        generateField.setLayout(new GridBagLayout());
        mainFrame.add(generateField, g);

        //Adding the help button to the toolbox
        c.gridx = 0;
        c.gridy = 0;
        helpButton = new JButton("Help");
        helpButton.addActionListener(this);
        toolbar.add(helpButton, c);

        //Adding the about button to the toolbox
        c.gridx = 0;
        c.gridy = 0;
        aboutButton = new JButton("About");
        aboutButton.addActionListener(this);
        toolbar.add(aboutButton, c);

        //Padding to fix a weird bug
        c.ipadx = 45;
        c.ipady = 20;
        c.insets = new Insets(0, 3, 3, 3);

        //Adding an combobox(data type)
        c.gridx = 0;
        c.gridy = 1;
        generateField.add(new JLabel("Data Type"),c);
        data_type = new JComboBox<>(new String[] {"Text", "MB"});
        c.gridx = 1;
        c.gridy = 1;
        generateField.add(data_type,c);

        //Adding an input window(file size)
        c.gridx = 0;
        c.gridy = 2;
        generateField.add(new JLabel("File Size (num_amount/bytes)"), c);
        fileSize = new JTextField();
        fileSize.setText("100");
        fileSize.setPreferredSize(new Dimension(75, 25));
        c.gridx = 1;
        c.gridy = 2;
        generateField.add(fileSize,c);

        //Adding an input window(generator choice)
        c.gridx = 0;
        c.gridy = 3;
        generateField.add(new JLabel("Generator"),c);
        generators = new JComboBox<>();
        c.gridx = 1;
        c.gridy = 3;
        generateField.add(generators,c);

        Provider[] providers = Security.getProviders();
        for (Provider provider: providers) {
            for (Provider.Service service : provider.getServices()) {
                if (service.getType().equals("SecureRandom")) {
                    generators.addItem(service.getAlgorithm());
                }
            }
        }

        //Adding an input window(file path)
        c.gridx = 0;
        c.gridy = 4;
        generateField.add(new JLabel("File Path"), c);
        localization = new JTextField();
        localization.setPreferredSize(new Dimension(75, 25));
        c.gridx = 1;
        c.gridy = 4;
        generateField.add(localization,c);
        //Generate file button
        generateButton = new JButton();
        generateButton.setText("Generate File");
        generateButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 5;
        generateField.add(generateButton,c);

        clearButton = new JButton("Clear");
        c.gridx = 1;
        c.gridy = 5;
        generateField.add(clearButton, c);
        clearButton.addActionListener(this);

        mainFrame.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent a) {
        Object source = a.getSource();
        if (source == generateButton) {
            String loc = localization.getText();
            String size = fileSize.getText();
            SecureRandom rndGen;
            int choice_id = data_type.getSelectedIndex();
            //If the localization input window is empty fill it with an example path
            if (Objects.equals(loc, "")) {
                if (choice_id == 0) {
                    localization.setText(generators.getSelectedItem() + "_sample" + ".txt");
                }
                else {
                    localization.setText(generators.getSelectedItem() + "_sample" + ".bin");
                }
            }
            //If not proceed with generating the file
            else {
                //Setting up the correct algorithm
                try {
                    rndGen = SecureRandom.getInstance((String) Objects.requireNonNull(generators.getSelectedItem()));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                try {
                    //Creating either a bin or txt file based on user choice
                    if (choice_id == 0) {
                        //Opening or creating a file
                        FileWriter myFileWriter = new FileWriter(loc);
                        myFileWriter.flush();
                        //Writing the header
                        myFileWriter.write("#==================================================================\n" +
                                "# generator Java " + generators.getSelectedItem() + "\n" +
                                "#==================================================================\n");
                        myFileWriter.write("type: d\n" + "count: " + size + "\n" + "numbit: 32\n");
                        //Writing the random numbers
                        for (int i = 0; i < (Integer.parseInt(size)); i++) {
                            myFileWriter.write(rndGen.nextLong(UPPERBOUND) + "\n");
                        }
                        myFileWriter.close();
                    } else {
                        //Opening or creating a file
                        DataOutputStream myOutputStream = new DataOutputStream(new FileOutputStream(loc));
                        myOutputStream.flush();
                        //Generating and writing random bytes
                        byte[] my_bytes = new byte[Integer.parseInt(size)*1048576];
                        rndGen.nextBytes(my_bytes);
                        myOutputStream.write(ByteBuffer.wrap(my_bytes).array());
                        myOutputStream.close();
                    }

                } catch (IOException e) {
                    System.out.println("ojojoj");
                }
            }

        }
        if (source == clearButton) {
            localization.setText("");
        } else if (source == helpButton) {
            helpFrame.setLocation(mainFrame.getLocation());
            helpFrame.setVisible(true);
        } else if (source == aboutButton) {
            aboutFrame.setLocation(mainFrame.getLocation());
            aboutFrame.setVisible(true);
        }
    }
}