package parser.dex;

import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * dex中class结构：id，名称，父亲，成员变量，方法。
 */
public class ClassDefItem {
    /**
     * 类ID
     */
    public int classIdx;
    public String className;
    public String superName;
    public List<TField> fields;
    public List<Method> methods;
    /**
     * 方法的内容，详情看 DumpDexCodeAdapter
     */
    public Map<String, String> methodBodys;
    /**
     * 后面增加内容,目的：收集这个类的所有字符串
     */
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

}
