package parser.elf;

import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by lai on 12/26/13.
 */
public class ElfTest {
    @Test
    public void testLoadStrings() throws Exception {
        Elf elf = new Elf(new File("/home/lai/Work/samples/Exploit.AndroidOS.Exploid/" +
                "exploid-09ef9c605c80d8d8180af8689d9e0d282c6530f320724ce1d15117de90e4e0ee"));
//        System.out.println(elf.getExportFunctions());
//        System.out.println("ImpFuctions" + elf.getImpFunctions());
//        System.out.println(elf.getImpLib());
//        System.out.println(elf.getImpFunctions());
//        List<String> list =  elf.getSymbols();
//        Collections.sort(list);
//        System.out.println(list);
        List<String> list1 = elf.loadStrings();



        elf = new Elf(new File("/home/lai/Work/samples/Exploit.AndroidOS.Exploid/" +
                "exploid-617efb2d51ad5c4aed50b76119ad880c6adcd4d2e386b3170930193525b0563d"));
//        System.out.println(elf.getExportFunctions());
//        System.out.println("ImpFuctions" + elf.getImpFunctions());
//        System.out.println(elf.getImpLib());
//        System.out.println(elf.getSymbols());


        List<String> list2 = elf.loadStrings();
        Collections.sort(list2);
        System.out.println(list2);

        System.out.println("list1:" + list1.size());
        System.out.println("list2:" + list2.size());

        Collections.sort(list1);
        Collections.sort(list2);

        int count = 0;
        for (String str : list1) {
            if (list2.contains(str)) {
                count++;
            }
        }

        System.out.println(count);
    }

    @Test
    public void testGetSymbols() throws Exception {

    }

    @Test
    public void testGetImpLib() throws Exception {

    }

    @Test
    public void testGetImpFuctions() throws Exception {

    }

    @Test
    public void testGetExportFuctions() throws Exception {

    }
}
