package parser.elf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dynsym {

    private final static int ST_NAME = 0X0;
    private final static int ST_VALUE = 0X04;
    private final static int ST_SIZE = 0X08;
    private final static int ST_INFO = 0X0c;
    private final static int ST_OTHER = 0X0d;
    private final static int ST_SHNDX = 0X0e;
    List<String> exportFuction = new ArrayList<>();
    List<String> symbols = new ArrayList<>();
    ByteBuffer data;
    int dynsymOff;
    int dynstrOff;
    int dynsymSize;

    public Dynsym(ByteBuffer data, int dynsymOff, int dynstrOff, int dynsymSize) {
        this.data = data;
        this.dynsymOff = dynsymOff;
        this.dynstrOff = dynstrOff;
        this.dynsymSize = dynsymSize;
        for (int i = 0; i < dynsymSize; i = i + 16) {
            int off = dynsymOff + i;
            int noff = dynstrOff + data.getInt(off + ST_NAME);
            int st_type = data.get(off + ST_INFO) & 0x0f;
            int st_shndx = data.getShort(off + ST_SHNDX);
//			System.out.println("\nst_value:"+Integer.toHexString(data.getInt(off+ST_VALUE)));
//			System.out.println("st_size:"+data.getInt(off+ST_SIZE));
//			System.out.println("st_bin:"+((data.get(off+ST_INFO)>>4)&0x0f));
//			System.out.println("st_type:"+(data.get(off+ST_INFO)&0x0f));
//			System.out.println("st_shndx:"+data.getShort(off+ST_SHNDX));
            int k = 0;
            while (data.get(noff + (k++)) != 0x0) ;
            String na = new String(Arrays.copyOfRange(data.array(), noff, noff + k - 1));
            symbols.add(na);
            if ((st_type == 1 || st_type == 2) && st_shndx != 0)
                exportFuction.add(na);
//				System.out.println(na+"->"+i/16);
        }
    }

    public List<String> getDynsymbols() {
        return symbols;
    }

    public List<String> getExportFuction() {
        return exportFuction;
    }
}
