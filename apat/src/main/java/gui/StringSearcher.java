package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import parser.apk.APK;
import parser.dex.DEX;
import parser.utils.FileTypesDetector;
import tree.TreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class StringSearcher extends JPanel {

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

        searchMethod = new JTextField();
        add(searchMethod, "2, 2, fill, fill");

        final JButton jButtonSearch = new JButton("Search");
        add(jButtonSearch, "4, 2, fill, fill");

        // -------------------------------- JTree -----------------------------------
        final JScrollPane jScrollPane = new JScrollPane();
        add(jScrollPane, "2, 6, 3, 1, fill, fill");

        treePkg = new JTree();
        jScrollPane.setViewportView(treePkg);

        // ------ Node -------
        jButtonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final StringSearcherTask task = new StringSearcherTask(Main.filePath,
                        searchMethod.getText(), treePkg, jButtonSearch);
                task.execute();
                jButtonSearch.setEnabled(false);
            }
        });

    }

}

class StringSearcherTask extends SwingWorker<Void, Void> {

    static int count = 0;
    private final JButton jButtonSearch;
    String filePath;
    String method;
    JTree methodTree;

    TreeNode treeNode;
    static int id = 0;


    public StringSearcherTask(String filePath, String method, JTree jTree, JButton jButtonSearch) {
        this.filePath = filePath;
        this.method = method;
        this.methodTree = jTree;
        this.jButtonSearch = jButtonSearch;
        treeNode = new TreeNode();
        treeNode.setSelfId(id);
        treeNode.setNodeName(method);
    }

    @Override
    public Void doInBackground() {
        HashMap<String, String> methods;
        File file = new File(filePath);
        try {
            if (FileTypesDetector.isAPK(file)) {
                APK apk = new APK(file);
                methods = apk.getMethods();
            } else if (FileTypesDetector.isDEX(file)) {
                DEX dex = new DEX(file);
                methods = dex.getMethods();
            } else {
                return null;
            }
            HashSet<String> methodSet = new HashSet<>();
            this.methodTree.setModel(new DefaultTreeModel(createNodes(method, methods, methodSet)));
            count = 0;
            id =0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 以递归的方式创建节点。
     *
     * @param method  方法名
     * @param methods 方法Map
     * @param methodSet 已搜索過的方法集合
     * @return rootNode DefaultMutableTreeNode
     */
    private DefaultMutableTreeNode createNodes(String method,
                                               HashMap<String, String> methods, HashSet<String> methodSet) {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(method);
        if (methodSet.contains(method)) {
            return rootNode;
        }

        for (String key : methods.keySet()) {
            String methodBody = methods.get(key);
            if (methodBody.contains(method)) {
                methodSet.add(method);
                if (key.contains(";.handleMessage")) {
                    key = "Handler;.send";
                    rootNode.add(createNodes(key, methods, methodSet));
                    continue;
                }

                if (key.contains("$")) {
                    key = key.split(";.")[0] + ";.<init>";
                    rootNode.add(createNodes(key, methods, methodSet));
                    continue;
                }

                if (key.contains("$") && key.contains(";.run()V")) {
                    key = key.split(";.")[0] + ";.start";
                    rootNode.add(createNodes(key, methods, methodSet));
                    continue;
                }

                rootNode.add(createNodes(key, methods, methodSet));
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
