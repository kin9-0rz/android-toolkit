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
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: lai
 * Date: 5/29/13
 * Time: 10:47 AM
 */
public class APIs extends JPanel {
//    private final JTextField jTextFieldFilePath;
    JTextArea jTextAreaSMS = null;
    JTextArea jTextAreaInternet = null;
    JTextArea jTextAreaShell = null;
    JTextArea jTextAreaPhoneInfo = null;
    JTextArea jTextAreaURI = null;
    /**
     * 暂时用来做测试特殊代码用.
     */
    JTextArea jTextAreaTest = null;


    public APIs() {

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

        final JButton jButtonClearAll = new JButton("Clear");
        add(jButtonClearAll, "4, 3, fill, fill");


        // -------------------------------- Tab -----------------------------------

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, "2, 6, 3, 1, fill, fill");

        final JScrollPane scrollPaneInfos = new JScrollPane();
        tabbedPane.addTab("SMS", null, scrollPaneInfos, null);
        jTextAreaSMS = new JTextArea();
        scrollPaneInfos.setViewportView(jTextAreaSMS);

        final JScrollPane scrollPaneCertificate = new JScrollPane();
        tabbedPane.addTab("Internet", null, scrollPaneCertificate, null);
        jTextAreaInternet = new JTextArea();
        scrollPaneCertificate.setViewportView(jTextAreaInternet);

        final JScrollPane scrollPaneReceivers = new JScrollPane();
        tabbedPane.addTab("PhoneInfo", null, scrollPaneReceivers, null);
        jTextAreaPhoneInfo = new JTextArea();
        scrollPaneReceivers.setViewportView(jTextAreaPhoneInfo);

        final JScrollPane scrollPaneActivities = new JScrollPane();
        tabbedPane.addTab("SHELL", null, scrollPaneActivities, null);
        jTextAreaShell = new JTextArea();
        scrollPaneActivities.setViewportView(jTextAreaShell);

        final JScrollPane scrollPaneServices = new JScrollPane();
        tabbedPane.addTab("URI", null, scrollPaneServices, null);
        jTextAreaURI = new JTextArea();
        scrollPaneServices.setViewportView(jTextAreaURI);

        final JScrollPane scrollPanePermissions = new JScrollPane();
        tabbedPane.addTab("NONE", null, scrollPanePermissions, null);
        jTextAreaTest = new JTextArea();
        scrollPanePermissions.setViewportView(jTextAreaTest);


        // ----------------------------------------------- Event -------------------------------------------------------
//
//        new FileDrop(System.out, jTextFieldFilePath,
//                new FileDrop.Listener() {
//                    @Override
//                    public void filesDropped(File[] files) {
//                        File f = files[0];
//                        if (f.isFile()) {
//                            jTextFieldFilePath.setText(f.getAbsolutePath());
//                        }
//                    }
//                });

//        jButtonPath.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                final JFileChooser fc = new JFileChooser();
//                final int returnVal = fc.showOpenDialog(null);
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    File f = fc.getSelectedFile();
//                    if (f.isFile()) {
//                        jTextFieldFilePath.setText(f.getAbsolutePath());
//                    }
//                }
//            }
//        });


        jButtonAnalysis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final APIsAnalysisTask task = new APIsAnalysisTask(Main.filePath, jTextAreaSMS, jTextAreaInternet,
                        jTextAreaPhoneInfo, jTextAreaShell, jTextAreaURI, jTextAreaTest, jButtonAnalysis);
                task.execute();
                jButtonAnalysis.setEnabled(false);
            }
        });

        jButtonClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextAreaSMS.setText("");
                jTextAreaInternet.setText("");
                jTextAreaShell.setText("");
                jTextAreaPhoneInfo.setText("");
                jTextAreaURI.setText("");
                jTextAreaTest.setText("");
            }
        });
    }
}

class APIsAnalysisTask extends SwingWorker<HashMap<Byte, String>, HashMap<Byte, String>> {


    final Byte FLAG_SMS = 0;
    final Byte FLAG_NETWORK = 1;
    final Byte FLAG_SHELL = 2;
    final Byte FLAG_PHONE_INFO = 3;
    final Byte FLAG_URI = 4;
    final Byte FLAG_TEST = 5;
    final String TAG_API = "api";
    final String ATTR_TYPE = "type";
    final String ATTR_METHOD = "method";
    private final JButton jButtonAnalysis;
    HashMap<Byte, String> behaviorType = new HashMap<>();
    JTextArea jTextAreaSMS = null;
    JTextArea jTextAreaNetwork = null;
    JTextArea jTextAreaShell = null;
    JTextArea jTextAreaPhoneInfos = null;
    JTextArea jTextAreaUri = null;
    JTextArea jTextAreaTest = null;
    String filePath;
    // api configure
    String API_PATH = "conf/apis.xml";
    /**
     * 存放 apis.xml 解析出来的敏感 api  :
     * <code>
     * <pre>
     * { API 类型：api 列表 }
     * </pre>
     * </code>
     * 根据不同的类型，放入不同的标签页中显示
     */
    HashMap<String, ArrayList<String>> apisMap = new HashMap<>();

    public APIsAnalysisTask(String text, JTextArea jTextAreaSMS, JTextArea jTextAreaNetwork,
                            JTextArea jTextAreaPhoneInfos, JTextArea jTextAreaShell,
                            JTextArea jTextAreaUri, JTextArea jTextAreaTest, JButton jButtonAnalysis) {
        this.filePath = text;
        this.jTextAreaSMS = jTextAreaSMS;
        this.jTextAreaNetwork = jTextAreaNetwork;
        this.jTextAreaShell = jTextAreaShell;
        this.jTextAreaPhoneInfos = jTextAreaPhoneInfos;
        this.jTextAreaUri = jTextAreaUri;
        this.jTextAreaTest = jTextAreaTest;
        this.jButtonAnalysis = jButtonAnalysis;


        behaviorType.put(FLAG_SMS, "SMS");
        behaviorType.put(FLAG_NETWORK, "NETWORK");
        behaviorType.put(FLAG_SHELL, "SHELL");
        behaviorType.put(FLAG_PHONE_INFO, "PHONE_INFO");
        behaviorType.put(FLAG_URI, "URI");
        behaviorType.put(FLAG_TEST, "TEST");


        initAPIsXML();

        if (UtilLocal.DEBUG) {
            System.out.println("初始化 apis.xml：");
            for (String key : apisMap.keySet()) {
                System.out.println(key);
                for (String str : apisMap.get(key)) {
                    System.out.println("\t" + str);
                }
            }
        }

    }

    @Override
    public HashMap<Byte, String> doInBackground() {
        HashMap<Byte, String> hashMap = new HashMap<>();

        try {
            APK apk = new APK(filePath);


            HashMap<String, String> methods = apk.getMethods();

            HashMap<String, ArrayList<String>> contentMap = new HashMap<>();
            ArrayList<String> uriList = new ArrayList<>();

            String methodBody;
            for (String key : methods.keySet()) {
                methodBody = methods.get(key);

                for (String type : apisMap.keySet()) {
                    for (String api : apisMap.get(type)) {
                        if (methodBody.contains(api)) {
                            if (contentMap.keySet().contains(type)) {
                                contentMap.get(type).add(key + " [ " + api + " ]\n");
                            } else {
                                ArrayList<String> arrayList = new ArrayList<>();
                                arrayList.add(key + " [ " + api + " ]\n");
                                contentMap.put(type, arrayList);
                            }

                        }
                    }
                }

                // 解析存在的所有的URI
                Pattern pattern = Pattern.compile("[A-Za-z]+://[A-Za-z0-9./?=:&-_%]+", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(methodBody);
                while (matcher.find()) {
                    uriList.add(key + " [ " + matcher.group() + " ]\n");
                }
            }


            if (UtilLocal.DEBUG) {
                System.out.println("匹配的结果：");
                for (String str : contentMap.keySet()) {
                    System.out.println(contentMap.get(str));
                }
            }


            // --------------------------------------------- Display ---------------------------------------------------

            StringBuilder sb = new StringBuilder();
            ArrayList<String> tmpList;

            for (String key : contentMap.keySet()) {
                tmpList = contentMap.get(key);
                Collections.sort(tmpList);
                for (String str : tmpList) {
                    sb.append(str);
                }
                sb.append("\n");

                for (Byte type : behaviorType.keySet()) {
                    if (behaviorType.get(type).equals(key)) {
                        hashMap.put(type, sb.toString());
                        sb.delete(0, sb.length());
                        break;
                    }
                }
            }

            // --------------------  URI  --------------------
            Collections.sort(uriList);
            for (String item : uriList) {
                sb.append(item);
            }
            sb.append("\n");
            hashMap.put(FLAG_URI, sb.toString());
            sb.delete(0, sb.length());

        } catch (Exception e) {
            if (UtilLocal.DEBUG) {
                e.printStackTrace();
            }
        }

        return hashMap;
    }

    /**
     * 初始化敏感 API
     */
    private void initAPIsXML() {
        FileInputStream fileInputStream = null;
        try {

            if (UtilLocal.DEBUG) {
                API_PATH = "/home/lai/Project/android-toolkit/apat/resources/conf/apis.xml";
            }

            fileInputStream = new FileInputStream(API_PATH);
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            final Document xmlDocument = documentBuilder.parse(fileInputStream);
            final Element rootElement = xmlDocument.getDocumentElement();

            for (Node node = rootElement.getFirstChild(); node != null; node = node
                    .getNextSibling()) {
                String nodeName = node.getNodeName();
                if (TAG_API.equals(nodeName)) {
                    parseTag(node, apisMap);
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
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

    /**
     * 对标签进行解析，并将解析后的内容保存在 apisMap 中.
     *
     * @param tag     待解析的标签
     * @param apisMap 存放解析结果的MAP
     */
    private void parseTag(Node tag, HashMap<String, ArrayList<String>> apisMap) {
        NamedNodeMap namedNodeMap = tag.getAttributes();
        String type;
        String mtd;

        Node nodeAttr = namedNodeMap.getNamedItem(ATTR_TYPE);
        if (nodeAttr != null) {
            type = nodeAttr.getNodeValue();
        } else {
            return;
        }

        nodeAttr = namedNodeMap.getNamedItem(ATTR_METHOD);
        if (nodeAttr != null) {
            mtd = nodeAttr.getNodeValue();
        } else {
            return;
        }

        if (apisMap.keySet().contains(type)) {
            apisMap.get(type).add(mtd);
        } else {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(mtd);
            apisMap.put(type, arrayList);
        }
    }

    @Override
    protected void done() {
        try {
            for (Byte key : get().keySet()) {
                if (key.equals(FLAG_SMS)) {
                    jTextAreaSMS.append(get().get(FLAG_SMS));
                } else if (key.equals(FLAG_NETWORK)) {
                    jTextAreaNetwork.append(get().get(FLAG_NETWORK));
                } else if (key.equals(FLAG_SHELL)) {
                    jTextAreaShell.append(get().get(FLAG_SHELL));
                } else if (key.equals(FLAG_PHONE_INFO)) {
                    jTextAreaPhoneInfos.append(get().get(FLAG_PHONE_INFO));
                } else if (key.equals(FLAG_URI)) {
                    jTextAreaUri.append(get().get(FLAG_URI));
                } else if (key.equals(FLAG_TEST)) {
                    jTextAreaTest.append(get().get(FLAG_TEST));
                }
            }

        } catch (InterruptedException e) {
            jTextAreaSMS.append("\nStop!\n");
            if (UtilLocal.DEBUG) {
                e.printStackTrace();
            }
        } catch (ExecutionException e) {
            jTextAreaSMS.append("\nIs it a android package file?\n");
            if (UtilLocal.DEBUG) {
                e.printStackTrace();
            }

        }  finally {
            jButtonAnalysis.setEnabled(true);
        }
    }

}
