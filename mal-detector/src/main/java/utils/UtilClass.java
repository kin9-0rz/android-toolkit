package utils;

import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.reader.DexFileReader;
import parser.dex.DexClass;

import javax.swing.tree.DefaultMutableTreeNode;

public class UtilClass {
    public static class FileNode extends DefaultMutableTreeNode {
        DexFileReader dexFile;
        String fileName;

        public FileNode(DexFileReader dexFile, String fileName) {
            super(dexFile, true);
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return fileName;
        }
    }

    public static class SOFile extends DefaultMutableTreeNode {
        String fileName;

        public SOFile(byte[] soFile, String fileName) {
            super(soFile, true);
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return fileName;
        }
    }

    public static class ClassNode extends DefaultMutableTreeNode {
        public ClassNode(Object userObject) {
            super(userObject, true);
        }

        @Override
        public String toString() {
            if (userObject == null)
                return null;
            else
                return UtilLocal.getClassName((DexClass) userObject);
        }
    }

    public static class FieldNode extends DefaultMutableTreeNode {
        public FieldNode(Object userObject) {
            super(userObject, true);
        }

        @Override
        public String toString() {
            if (userObject == null)
                return null;
            else
                return UtilLocal.getFieldName((DexClass.TField) userObject);
        }
    }

    public static class MethodNode extends DefaultMutableTreeNode {
        public MethodNode(Object userObject) {
            super(userObject, true);
        }

        @Override
        public String toString() {
            if (userObject == null)
                return null;
            else
                return UtilLocal.getMethodName((Method) userObject);
        }
    }

}
