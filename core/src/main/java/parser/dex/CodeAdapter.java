package parser.dex;

import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.util.DumpDexCodeAdapter;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    @Override
    public void visitEnd() {
        for (String str : writer.toString().split("\n")) {
            if (str.contains("CONST_STRING")) {
                String tmp = str.split("=\"")[1];
                int len = tmp.length();
                dexClass.stringData.add(tmp.substring(0, len - 1));
            }
        }
        dexClass.methodMap.put(method.toString(), writer.toString());
    }
}
