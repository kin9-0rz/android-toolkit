package parser.utils;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by lai on 1/14/14.
 */
public class DirectoryTraversal {
    /**
     * 260522文件 151910 ms
     * @param dirPath
     */
    public static void unrecursive(String dirPath) {
        /**
         * 文件计数器
         */
        long count = 0;
        long countd = 0;

        //链表
        LinkedList<File> fileLinkedList = new LinkedList<File>();
        File dir = new File(dirPath);
        File[] file = dir.listFiles();

        if (file == null) {
            return;
        }

        for (int i = 0; i < file.length; i++) {
            if (file[i].isDirectory())
                //把第一层的目录，全部放入链表
                fileLinkedList.add(file[i]);
            else
                count++;
            System.out.println("文件" + count + ":" + file[i].getAbsolutePath());
        }


        File tmp = null;

        //循环遍历链表
        while (!fileLinkedList.isEmpty()) {
            //把链表的第一个记录删除
            tmp = fileLinkedList.removeFirst();
            //如果删除的目录是一个路径的话
            if (tmp.isDirectory()) {
                //列出这个目录下的文件到数组中
                file = tmp.listFiles();
                if (file == null)
                    continue;
                //遍历文件数组
                for (int i = 0; i < file.length; i++) {
                    if (file[i].isDirectory())
                        //如果遍历到的是目录，则继续加入链表
                        fileLinkedList.add(file[i]);
                    else
                        count++;
                    System.out.println("文件" + count + ":" + file[i].getAbsolutePath());
                }
            } else {
                countd++;
                System.out.println("目录[" + countd + "]路径:" + tmp.getAbsolutePath());
            }
        }
    }


//    private static ArrayList filelist = new ArrayList();
    public static int c = 0;
    public static void main(String[] args) {
        long a = System.currentTimeMillis();
//        unrecursive("/home/lai/");
        recursive("/home/lai/Downloads/malware_analysised/");
//        File file = new File("/home/lai/");
//        final Collection<File> files = FileUtils.listFiles(file, null, true);
        System.out.println(System.currentTimeMillis() - a);
    }


    /**
     * 260522文件 100161 ms
     * @param strPath
     */
    public static void recursive(String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files == null)
            return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                recursive(files[i].getAbsolutePath());
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
//                filelist.add(files[i].getAbsolutePath());
                c++;
                System.out.println(c + " " + strFileName);
            }
        }
    }
}
