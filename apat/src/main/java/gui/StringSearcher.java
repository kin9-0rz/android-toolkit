package gui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import parser.apk.APK;
import tree.TreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StringSearcher extends JPanel {

//    final JTextField jTextFieldFilePath;
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

//
//        jTextFieldFilePath = new JTextField();
//        add(jTextFieldFilePath, "2, 2, fill, fill");

        searchMethod = new JTextField();
        add(searchMethod, "2, 2, fill, fill");


        // -------------------------------- Button -----------------------------------

//        final JButton jButtonPath = new JButton("File");
//        add(jButtonPath, "4, 2, fill, fill");

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

//        new FileDrop(System.out, jTextFieldFilePath,
//                new FileDrop.Listener() {
//                    @Override
//                    public void filesDropped(java.io.File[] files) {
//                        File f = files[0];
//                        if (f.isFile()) {
//                            jTextFieldFilePath.setText(f.getAbsolutePath());
//                        }
//                    }
//                });

    }

}

class StringSearcherTask extends SwingWorker<Void, Void> {

    static int count = 0;
    private final JButton jButtonSearch;
    private final String FIRST_METHOD;
    String filePath;
    String method;
    JTree methodTree;
    ArrayList<String> elderList = new ArrayList<>();

    TreeNode treeNode;
    static int id = 0;


    public StringSearcherTask(String filePath, String method, JTree jTree, JButton jButtonSearch) {
        this.filePath = filePath;
        this.method = method;
        this.methodTree = jTree;
        this.jButtonSearch = jButtonSearch;
        FIRST_METHOD = method;
        treeNode = new TreeNode();
        treeNode.setSelfId(id);
        treeNode.setNodeName(FIRST_METHOD);
    }

    @Override
    public Void doInBackground() {
        try {
            APK apk = new APK(filePath);
            HashMap<String, String> methods = apk.getMethods();
//            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(method);
//            this.methodTree.setModel(new DefaultTreeModel(createNodes(method, methods)));
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
     * @param method
     * @param methods
     * @param rootNode
     * @return
     * @deprecated
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
     *
     * @param method  方法名
     * @param methods 方法Map
     * @return rootNode DefaultMutableTreeNode
     */
    private DefaultMutableTreeNode createNodes0(String method, HashMap<String, String> methods) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(method);
        String methodBody;

//        if (elderList.contains(method)) {
//            return rootNode;
//        } else {
//            elderList.add(method);
//        }

//        count++;
//        if (count > 100) {
//            return rootNode;
//        }

        String tmpStr = method;
        if (tmpStr.contains("$") &&
                tmpStr.contains("run") || tmpStr.contains("handleMessage")) {
            System.out.println("Thread Method : " + tmpStr);
            method = tmpStr.split(";.")[0] + ";.start";
            System.out.println("Thread Method : " + method);
        } else if (tmpStr.contains("doInBackground")
                || tmpStr.contains("onPostExecute")
                || tmpStr.contains("onPreExecute")
                || tmpStr.contains("onProgressUpdate")) {
            System.out.println("Task Method : " +tmpStr);
            method = tmpStr.split(";\\.")[0] + ";.execute";
            System.out.println("Task Method : " + method);
        }

        System.out.print("搜索 : " + method);


        for (String key : methods.keySet()) {
            if (key.equals(method) || key.equals(FIRST_METHOD)) {
                continue;
            }

            String str = method + " -> " + key;
            if (elderList.contains(str)) {
                continue;
            }


            methodBody = methods.get(key);
            if (methodBody.contains(method) && !key.equals(method)) {
                elderList.add(str);
                rootNode.add(createNodes0(key, methods));
            }
        }

        System.out.println(elderList);

        return rootNode;
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
            System.out.println(method);
            return rootNode;
        }

        /**
         * 是否找到调用标志，找到则是调用，找不到则是最终调用。
         */
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

        System.out.println(method);


        return rootNode;
    }


    @Override
    protected void done() {
        System.out.println("搜索完毕。");
        jButtonSearch.setEnabled(true);
    }

}
