package parser.dex;

import com.googlecode.dex2jar.DexOpcodes;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.util.DumpDexCodeAdapter;
import com.googlecode.dex2jar.visitors.DexAnnotationAble;
import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexCodeVisitor;
import com.googlecode.dex2jar.visitors.DexMethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.PrintWriter;
import java.io.StringWriter;

//import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

public class MethodAdapter implements DexMethodVisitor, Opcodes {


    protected int accessFlags;
    final protected Method method;
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
        return new DumpDexCodeAdapter((accessFlags & DexOpcodes.ACC_STATIC) != 0,
                method,
                new PrintWriter(writer)) {
            @Override
            public void visitEnd() {
                // TODO here maybe you can init the stringData
//                System.out.println("方法内容：" + writer.toString());
                dexClass.methodMap.put(method.toString(), writer.toString());
            }
        };
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
