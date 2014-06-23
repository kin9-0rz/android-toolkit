package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import parser.apk.APK;
import parser.dex.DexClass;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * List Package ...
 */
public class PkgList extends JPanel {
    private JTextArea jTextAreaInfos = null;

    public PkgList() {

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
                        FormFactory.DEFAULT_ROWSPEC,}));


        final JButton jButtonAnalysis = new JButton("Analysis");
        add(jButtonAnalysis, "4, 2, fill, fill");
        jButtonAnalysis.setSize(10 , 10);

        final JButton jButtonClearAll = new JButton("Clear");
        add(jButtonClearAll, "4, 3, fill, fill");


        // -------------------------------- Tab -----------------------------------

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, "2, 6, 3, 1, fill, fill");

        final JScrollPane scrollPaneInfos = new JScrollPane();
        tabbedPane.addTab("Infos", null, scrollPaneInfos, null);
        jTextAreaInfos = new JTextArea();
        scrollPaneInfos.setViewportView(jTextAreaInfos);

        jButtonAnalysis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final PkgListTask task = new PkgListTask(Main.filePath, jTextAreaInfos, jButtonAnalysis);
                task.execute();

                jButtonAnalysis.setEnabled(false);
            }
        });

        jButtonClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextAreaInfos.setText("");
            }
        });
    }

}



class PkgListTask extends SwingWorker<HashMap<Byte, String>, String> {

    private final JButton jButtonAnalysis;
    private JTextArea jTextAreaInfos = null;
    private final String filePath;


    public PkgListTask(String text, JTextArea jTextAreaInfos, JButton jButtonAnalysis) {
        this.filePath = text;
        this.jTextAreaInfos = jTextAreaInfos;
        this.jButtonAnalysis = jButtonAnalysis;

    }


    private HashMap<Byte, String> doIt() throws Exception {
        APK apk = new APK(filePath, true, true, false);

        List<DexClass> dexClasses1 = apk.getDexClasses();
        List<DexClass> dexClasses2 = apk.getDexClasses();
        ArrayList<HashSet<String>> familySet = new ArrayList<>();

        boolean tmpFlag;
        for (DexClass dexClass1 : dexClasses1) {
            tmpFlag = false;
            HashSet<String> tmpFamily = new HashSet<>();
            tmpFamily.add(dexClass1.className);

            for (DexClass dexClass2 : dexClasses2) {
                for (String member : tmpFamily) {
                    if (dexClass2.toString().contains(member)) {
                        tmpFamily.add(dexClass2.className);

                        break;
                    }
                }
            }

            for (HashSet<String> family : familySet) {
                for (String member : tmpFamily) {
                    if (family.contains(member)) {
                        family.addAll(tmpFamily); // 存在共同类，融合
                        tmpFlag = true;
                        break;
                    }
                }

                if (tmpFlag) {
                    break;
                }
            }

            // 与已有的集合比较
            if (!tmpFlag) {
                familySet.add(tmpFamily);
            }
        }

//        for (HashSet<String> family : familySet) {
//            System.out.println(family);
//        }


        publish("[包列表]");
        HashSet<HashSet<String>> groupSet = new HashSet<>();
        for (HashSet<String> family : familySet) {
            tmpFlag = false;

            HashSet<String> newGroup = new HashSet<>();
            for (String member : family) {
                if (member.contains("/")) {
                   int index = member.lastIndexOf("/");
                    newGroup.add(member.substring(0, index));
                } else {
                    newGroup.add("L");
                }
            }

            for (HashSet<String> group : groupSet) {
                for (String member : newGroup) {
                    if (group.contains(member)) {
                        group.addAll(newGroup); // 存在共同类，融合
                        tmpFlag = true;
                        break;
                    }
                }

                if (tmpFlag) {
                    break;
                }
            }

            // 未融合
            if (!tmpFlag) {
                groupSet.add(newGroup);
            }
        }

        for (HashSet<String> group : groupSet) {
            ArrayList<String> arrayList = new ArrayList<>(group);
            Collections.sort(arrayList);
            for (String str : arrayList) {
                publish(str);
            }
            publish("--------------------------------------------------------------");
        }

        publish("==================================================================================================");

        publish("[类列表]");


        for (HashSet<String> family : familySet) {
            ArrayList<String> arrayList = new ArrayList<>(family);
            Collections.sort(arrayList);
            for (String str : arrayList) {
                publish(str);
            }
            publish("--------------------------------------------------------------");

        }


        return null;
    }


    @Override
    public HashMap<Byte, String> doInBackground() throws Exception {
        return doIt();
    }


    @Override
    protected void process(List<String> chunks) {
        for (String str : chunks) {
            jTextAreaInfos.append(str + "\n");
        }
    }



    @Override
    protected void done() {
        jButtonAnalysis.setEnabled(true);
    }
}
