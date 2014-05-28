package parser.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFile {
    private File rootDir = null;
    private List<File> result = new ArrayList<File>();
    private int count = 0;

    public SearchFile(File rootDir) {
        this.rootDir = rootDir;
    }

    public SearchFile(String rootDir) {
        this.rootDir = new File(rootDir);
    }

    public final List<File> listDAFolders() {
        result.clear();
        File[] folders = rootDir.listFiles();
        if (folders != null) {
            for (int i = 0; i < folders.length; i++) {
                if (!folders[i].isFile()
                        && folders[i].getName().matches("^.*\\_.*\\_S$")) {
                    result.add(folders[i]);
                    count++;
                    if (count > 10) {
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取文件夹内 matchingExtension 类型的所有文件
     *
     * @param matchingExtension
     * @return
     */
    public List<File> listAllFiles(String... matchingExtension) {
        result.clear();
        recursiveSearch(rootDir, matchingExtension);
        return result;
    }

    public List<File> listCMFiles() {
        result.clear();
        recursiveSearch50(rootDir, "apk", "sis", "sisx");
        return result;
    }

    private void recursiveSearch50(File rootDir, String... suffix) {
        File[] files = rootDir.listFiles();
        if (files != null)
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    String fileName = files[i].getName().toLowerCase();
                    for (String ext : suffix) {
                        if (count > 50) {
                            return;
                        }
                        if (fileName.toLowerCase().endsWith(ext)) {
                            result.add(files[i]);
                            count++;
                        }
                    }
                } else
                    recursiveSearch50(files[i], suffix);
            }
    }

    private void recursiveSearch(File rootDir, String... suffix) {
        File[] files = rootDir.listFiles();
        if (files != null)
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    String fileName = files[i].getName().toLowerCase();
                    for (String ext : suffix) {
                        if (fileName.endsWith(ext))
                            result.add(files[i]);
                    }
                } else
                    recursiveSearch(files[i], suffix);
            }
    }

    protected final List<File> listFiles() {
        result.clear();
        File[] folders = rootDir.listFiles();
        if (folders != null) {
            for (int i = 0; i < folders.length; i++) {
                if (folders[i].isFile()) {
                    result.add(folders[i]);
                }
            }
        }
        return result;
    }

}
