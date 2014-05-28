package parser.utils;

import org.junit.Test;

import java.io.File;

public class FileTypesDetectorTest {


    @Test
    public void testGetType() throws Exception {
        String path = "/home/lai/Work/svn/08个人目录/lyb/malware_analysised/a.payment.PCSuitUpdate/a.payment.PCSuitUpdate.b/a.payment.PCSuitUpdate.b_001.apk";
        System.out.println("文件类型 " + FileTypesDetector.getType(path));
        System.out.println("是否APK " + FileTypesDetector.isAPK(new File(path)));

        System.out.println("=====================");

      //  path = "/home/lai/Downloads/2006FB65.exe.重命名";
       // System.out.println(FileTypesDetector.getType(path) + " - " + path);

        //System.out.println("=====================");
        //path = "/home/lai/Downloads/2006FBAF.exe.重命名";
        //System.out.println(FileTypesDetector.getType(path) + " - " + path);

        //System.out.println("=====================");


    }


}
