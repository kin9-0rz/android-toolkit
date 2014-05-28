package parser.elf;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class Sections {
    //各个属性节点的偏移地址。
    private static final int SH_NAME = 0x00;
    private static final int SH_TYPE = 0x04;
    private static final int SH_FLAGES = 0x08;
    private static final int SH_ADDR = 0xc;
    private static final int SH_OFFSET = 0x10;
    private static final int SH_SIZE = 0x14;
    private static final int SH_LINK = 0x18;
    private static final int SH_INFO = 0x1c;
    private static final int SH_ADDRALIGN = 0x20;
    private static final int SH_ENTSIZE = 0x24;
    //section属性
    private ByteBuffer m_sectionData;
    private int m_shoff;
    private int m_shentsize;
    private int m_shnum;
    private int m_shstrndx;
    private int m_nameOff;
    private int dynstrOff;
    private int dynsymOff;
    private int dynsymSize;
    private int dynamicOff;
    private int dynamicSize;
    private int relpltOff;
    private int relpltSize;
    private int roDataOff;
    private int roDataSize;
    private int roDataAlign;
    //各类字串字段
    private List<String> roDataStrings;
    private List<String> symbols;
    private List<String> impLib;
    private List<String> impFuctions;
    private List<String> exportFuctions;

    Sections(ByteBuffer data, int shoff, int sh_nameOff) {
        int nameoff = sh_nameOff + data.getInt(shoff + SH_NAME);
        int k = 0;
        while (data.get(nameoff + (k++)) != 0x0) ;
        String na = new String(Arrays.copyOfRange(data.array(), nameoff, nameoff + k - 1));
        System.out.println("\nsection:");
        System.out.println("sh_name:" + na);
        System.out.println("sh_type:" + Integer.toHexString(data.getInt(shoff + SH_TYPE)));
        System.out.println("sh_addr:" + Integer.toHexString(data.getInt(shoff + SH_ADDR)));
        System.out.println("sh_off:" + Integer.toHexString(data.getInt(shoff + SH_OFFSET)));
        System.out.println("sh_size:" + Integer.toHexString(data.getInt(shoff + SH_SIZE)));
        System.out.println("sh_link:" + Integer.toHexString(data.getInt(shoff + SH_LINK)));
        System.out.println("sh_info:" + Integer.toHexString(data.getInt(shoff + SH_INFO)));
        System.out.println("sh_addralign:" + Integer.toHexString(data.getInt(shoff + SH_ADDRALIGN)));
        System.out.println("sh_entrysize:" + Integer.toHexString(data.getInt(shoff + SH_ENTSIZE)));
        int sh_type = data.getInt(shoff + SH_TYPE);
        int sh_off = data.getInt(shoff + SH_OFFSET);
        int sh_size = data.getInt(shoff + SH_SIZE);
        if (sh_type == 3) {
            for (int i = 0; i < sh_size; ) {
                int b = i;
                while (data.get(sh_off + (i++)) != 0) {
                }
                byte[] aa = Arrays.copyOfRange(data.array(), sh_off + b, sh_off + i - 1);
                System.out.println(new String(aa));
            }
        }
    }

    Sections(ByteBuffer data, int shoff, int shentsize, int shnum, int shstrndx) {
        m_sectionData = data;
        m_shoff = shoff;
        m_shentsize = shentsize;
        m_shnum = shnum;
        m_shstrndx = shstrndx;
        int nameOff = m_sectionData.getInt(m_shoff + m_shentsize * m_shstrndx + SH_OFFSET);
        for (int i = 0; i < m_shnum; i++) {
            int off = m_shoff + i * m_shentsize;
            int noff = nameOff + m_sectionData.getInt(off + SH_NAME);
            int k = 0;
            while (m_sectionData.get(noff + (k++)) != 0x0) ;
            String na = new String(Arrays.copyOfRange(m_sectionData.array(), noff, noff + k - 1));
            if (".dynsym".equals(na)) {
                dynsymOff = m_sectionData.getInt(off + SH_OFFSET);
                dynsymSize = m_sectionData.getInt(off + SH_SIZE);
            } else if (".dynstr".equals(na) || ".strtab".equals(na)) {
                dynstrOff = m_sectionData.getInt(off + SH_OFFSET);
            } else if (".dynamic".equals(na)) {
                dynamicOff = m_sectionData.getInt(off + SH_OFFSET);
                dynamicSize = m_sectionData.getInt(off + SH_SIZE);
            } else if (".rel.plt".equals(na)) {
                relpltOff = m_sectionData.getInt(off + SH_OFFSET);
                relpltSize = m_sectionData.getInt(off + SH_SIZE);
            } else if (".rodata".equals(na)) {
                roDataOff = m_sectionData.getInt(off + SH_OFFSET);
                roDataSize = m_sectionData.getInt(off + SH_SIZE);
                roDataAlign = m_sectionData.getInt(off + SH_ADDRALIGN);

            }
//			System.out.printf("\nsection:%d\n",i);
//			System.out.println("sh_name:"+na);
//			System.out.println("sh_type:"+Integer.toHexString(data.getInt(off+SH_TYPE)));
//			System.out.println("sh_addr:"+Integer.toHexString(data.getInt(off+SH_ADDR)));
//			System.out.println("sh_off:"+Integer.toHexString(data.getInt(off+SH_OFFSET)));
//			System.out.println("sh_size:"+Integer.toHexString(data.getInt(off+SH_SIZE)));
//			System.out.println("sh_link:"+Integer.toHexString(data.getInt(off+SH_LINK)));
//			System.out.println("sh_info:"+Integer.toHexString(data.getInt(off+SH_INFO)));
//			System.out.println("sh_addralign:"+Integer.toHexString(data.getInt(off+SH_ADDRALIGN)));
//			System.out.println("sh_entrysize:"+Integer.toHexString(data.getInt(off+SH_ENTSIZE)));
        }
        Dynsym dynsym = new Dynsym(data, dynsymOff, dynstrOff, dynsymSize);
        symbols = dynsym.getDynsymbols();
        exportFuctions = dynsym.getExportFuction();
        Dynamic dynamic = new Dynamic(data, dynamicOff, dynstrOff, dynamicSize);
        impLib = dynamic.getImpLib();
        RelPlt relplt = new RelPlt(data, relpltOff, relpltSize, symbols);
        impFuctions = relplt.getImpFuctions();
        Datasym datas = new Datasym(data, roDataOff, roDataSize, roDataAlign);
        roDataStrings = datas.getRoDataStrings();

    }

    void dynsymParser() {

    }

    public int getDynstrOff() {
        return dynstrOff;
    }

    public int getDynsymOff() {
        return dynsymOff;
    }

    public List<String> getRoDataStrings() {
        return roDataStrings;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public List<String> getImpLib() {
        return impLib;
    }

    public List<String> getImpFuctions() {
        return impFuctions;
    }

    public List<String> getExportFuctions() {
        return exportFuctions;
    }

    public byte[] getNameArray(int shoff) {
        int sh_off = m_sectionData.getInt(shoff + SH_OFFSET);
        int sh_size = m_sectionData.getInt(shoff + SH_SIZE);
        System.out.println("sh_off:" + Integer.toHexString(sh_off));
        return Arrays.copyOfRange(m_sectionData.array(), sh_off, sh_off + sh_size);
    }


}
