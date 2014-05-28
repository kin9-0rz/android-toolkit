package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import org.apache.commons.io.FileUtils;
import utils.Util;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.sql.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CertImport extends JPanel {
    private final JTextField txtDir;
    private final JTextArea textArea;

    public CertImport() {
        setLayout(new FormLayout(new ColumnSpec[]{ColumnSpec.decode("11dlu"),
                ColumnSpec.decode("max(211dlu;min):grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("100px:grow"), ColumnSpec.decode("10dlu"),},
                new RowSpec[]{RowSpec.decode("12dlu"), RowSpec.decode("23px"),
                        RowSpec.decode("38px"), RowSpec.decode("15px"), RowSpec.decode("23px"),
                        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"),
                        FormFactory.DEFAULT_ROWSPEC,}));

        final JButton btnSrcPath = new JButton("目录...");
        btnSrcPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final JFileChooser fc = new JFileChooser();
                final int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    final File f = fc.getSelectedFile();
                    txtDir.setText(f.getAbsolutePath());
                }
            }
        });
        txtDir = new JTextField();
        add(txtDir, "2, 2, fill, fill");
        add(btnSrcPath, "4, 2, fill, fill");
        new FileDrop(System.out, txtDir, /* dragBorder, */new FileDrop.Listener() {
            @Override
            public void filesDropped(File[] files) {
                final File f = files[0];
                txtDir.setText(f.getAbsolutePath());
            }
        });

        final JComboBox comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(new String[]{"w-白名单", "b-黑名单", "s-可疑", "u-未知"}));
        add(comboBox, "4, 3, fill, default");

        final JButton btnEncrypt = new JButton("提取");
        add(btnEncrypt, "4, 5, fill, fill");
        btnEncrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                final String srcPath = txtDir.getText();
                final String type = (String) (comboBox.getSelectedItem());
                final CertImportTask task = new CertImportTask(srcPath, type.charAt(0), textArea);
                task.execute();
            }
        });

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new LineBorder(new Color(130, 135, 144)));
        add(scrollPane, "2, 7, 3, 1, fill, fill");

        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);
    }
}

class CertImportTask extends SwingWorker<Integer, String> {
    //
    private final String path;
    private final char type;
    private final JTextArea txtOutput;

    CertImportTask(String path, char type, JTextArea txtOutput) {
        this.path = path;
        this.type = type;
        this.txtOutput = txtOutput;
    }

    int getCert(File file, Connection conn) {
        final Certificate[] certs = Util.getJarCerts(file);
        if (certs == null || certs.length <= 0)
            return -1;

        final String md5 = Util.getCertMd5(certs[0]);
        final String type = String.valueOf(this.type);
        final String fileName = file.getName();
        final String baseinfo = Util.getCertInfo(certs[0]).replace(',', '|');
        String raw = "";
        try {
            final byte[] code = certs[0].getEncoded();
            raw = Util.hex2Str(code);
        } catch (final CertificateEncodingException e) {
            e.printStackTrace();
        }

        //save to db
        final int r = save2Db(conn, md5, type, fileName, baseinfo, raw);
        if (r != -1) {
            final StringBuilder sb = new StringBuilder();
            sb.append(md5);
            sb.append(",");
            sb.append(type);
            sb.append(",");
            sb.append(fileName);
            sb.append(",");
            sb.append(baseinfo);
            sb.append(",");
            sb.append(raw);
            sb.append("\n");
            publish(sb.toString());
        } else
            publish(md5 + ":already import!\n");
        return r;
    }

    private int save2Db(Connection conn, String md5, String type, String fileName, String baseinfo, String raw) {
        int r = -1;
        if (conn != null) {
            PreparedStatement st = null;
            try {
                st = conn.prepareStatement("insert into cert_info(md5, type, filename, baseinfo,raw) values(?, ?, ?, ?,?)");
                st.setString(1, md5);
                st.setString(2, type);
                st.setString(3, fileName);
                st.setString(4, baseinfo);
                st.setString(5, raw);
                st.execute();
                r = 0;
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return r;
    }

    private Connection prepareDb() {
        Connection conn = null;
        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:cert.db");
            final Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE CERT_INFO (md5 TEXT, type TEXT, filename TEXT, baseinfo TEXT, raw TEXT, comment TEXT)");
            st.executeUpdate("CREATE UNIQUE INDEX [IDX_CERT_INFO_] ON [CERT_INFO]([md5]  ASC)");
            st.executeUpdate("CREATE VIEW [cert_info_export] AS select md5, type from cert_info");
            st.close();
        } catch (final ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private void releaseDb(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer doInBackground() {
        final Connection conn = prepareDb();

        final Date tStart = new Date();
        final File dir = new File(path);
        final Collection<File> files = FileUtils.listFiles(dir, null, true);
        int count = 0;
        for (final File file : files) {
            final int r = getCert(file, conn);
            if (r != -1)
                ++count;
        }


        final long delta = new Date().getTime() - tStart.getTime();
        publish(String.format("*******time:%dms,files:%d,success:%d*******\n", delta, files.size(), count));

        releaseDb(conn);
        return files.size();
    }

    @Override
    protected void process(List<String> chunks) {
        for (final String txt : chunks)
            txtOutput.append(txt);
    }
}
