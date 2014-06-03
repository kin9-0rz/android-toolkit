package parser.dex;

import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * dex中class结构：id，名称，父亲，成员变量，方法。
 */
public class DexClass {

    /**
     * 类ID
     */
    public int classIdx;
    public String className;
    public String superName;
    public List<TField> fields;
    public List<Method> methods;

    /**
     * method name : method body
     */
    public Map<String, String> methodMap;

    // TODO 这个还没有收集
    public List<String> stringData = new ArrayList<String>() {
        @Override
        public boolean add(String a) {

            if (!contains(a))
                super.add(a);
            return true;
        }
    };

    public static class TField {
        public Field field;
        public Object value;
    }

    @Override
    public String toString() {
        return "DexClass{" +
                "classIdx=" + classIdx +
                ", className='" + className + '\'' +
                ", superName='" + superName + '\'' +
                ", fields=" + fields +
                ", methods=" + methods +
                ", methodMap=" + methodMap +
                ", stringData=" + stringData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DexClass dexClass = (DexClass) o;

        return classIdx == dexClass.classIdx && className.equals(dexClass.className);

    }

    @Override
    public int hashCode() {
        int result = classIdx;
        result = 31 * result + className.hashCode();
        return result;
    }
}
