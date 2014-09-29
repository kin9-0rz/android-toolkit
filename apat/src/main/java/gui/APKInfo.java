package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import parser.apk.APK;
import utils.UtilLocal;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: lai
 * Date: 5/29/13
 * Time: 10:47 AM
 */
public class APKInfo extends JPanel {
//    private final JTextField jTextFieldFilePath;
    private JTextArea jTextAreaInfos = null;
    private JTextArea jTextAreaCertificate = null;
    private JTextArea jTextAreaActivities = null;
    private JTextArea jTextAreaReceivers = null;
    private JTextArea jTextAreaServices = null;
    private JTextArea jTextAreaPermissions = null;
    private JTextArea jTextAreaMetaData = null;


    public APKInfo() {

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


//        jTextFieldFilePath = new JTextField();
//        add(jTextFieldFilePath, "2, 2, fill, fill");

        // -------------------------------- Button -----------------------------------

//        final JButton jButtonPath = new JButton("File");
//        add(jButtonPath, "4, 2, fill, fill");

        final JButton jButtonAnalysis = new JButton("Analysis");
        add(jButtonAnalysis, "4, 2, fill, fill");
//        add(jButtonAnalysis, "2, 2, 1, 1");
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

        final JScrollPane scrollPaneCertificate = new JScrollPane();
        tabbedPane.addTab("Certificate", null, scrollPaneCertificate, null);
        jTextAreaCertificate = new JTextArea();
        scrollPaneCertificate.setViewportView(jTextAreaCertificate);

        final JScrollPane scrollPaneActivities = new JScrollPane();
        tabbedPane.addTab("Activities", null, scrollPaneActivities, null);
        jTextAreaActivities = new JTextArea();
        scrollPaneActivities.setViewportView(jTextAreaActivities);

        final JScrollPane scrollPaneReceivers = new JScrollPane();
        tabbedPane.addTab("Receivers", null, scrollPaneReceivers, null);
        jTextAreaReceivers = new JTextArea();
        scrollPaneReceivers.setViewportView(jTextAreaReceivers);

        final JScrollPane scrollPaneServices = new JScrollPane();
        tabbedPane.addTab("Services", null, scrollPaneServices, null);
        jTextAreaServices = new JTextArea();
        scrollPaneServices.setViewportView(jTextAreaServices);

        final JScrollPane scrollPanePermissions = new JScrollPane();
        tabbedPane.addTab("Permissions", null, scrollPanePermissions, null);
        jTextAreaPermissions = new JTextArea();
        scrollPanePermissions.setViewportView(jTextAreaPermissions);

        final JScrollPane scrollPaneMetaData = new JScrollPane();
        tabbedPane.addTab("meta-data", null, scrollPaneMetaData, null);
        jTextAreaMetaData = new JTextArea();
        scrollPaneMetaData.setViewportView(jTextAreaMetaData);


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
                final AnalysisTask task = new AnalysisTask(Main.filePath, jTextAreaInfos, jTextAreaCertificate,
                        jTextAreaActivities, jTextAreaReceivers, jTextAreaServices, jTextAreaPermissions, jTextAreaMetaData , jButtonAnalysis);
                task.execute();

                jButtonAnalysis.setEnabled(false);
            }
        });

        jButtonClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextAreaInfos.setText("");
                jTextAreaCertificate.setText("");
                jTextAreaActivities.setText("");
                jTextAreaReceivers.setText("");
                jTextAreaServices.setText("");
                jTextAreaPermissions.setText("");
                jTextAreaMetaData.setText("");
            }
        });
    }


}

class AnalysisTask extends SwingWorker<HashMap<Byte, String>, HashMap<Byte, String>> {

    private final Byte FLAG_INFOS = 0;
    private final Byte FLAG_CERTIFICATE = 1;
    private final Byte FLAG_ACTIVITIES = 2;
    private final Byte FLAG_RECEIVERS = 3;
    private final Byte FLAG_SERVICES = 4;
    private final Byte FLAG_PERMISSIONS = 5;
    private final Byte FLAG_METADATA = 6;

    private final JButton jButtonAnalysis;
    private JTextArea jTextAreaInfos = null;
    private JTextArea jTextAreaCertificate = null;
    private JTextArea jTextAreaActivities = null;
    private JTextArea jTextAreaReceivers = null;
    private JTextArea jTextAreaServices = null;
    private JTextArea jTextAreaPermissions = null;
    private JTextArea jTextAreaMetaData = null;
    private final String filePath;

    // permission configure
    private String PERMISSION_PATH = "conf/permissions.xml";
    // TODO 增加 intent 分析。
    private String INTENT_PATH = "conf/intents.xml";

    private final HashMap<String, String> permissionsMap = new HashMap<>();
    private final HashMap<String, String> intentsMap = new HashMap<>();

    public AnalysisTask(String text, JTextArea jTextAreaInfos, JTextArea jTextAreaCertificate,
                        JTextArea jTextAreaActivities, JTextArea jTextAreaReceivers,
                        JTextArea jTextAreaServices, JTextArea jTextAreaPermissions,
                        JTextArea jTextAreaMetaData, JButton jButtonAnalysis) {
        this.filePath = text;
        this.jTextAreaInfos = jTextAreaInfos;
        this.jTextAreaCertificate = jTextAreaCertificate;
        this.jTextAreaActivities = jTextAreaActivities;
        this.jTextAreaReceivers = jTextAreaReceivers;
        this.jTextAreaServices = jTextAreaServices;
        this.jTextAreaPermissions = jTextAreaPermissions;
        this.jTextAreaMetaData = jTextAreaMetaData;
        this.jButtonAnalysis = jButtonAnalysis;

        initPermissionXML();
    }


    private void initPermissionXML() {
        FileInputStream fileInputStream = null;
        try {
            if (UtilLocal.DEBUG) {
                PERMISSION_PATH = "./apat/resources/conf/permissions.xml";
            }
            fileInputStream = new FileInputStream(PERMISSION_PATH);
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            final Document xmlDocument = documentBuilder.parse(fileInputStream);
            final Element rootElement = xmlDocument.getDocumentElement();

            for (Node node = rootElement.getFirstChild(); node != null; node = node
                    .getNextSibling()) {
                String nodeName = node.getNodeName();
                String TAG_PERMISSION = "perm";
                if (TAG_PERMISSION.equals(nodeName)) {
                    parseTag(node, permissionsMap);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null)
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void parseTag(Node tag, HashMap<String, String> permissionsMap) {
        NamedNodeMap namedNodeMap = tag.getAttributes();
        String name;
        String desc;

        String ATTR_NAME = "name";
        Node nodeAttr = namedNodeMap.getNamedItem(ATTR_NAME);
        if (nodeAttr != null) {
            name = nodeAttr.getNodeValue();
        } else {
            return;
        }

        String ATTR_DESCRIPTION = "description";
        nodeAttr = namedNodeMap.getNamedItem(ATTR_DESCRIPTION);
        if (nodeAttr != null) {
            desc = nodeAttr.getNodeValue();
        } else {
            return;
        }

        permissionsMap.put(name, desc);

    }

    private HashMap<Byte, String> getAPKInfo() throws Exception {
        HashMap<Byte, String> hashMap = new HashMap<>();
        APK apk = new APK(filePath, true, false, true);

        System.out.println("--------------------->>>>>>>>>");

        StringBuilder sb = new StringBuilder();
        sb.append("FileName : ").append(apk.getFileName()).append("\n");
        sb.append("PackageName : ").append(apk.getPackageName()).append("\n");
        sb.append("Label : ").append(apk.getLabel()).append("\n");
        sb.append("VersionCode : ").append(apk.getVersionCode()).append("\n");
        sb.append("VersionName : ").append(apk.getVersionName()).append("\n\n");
        hashMap.put(FLAG_INFOS, sb.toString());

        sb.delete(0, sb.length());
        if (apk.getCertificateInfos().keySet().size() == 0) {
            hashMap.put(FLAG_CERTIFICATE, "This file has not certificate file.");
        } else {
            for (String key : apk.getCertificateInfos().keySet()) {
                sb.append("MD5 : ").append(key).append("\n");
                sb.append("Sub : ").append(apk.getCertificateInfos().get(key)).append("\n\n");
            }
            hashMap.put(FLAG_CERTIFICATE, sb.toString());
        }

        sb.delete(0, sb.length());
        ArrayList<String> keyList = new ArrayList<>(apk.getActivities().keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            sb.append(key).append("\n");
            for (String action : apk.getActivities().get(key)) {
                sb.append("        ").append(action).append("\n");
            }
        }
        sb.append("\n\n");
        hashMap.put(FLAG_ACTIVITIES, sb.toString());

        keyList.clear();
        keyList = new ArrayList<>(apk.getReceivers().keySet());
        Collections.sort(keyList);
        sb.delete(0, sb.length());
        for (String key : keyList) {
            sb.append(key).append("\n");
            for (String action : apk.getReceivers().get(key)) {
                sb.append("        ").append(action).append("\n");
            }
        }
        sb.append("\n\n");
        hashMap.put(FLAG_RECEIVERS, sb.toString());

        sb.delete(0, sb.length());
        for (String key : apk.getServices()) {
            sb.append(key).append("\n");
        }
        sb.append("\n");
        hashMap.put(FLAG_SERVICES, sb.toString());

        sb.delete(0, sb.length());

        for (String perm : apk.getPermissions()) {
            perm = perm.replace("android.permission.", "");
            sb.append(perm);
            if (permissionsMap.keySet().contains(perm)) {
                sb.append(" [ ").append(permissionsMap.get(perm)).append(" ] ");
            }
            sb.append("\n");

        }
        sb.append("\n");
        hashMap.put(FLAG_PERMISSIONS, sb.toString());


        sb.delete(0, sb.length());
        for (String key : apk.getMetaDatas().keySet()) {
            sb.append(key).append(" : ").append(apk.getMetaDatas().get(key)).append("\n");
        }
        sb.append("\n");
        hashMap.put(FLAG_METADATA, sb.toString());

        return hashMap;
    }


    @Override
    public HashMap<Byte, String> doInBackground() throws Exception {
        return getAPKInfo();
    }

    @Override
    protected void done() {
        try {
            for (Byte key : get().keySet()) {
                if (key.equals(FLAG_INFOS)) {
                    jTextAreaInfos.append(get().get(FLAG_INFOS));
                } else if (key.equals(FLAG_CERTIFICATE)) {
                    jTextAreaCertificate.append(get().get(FLAG_CERTIFICATE));
                } else if (key.equals(FLAG_ACTIVITIES)) {
                    jTextAreaActivities.append(get().get(FLAG_ACTIVITIES));
                } else if (key.equals(FLAG_RECEIVERS)) {
                    jTextAreaReceivers.append(get().get(FLAG_RECEIVERS));
                } else if (key.equals(FLAG_SERVICES)) {
                    jTextAreaServices.append(get().get(FLAG_SERVICES));
                } else if (key.equals(FLAG_PERMISSIONS)) {
                    jTextAreaPermissions.append(get().get(FLAG_PERMISSIONS));
                }else if (key.equals(FLAG_METADATA)) {
                    jTextAreaMetaData.append(get().get(FLAG_METADATA));
                }
            }

        } catch (InterruptedException e) {
            jTextAreaInfos.append("\nStop!\n");
        } catch (Exception e) {
            jTextAreaInfos.append(e.getMessage() + "\n\n");
        } finally {
            jButtonAnalysis.setEnabled(true);
        }
    }
}
