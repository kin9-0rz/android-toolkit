package parser.axml;

import org.junit.Test;

import java.io.File;

public class ParserTest {

    @Test
    public void testReadAxml() throws Exception {
        File pFile = new File("./test-apk/error.apk");

        Parser parser = new Parser(pFile);
        ManifestInfo manifestInfo = parser.getManifestInfo();
//
//        for (String key : manifestInfo.metaData.keySet()) {
//            System.out.println(key + ":" + manifestInfo.metaData.get(key));
//        }
        System.out.println(manifestInfo);


    }
}