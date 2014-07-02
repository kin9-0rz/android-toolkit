package pxb.android.axml.test;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import pxb.android.Example;
import pxb.android.axml.AxmlReader;
import pxb.android.axml.AxmlWriter;
import pxb.android.axml.DumpAdapter;
import pxb.android.axml.NodeVisitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Test4 {
    @Test
    public void test() throws IOException {
        AxmlWriter aw = new AxmlWriter();
        NodeVisitor nv = aw.child("http://abc.com", "abc");
        nv.end();
        nv = aw.child("http://efg.com", "efg");
        nv.end();
        aw.end();
        AxmlReader ar = new AxmlReader(aw.toByteArray());
        ar.accept(new DumpAdapter());
    }

    @Test
    public void test2() throws IOException {
        AxmlWriter aw = new AxmlWriter();
        aw.ns("efg", "http://abc.com", -1);
        NodeVisitor nv = aw.child("http://abc.com", "abc");
        nv.end();
        nv = aw.child("http://efg.com", "efg");
        nv.end();
        aw.end();
        AxmlReader ar = new AxmlReader(aw.toByteArray());
        ar.accept(new DumpAdapter());
    }


    @Test
    public void testA() throws IOException {
        ZipFile zipFile;
        InputStream aXMLInputStream = null;
        InputStream arscInputStream = null;

        File pFile = new File("/home/lai/Work/360卫士.apk");
        zipFile = new ZipFile(pFile);
        ZipEntry zipEntry = zipFile.getEntry("AndroidManifest.xml");

        if (zipEntry != null) {
            aXMLInputStream = zipFile.getInputStream(zipEntry);

            Example example = new Example();
            example.readAxml(IOUtils.toByteArray(aXMLInputStream));
        }
    }
}
