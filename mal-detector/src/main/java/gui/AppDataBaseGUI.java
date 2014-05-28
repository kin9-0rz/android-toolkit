package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import parser.apk.APK;
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
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 这个界面主要是样本的数据导入到数据库中.
 */
public class AppDataBaseGUI extends JPanel {
    private final JTextField filePath;
    JTextArea jTextAreaSMS = null;
    JTextArea jTextAreaInternet = null;
    JTextArea jTextAreaShell = null;
    JTextArea jTextAreaPhoneInfo = null;
    JTextArea jTextAreaURI = null;
    /**
     * 暂时用来做测试特殊代码用.
     */
    JTextArea jTextAreaTest = null;


    public AppDataBaseGUI() {

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


        filePath = new JTextField();
//        filePath.setText("");
        add(filePath, "2, 2, fill, fill");

        // -------------------------------- Button -----------------------------------

        final JButton jButtonPath = new JButton("File");
        add(jButtonPath, "4, 2, fill, fill");

        final JButton jButtonAnalysis = new JButton("Import");
        add(jButtonAnalysis, "4, 3, fill, fill");

        final JButton jButtonClearAll = new JButton("Clear");
        add(jButtonClearAll, "4, 4, fill, fill");


        // -------------------------------- Tab -----------------------------------

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, "2, 6, 3, 1, fill, fill");

        final JScrollPane scrollPaneInfos = new JScrollPane();
        tabbedPane.addTab("Log1", null, scrollPaneInfos, null);
        jTextAreaSMS = new JTextArea();
        scrollPaneInfos.setViewportView(jTextAreaSMS);

        final JScrollPane scrollPaneCertificate = new JScrollPane();
        tabbedPane.addTab("Log2", null, scrollPaneCertificate, null);
        jTextAreaInternet = new JTextArea();
        scrollPaneCertificate.setViewportView(jTextAreaInternet);

        final JScrollPane scrollPaneReceivers = new JScrollPane();
        tabbedPane.addTab("Log3", null, scrollPaneReceivers, null);
        jTextAreaPhoneInfo = new JTextArea();
        scrollPaneReceivers.setViewportView(jTextAreaPhoneInfo);

        final JScrollPane scrollPaneActivities = new JScrollPane();
        tabbedPane.addTab("Log4", null, scrollPaneActivities, null);
        jTextAreaShell = new JTextArea();
        scrollPaneActivities.setViewportView(jTextAreaShell);

        final JScrollPane scrollPaneServices = new JScrollPane();
        tabbedPane.addTab("Log5", null, scrollPaneServices, null);
        jTextAreaURI = new JTextArea();
        scrollPaneServices.setViewportView(jTextAreaURI);

        final JScrollPane scrollPanePermissions = new JScrollPane();
        tabbedPane.addTab("Log6", null, scrollPanePermissions, null);
        jTextAreaTest = new JTextArea();
        scrollPanePermissions.setViewportView(jTextAreaTest);


        // ----------------------------------------------- Event -------------------------------------------------------

        new FileDrop(System.out, filePath,
                new FileDrop.Listener() {
                    @Override
                    public void filesDropped(File[] files) {
                        File f = files[0];
                        filePath.setText(f.getAbsolutePath());
                    }
                });

        jButtonPath.addActionListener(new ActionListener() {
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
                final APIsAnalysisTask task = new APIsAnalysisTask(filePath.getText(), jTextAreaSMS, jTextAreaInternet,
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

class APIsAnalysisTask extends SwingWorker<List<String>, String> {


    final Byte FLAG_SMS = 0;
    final Byte FLAG_NETWORK = 1;
    final Byte FLAG_SHELL = 2;
    final Byte FLAG_PHONE_INFO = 3;
    final Byte FLAG_URI = 4;
    final Byte FLAG_TEST = 5;
    final Connection conn;
    private final JButton jButtonAnalysis;
    HashMap<Byte, String> behaviorType = new HashMap<>();
    JTextArea jTextAreaSMS = null;
    JTextArea jTextAreaNetwork = null;
    JTextArea jTextAreaShell = null;
    JTextArea jTextAreaPhoneInfos = null;
    JTextArea jTextAreaUri = null;
    JTextArea jTextAreaTest = null;
    String path;
//    Scanner scanner;

    // ------- ---- 线程池相关参数 ---------------
    ThreadPoolExecutor threadPoolExecutor;
    int maximumPoolSize = Runtime.getRuntime().availableProcessors() / 2;
    int corePoolSize = maximumPoolSize / 2;
    long keepAliveTime = 3L;

    public APIsAnalysisTask(String text, JTextArea jTextAreaSMS, JTextArea jTextAreaNetwork,
                            JTextArea jTextAreaPhoneInfos, JTextArea jTextAreaShell,
                            JTextArea jTextAreaUri, JTextArea jTextAreaTest, JButton jButtonAnalysis) {
        this.path = text;
        this.jTextAreaSMS = jTextAreaSMS;
        this.jTextAreaNetwork = jTextAreaNetwork;
        this.jTextAreaShell = jTextAreaShell;
        this.jTextAreaPhoneInfos = jTextAreaPhoneInfos;
        this.jTextAreaUri = jTextAreaUri;
        this.jTextAreaTest = jTextAreaTest;
        this.jButtonAnalysis = jButtonAnalysis;

        conn = prepareDb();


        behaviorType.put(FLAG_SMS, "SMS");
        behaviorType.put(FLAG_NETWORK, "NETWORK");
        behaviorType.put(FLAG_SHELL, "SHELL");
        behaviorType.put(FLAG_PHONE_INFO, "PHONE_INFO");
        behaviorType.put(FLAG_URI, "URI");
        behaviorType.put(FLAG_TEST, "TEST");


        initThreadPoolExecutor();
//        scanner = new Scanner();

    }

    private void initThreadPoolExecutor() {


        threadPoolExecutor =
                new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(maximumPoolSize),
                        new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 分析APK，并将其信息存入数据库
     *
     * @param file file
     */
    public void printAppInfo(File file) {

        try {
            if (FileTypesDetector.getType(new FileInputStream(file)).contains("ELF")){
                String fileSHA256 = HashTool.getSHA256(file);
                String varNames = file.getParentFile().getName();

                byte result = insert_elf_info(conn, fileSHA256, file.getName(), varNames);
                if (result == 1) {
                    publish(file.getName() + " 导入成功\n");
                } else if (result == -1) {
                    publish(file.getName() + " 导入失败\n");
                } else {
                    publish(file.getName() + " 数据已存在\n");
                }
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            APK apk = new APK(file);
            String fileSHA256 = HashTool.getSHA256(file);

            HashMap<String, String> certMap = apk.getCertificateInfos();
            String permissions = apk.getPermissions().toString();
            String services = apk.getServices().toString();
            String receivers = apk.getReceivers().toString();
            String varNames = file.getParentFile().getName();
            // ArrayList<String> elfHash256List = apk.getelfHash256List();

            byte result = insert_sample_info(conn, fileSHA256, file.getName(), varNames, apk.getDexSHA256(),
                    apk.getPackageName(), apk.getLabel(), certMap.toString(), permissions, receivers, services);
            if (result == 1) {
                publish(file.getName() + " 导入成功\n");
            } else if (result == -1) {
                publish(file.getName() + " 导入失败\n");
            } else {
                publish(file.getName() + " 数据已存在\n");
            }
        } catch (Exception e) {
            if (e.getMessage().contains("IT IS NOT A APK FILE")) {
                publish(file.getName() + " 非APK文件\n");
            } else {
                publish(file.getName() + " 导入异常！！！！\n");
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> doInBackground() {
        final File pathFile = new File(path);
        if (pathFile.isFile()) {
            publish(new Date().toString());
            publish("路径 : " + pathFile.getParentFile().getName() + "/" + pathFile.getName());
            printAppInfo(pathFile);
        } else if (pathFile.isDirectory()) {
            int count = 0;

            publish(new Date() + " > 开始导入数据：目录（多线程, 非递归）:" + pathFile.getName());
            LinkedList<File> linkedList = new LinkedList<>();
            linkedList.addLast(pathFile);
            while (linkedList.size() > 0) {
                File file = linkedList.removeFirst();
                File[] files = file.listFiles();

                if (files == null) {
                    continue;
                }

                for (final File subFile : files) {
                    if (subFile.isDirectory()) {
                        linkedList.addLast(subFile);
                    } else {
                        count++;
                        final int finalCount = count;
                        threadPoolExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                publish(String.valueOf(finalCount) + " : " + subFile.getName() + "...................");
                                printAppInfo(subFile);
                            }
                        });

                        while (threadPoolExecutor.getActiveCount() >= maximumPoolSize) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (count % 1000 == 0) {
                            try {
                                conn.commit();
                            } catch (SQLException e) {
                                if (e.getMessage().contains("database is locked")) {
                                    publish("database is locked");
                                }
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }


        }


        while (true) {
            if (threadPoolExecutor.getActiveCount() == 0
                    && threadPoolExecutor.getQueue().size() == 0) {
                break;
            }
            publish("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            publish("Active Count : " + threadPoolExecutor.getActiveCount());
            publish("Core Pool Size : " + threadPoolExecutor.getCorePoolSize());
            publish("Maximum Pool Size : " + threadPoolExecutor.getMaximumPoolSize());
            publish("Pool Size : " + threadPoolExecutor.getPoolSize());
            publish("Completed Task Count : " + threadPoolExecutor.getCompletedTaskCount());
            publish("Queue Size : " + threadPoolExecutor.getQueue().size());
            publish("Task Count : " + threadPoolExecutor.getTaskCount());
            publish("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        publish("=========================================================");
        publish("Active Count : " + threadPoolExecutor.getActiveCount());
        publish("Core Pool Size : " + threadPoolExecutor.getCorePoolSize());
        publish("Maximum Pool Size : " + threadPoolExecutor.getMaximumPoolSize());
        publish("Pool Size : " + threadPoolExecutor.getPoolSize());
        publish("Completed Task Count : " + threadPoolExecutor.getCompletedTaskCount());
        publish("Queue Size : " + threadPoolExecutor.getQueue().size());
        publish("Task Count : " + threadPoolExecutor.getTaskCount());
        publish("=========================================================");

        try {
            conn.commit();
        } catch (SQLException e) {
            if (e.getMessage().contains("database is locked")) {
                publish("database is locked");
            }
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void done() {
        releaseDb(conn);
        threadPoolExecutor.shutdown();
        jButtonAnalysis.setEnabled(true);
        publish(new Date() + " 全部操作完毕 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    @Override
    protected void process(List<String> chunks) {
        for (String str : chunks) {
            jTextAreaSMS.append(str + "\n");
        }
    }


    // -----------------------------------数据库操作------------------------------------------------
    //

    /**
     * 初始化数据库
     *
     * @return 返回数据库连接器
     */

    private Connection prepareDb() {
        Connection conn = null;
        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
//            conn = DriverManager.getConnection("jdbc:sqlite::memory:");
            conn = DriverManager.getConnection("jdbc:sqlite:samples.db");
            final Statement statement = conn.createStatement();

            // 恶意软件表（恶意软件ID，恶意软件名，恶意软件描述）
//            statement.executeUpdate("CREATE TABLE IF NOT EXISTS MAL_INFO (" +
//                    "\"mal_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
//                    "\"mal_name\" TEXT(20) NOT NULL, " +
//                    "\"mal_dst\" TEXT(300)" +
//                    ")");
//            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS \"IDX_MAL_INFO\" ON \"MAL_INFO\" (\"mal_name\"  ASC)");


            // 变种表（变种ID，变种名，恶意软件ID）
//            statement.executeUpdate("CREATE TABLE IF NOT EXISTS VARIETY_INFO (" +
//                    "\"var_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
//                    "\"var_name\" TEXT(20) NOT NULL," +
//                    "\"mal_id\" INTEGER NOT NULL" +
//                    ")");
//            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS \"IDX_VARIETY_INFO\" ON \"VARIETY_INFO\" (\"var_name\"  ASC)");

            // 样本表（样本hash, 变种ID）
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS SAMPLE_INFO (" +
                    "\"file_sha256\" TEXT(32) PRIMARY KEY NOT NULL, " +
                    "\"file_name\" TEXT(100) NOT NULL, " +
                    "\"var_name\" TEXT(100) NOT NULL, " +
                    "\"dex_sha256\" TEXT(32) NOT NULL, " +
                    "\"pkg_name\" TEXT(50) NOT NULL, " +
                    "\"label\" TEXT(100) NOT NULL, " +
                    "\"cert\" TEXT(100), " +
                    "\"permissions\" TEXT(1000), " +
                    "\"receivers\" TEXT(2000), " +
                    "\"services\" TEXT(1000), " +
                    "\"feature_code\" TEXT(50), " +
                    "\"sub_file_sha256\" TEXT(64)" +
                    ")");
            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS \"IDX_SAMPLE_INFO\" ON \"SAMPLE_INFO\" (\"file_sha256\" ASC)");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ELF_INFO (" +
                    "\"file_sha256\" TEXT(32) PRIMARY KEY NOT NULL, " +
                    "\"file_name\" TEXT(100) NOT NULL, " +
                    "\"var_name\" TEXT(100) NOT NULL, " +
                    "\"feature_code\" TEXT(100)" +
                    ")");
            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS \"IDX_SAMPLE_INFO\" ON \"ELF_INFO\" (\"file_sha256\" ASC)");

            // 证书表（证书MD5, 证书信息，证书类型[暂时没用]）
//            statement.executeUpdate("CREATE TABLE IF NOT EXISTS CERT_INFO (" +
//                    "\"md5\" TEXT(32) PRIMARY KEY NOT NULL, " +
//                    "\"owner\" TEXT(100)" +
//                    ")");
//            statement.executeUpdate("CREATE UNIQUE INDEX  IF NOT EXISTS [IDX_CERT_INFO] ON [CERT_INFO]([cert_md5] ASC)");

            // 清单信息表
//            statement.executeUpdate("CREATE TABLE IF NOT EXISTS AM_INFO (" +
//                    "\"file_md5\" TEXT, " +
//                    "\"pkg_name\" TEXT, " +
//                    "\"receivers\" TEXT, " +
//                    "\"services\" TEXT)");


//            statement.executeUpdate("CREATE TABLE IF NOT EXISTS DEX_INFO (file_md5 TEXT, dex_md5 TEXT)");
            statement.close();

            // 让其不自动提交
            conn.setAutoCommit(false);
        } catch (final ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * 插入恶意软件信息
     *
     * @param mal_name 恶意软件名
     */
    @SuppressWarnings("UnusedDeclaration")
    private boolean insert_mal_info(String mal_name) {
        boolean result = false;
        if (conn != null) {
            PreparedStatement st;
            try {
                st = conn.prepareStatement("insert into mal_info(mal_id, mal_name, mal_dst) values(?, ?, ?)");
                st.setString(2, mal_name);
                st.setString(3, mal_name);
                st.execute();
                result = true;
            } catch (final SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

        return result;
    }

    /**
     * 插入变种信息
     *
     * @param varName 变种名
     */
    @SuppressWarnings("UnusedDeclaration")
    private boolean insert_var_info(String varName) {
        boolean result = false;
        if (conn != null) {
            PreparedStatement st;
            try {
                st = conn.prepareStatement("INSERT INTO VARIETY_INFO(var_id, var_name, mal_id) values(?, ?, ?)");
                st.setString(2, varName);
                st.setInt(3, 1);
                st.execute();
                result = true;
            } catch (final SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

        return result;
    }

    /**
     * 插入样本信息
     *
     * @param conn     database connection
     * @param fileSHA256  file md5
     * @param fileName file name
     * @param varName  variety name
     * @param label    apk label
     * @param cert     certificate info
     * @param perms    permissions
     * @param recs     receivers
     * @param services services
     * @return 1 插入成功，0 数据已存在， -1 插入异常。
     */
    private byte insert_sample_info(Connection conn, String fileSHA256, String fileName, String varName,
                                    String dexSHA256, String pkgName, String label, String cert, String perms,
                                    String recs, String services) {
        byte result = -1;
        if (conn != null) {
            PreparedStatement st;
            try {
                st = conn.prepareStatement("insert into sample_info(file_sha256, file_name, var_name, dex_sha256, " +
                        "pkg_name, label, cert, permissions, receivers, services) " +
                        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                st.setString(1, fileSHA256);
                st.setString(2, fileName);
                st.setString(3, varName);
                st.setString(4, dexSHA256);
                st.setString(5, pkgName);
                st.setString(6, label);
                st.setString(7, cert);
                st.setString(8, perms);
                st.setString(9, recs);
                st.setString(10, services);
                st.execute();
                result = 1;
            } catch (final SQLException e) {
                if (e.getMessage().contains("column file_sha256 is not unique")) {
                    result = 0;
                } else {
                    result = -1;
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private byte insert_elf_info(Connection conn, String fileSHA256, String fileName, String varName) {
        byte result = -1;
        if (conn != null) {
            PreparedStatement st;
            try {
                st = conn.prepareStatement("insert into elf_info(file_sha256, file_name, var_name) values(?, ?, ?)");
                st.setString(1, fileSHA256);
                st.setString(2, fileName);
                st.setString(3, varName);
                st.execute();
                result = 1;
            } catch (final SQLException e) {
                if (e.getMessage().contains("column file_sha256 is not unique")) {
                    result = 0;
                } else {
                    result = -1;
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * 插入证书信息
     *
     * @param conn  database connection
     * @param md5   certificate md5
     * @param owner owner
     */
    @SuppressWarnings("UnusedDeclaration")
    private boolean insert_cert_info(Connection conn, String md5, String owner) {
        boolean result = false;
        if (conn != null) {
            PreparedStatement st;
            try {
                st = conn.prepareStatement("insert into cert_info(cert_md5, owner) values(?, ?)");
                st.setString(1, md5);
                st.setString(2, owner);
                st.execute();
                result = true;
            } catch (final SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

        return result;
    }

    private void releaseDb(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

}