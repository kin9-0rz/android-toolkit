package parser.utils;

import org.junit.Test;

import java.io.File;

public class CertToolTest {

    @Test
    public void testGetCertificateInfos() throws Exception {
        String path = "/home/lai/Work/360卫士.apk";
        System.out.println(CertTool.getCertificateInfos(new File(path)));
    }
}