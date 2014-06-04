package parser.dex;

import com.googlecode.dex2jar.visitors.DexClassVisitor;
import com.googlecode.dex2jar.visitors.DexFileVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 收集 dex 的class
 */
public class DexFileAdapter implements DexFileVisitor {
    protected List<DexClass> dexClassList;
    protected int classIdx = 0;
    protected int config;

    public DexFileAdapter(List<DexClass> dexClasses) {
        this.dexClassList = dexClasses;
    }

    public DexFileAdapter(List<DexClass> dexClasses, int config) {
        this.dexClassList = dexClasses;
        this.config = config;
    }

    @Override
    public DexClassVisitor visit(int access_flags, String className, String superClass,
                                 String[] interfaceNames) {
        final DexClass dexClass = new DexClass();
        dexClass.classIdx = classIdx++;
        dexClass.className = className;
        dexClass.superName = superClass;
        dexClass.fields = new ArrayList<>();
        dexClass.methods = new ArrayList<>();
        dexClass.methodMap = new HashMap<>();
        dexClassList.add(dexClass);

//        System.out.println("类：" + className);

        return new ClassAdapter(access_flags,  className,  superClass, interfaceNames, config, dexClass);
    }

    @Override
    public void visitEnd() {
    }

}