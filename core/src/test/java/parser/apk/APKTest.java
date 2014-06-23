package parser.apk;

import com.googlecode.dex2jar.reader.DexFileReader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipException;

/**
 * Created with IntelliJ IDEA.
 * User: lai
 * Date: 5/28/13
 * Time: 11:50 AM
 */
public class APKTest {
    @Test
    public void testGetFileName() {


        APK apk = null;
        try {
            apk = new APK(new File("/home/lai/Work/samples/Exploit.AndroidOS.Lotoor/test/2011-02-05-0ed1ce633b6cfe411f251af1ce290f5ed0845845b0c96e671f8cddfe97969f47.apk"));
        } catch (ZipException e) {
            System.out.println("Zip 异常 : " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        } catch (Exception e) {
            System.out.println("异常:" + e.getCause());
            return;
        }




        System.out.println("<><><><>ADASDASD");

        System.out.println(apk.getCertificateInfos());
        System.out.println(apk.getFileName());
        System.out.println(apk.getPackageName());
        System.out.println(apk.getActivities());
        System.out.println(apk.getLabel());
        System.out.println(apk.getPermissions());
        System.out.println(apk.getReceivers());
        System.out.println(apk.getServices());
        System.out.println(apk.getVersionCode());
        System.out.println(apk.getVersionName());


//        System.out.println(apk.getStrings());

//        System.out.println(apk.getMethods());

        DexFileReader dexFileReader = apk.getDexFileReader();
        if (dexFileReader == null) {
            System.out.println("dexFileReader is null");
        } else {
            System.out.println(dexFileReader.loadStrings());
        }

        apk.getMethods();





        /*
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> methods = apk.getMethods();
        for (String key : methods.keySet()) {
            sb.append(methods.get(key));
            if (sb.indexOf("execute") != -1) {
                System.out.println(key);
            }
            if (sb.toString().contains("execute")) {
                System.out.println(key);
            }

            sb.delete(0, sb.length());
        }
           */

//        HashMap<String, HashSet<String>> set = apk.getStringsMap();
//        for (String cls : set.keySet()) {
//            System.out.print(cls + " >>> ");
//            System.out.println(set.get(cls));
//        }

//        List<ClassDefItem> classDefItems = apk.getDexClasses();
//
//        System.out.println(classDefItems.get(3).stringData);


//        DexFileReader dexFileReader = new DexFileReader(new File("/home/lai/Work/scan/20130521/test2.apk"));

//        System.out.println(dexFileReader.getClassSize());
//        for (String str : dexFileReader.loadStrings()) {
//            System.out.println(str);
//        }

//        final List<ClassDefItem> classList = new ArrayList<ClassDefItem>();
//        dexFileReader.accept(new ClassCollector(classList));
//        Collections.sort(classList);
//        Collections.sort(classList, new ComparatorClass());

//        for (ClassDefItem classDefItem : classList) {
//            System.out.print(classDefItem.className);

//
//            dexFileReader.visitClass(new CodeCollector(classDefItem), classDefItem.classIdx,
//                    DexFileReader.SKIP_DEBUG | DexFileReader.SKIP_ANNOTATION);


//            System.out.print("-" + classDefItem.fields.size());
//            System.out.print("-" + classDefItem.methods.size());

//            System.out.println("-" + classDefItem.methodMap.size());
//            if (classDefItem.methods.size() > 0) {
//                for (Method mtd : classDefItem.methods) {
//                    System.out.println("\t" + mtd.getName());
//                }
//            }
//
//            if (classDefItem.methodMap.size() > 0) {
//                for (String key : classDefItem.methodMap.keySet()) {
////                    System.out.println("\t" + key);
//                    if(classDefItem.methodMap.get(key).contains("Lorg/apache/http/impl/client/DefaultHttpClient;.execute")) {
//                        System.out.print(classDefItem.className);
//                        System.out.println("\t" + key);

//                        System.out.println(classDefItem.methodMap.get(key));
//                    }
//
//                }
//            }

    }

//    class ComparatorClass implements Comparator<ClassDefItem> {
//        @Override
//        public int compare(ClassDefItem arg0, ClassDefItem arg1) {
//            final String name0 = arg0.className;
//            final String name1 = arg1.className;
//            return name0.compareTo(name1);
//        }
//    }


    @Test
    public void testGetMethods() {
        String path = "/home/lai/Work/test.apk";
        APK apk;
        try {
            apk = new APK(new File(path));
//            System.out.println(apk.getMethods());

            System.out.println(apk.getStringsMap());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Test
    public void testAPKSubAPK() {




        APK apk;
        try {
            apk = new APK("/home/lai/Work/test.apk");
            System.out.println(apk.getCertificateInfos());

            apk = new APK("/home/lai/Work/samples/Exploit.AndroidOS.DroidDream/Test/" +
                    "2011-02-17-f49410d96e93822e44dfa1a45b55e250abca233a43f4afb0237a1a0f76b62e67.apk");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            HashMap<String, APK> hashMap = apk.getSubApkDataMap();
            for (String str : hashMap.keySet()) {
                System.out.println(str);
                System.out.println(hashMap.get(str).getCertificateInfos());
                System.out.println(hashMap.get(str).getPackageName());
                System.out.println(hashMap.get(str).getPermissions());

            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }




//        System.out.println(apk.getStrings());

//        System.out.println(apk.getMethods());

//        DexFileReader dexFileReader = apk.getDexFileReader();
//        if (dexFileReader == null) {
//            System.out.println("dexFileReader is null");
//        } else {
//            System.out.println(dexFileReader.loadStrings());
//        }
//
//        System.out.println(apk.getMethods());

    }


}
