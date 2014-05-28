package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class DelDetectorMain {
    private JFrame jFrame;

    /**
     * Create the application.
     */
    public DelDetectorMain() {
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final DelDetectorMain window = new DelDetectorMain();
                    window.jFrame.setVisible(true);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initialize the contents of the jFrame.
     */
    private void initialize() {
        /**
         *    Frame
         */
        jFrame = new JFrame();
        jFrame.setTitle("Malware Detector");
        jFrame.setBounds(0, 0, 800, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));


        /**
         *     MenuBar
         */
        JMenuBar jMenuBar = new JMenuBar();
        jFrame.setJMenuBar(jMenuBar);


        // ------------------------------- File Menu -----------------------------------
        JMenu jMenuFile = new JMenu("File");
        JMenu jMenuAbout = new JMenu("About");
        jMenuBar.add(jMenuFile);
        jMenuBar.add(jMenuAbout);

        // ------------------------------- File Menu Item -------------------------------
        JMenuItem jMenuItemSetting = new JMenuItem("Setting");
        jMenuFile.add(jMenuItemSetting);

        JMenuItem jMenuItemQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
        jMenuFile.add(jMenuItemQuit);
        KeyStroke keyStrokeCtrlQ = KeyStroke.getKeyStroke("control Q");
        jMenuItemQuit.setAccelerator(keyStrokeCtrlQ);

        // ------------------------------- File Menu Item Action -------------------------
        MenuActionListener menuListener = new MenuActionListener();
        jMenuItemQuit.addActionListener(menuListener);


        /**
         *    TABS
         */
        final JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        jFrame.getContentPane().add(jTabbedPane);

        jTabbedPane.addTab("Detector", null, new DetectorGUI(), null);
        jTabbedPane.addTab("APP DataBase", null, new AppDataBaseGUI(), null);
    }

    static class MenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (event.getActionCommand().equals("Quit")) {
                System.exit(0);
            }

        }
    }
}
