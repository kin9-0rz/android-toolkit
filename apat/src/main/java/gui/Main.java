package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import utils.FileDrop;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;


public class Main {
    public static String filePath;
    public JTextField jTextFieldFilePath;
    private JFrame jFrame;

    /**
     * Create the application.
     */
    public Main() {
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final Main window = new Main();
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
        jFrame.setTitle("Android Package Analysing Tool");
        jFrame.setBounds(0, 0, 800, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        jFrame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

        jFrame.getContentPane().setLayout(new BorderLayout(0, 0));

        // 添加分割面板
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(50);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        jFrame.getContentPane().add(splitPane, BorderLayout.CENTER);


        /**
         *     MenuBar
         */
        JMenuBar jMenuBar = new JMenuBar();
        jFrame.setJMenuBar(jMenuBar);


        // ------------------------------------ File Menu -------------------------------
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


        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FormLayout(new ColumnSpec[]{ColumnSpec.decode("11dlu"),
                ColumnSpec.decode("min:grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("100px"), ColumnSpec.decode("5dlu"),},
                new RowSpec[]{RowSpec.decode("15dlu"),
                        RowSpec.decode("20px"), RowSpec.decode("20px"),
                        RowSpec.decode("20px"),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.DEFAULT_ROWSPEC,}));

        jTextFieldFilePath = new JTextField();
//        jFrame.getContentPane().add(jTextFieldFilePath, "2, 2, fill, fill");
        jPanel.add(jTextFieldFilePath, "2, 2, fill, fill");

        // -------------------------------- Button -----------------------------------

        final JButton jButtonPath = new JButton("File");
//        jFrame.getContentPane().add(jButtonPath, "4, 2, fill, fill");
        jPanel.add(jButtonPath, "4, 2, fill, fill");

        splitPane.setTopComponent(jPanel);

        // ----------------------------------------------- Event -------------------------------------------------------

        new FileDrop(System.out, jTextFieldFilePath,
                new FileDrop.Listener() {
                    @Override
                    public void filesDropped(File[] files) {
                        File f = files[0];
                        if (f.isFile()) {
                            filePath = f.getAbsolutePath();
                            jTextFieldFilePath.setText(filePath);
                        }
                    }
                });

        jButtonPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final JFileChooser fc = new JFileChooser();
                final int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    if (f.isFile()) {
                        filePath = f.getAbsolutePath();
                        jTextFieldFilePath.setText(filePath);
                    }
                }
            }
        });

        jTextFieldFilePath.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filePath = jTextFieldFilePath.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filePath = jTextFieldFilePath.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filePath = jTextFieldFilePath.getText();
            }
        });


        /**
         *    TABS
         */
        final JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        splitPane.setBottomComponent(jTabbedPane);
//        jTabbedPane.setSize(400, 400);
//        jFrame.getContentPane().add(jTabbedPane, "2, 4, fill, fill");
//        jFrame.getContentPane().add(jTabbedPane);
//
        jTabbedPane.addTab("APK Infos", null, new APKInfo(), null);
        jTabbedPane.addTab("Pkg List", null, new PkgList(), null);
        jTabbedPane.addTab("APIs", null, new APIs(), null);
        jTabbedPane.addTab("Search Ref", null, new StringSearcher(), null);
        jTabbedPane.addTab("Stings", null, new FeatureCode(), null);
//        jTabbedPane.addTab("CodeView", null, new CodeView(), null);
        jTabbedPane.addTab("Elf Info", null, new ElfInfo(), null);
    }

    static class MenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (event.getActionCommand().equals("Quit")) {
                System.out.println("Quit Now....");
                System.exit(0);
            }

        }
    }
}
