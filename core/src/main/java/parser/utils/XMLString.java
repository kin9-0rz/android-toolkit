package parser.utils;

/**
 * Created by SlowMan on 14-6-4.
 * XML內容的轉義處理
 */
public class XMLString {
    public static String escape(String str) {
        str = str.replace("\\\'", "\\");
        str = str.replace("\n", "&#x000A;");
        str = str.replace("&", "&amp;");
        str = str.replace(">", "&gt;");
        str = str.replace("<", "&lt;");
        str = str.replace("\\t", "&#x0009");
        str = str.replace("\\\'", "&apos;");
        str = str.replace("\\\"", "&quot;");

        return str;
    }

    /**
     * XML 字符串的轉意
     * @param obj
     * @return
     */
    public static String escape(Object obj) {
        String str = obj.toString();
        str = str.replace("\\\'", "\\");
        str = str.replace("\n", "&#x000A;");
        str = str.replace("&", "&amp;");
        str = str.replace(">", "&gt;");
        str = str.replace("<", "&lt;");
        str = str.replace("\\t", "&#x0009");
        str = str.replace("\\\'", "&apos;");
        str = str.replace("\\\"", "&quot;");

        return str;
    }
}
