package parser.axml;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ManifestParserTest {
    @Test
    public void print() {
        // 解析清单信息
        final ManifestParser mp = new ManifestParser();

        try {
            ManifestInfo manifestInfo = mp.parse(new File("/Users/bin/Downloads/error.apk"));
            System.out.println(manifestInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

