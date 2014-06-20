package parser.dex;

import com.googlecode.dex2jar.DexOpcodes;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.visitors.DexAnnotationAble;
import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexCodeVisitor;
import com.googlecode.dex2jar.visitors.DexMethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.StringWriter;

//import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

public class MethodAdapter implements DexMethodVisitor, Opcodes {


    final protected Method method;
    protected int accessFlags;
    protected int config;
    protected DexClass dexClass;

    public MethodAdapter(int accessFlags, Method method, DexClass dexClass) {
        this(accessFlags, method, 0, dexClass);
    }

    public MethodAdapter(int accessFlags, Method method, int config, DexClass dexClass) {
        super();
        this.dexClass = dexClass;
        this.method = method;
        final int cleanFlag = ~((DexOpcodes.ACC_DECLARED_SYNCHRONIZED | DexOpcodes.ACC_CONSTRUCTOR));
        this.accessFlags = accessFlags & cleanFlag;
        this.config = config;
    }


    @Override
    public DexCodeVisitor visitCode() {
        final StringWriter writer = new StringWriter(1024 * 10);
//        final PrintWriter printWriter = new PrintWriter(writer);
//        return new DumpDexCodeAdapter((accessFlags & DexOpcodes.ACC_STATIC) != 0, method, printWriter) {
//            @Override
//            public void visitEnd() {
//                dexClass.methodMap.put(method.toString(), writer.toString());
//                printWriter.close();
//            }
//        };
        return new CodeAdapter((accessFlags & DexOpcodes.ACC_STATIC) != 0, method, writer, dexClass);
    }

    @Override
    public void visitEnd() {

    }

    @Override
    public DexAnnotationAble visitParameterAnnotation(int index) {
        return null;
    }

    @Override
    public DexAnnotationVisitor visitAnnotation(String name, boolean visible) {
        return null;
    }
}
