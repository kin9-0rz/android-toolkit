package parser.dex;

import com.googlecode.dex2jar.DexOpcodes;
import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.util.DumpDexCodeAdapter;
import com.googlecode.dex2jar.visitors.*;
import parser.dex.DexClass.TField;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取代码
 *
 */
public class CodeCollector implements DexFileVisitor {
    DexClass classItem;

    /**
     * 该收集器主要功能——完善 classItem 内容：className,superName,fields,methods 和 methodBody
     */
    public CodeCollector(DexClass c) {
        this.classItem = c;
    }

    @Override
    public DexClassVisitor visit(int access_flags, String className, String superClass,
                                 String[] interfaceNames) {

        classItem.className = className;
        classItem.superName = superClass;
        classItem.fields = new ArrayList<>();
        classItem.methods = new ArrayList<>();
        classItem.methodMap = new HashMap<>();

        return new EmptyVisitor() {

            @Override
            public DexFieldVisitor visitField(int access_flags, Field field, Object value) {
                TField f = new TField();
                f.field = field;
                f.value = value;
                classItem.fields.add(f);
                return null;
            }

            //反编译获取代码
            @Override
            public DexMethodVisitor visitMethod(final int access_flags, final Method method) {
                classItem.methods.add(method);

                return new EmptyVisitor() {
                    @Override
                    public DexCodeVisitor visitCode() {
                        final StringWriter writer = new StringWriter(1024 * 10);
                        return new DumpDexCodeAdapter((access_flags & DexOpcodes.ACC_STATIC) != 0,
                                method,
                                new PrintWriter(writer)) {
                            @Override
                            public void visitEnd() {
                                // TODO here maybe you can init the stringData
                                classItem.methodMap.put(method.toString(), writer.toString());
                            }
                        };
                    }
                };
            }
        };
    }

    @Override
    public void visitEnd() {
    }
}
