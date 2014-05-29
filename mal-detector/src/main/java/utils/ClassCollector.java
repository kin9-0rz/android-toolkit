package utils;

import com.googlecode.dex2jar.visitors.DexClassVisitor;
import com.googlecode.dex2jar.visitors.DexFileVisitor;
import parser.dex.DexClass;

import java.util.List;

/**
 * 收集dex的class
 * @author HJF
 *
 */
public class ClassCollector implements DexFileVisitor// Dex代码扫描
{
    List<DexClass> classList;
    int classIdx = 0;
    /**收集dex的class*/
    public ClassCollector(List<DexClass> c)
    {
        this.classList = c;
    }
    //该文件的“类”
    @Override
    public DexClassVisitor visit(int access_flags, String className, String superClass,
            String[] interfaceNames)
    {
        final DexClass item = new DexClass();
        item.className = className;
        item.superName = superClass;
        item.classIdx = classIdx++;
        classList.add(item);
        return null;
    }

    @Override
    public void visitEnd()
    {
    }
}
