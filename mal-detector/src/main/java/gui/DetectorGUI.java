package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import org.apache.commons.io.FileUtils;
import parser.apk.APK;
import parser.elf.Elf;
import parser.utils.FileTypesDetector;
import parser.utils.HashTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lai
 * Date: 5/29/13
 * Time: 10:47 AM
 */
public class DetectorGUI extends JPanel {
    private final JTextField filePath;
    private JTextArea jTextAreaInfos = null;
    private JTextArea jTextAreaCertificate = null;
    private JTextArea jTextAreaActivities = null;
    private JTextArea jTextAreaReceivers = null;
    private JTextArea jTextAreaServices = null;
    private JTextArea jTextAreaPermissions = null;


    public DetectorGUI() {

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


        filePath = new JTextField();
        filePath.setText("/home/lai/Work/samples/Test/Trojan-SMS.AndroidOS.Raden/");
        add(filePath, "2, 2, fill, fill");

        // -------------------------------- Button -----------------------------------

        final JButton jButtonFile = new JButton("File");
        add(jButtonFile, "4, 2, fill, fill");

        final JButton jButtonAnalysis = new JButton("Analysis");
        add(jButtonAnalysis, "4, 3, fill, fill");

        final JButton jButtonClearAll = new JButton("Clear");
        add(jButtonClearAll, "4, 4, fill, fill");


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


        // ----------------------------------------------- Event -------------------------------------------------------

        new FileDrop(System.out, filePath,
                new FileDrop.Listener() {
                    @Override
                    public void filesDropped(File[] files) {
                        File f = files[0];
                        filePath.setText(f.getAbsolutePath());
                    }
                }
        );

        jButtonFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final JFileChooser fc = new JFileChooser();
                final int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filePath.setText(fc.getSelectedFile().getAbsolutePath());
                }
            }
        });


        jButtonAnalysis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final AnalysisTask task = new AnalysisTask(filePath.getText(), jTextAreaInfos, jTextAreaCertificate,
                        jTextAreaActivities, jTextAreaReceivers, jTextAreaServices, jTextAreaPermissions, jButtonAnalysis);
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
            }
        });
    }


}

class AnalysisTask extends SwingWorker<HashMap<Byte, String>, String> {

    private final Byte FLAG_INFOS = 0;
    private final Byte FLAG_CERTIFICATE = 1;
    private final Byte FLAG_ACTIVITIES = 2;
    private final Byte FLAG_RECEIVERS = 3;
    private final Byte FLAG_SERVICES = 4;
    private final Byte FLAG_PERMISSIONS = 5;
    private final JButton jButtonAnalysis;
    private final String filePath;
    private final HashMap<String, String> permissionsMap = new HashMap<>();
    private final Connection conn;
    private JTextArea jTextAreaInfos = null;
    private JTextArea jTextAreaCertificate = null;
    private JTextArea jTextAreaActivities = null;
    private JTextArea jTextAreaReceivers = null;
    private JTextArea jTextAreaServices = null;
    private JTextArea jTextAreaPermissions = null;
    // permission configure
    private String PERMISSION_PATH = "conf/permissions.xml";


    public AnalysisTask(String text, JTextArea jTextAreaInfos, JTextArea jTextAreaCertificate,
                        JTextArea jTextAreaActivities, JTextArea jTextAreaReceivers,
                        JTextArea jTextAreaServices, JTextArea jTextAreaPermissions, JButton jButtonAnalysis) {
        this.filePath = text;
        this.jTextAreaInfos = jTextAreaInfos;
        this.jTextAreaCertificate = jTextAreaCertificate;
        this.jTextAreaActivities = jTextAreaActivities;
        this.jTextAreaReceivers = jTextAreaReceivers;
        this.jTextAreaServices = jTextAreaServices;
        this.jTextAreaPermissions = jTextAreaPermissions;
        this.jButtonAnalysis = jButtonAnalysis;

        conn = prepareDb();

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
            sb.append(perm);
            if (permissionsMap.keySet().contains(perm)) {
                sb.append(" [ ").append(permissionsMap.get(perm)).append(" ] ");
            }
            sb.append("\n");

        }
        sb.append("\n");
        hashMap.put(FLAG_PERMISSIONS, sb.toString());

        return hashMap;
    }


    @Override
    public HashMap<Byte, String> doInBackground() throws Exception {

        final File file = new File(filePath);

        if (file.isFile()) {
            analysis(file);
            return null;
        } else if (file.isDirectory()) {
            final Collection<File> files = FileUtils.listFiles(file, null, true);
            publish("总" + files.size());
            for (final File f : files) {
                analysis(f);
                publish("———————————————————————————————————————————————————————————————————————————————————————————————————————————");
            }
        }

        return null;
    }

    private void analysisAXML() {

    }


    /**
     * 对非 APK 文件的检测逻辑。
     *
     * @param f 文件
     * @throws IOException
     */
    private String analysisOtherFile(File f) throws IOException {
        String str;
        String fileHash = HashTool.getSHA256(f);
        // 如果是 ELF 文件，则优先查询 ELF 表
        if (FileTypesDetector.getType(new FileInputStream(f)).contains("ELF")) {
            str = querySampleByElfSHA256(fileHash);
            if (str != null) {
                return str;
            }

            Elf elf = new Elf(f);
            List<String> list = elf.loadStrings();
            for (String item : list) {
                str = querySampleByElfFeatureCode(item);
                if (str != null) {
                    return str;
                }
            }

            return "Not Found.";
        }

        // ELF表找不到，或其他文件，则找子文件字段
        str = querySampleBySubFileSHA256(fileHash);
        if (str != null) {
            return str;
        } else {
            return "Not Found.";
        }
    }

    /**
     * 对于子包，也需要做同样的动作。
     *
     * @param apk
     */
    private void analysisSubAPK(APK apk) {
        publish("分析子包：" + apk.getFileName());
    }

    private void analysis(File f) throws IOException {
        publish("分析：" + f.getAbsolutePath());

        /*****************************************************
         *
         *                  非APK文件分析逻辑
         *
         *****************************************************/

        if (!FileTypesDetector.isAPK(f)) {
            publish(analysisOtherFile(f));
            publish("———————————————————————————————————————————————————————————————————————————————————————————————————————————");
            return;
        }


        /*****************************************************
         *
         *                  APK文件样本检测逻辑
         *
         *****************************************************/

        String tmpStr = querySampleByFileSHA256(HashTool.getSHA256(f));
        if (tmpStr != null) {
            publish("结果：" + tmpStr);
            publish("———————————————————————————————————————————————————————————————————————————————————————————————————————————");
            return;
        }


        /**************************************************************
         *
         *                      基于已知样本的近似分析
         *          （dex-sha256、证书+清单、特征码、敏感子文件、elf文件）
         *
         *×************************************************************/
        boolean flag;

        APK apk = new APK(f);

        tmpStr = querySampleByDexSHA256(apk.getDexSHA256());
        if (tmpStr != null) {
            publish("结果：" + tmpStr);
            publish("———————————————————————————————————————————————————————————————————————————————————————————————————————————");
            return;
        }

        // 基于证书的样本近似分析
        // FIXME 找出证书一样的样本，然后，找出清单近似的样本。（可能需要持续完善）
        for (String key : apk.getCertificateInfos().keySet()) {
            ArrayList<SampleInfo> sampleInfos = querySampleByCertMD5(key);
            if (sampleInfos != null) {
                for (SampleInfo sampleInfo : sampleInfos) {
                    StringBuilder stringBuilder = new StringBuilder("cert|");
                    StringBuilder likeBuilder = new StringBuilder();

                    // 证书+包名
                    if (apk.getPackageName().equals(sampleInfo.pkgName)) {
                        publish("结果：" + "CP-" + sampleInfo.varName + "|" + sampleInfo.fileName);
                        return;
                    }

                    // 证书+应用名
                    if (apk.getLabel().equals(sampleInfo.label)) {
                        publish("结果：" + "CL-" + sampleInfo.varName + "|" + sampleInfo.fileName);
                        return;
                    }


                    // TODO 证书+权限+接收器+服务+Intent ———— 这个还不足以判断近似，现阶段 ————但是因为证书的存在，噪音会小很多吧，得测试。
                    // FIXME 权限——敏感命中情况（也许需要加强） ————这个会干扰？
                    // FIXME Perm:Rec:Serv:Intent 0.2:0.3:0.3:0.2
                    double value = 0.5;
                    final double PERM_WEIGHT = 0.3;
                    final double RECV_WEIGHT = 0.2;
                    final double SERV_WEIGHT = 0.2;
                    final double INTE_WEIGHT = 0.2;

                    int count = 0;
                    likeBuilder.append("Perms:");
                    for (String perm : apk.getPermissions()) {
                        if (sampleInfo.permissions.contains(perm)) {
                            likeBuilder.append(perm).append("|");
                            count++;
                        }
                    }

                    int sum = apk.getPermissions().size();
                    likeBuilder.append("\n");
                    stringBuilder.append(count).append("/").
                            append(sum).append("Perms|");
                    if (sum != 0) {
                        value += (float) count / (float) sum * PERM_WEIGHT;
                    }

                    // 服务
                    count = 0;
                    likeBuilder.append("Services:");
                    for (String service : apk.getServices()) {
                        int lastIndex = service.lastIndexOf(".");

                        String str = service;
                        if (lastIndex != -1) {
                            int len = service.length();
                            str = service.substring(lastIndex, len);
                        }

                        if (sampleInfo.services.contains(str)) {
                            likeBuilder.append(str).append("|");
                            count++;
                        }
                    }
                    likeBuilder.append("\n");
                    sum = apk.getServices().size();
                    stringBuilder.append(count).append("/").
                            append(sum).append("services|");
                    if (sum != 0) {
                        value += (float) count / (float) sum * SERV_WEIGHT;
                    }


                    // 接收器/intents
                    int recCount = 0;
                    int recs = 0;
                    int intents = 0;

                    count = 0;
                    for (String rec : apk.getReceivers().keySet()) {
                        recs++;
                        if (sampleInfo.receivers.contains(rec)) {
                            likeBuilder.append("Receivers:").append(rec).append("|").append("\n");
                            recCount++;
                        }

                        for (String intent : apk.getReceivers().get(rec)) {
                            intents++;
                            if (sampleInfo.receivers.contains(intent)) {
                                likeBuilder.append("Intent:").append(intent).append("|");
                                count++;
                            }
                        }
                        likeBuilder.append("\n");

                    }

                    stringBuilder.append(recCount).append("/").
                            append(recs).append("receivers|");

                    stringBuilder.append(count).append("/").
                            append(intents).append("intents|");

//                    publish(apk.toString());
//                    publish(sampleInfo.receivers);
                    if (recs != 0) {
                        value += (float) recCount / (float) recs * RECV_WEIGHT;
                    } else if (sampleInfo.receivers.length() == 0) {
                        value += RECV_WEIGHT;
                    }

                    if (intents != 0) {
                        value += (float) count / (float) intents * INTE_WEIGHT;
                    }

//                    publish("最终值==" + String.valueOf(value));
                    publish("最终值==" + String.valueOf((int)(value * 100)));
//                    publish(stringBuilder.toString());

                    if ((int) (value * 100) >= 70) {
                        publish("结果：" + "CPRSI-" + sampleInfo.varName + "|" + sampleInfo.fileName);
                        publish(stringBuilder.toString());
                        publish(sampleInfo.varName + "|" + sampleInfo.fileName);
                        publish(sampleInfo.toString() + "\n");
                        publish(likeBuilder.toString() + "\n");
                        return;
                    }
                }
            }
        }


        for (String str : apk.getStrings()) {
            String result = querySampleByFeatureCode(str);
            if (result != null) {
                publish(result + "\n");
                publish("———————————————————————————————————————————————————————————————————————————————————————————————————————————");
                return;
            }
        }


        /** 敏感子文件(非elf/APK) Hash 查询 */
        flag = false;
        HashMap<String, String> subFileHash256Map = apk.getSubFileHash256Map();
        for (String key : subFileHash256Map.keySet()) {
            String r = querySampleBySubFileSHA256(subFileHash256Map.get(key));
            if (r != null) {
                publish("子文件命中：" + key);
                publish(r);
                flag = true;
                break;
            }
        }


        if (flag) {
            publish("\n********************************************************************\n");
            return;
        }


        /**
         * elf 文件近似分析，若存在恶意 elf 类型文件
         */
        HashMap<String, APK.ElfData> elfHashMap = apk.getElfDataHashMap();
        for (String key : elfHashMap.keySet()) {
            String str = querySampleByElfSHA256(elfHashMap.get(key).getHash());
            if (str != null) {
                publish("ELF 哈希命中：" + key);
                publish(str + "\n");
                continue;
            }
            for (String fc : elfHashMap.get(key).getStringList()) {
                str = querySampleByElfFeatureCode(fc);
                if (str != null) {
                    publish("ELF 特征命中：" + key);
                    publish(str + "\n");
                    break;
                }
            }
        }

        HashMap<String, APK> apkHashMap = apk.getSubApkDataMap();
        for (String name : apkHashMap.keySet()) {
//            analysis(apkHashMap.get(name));
        }

        /**
         *  未知恶意软件分析-HRUE
         */

        // 敏感权限
        ArrayList<String> apkPermissions = apk.getPermissions();
        HashSet<String> perms = new HashSet<>();
        for (String str : apkPermissions) {
            if (str.contains("SMS")) {
                perms.add(str);
                continue;
            }

            if (str.contains("INTERNET")) {
                perms.add(str);
                continue;
            }

            if (str.contains("BOOT_COMPLETED")) {
                perms.add(str);
                continue;
            }

            if (str.contains("PHONE")) {
                perms.add(str);
            }
        }

        publish("敏感权限：" + perms.toString());


        publish("未知恶意软件分析-HRUE");
        HashMap<String, String> methods = apk.getMethods();
        HashSet<String> methodSet = new HashSet<>();
        HashSet<String> callOnSet = new HashSet<>();

        String systemApp = "/system";
        searchCallOn(systemApp, methods, methodSet, callOnSet, 0);
        if (!callOnSet.isEmpty()) {
            publish(systemApp + callOnSet.toString());
        }

        callOnSet.clear();
        methodSet.clear();
        searchCallOn("content://sms", methods, methodSet, callOnSet, 0);
        if (!callOnSet.isEmpty()) {
            publish("content://sms" + callOnSet.toString());
        }

        callOnSet.clear();
        methodSet.clear();
        searchCallOn("SmsManager;.sendTextMessage", methods, methodSet, callOnSet, 0);
        if (!callOnSet.isEmpty()) {
            publish("SmsManager;.sendTextMessage" + callOnSet.toString());
        }

        callOnSet.clear();
        methodSet.clear();
        searchCallOn(";.abortBroadcast()V", methods, methodSet, callOnSet, 0);
        if (!callOnSet.isEmpty()) {
            publish(";.abortBroadcast()V" + callOnSet.toString());
        }

        methodSet.clear();
        callOnSet.clear();
        searchCallOn("TelephonyManager;.getDeviceId", methods, methodSet, callOnSet, 0);
        if (!callOnSet.isEmpty()) {
            publish("TelephonyManager;.getDeviceId" + callOnSet.toString());
        }

        callOnSet.clear();
        methodSet.clear();
        searchCallOn("URL;.openConnection", methods, methodSet, callOnSet, 0);
        if (!callOnSet.isEmpty()) {
            publish("URL;.openConnection" + callOnSet.toString());
        }

        callOnSet.clear();
        methodSet.clear();
        searchCallOn("Ljava/util/Timer;.schedule", methods, methodSet, callOnSet, 0);
        if (!callOnSet.isEmpty()) {
            publish("Ljava/util/Timer;.schedule" + callOnSet.toString());
        }

    }

    private void searchCallOn(String method, HashMap<String, String> methods,
                              HashSet<String> methodSet, HashSet<String> callOnSet, int i) {

        if (methodSet.contains(method)) {
            return;
        }

        methodSet.add(method);

        String space = "";
        for (int x = 0; x < i; x++) {
            space += "-";
        }

        System.out.println(space + method);
        i++;
        boolean flag = true;
        for (String key : methods.keySet()) {
            String methodBody = methods.get(key);
            if (methodBody.contains(method)) {
//                if (key.contains(".on")) {
//                    callOnSet.add(key);
//                    System.out.println("+" + space + key);
//                    continue;
//                }
//
//                if (key.contains("handleMessage")) {
//                    System.out.println(space + key);
//                    key = "Handler;.send";
//                }

                if (key.contains("$") && key.contains(";.run()V")) {
                    searchCallOn(key, methods, methodSet, callOnSet, i);
                    key = key.split(";.")[0] + ";.<init>";
                    searchCallOn(key, methods, methodSet, callOnSet, i);
                    key = key.split(";.")[0] + ";.start";
                    searchCallOn(key, methods, methodSet, callOnSet, i);
                    continue;
                }

                searchCallOn(key, methods, methodSet, callOnSet, i);
                flag = false;
            }
        }

        if (flag && i > 1) {
            callOnSet.add(method);
        }
    }


    private String sqliteEscape(String str) {
        str = str.replace("/", "//");
        str = str.replace("'", "''");
        str = str.replace("\"", "/\"");
        str = str.replace("[", "/[");
        str = str.replace("]", "/]");
        str = str.replace("%", "/%");
        str = str.replace("&", "/&");
        str = str.replace("_", "/_");
        str = str.replace(")", "/)");
        str = str.replace("(", "/(");
        return str.trim();
    }


    private ArrayList<String> str2ArrayList(String str) {
        String tmp = str.replace("[", "").replace("]", "");
        String[] strings = tmp.split(", ");
        return new ArrayList<>(Arrays.asList(strings));
    }

    @Override
    protected void process(List<String> chunks) {
        for (String str : chunks) {
            if (str.contains("[LOG]")) {
                jTextAreaCertificate.append(str + "\n");
                continue;
            }
            jTextAreaInfos.append(str + "\n");
        }
    }

    @Override
    protected void done() {
        publish("处理完毕");
        jButtonAnalysis.setEnabled(true);
    }


    /********************************************************************
     *                          数据库操作等函数
     *********************************************************************/

    /**
     * prepare database.
     *
     * @return Connection
     */
    private Connection prepareDb() {
        Connection conn = null;
        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:samples.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * 查询特征码库
     *
     * @param featureCode 特征码
     * @return 查询结果
     */
    private String querySampleByFeatureCode(String featureCode) {
        featureCode = sqliteEscape(featureCode);

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "select * from sample_info where feature_code like '" + featureCode + "' escape '/'");
            if (resultSet.next()) {
                return resultSet.getString("file_name") + "|" + resultSet.getString("var_name")
                        + "|" + resultSet.getString("feature_code");
            }
        } catch (SQLException e) {
            System.out.println("querySampleByFeatureCode 异常, 存在特殊字符：" + featureCode);
            e.printStackTrace();
        }

        return null;
    }

    private String querySampleBySubFileSHA256(String sub_file_hash256) {
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "select * from sample_info where sub_file_sha256 like \"%" + sub_file_hash256 + "%\"");
            if (resultSet.next()) {
                return resultSet.getString("var_name") + "|"
                        + resultSet.getString("file_name");
            }
        } catch (SQLException e) {
            System.out.println("语法错误：" + sub_file_hash256);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询样本 SHA256
     *
     * @param fileSHA256 文件sha256
     * @return 查询结果
     */
    private String querySampleByFileSHA256(String fileSHA256) {
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from sample_info where file_sha256 like \"%" + fileSHA256 + "%\"");
            if (resultSet.next()) {
                return resultSet.getString("var_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * DEX sha256 查询
     *
     * @param dexSHA256 dex sha256
     * @return 查询结果
     */
    private String querySampleByDexSHA256(String dexSHA256) {
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from sample_info where dex_sha256 like \"%" + dexSHA256 + "%\"");
            if (resultSet.next()) {
                return resultSet.getString("file_name") + "|" + resultSet.getString("var_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 查询 elf_info 表的样本
     *
     * @param elfHash256 elf 文件的md5
     * @return 结果
     */
    private String querySampleByElfSHA256(String elfHash256) {
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from elf_info where file_sha256=\"" + elfHash256 + "\"");
            if (resultSet.next()) {
                return resultSet.getString("var_name") + "|"
                        + resultSet.getString("file_name");
            }
        } catch (SQLException e) {
            System.out.println("语法错误：" + elfHash256);
            e.printStackTrace();
        }

        return null;
    }

    private String querySampleByElfFeatureCode2(String featureCode) {
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from elf_info where feature_code=\"" + featureCode + "\"");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("var_name") + "|"
                        + resultSet.getString("file_name") + "|"
                        + resultSet.getString("feature_code"));
            }
        } catch (SQLException e) {
            System.out.println("语法错误：" + featureCode);
            e.printStackTrace();
        }

        return null;
    }

    private String querySampleByElfFeatureCode(String featureCode) {
        featureCode = sqliteEscape(featureCode);

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from elf_info where feature_code like '" + featureCode + "' escape '/'");
            if (resultSet.next()) {
                return resultSet.getString("var_name") + "|"
                        + resultSet.getString("file_name") + "|"
                        + resultSet.getString("feature_code");
            }
        } catch (SQLException e) {
            System.out.println("querySampleByElfFeatureCode, 特殊字符串：" + featureCode);
            e.printStackTrace();
        }

        return null;
    }


    private ArrayList<SampleInfo> querySampleByCertMD5(String certMD5) {
        ArrayList<SampleInfo> sampleInfos = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from sample_info where cert like \"%" + certMD5 + "%\"");

            while (resultSet.next()) {
                SampleInfo sampleInfo = new SampleInfo();
                sampleInfo.fileSHA256 = resultSet.getString("file_sha256");
                sampleInfo.fileName = resultSet.getString("file_name");
                sampleInfo.pkgName = resultSet.getString("pkg_name");
                sampleInfo.label = resultSet.getString("label");
                sampleInfo.cert = resultSet.getString("cert");
                sampleInfo.permissions = resultSet.getString("permissions");
                sampleInfo.receivers = resultSet.getString("receivers");
                sampleInfo.services = resultSet.getString("services");
                sampleInfo.varName = resultSet.getString("var_name");
                sampleInfos.add(sampleInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (sampleInfos.size() == 0) {
            return null;
        }

        return sampleInfos;
    }
}
