package parser.dex;

import com.googlecode.dex2jar.reader.DexFileReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by acgmohu on 14-7-3.
 */
public class DEX {
    private DexFileReader dexFileReader;
    private List<DexClass> dexClasses = new ArrayList<>();

    public DEX(File file) throws IOException {
        dexFileReader = new DexFileReader(file);
        dexFileReader.accept(new DexFileAdapter(dexClasses),
                DexFileReader.SKIP_DEBUG | DexFileReader.SKIP_ANNOTATION);
    }

    public List<DexClass> getDexClasses() {
        return dexClasses;
    }

    /**
     * 获取所有类/方法/内容
     * La/b/c;->mtd;
     *
     * @return 方法Map <method : method body>
     */
    public HashMap<String, String> getMethods() {
        HashMap<String, String> methods = new HashMap<>();

        for (DexClass dexClass : dexClasses) {
            if (dexClass.methodMap.size() > 0) {
                for (String key : dexClass.methodMap.keySet()) {
                    methods.put(key, dexClass.methodMap.get(key));
                }
            }
        }

        return methods;
    }

    /**
     * 获得 DEX 中存在的字符
     *
     * @return 获取字符串
     */
    public List<String> getStrings() {
        return dexFileReader.loadStrings();
    }

}
