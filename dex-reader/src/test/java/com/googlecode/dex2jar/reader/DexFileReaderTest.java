package com.googlecode.dex2jar.reader;

import junit.framework.TestCase;

import java.io.File;

//import parser.dex.ClassCollector;
//import parser.dex.ClassDefItem;
//import parser.dex.CodeCollector;
//

/**
 * Created by lai on 12/4/13.
 */
public class DexFileReaderTest extends TestCase {
    DexFileReader dexFileReader;

//    @Before
    public void setUp() throws Exception {
        String dexFilePath = "/home/lai/Project/android-toolkit/dexs/classes2.dex";
        dexFileReader = new DexFileReader(new File(dexFilePath));
    }

//    @Test
    public void testLoadStrings() throws Exception {
        dexFileReader.loadStrings();

    }

//    @Test
    public void testReadDex() throws Exception {

    }


//    @Test
    public void testIsOdex() throws Exception {
        System.out.println("is odex? " + dexFileReader.isOdex());

    }

//    @Test
//    public void testParse() throws Exception {
//        HashMap<String, String> methods = new HashMap<String, String>();
//
//        final List<ClassDefItem> classDefItems = new ArrayList<ClassDefItem>();
//        dexFileReader.accept(new ClassCollector(classDefItems));
//        for (ClassDefItem classDefItem : classDefItems) {
//            dexFileReader.visitClass(new CodeCollector(classDefItem), classDefItem.classIdx,
//                    DexFileReader.SKIP_DEBUG | DexFileReader.SKIP_ANNOTATION);
//
//            if (classDefItem.methodBodys.size() > 0) {
//                for (String key : classDefItem.methodBodys.keySet()) {
////                    System.out.println(key);
////                    System.out.println(classDefItem.methodBodys.get(key));
////                    methods.put(key, classDefItem.methodBodys.get(key));
//                }
//            }
//        }
//
//    }

//    @Test
    public void testGetClassSize() throws Exception {

    }
}
