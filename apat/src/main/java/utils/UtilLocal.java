package utils;

import com.googlecode.dex2jar.Method;
import parser.dex.ClassDefItem;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public class UtilLocal {

    public static boolean DEBUG = false;

    public static String getPackageName(ClassDefItem clsDef) {
        final String className = clsDef.className;
        final String[] s = className.substring(1, className.length() - 1).split("/", 100);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length - 1; ++i) {
            sb.append(s[i]);
            sb.append(".");
        }
        final String join = sb.toString();
        return join.isEmpty() ? "(default)" : join.substring(0, join.length() - 1);
    }

    public static String getClassName(ClassDefItem clsDef) {
        final String className = clsDef.className;
        final String[] s = className.substring(1, className.length() - 1).split("/", 100);
        return s[s.length - 1];
    }

    public static String getFieldName(ClassDefItem.TField f) {
        final String s = f.field.getName();
        final String s1 = f.field.getType();
        String s2 = f.value != null ? "=" + f.value.toString() : "";
        return s + ":" + s1 + s2;
    }

    public static String getMethodName(Method method) {
        final String s = method.getName();
        final String s2 = method.getDesc();
        return s + s2;
    }

    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public static void expandAll(JTree tree, boolean expand) {
        final TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    public static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        final TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (final Enumeration e = node.children(); e.hasMoreElements(); ) {
                final TreeNode n = (TreeNode) e.nextElement();
                final TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    /**
     * @param tree        暂时未使用
     * @param parent      父节点
     * @param packageName 查找或插入的节点名
     * @return 查找或插入的节点
     */
    public static DefaultMutableTreeNode findOrAddNode(JTree tree, TreePath parent, String packageName) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();

        for (final Enumeration e = node.children(); e.hasMoreElements(); ) {
            final DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
            if (packageName.equals(n.toString()))
                return n;
        }

        final DefaultMutableTreeNode nodeCls = new DefaultMutableTreeNode(packageName);
        node.add(nodeCls);
        return nodeCls;
    }

    public static String hex2Str(byte[] data) {
        final String HEX = "0123456789ABCDEF";
        final StringBuilder sb = new StringBuilder();
        for (final byte b : data) {
            sb.append(HEX.charAt((b >> 4) & 0x0F));
            sb.append(HEX.charAt(b & 0x0F));
        }
        return sb.toString();
    }
}
