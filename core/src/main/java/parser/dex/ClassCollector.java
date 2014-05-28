package parser.dex;

import com.googlecode.dex2jar.visitors.DexClassVisitor;
import com.googlecode.dex2jar.visitors.DexFileVisitor;

import java.util.List;

/**
 * 收集dex的class
 */
public class ClassCollector implements DexFileVisitor// Dex代码扫描
{
    List<ClassDefItem> classDefItemList;
    int classIdx = 0;

    /**
     * 收集dex的class
     */
    public ClassCollector(List<ClassDefItem> classDefItems) {
        this.classDefItemList = classDefItems;
    }

    // 该文件的“类”
    @Override
    public DexClassVisitor visit(int access_flags, String className, String superClass,
                                 String[] interfaceNames) {
        final ClassDefItem item = new ClassDefItem();
        item.className = className;
        item.superName = superClass;
        item.classIdx = classIdx++;
        classDefItemList.add(item);
        return null;
    }

    @Override
    public void visitEnd() {
    }
}
