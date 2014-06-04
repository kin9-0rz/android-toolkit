package gui;

import com.googlecode.dex2jar.reader.DexFileReader;
import parser.apk.APK;
import parser.dex.ApkActionPos;
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

/**
 * Created with IntelliJ IDEA.
 * User: lai
 * Date: 8/5/13
 * Time: 9:53 AM
 */
public class FeatureCode extends JPanel {
    final JTextArea stringList;
    JTree treePkg;

    public FeatureCode() {
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
            public void filesDropped(java.io.File[] files) {
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

                ArrayList<ClassNode> classNodes = new ArrayList<>();
                stringList.setText("");

                // 遍历下面的所有的叶子节点
                for (TreePath treePath : treePaths) {
                    final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    if (treeNode.getUserObject() instanceof DexClass) {
                        classNodes.add((ClassNode) treeNode);
                    } else {

                        classNodes.addAll(getClassNodes(treeNode));
                    }
                }

                stringList.append(getStrings(classNodes).replace("[<i>", "<i>").replace(", <i>", "<i>"));


                //get the current lead path
                final TreePath newPath = treeSelectionEvent.getNewLeadSelectionPath();
                if (newPath == null)
                    return;
                final DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) newPath.getLastPathComponent();
                if (selectNode.getUserObject() instanceof DexFileReader) {
                    final DexFileReader dexFile = (DexFileReader) selectNode.getUserObject();
                    System.out.println("dexFile.getClassSize : " + dexFile.getClassSize());
                } else if (selectNode.getUserObject() instanceof DexClass) {
                    DexClass classDefItem = (DexClass) selectNode.getUserObject();

//                    System.out.println("StringData : " + classDefItem.stringData);
                    StringBuilder sb = new StringBuilder();
                    List<String> strList = classDefItem.stringData;
                    Collections.sort(strList);
                    for (String str : strList) {
                        sb.append("<i>").append(str).append("</i>").append('\n');
                    }
                    stringList.setText(sb.toString());
                } else {
//                    System.out.println(selectNode.depthFirstEnumeration().toString());
//                    System.out.println(selectNode.getChildCount());

                    stringList.setText(getStrings(getClassNodes(selectNode)).replace("[<i>", "<i>").replace(", <i>", "<i>"));
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

    String getStrings(ArrayList<ClassNode> classNodes) {
        HashSet<String> hashSet = new HashSet<>();
        for (ClassNode classNode : classNodes) {
            List<String> strList = classNode.dexClass.stringData;
            for (String str : strList) {
                hashSet.add("<i>" + str + "</i>" + '\n');
            }

        }

        ArrayList<String> arrayList = new ArrayList<>(hashSet);
        Collections.sort(arrayList);

        return arrayList.toString();
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

            // initialize class List, and sort them.
//            final List<DexClass> classList = new ArrayList<>();
            final List<DexClass> classList = apk.getCodes();

//            DexFileReader dexFileReader = apk.getDexFileReader();

//            try {
//                dexFileReader.accept(new ClassCollector(classList));
//            } catch (Exception e) {
//                System.out.println("Accept Code Failed." + e.getMessage());
//                return;
//            }

            //sort
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

                if (dexClass.fields == null && dexClass.methods == null) {
//                    dexFileReader.visitClass(new CodeCollector(classDefItem), classDefItem.classIdx,
//                            DexFileReader.SKIP_DEBUG | DexFileReader.SKIP_ANNOTATION);
                    apk.getDexFileReader().visitClass(new ApkActionPos(dexClass), dexClass.classIdx,
                            DexFileReader.SKIP_DEBUG | DexFileReader.SKIP_ANNOTATION);
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

//    class PackageNode {
//        String packageName;
//
//        PackageNode(String packageName) {
//            this.packageName = packageName;
//        }
//
//        @Override
//        public String toString() {
//            return packageName;
//        }
//    }

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

    /**
     * 比较两个 ClassDefItem 对象（排序）。
     */
    class ComparatorClass implements Comparator<DexClass> {
        @Override
        public int compare(DexClass arg0, DexClass arg1) {
            final String name0 = arg0.className;
            final String name1 = arg1.className;

            if (name0.contains("/")) {
                if (name1.contains("/")) {
                    return name0.compareTo(name1);
                } else {
                    return "".compareTo(name0);
                }
            }

            if (name1.contains("/")) {
                return name1.compareTo("");
            } else {
                return name0.compareTo(name1);
            }
        }

    }

}
