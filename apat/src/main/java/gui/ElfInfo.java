package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import parser.elf.Elf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lai
 * Date: 5/29/13
 * Time: 10:47 AM
 */
public class ElfInfo extends JPanel {
    //    private final JTextField jTextFieldFilePath;
    private JTextArea jTextAreaStrings = null;
    private JTextArea jTextAreaExpFuns = null;
    private JTextArea jTextAreaImpFuns = null;
    private JTextArea jTextAreaImpLibs = null;
    private JTextArea jTextAreaSyms = null;
    private JTextArea jTextAreaPermissions = null;


    public ElfInfo() {

        // ----------------------------------------------- Layout ------------------------------------------------------

        setLayout(new FormLayout(new ColumnSpec[]{ColumnSpec.decode("11dlu"),
                ColumnSpec.decode("min:grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("100px"), ColumnSpec.decode("10dlu"),},
                new RowSpec[]{RowSpec.decode("15dlu"),
                        RowSpec.decode("23px"), RowSpec.decode("21px"),
                        RowSpec.decode("23px"),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.DEFAULT_ROWSPEC,}
        ));


//        jTextFieldFilePath = new JTextField();
//        add(jTextFieldFilePath, "2, 2, fill, fill");

        // -------------------------------- Button -----------------------------------

//        final JButton jButtonPath = new JButton("File");
//        add(jButtonPath, "4, 2, fill, fill");

        final JButton jButtonAnalysis = new JButton("Analysis");
        add(jButtonAnalysis, "4, 2, fill, fill");
//        add(jButtonAnalysis, "2, 2, 1, 1");
        jButtonAnalysis.setSize(10, 10);

        final JButton jButtonClearAll = new JButton("Clear");
        add(jButtonClearAll, "4, 3, fill, fill");


        // -------------------------------- Tab -----------------------------------

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, "2, 6, 3, 1, fill, fill");

        final JScrollPane scrollPaneInfos = new JScrollPane();
        tabbedPane.addTab("Strings", null, scrollPaneInfos, null);
        jTextAreaStrings = new JTextArea();
        scrollPaneInfos.setViewportView(jTextAreaStrings);

        final JScrollPane scrollPaneCertificate = new JScrollPane();
        tabbedPane.addTab("ExpFuns", null, scrollPaneCertificate, null);
        jTextAreaExpFuns = new JTextArea();
        scrollPaneCertificate.setViewportView(jTextAreaExpFuns);

        final JScrollPane scrollPaneActivities = new JScrollPane();
        tabbedPane.addTab("ImpFuns", null, scrollPaneActivities, null);
        jTextAreaImpFuns = new JTextArea();
        scrollPaneActivities.setViewportView(jTextAreaImpFuns);

        final JScrollPane scrollPaneReceivers = new JScrollPane();
        tabbedPane.addTab("ImpLibs", null, scrollPaneReceivers, null);
        jTextAreaImpLibs = new JTextArea();
        scrollPaneReceivers.setViewportView(jTextAreaImpLibs);

        final JScrollPane scrollPaneServices = new JScrollPane();
        tabbedPane.addTab("Symbols", null, scrollPaneServices, null);
        jTextAreaSyms = new JTextArea();
        scrollPaneServices.setViewportView(jTextAreaSyms);

        // ----------------------------------------------- Event -------------------------------------------------------
/*
        new FileDrop(System.out, jTextFieldFilePath,
                new FileDrop.Listener() {
                    @Override
                    public void filesDropped(java.io.File[] files) {
                        File f = files[0];
                        if (f.isFile()) {
                            jTextFieldFilePath.setText(f.getAbsolutePath());
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
                        jTextFieldFilePath.setText(f.getAbsolutePath());
                    }
                }
            }
        });*/


        jButtonAnalysis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Task task = new Task(Main.filePath, jTextAreaStrings, jTextAreaExpFuns,
                        jTextAreaImpFuns, jTextAreaImpLibs, jTextAreaSyms, jButtonAnalysis);
                task.execute();

                jButtonAnalysis.setEnabled(false);
            }
        });

        jButtonClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextAreaStrings.setText("");
                jTextAreaExpFuns.setText("");
                jTextAreaImpFuns.setText("");
                jTextAreaImpLibs.setText("");
                jTextAreaSyms.setText("");
            }
        });
    }


}

class Task extends SwingWorker<HashMap<Byte, String>, HashMap<Byte, String>> {

    private final Byte FLAG_STRINGS = 0;
    private final Byte FLAG_EXPORT_FUNCTIONS = 1;
    private final Byte FLAG_IMPORT_FUNCTIONS = 2;
    private final Byte FLAG_IMPORT_LIBS = 3;
    private final Byte FLAG_SYMBOLS = 4;
    private final JButton jButtonAnalysis;
    private final Elf elf;
    private JTextArea jTextAreaStrings = null;
    private JTextArea jTextAreaExpFuns = null;
    private JTextArea jTextAreaImpFuns = null;
    private JTextArea jTextAreaImpLibs = null;
    private JTextArea jTextAreaSyms = null;

    public Task(String text, JTextArea jTextAreaStrings, JTextArea jTextAreaExpFuns,
                JTextArea jTextAreaImpFuncs, JTextArea jTextAreaImpLibs,
                JTextArea jTextAreaSymbols, JButton jButtonAnalysis) {
        this.jTextAreaStrings = jTextAreaStrings;
        this.jTextAreaExpFuns = jTextAreaExpFuns;
        this.jTextAreaImpFuns = jTextAreaImpFuncs;
        this.jTextAreaImpLibs = jTextAreaImpLibs;
        this.jTextAreaSyms = jTextAreaSymbols;
        this.jButtonAnalysis = jButtonAnalysis;

        elf = new Elf(new File(text));
    }

    private HashMap<Byte, String> getAPKInfo() throws Exception {
        HashMap<Byte, String> hashMap = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        List<String> list = elf.loadStrings();
        Collections.sort(list);
        for (String s : list) {
            sb.append(s.trim()).append("|\n");
        }
        hashMap.put(FLAG_STRINGS, sb.toString());

        sb.delete(0, sb.length());
        list = elf.getExportFunctions();
        Collections.sort(list);
        for (String s : list) {
            sb.append(s).append("\n");
        }
        hashMap.put(FLAG_EXPORT_FUNCTIONS, sb.toString());

        sb.delete(0, sb.length());
        list = elf.getImpFunctions();
        Collections.sort(list);
        for (String s : list) {
            sb.append(s).append("\n");
        }
        sb.append("\n\n");
        hashMap.put(FLAG_IMPORT_FUNCTIONS, sb.toString());

        sb.delete(0, sb.length());
        list = elf.getImpLib();
        Collections.sort(list);
        for (String s : list) {
            sb.append(s).append("\n");
        }
        sb.append("\n\n");
        hashMap.put(FLAG_IMPORT_LIBS, sb.toString());

        sb.delete(0, sb.length());
        list = elf.getSymbols();
        Collections.sort(list);
        for (String s : list) {
            sb.append(s).append("\n");
        }
        sb.append("\n");
        hashMap.put(FLAG_SYMBOLS, sb.toString());

        return hashMap;
    }


    @Override
    public HashMap<Byte, String> doInBackground() throws Exception {
        return getAPKInfo();
    }

    @Override
    protected void process(List<HashMap<Byte, String>> chunks) {

//        for (HashMap chunk : chunks) {
//
//            if (key.equals(FLAG_STRINGS)) {
//                jTextAreaStrings.append(chunks.get(FLAG_STRINGS));
//            } else if (key.equals(FLAG_EXPORT_FUNCTIONS)) {
//                jTextAreaExpFuns.append(get().get(FLAG_EXPORT_FUNCTIONS));
//            } else if (key.equals(FLAG_IMPORT_FUNCTIONS)) {
//                jTextAreaImpFuns.append(get().get(FLAG_IMPORT_FUNCTIONS));
//            } else if (key.equals(FLAG_IMPORT_LIBS)) {
//                jTextAreaImpLibs.append(get().get(FLAG_IMPORT_LIBS));
//            } else if (key.equals(FLAG_SYMBOLS)) {
//                jTextAreaSyms.append(get().get(FLAG_SYMBOLS));
//            }
//        }
    }



    @Override
    protected void done() {
        try {
            for (Byte key : get().keySet()) {
                if (key.equals(FLAG_STRINGS)) {
                    jTextAreaStrings.append(get().get(FLAG_STRINGS));
                } else if (key.equals(FLAG_EXPORT_FUNCTIONS)) {
                    jTextAreaExpFuns.append(get().get(FLAG_EXPORT_FUNCTIONS));
                } else if (key.equals(FLAG_IMPORT_FUNCTIONS)) {
                    jTextAreaImpFuns.append(get().get(FLAG_IMPORT_FUNCTIONS));
                } else if (key.equals(FLAG_IMPORT_LIBS)) {
                    jTextAreaImpLibs.append(get().get(FLAG_IMPORT_LIBS));
                } else if (key.equals(FLAG_SYMBOLS)) {
                    jTextAreaSyms.append(get().get(FLAG_SYMBOLS));
                }
            }

        } catch (InterruptedException e) {
            jTextAreaStrings.append("\nStop!\n");
        } catch (Exception e) {
            jTextAreaStrings.append(e.getMessage() + "\n\n");
        } finally {
            jButtonAnalysis.setEnabled(true);
        }
    }
}
