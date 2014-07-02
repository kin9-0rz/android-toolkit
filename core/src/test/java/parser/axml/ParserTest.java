package parser.axml;

import org.junit.Test;

import java.io.File;

public class ParserTest {

    @Test
    public void testReadAxml() throws Exception {
        File pFile = new File("/home/lai/Work/360卫士.apk");

        Parser parser = new Parser(pFile);
        ManifestInfo manifestInfo = parser.getManifestInfo();
//
//        for (String key : manifestInfo.metaData.keySet()) {
//            System.out.println(key + ":" + manifestInfo.metaData.get(key));
//        }

        for (String key : manifestInfo.activities.keySet()) {

            System.out.println(key + " : "+ manifestInfo.activities.get(key));
        }

    }
}