package parser.dex;

import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexClassVisitor;
import com.googlecode.dex2jar.visitors.DexFieldVisitor;
import com.googlecode.dex2jar.visitors.DexMethodVisitor;

/**
 * Created by SlowMan on 14-5-28.
 * 對類進行解析。
 *
 */
public class ClassAdapter implements DexClassVisitor {

    protected int access_flags;
    protected String className;
    protected int config;
    protected String file;
    protected String[] interfaceNames;
    protected String superClass;
    protected DexClass dexClass;


    public ClassAdapter(int access_flags, String className, String superClass,
                        String[] interfaceNames, int config) {
        super();
        this.access_flags = access_flags;
        this.className = className;
        this.superClass = superClass;
        this.interfaceNames = interfaceNames;
        this.config = config;
    }

    public ClassAdapter(int access_flags, String className, String superClass,
                        String[] interfaceNames, int config, DexClass dexClass) {
        super();
        this.access_flags = access_flags;
        this.className = className;
        this.superClass = superClass;
        this.interfaceNames = interfaceNames;
        this.config = config;
        this.dexClass = dexClass;
    }


    @Override
    public void visitSource(String file) {

    }

    @Override
    public DexFieldVisitor visitField(int accessFlags, Field field, Object value) {
        DexClass.TField tField = new DexClass.TField();
        tField.field = field;
        tField.value = value;
        dexClass.fields.add(tField);

        if (null != value) {
            dexClass.stringData.add(field.getName());
            dexClass.stringData.add(value.toString());
        }
//        System.out.println("成员：" + tField.toString());
        return new FieldAdapter(accessFlags, field, value, tField);
    }

    @Override
    public DexMethodVisitor visitMethod(int accessFlags, final Method method) {
        dexClass.methods.add(method);
        dexClass.stringData.add(method.getName());
//        System.out.println("方法：" + method);
        return new MethodAdapter(accessFlags, method, dexClass);
    }

    @Override
    public void visitEnd() {

    }

    @Override
    public DexAnnotationVisitor visitAnnotation(String name, boolean visible) {
        return null;
    }
}
