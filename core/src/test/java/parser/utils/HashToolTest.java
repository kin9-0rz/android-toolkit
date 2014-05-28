package parser.utils;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class HashToolTest {
    String path;

    @Before
    public void init() {
        path = "/home/lai/Work/samples/HackTool.AndroidOS.SuperUser/su-f31d95c90863bc03fb4c3361d12fed07eb4095cb9ed1369a2b1f262c15fbaa8a";
        System.out.println(new File(path).getParentFile().getName());
    }

    @Test
    public void testGetSHA256() throws Exception {
        System.out.println(HashTool.getSHA256(new File(path)));
    }

    @Test
    public void testGetMD5() throws Exception {

        System.out.println("745513a53af2befe3dc00d0341d80ca6");
        System.out.println(HashTool.getMD5(new File(path)));

    }
}