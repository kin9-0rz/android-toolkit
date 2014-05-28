package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import parser.apk.APK;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

public class StringSearcher extends JPanel {

    final JTextField filePath;
    final JTextField searchMethod;
    JTree treePkg;


    public StringSearcher() {

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
        add(filePath, "2, 2, fill, fill");

        searchMethod = new JTextField();
        add(searchMethod, "2, 3, fill, fill");


        // -------------------------------- Button -----------------------------------

        final JButton jButtonPath = new JButton("File");
        add(jButtonPath, "4, 2, fill, fill");

        final JButton jButtonSearch = new JButton("Search");
        add(jButtonSearch, "4, 3, fill, fill");

        // -------------------------------- JTree -----------------------------------
        final JScrollPane jScrollPane = new JScrollPane();
        add(jScrollPane, "2, 6, 3, 1, fill, fill");

        treePkg = new JTree();
        jScrollPane.setViewportView(treePkg);


        // ------ Node -------
        jButtonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final StringSearcherTask task = new StringSearcherTask(filePath.getText(),
                        searchMethod.getText(), treePkg, jButtonSearch);
                task.execute();
                jButtonSearch.setEnabled(false);
            }
        });

        new FileDrop(System.out, filePath,
                new FileDrop.Listener() {
                    @Override
                    public void filesDropped(File[] files) {
                        File f = files[0];
                        if (f.isFile()) {
                            filePath.setText(f.getAbsolutePath());
                        }
                    }
                });

    }

}

class StringSearcherTask extends SwingWorker<Void, Void> {

    private final JButton jButtonSearch;
    String filePath;
    String method;
    JTree methodTree;

    public StringSearcherTask(String filePath, String method, JTree jTree, JButton jButtonSearch) {
        this.filePath = filePath;
        this.method = method;
        this.methodTree = jTree;
        this.jButtonSearch = jButtonSearch;
    }

    @Override
    public Void doInBackground() {
        try {
            APK apk = new APK(filePath);
            HashMap<String, String> methods = apk.getMethods();
//            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(method);
            this.methodTree.setModel(new DefaultTreeModel(createNodes(method, methods)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @deprecated
     * @param method
     * @param methods
     * @param rootNode
     * @return
     */
    private DefaultMutableTreeNode addNode2(String method, HashMap<String, String> methods, DefaultMutableTreeNode rootNode) {
        String methodBody;
        for (String key : methods.keySet()) {
            methodBody = methods.get(key);
            if (methodBody.contains(method)) {
                DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(key);
                rootNode.add(treeNode);
                addNode2(key, methods, treeNode);
            }
        }
        return rootNode;
    }

    /**
     * 以递归的方式创建节点。
     * @param method    方法名
     * @param methods   方法Map
     * @return rootNode DefaultMutableTreeNode
     */
    private DefaultMutableTreeNode createNodes(String method, HashMap<String, String> methods) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(method);
        String methodBody;
        for (String key : methods.keySet()) {
            methodBody = methods.get(key);
            if (methodBody.contains(method)) {
                rootNode.add(createNodes(key, methods));
            }
        }
        return rootNode;
    }

    @Override
    protected void done() {
        System.out.println("搜索完毕。");
        jButtonSearch.setEnabled(true);
    }

}
