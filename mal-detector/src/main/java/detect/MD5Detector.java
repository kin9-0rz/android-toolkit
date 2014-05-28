package detect;

/**
 * 检索样本库
 * file_md5, mal_id.
 * mal
 */
public class MD5Detector {

    public static String detect(String fileMd5) {
        return query(fileMd5);
    }

    /**
     * 在样本库查询，文件MD5.
     * @param fileMd5   文件MD5
     * @return mal_id
     */
    private static String query(String fileMd5) {
        return null;
    }
}
