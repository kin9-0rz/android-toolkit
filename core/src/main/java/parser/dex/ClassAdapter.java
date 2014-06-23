package parser.dex;

import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexClassVisitor;
import com.googlecode.dex2jar.visitors.DexFieldVisitor;
import com.googlecode.dex2jar.visitors.DexMethodVisitor;


public class ClassAdapter implements DexClassVisitor {

    protected int access_flags;
    protected String className;
    protected String file;
    protected String[] interfaceNames;
    protected String superClass;
    protected DexClass dexClass;

    public ClassAdapter(int access_flags, String className, String superClass,
                        String[] interfaceNames, DexClass dexClass) {
        super();
        this.access_flags = access_flags;
        this.className = className;
        this.superClass = superClass;
        this.interfaceNames = interfaceNames;
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
            if (value instanceof String) {
                dexClass.stringData.add(value.toString());
            }
        }

        return null;
    }

    @Override
    public DexMethodVisitor visitMethod(int accessFlags, final Method method) {
        dexClass.methods.add(method);
        dexClass.stringData.add(method.getName());

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
