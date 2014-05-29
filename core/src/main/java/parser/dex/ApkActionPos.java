package parser.dex;

import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.visitors.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * apk 行为位置定位和 class 相应字串收集
 *
 */
public class ApkActionPos implements DexFileVisitor {

    final Map<String, String> lApis;
    final Map<String, String> eApis;
    /**
     * 行为位置收集器
     */
    final List<String> posCollector;
    DexClass classItem;


    public ApkActionPos(DexClass c, List<String> collector, Map<String, String> lapis, Map<String, String> eapis) {
        this.classItem = c;
        this.posCollector = collector;
        lApis = lapis;
        eApis = eapis;
    }

    /**
     * 更新ClassDefItem，保存其相关的字串值
     */
    public ApkActionPos(DexClass c) {
        classItem = c;
        posCollector = null;
        lApis = null;
        eApis = null;
    }

    @Override
    public DexClassVisitor visit(int access_flags, String className, String superClass,
                                 String[] interfaceNames) {
        return new EmptyVisitor() {

            Method method;

            String field2String(Field field) {
                return field.getOwner() + "->" + field.getName() + ":" + field.getType();
            }

            String method2String(Method method) {
                return method.getOwner() + "->" + method.getName() + method.getDesc();
            }

            //DexClassVisitor
            @Override
            public DexFieldVisitor visitField(int accessFlags, Field field, Object value) {
                if (null != value) {
//                	classItem.stringData.add(field.getOwner());
                    classItem.stringData.add(field.getName());
//                	classItem.stringData.add(field.getType());
                    classItem.stringData.add(value.toString());
                    if (posCollector == null)
                        return this;
                    final String codeLine = field2String(field) + "=" + value.toString();
                    String type = filterKeyword(codeLine, lApis);
                    if (type != null) {
                        posCollector.add(classItem.className + "--->" + field.getName() + "\t" + type);
                    }
                }
                return this;
            }

            @Override
            public DexMethodVisitor visitMethod(int accessFlags, Method method) {
//            	classItem.stringData.add(method.getOwner());
//            	classItem.stringData.add(method.getName());
//            	for(String para : method.getParameterTypes())
//            		classItem.stringData.add(para);
//            	classItem.stringData.add(method.getReturnType());
                this.method = method;
                return this;
            }

            //DexMethodVisitor
            @Override
            public DexCodeVisitor visitCode() {
                return this;
            }

            //DexCodeVisitor             
            @Override
            public void visitFieldStmt(int opcode, int fromOrToReg, Field field, int xt) {
//            	classItem.stringData.add(field.getOwner());
                classItem.stringData.add(field.getName());
//            	classItem.stringData.add(field.getType());
                if (posCollector == null)
                    return;
                final String codeline = field2String(field);
                String type = filterKeyword(codeline, lApis);
                if (type != null) {
                    posCollector.add(classItem.className + "--->" + method.getName() + method.getDesc() + "\t" + type);
                }
            }

            @Override
            public void visitMethodStmt(int opcode, int[] regs, Method method) {
//            	classItem.stringData.add(method.getOwner());
                classItem.stringData.add(method.getName());
//            	for(String para : method.getParameterTypes())
//            		classItem.stringData.add(para);
//            	classItem.stringData.add(method.getReturnType());
                if (posCollector == null)
                    return;
                final String codeline = method2String(method);
                String type = filterKeyword(codeline, lApis);
                if (type != null) {
                    posCollector.add(classItem.className + "--->" + this.method.getName() + this.method.getDesc() + "\t" + type);
                }
            }

            @Override
            public void visitConstStmt(int opcode, int reg, Object value, int xt) {
                switch (opcode) {
                    case OP_CONST_STRING:
                        final String codeline = (String) value;
                        classItem.stringData.add(codeline);
                        if (posCollector == null)
                            return;
                        String type = filterKeyword(codeline, lApis);
                        if (type == null)
                            type = equalsKeyword(codeline, eApis);
                        if (type != null) {
                            posCollector.add(classItem.className + "--->" + method.getName() + method.getDesc() + "\t" + type);
                            break;
                        }
                        if (codeline.startsWith("106")) {
                            posCollector.add(classItem.className + "--->" + method.getName() + method.getDesc() + "\t" + codeline);
                        } else if (codeline.matches("(86|)(13|15|18)\\d{9}")) {
                            posCollector.add(classItem.className + "--->" + method.getName() + method.getDesc() + "\t" + codeline);
                        }

                        break;
                }
            }

        };
    }

    @Override
    public void visitEnd() {

    }

    private String filterKeyword(String codeline, Map<String, String> m) {
        if (m == null) {
            return null;
        }
        for (Entry<String, String> entrySet : m.entrySet()) {
            if (codeline.contains(entrySet.getKey())) {
                return entrySet.getValue();
            }
        }
        return null;
    }

    private String equalsKeyword(String codeline, Map<String, String> m) {
        if (m == null) {
            return null;
        }
        for (Entry<String, String> entrySet : m.entrySet()) {
            if (codeline.equals(entrySet.getKey())) {
                return entrySet.getValue();
            }
        }
        return null;
    }


}
