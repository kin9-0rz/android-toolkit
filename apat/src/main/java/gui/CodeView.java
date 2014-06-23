package gui;

import com.googlecode.dex2jar.reader.DexFileReader;
import parser.apk.APK;
import parser.dex.DexClass;
import utils.FileDrop;
import utils.UtilLocal;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class CodeView extends JPanel {
    final JTextArea stringList;
    JTree treePkg;

    public CodeView() {
        setLayout(new BorderLayout(0, 0));

        // 添加分割面板
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);


        treePkg = new JTree();
        treePkg.setRootVisible(false);
        treePkg.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane jScrollPaneLeft = new JScrollPane(treePkg);
        splitPane.setLeftComponent(jScrollPaneLeft);

        stringList = new JTextArea();
        stringList.setText("功能：提取类的字符串。\n" +
                "\n" +
                "使用方法：\n" +
                "将 APK 文件拉入左边的框即可。\n" +
                "点击相应的类，则可以显示该类中出现的字符串。");
        JScrollPane jScrollPaneRight = new JScrollPane(stringList);
        jScrollPaneRight.setViewportView(stringList);
        splitPane.setRightComponent(jScrollPaneRight);

        //监听拖动文件
        new FileDrop(System.out, treePkg, /* dragBorder, */new FileDrop.Listener() {
            @Override
            public void filesDropped(File[] files) {
                createNodes(files);
            }
        });


        //左部被点击后动作
        treePkg.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {


                JTree treeSource = (JTree) treeSelectionEvent.getSource();
                TreePath[] treePaths = treeSource.getSelectionPaths();

                if (treePaths == null) {
                    return;
                }

//                ArrayList<ClassNode> classNodes = new ArrayList<>();
                stringList.setText("");

//                for (TreePath treePath : treePaths) {
//                    final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
//                    if (treeNode.getUserObject() instanceof ClassDefItem) {
//                        classNodes.add((ClassNode) treeNode);
//                    } else {
//                        classNodes.addAll(getClassNodes(treeNode));
//                    }
//                }

//                get the current lead path
                final TreePath treeSelectionPath = treeSelectionEvent.getNewLeadSelectionPath();
                if (treeSelectionPath == null)
                    return;
                final DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) treeSelectionPath.getLastPathComponent();

                if (selectNode.getUserObject() instanceof DexClass) {
                    DexClass dexClass = (DexClass) selectNode.getUserObject();

                    for (String method : dexClass.methodMap.keySet()) {
                        stringList.append(method + "\n");
                        stringList.append(dexClass.methodMap.get(method).replace("|     |", "") + "\n");
                        stringList.append("\n");
                    }

                }
            }
        });

    }

    /**
     * @param parent      父节点
     * @param packageName 查找或插入的节点名
     * @return 查找或插入的节点
     */
    public static DefaultMutableTreeNode findOrAddNode(TreePath parent, String packageName) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();

        for (final Enumeration e = node.children(); e.hasMoreElements(); ) {
            final DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) e.nextElement();
            if (packageName.equals(mutableTreeNode.toString()))
                return mutableTreeNode;
        }

        final DefaultMutableTreeNode nodeCls = new DefaultMutableTreeNode(packageName);
        node.add(nodeCls);
        return nodeCls;
    }

    /**
     * 获取类节点
     *
     * @param selectNode 类节点
     * @return 类节点包含的节点列表
     */
    ArrayList<ClassNode> getClassNodes(DefaultMutableTreeNode selectNode) {
        ArrayList<ClassNode> classNodes = new ArrayList<>();

        for (final Enumeration ee = selectNode.children(); ee.hasMoreElements(); ) {
            final DefaultMutableTreeNode n = (DefaultMutableTreeNode) ee.nextElement();
//            System.out.println(n.toString() + " : " + n.isLeaf());

            if (n.isLeaf()) {
                classNodes.add((ClassNode) n);
            } else {
                ArrayList<ClassNode> subClassNodes = getClassNodes(n);
                classNodes.addAll(subClassNodes);
            }
        }

        return classNodes;
    }

    /**
     * 添加包和类节点（可以添加多个文件）
     *
     * @param files drop files...
     */
    private void createNodes(File[] files) {
        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Files");
        APK apk;
        // 1.parse files
        for (final File file : files) {
            try {
                apk = new APK(file);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (apk.getDexFileReader() == null)
                continue;

            // 创建根节点.
            DefaultMutableTreeNode fileNode = new FileNode(apk.getDexFileReader(), file.getAbsolutePath());
            rootNode.add(fileNode);

            List<DexClass> classList = apk.getDexClasses();
            Collections.sort(classList, new ComparatorClass());

            for (final DexClass dexClass : classList) {
                // 完整类名（com.pkg1.pkg2.cls;）
                String className = dexClass.className;

                // 获得节点字符串数组（com pkg1 pkg2 cls）
                final String[] strings = className.substring(1, className.length() - 1).split("/");

                if (UtilLocal.DEBUG) {
                    System.out.print(className + " ");
                    for (String name : strings) {
                        System.out.print(name + " ");
                    }
                    System.out.println();
                }

                int len = strings.length;
                DefaultMutableTreeNode pkgNode = fileNode;
                for (int i = 0; i < len - 1; i++) {
                    pkgNode = findOrAddNode(new TreePath(pkgNode), strings[i]);
                }

                ClassNode classNode = new ClassNode(dexClass);

                pkgNode.add(classNode);
            }

            treePkg.setModel(new DefaultTreeModel(rootNode));

        }
    }

    private class FileNode extends DefaultMutableTreeNode {
        String fileName;

        public FileNode(DexFileReader dexFileReader, String filePath) {
            super(dexFileReader, true);
            fileName = new File(filePath).getName();
        }

        @Override
        public String toString() {
            return fileName;
        }
    }


    class ClassNode extends DefaultMutableTreeNode {
        String className;
        DexClass dexClass;

        ClassNode(DexClass dexClass) {
            super(dexClass, true);
            final String[] strings = dexClass.className.substring
                    (1, dexClass.className.length() - 1).split("/");

            className = strings[strings.length - 1];
            this.dexClass = dexClass;
        }

        @Override
        public String toString() {
            return className;
        }
    }

}
