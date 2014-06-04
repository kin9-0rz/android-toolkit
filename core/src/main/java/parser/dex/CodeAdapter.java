package parser.dex;

import com.googlecode.dex2jar.DexOpcodes;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.util.DumpDexCodeAdapter;
import com.googlecode.dex2jar.visitors.DexCodeVisitor;
import org.objectweb.asm.Opcodes;
import parser.utils.XMLString;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by SlowMan on 14-6-4.
 * 1、解析方法的具體內容。
 * 2、解析OP_CONST_STRING 對應的字符串。
 */
public class CodeAdapter extends DumpDexCodeAdapter implements DexCodeVisitor, Opcodes, DexOpcodes {
    DexClass dexClass;
    Method method;
    StringWriter writer;

    public CodeAdapter(boolean isStatic, Method method, StringWriter writer, DexClass dexClass) {
        super(isStatic, method, new PrintWriter(writer));
        this.dexClass = dexClass;
        this.method = method;
        this.writer = writer;
    }

    @Override
    public void visitConstStmt(int opcode, int toReg, Object value, int xt) {
        switch (opcode) {
            case OP_CONST_STRING:
                dexClass.stringData.add(XMLString.escape(value));
                break;
        }
    }

    @Override
    public void visitEnd() {
        dexClass.methodMap.put(method.toString(), writer.toString());
    }
}
