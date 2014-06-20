package parser.dex;

import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.util.DumpDexCodeAdapter;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by SlowMan on 14-6-4.
 * 1、解析方法的具體內容。
 * 2、解析OP_CONST_STRING 對應的字符串。
 */
public class CodeAdapter extends DumpDexCodeAdapter {
    DexClass dexClass;
    Method method;
    StringWriter writer;

    public CodeAdapter(boolean isStatic, Method method, StringWriter writer, DexClass dexClass) {
        super(isStatic, method, new PrintWriter(writer));
        this.dexClass = dexClass;
        this.method = method;
        this.writer = writer;
    }


    // FIXME 這部分代碼會影響對方法的收集，丟失字符串部分。需要手工從方法體內，提取字符串。
//    @Override
//    public void visitConstStmt(int opcode, int toReg, Object value, int xt) {
//        switch (opcode) {
//            case OP_CONST_STRING:
//                dexClass.stringData.add(XMLString.escape(value));
//                break;
//        }
//    }

    @Override
    public void visitEnd() {
        dexClass.methodMap.put(method.toString(), writer.toString());
    }
}
