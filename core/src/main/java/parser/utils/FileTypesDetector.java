package parser.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 文件类型检测器
 *
 * @author lai
 */
public final class FileTypesDetector {

    /**
     * 将文件头转换成16进制字符串
     *
     * @param src File bytes
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        //for (int i = 0; i < src.length; i++) {
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    private static String isTextFile(InputStream inputStream) throws IOException {
        int len = 180;
        int off = 0;
        if (inputStream.available() < 180) {
            len = inputStream.available();
        }

        byte[] byteArr = new byte[len];
        int total = inputStream.read(byteArr, off, len);

//        System.out.println(total + " - isTextFile()");

        if (Charset.forName("US-ASCII").newEncoder().canEncode(new String(byteArr))) {
//            System.out.println("US-ASCII");
            return "US-ASCII text";
        }

        if (Charset.forName("ISO-8859-1").newEncoder().canEncode(new String(byteArr))) {
//            System.out.println("ISO-8859-1");
            return "US-ASCII text";
        }

        if (Charset.forName("GB2312").newEncoder().canEncode(new String(byteArr))) {
//            System.out.println("GB2312");
            return "GB2312 text";
        }

//        if (Charset.forName("GB18030").newEncoder().canEncode(new String(byteArr))) {
//            System.out.println("GB18030");
//            return "GB18030 text";
//        }

//        if (Charset.forName("HZ").newEncoder().canEncode(new String(byteArr))) {
//            System.out.println("HZ");
//            return "HZ text";
//        }

        if (Charset.forName("BIG5").newEncoder().canEncode(new String(byteArr))) {
//            System.out.println("BIG5");
            return "BIG5 text";
        }

  /*
        int counter = 0;
        int flag = 0;
        for (byte b : byteArr) {
            if (counter % 10 == 0) {
                System.out.println();
            }
            System.out.print(b + "\t");

            // 判断有没有char(0)字符。
            // 二进制文件基本上都会有char(0)，注意，是“基本上” 。
            if (b == 0) {
                if (flag == 0) {
                    flag = 1;
                } else {
                    return null;
                }
            }

            if (b != 0 && flag == 1) {
                flag = 0;
            }


//            if (b > 0 && flag == 1) {
//                System.out.println("\n");
//                return null;
//            }
            counter++;
        }

        System.out.println("\n");

        */
        return "text";
    }

    /**
     * @param begin       off
     * @param offset      offset
     * @param inputStream inputStream
     * @return String
     * @throws IOException
     */
    private static String readOffset(int begin, int offset, InputStream inputStream) throws IOException {
//        System.out.println(inputStream.markSupported());
        byte[] b = new byte[offset];
        inputStream.skip(begin);
        int total = inputStream.read(b, 0, offset);

//        System.out.println(total + "readOffset");

        if (total == -1) {
            return null;
        }

        return new String(b);
    }

    /**
     * 判断文件类型
     *
     * @param is 文件
     * @return 文件类型
     */
    public static String getType(InputStream is) throws IOException {

        int len = 100;
        int off = 0;
        if (is.available() <= 0) {
            return "Empty File";
        }

        if (is.available() < 100) {
            len = is.available();
        }

        InputStream inputStream;
        if (!is.markSupported()) {
            inputStream = new BufferedInputStream(is);
        } else {
            inputStream = is;
        }

        inputStream.mark(len);

        String fileHead;
        byte[] bytesArr = new byte[len];
        int total = inputStream.read(bytesArr, off, len);
        if (total != -1) {
            fileHead = bytesToHexString(bytesArr);
//            System.out.println(fileHead);
        } else {
            return null;
        }

        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }

        FileType[] fileTypes = FileType.values();

        for (FileType type : fileTypes) {
            if (fileHead.startsWith(type.getValue())) {

                return type.toString();
//                if (type.toString().equals("ZIP")) {
//                    if (fileHead.startsWith("504B0304140008000800")) {
//                        return "APK";
//                    }
//                    inputStream.reset();
//                    String str = FileTypesDetector.readOffset(off, len, inputStream);
//                    inputStream.reset();
//                    System.out.println(str);
//                    if (str.contains("META-INF/MANIFEST.MF") || str.contains("res/") || str.contains("classes.dex"))
//                        return "APK";
//                    return type.toString();
//                } else {
//                    return type.toString();
//                }

            }
        }
        inputStream.reset();
        String txt = isTextFile(inputStream);
        inputStream.close();
        if (txt != null) {
            return txt;
        }

        if (fileHead.startsWith("FFF")) {
            return "AUDIO_FILE";
        }

        return "DATA";
    }

    /**
     * 判断文件类型
     *
     * @param filePath 文件路径
     * @return 文件类型
     */
    public static String getType(String filePath) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
        return getType(inputStream);
    }

    public static boolean isAPK(InputStream inputStream) {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry;
        try {
            while (true) {
                entry = zipInputStream.getNextEntry();
                if (entry == null) {
                    break;
                }

                if (entry.getName().equals("classes.dex")) {
                    return true;
                }
            }

        }
        catch (ZipException e) {
//            System.out.println("It's a encrypted ZIP.");
            return false;
        }  catch (IOException e) {
            e.printStackTrace();
            return false;
        }  finally {
            try {
                zipInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    /**
     * 判断文件是否为 APK 文件
     *
     * @param pFile 文件
     * @return 为APK文件返回 true, 否则, 返回false.
     */
    public static boolean isAPK(File pFile) {

        String fileType;
        try {
            fileType = FileTypesDetector.getType(pFile.getAbsolutePath());
        } catch (IOException e) {
            return false;
        }

        if (!fileType.equals("ZIP") && !fileType.equals("APK")) {
            return false;
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(pFile);
            ZipEntry zipEntry = zipFile.getEntry("classes.dex");
            return zipEntry != null;
        } catch (IOException e) {
            return false;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断文件是否为 SIS 文件
     *
     * @param pFile 文件
     * @return 为APK文件返回 true, 否则, 返回false.
     */
    public static boolean isSIS(File pFile) {

        String fileType;
        try {
            fileType = FileTypesDetector.getType(pFile.getAbsolutePath());
        } catch (IOException e) {
            return false;
        }

        return fileType.contains("SIS");
    }


    public static boolean isDEX(File pFile) {
        String fileType;
        try {
            fileType = FileTypesDetector.getType(pFile.getAbsolutePath());
        } catch (IOException e) {
            return false;
        }

        return fileType.contains("DEX");
    }
}
