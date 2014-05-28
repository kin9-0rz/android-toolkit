package parser.elf;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class Elf {
    //elf头部偏移地址
    private static final int E_IDENT = 0x00;
    private static final int E_TYPE = 0x10;
    private static final int E_MACHINE = 0x12;
    private static final int E_VERSION = 0x14;
    private static final int E_ENTRY = 0x18;
    private static final int E_PHOFF = 0x1C;
    private static final int E_SHOFF = 0x20;
    private static final int E_FLAGES = 0x24;
    private static final int E_EHSIZE = 0x28;
    private static final int E_PHENTSIZE = 0x2A;
    private static final int E_PHNUM = 0x2C;
    private static final int E_SHENTSIZE = 0x2E;
    private static final int E_SHNUM = 0x30;
    private static final int E_SHSTRNDX = 0x32;
    private List<String> roDataString;
    private List<String> symbols;
    private List<String> impLib;
    private List<String> impFunctions;
    private List<String> exportFunctions;

    //头节点属性
    private int type;
    private int entry;
    private int phoff;
    private int phentsize;
    private int phnum;
    private int shoff;
    private int shentsize;
    private int shnum;
    private int shstrndx;
    private int sh_nameOff;

    /**
     * @param elfFile :so文件。
     */
    public Elf(File elfFile) {
        this(FileToByte(elfFile));
    }

    /**
     * @param data :so字节序列
     */
    public Elf(byte[] data) {
        ByteBuffer elf = ByteBuffer.wrap(data);
        elf.order(ByteOrder.LITTLE_ENDIAN);
//            System.out.println("type:\t"+elf.getShort(E_TYPE));
//            System.out.println("machine:\t"+elf.getShort(E_MACHINE));
//            System.out.println("version:\t"+elf.getShort(E_VERSION));
//            System.out.println("entry:\t"+elf.getInt(E_ENTRY));
//            System.out.println("phoff:\t"+elf.getInt(E_PHOFF));
//            System.out.println("shoff:\t"+Integer.toHexString(elf.getInt(E_SHOFF)));
//            System.out.println("flages:\t"+elf.getInt(E_FLAGES));
//            System.out.println("ehsize:\t"+elf.getShort(E_EHSIZE));
//            System.out.println("phentsize:\t"+elf.getShort(E_PHENTSIZE));
//            System.out.println("phnum:\t"+elf.getShort(E_PHNUM));
//            System.out.println("shentsize:\t"+elf.getShort(E_SHENTSIZE));
//            System.out.println("shnum:\t"+elf.getShort(E_SHNUM));
//            System.out.println("shstrndx:\t"+elf.getShort(E_SHSTRNDX));
        type = elf.getShort(E_TYPE);
        entry = elf.getInt(E_ENTRY);
        phoff = elf.getInt(E_PHOFF);
        phentsize = elf.getShort(E_PHENTSIZE);
        phnum = elf.getShort(E_PHNUM);
        shoff = elf.getInt(E_SHOFF);
        shentsize = elf.getShort(E_SHENTSIZE);
        shnum = elf.getShort(E_SHNUM);
        shstrndx = elf.getShort(E_SHSTRNDX);
        Sections sec = new Sections(elf, shoff, shentsize, shnum, shstrndx);
        roDataString = sec.getRoDataStrings();
        symbols = sec.getSymbols();
        impFunctions = sec.getImpFuctions();
        exportFunctions = sec.getExportFuctions();
        impLib = sec.getImpLib();
//        Program proc = new Program(elf, phoff, phentsize, phnum);

    }

    private static byte[] FileToByte(File elfFile) {
        byte[] data = new byte[(int) elfFile.length()];
        try {
            FileInputStream fis = new FileInputStream(elfFile);
            //noinspection ResultOfMethodCallIgnored
            fis.read(data);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * @return 数据段数据
     */
    public List<String> loadStrings() {
        return roDataString;
    }

    /**
     * @return 符号表数据
     */
    public List<String> getSymbols() {
        return symbols;
    }

    /**
     * @return 相关联的动态链接库
     */
    public List<String> getImpLib() {
        return impLib;
    }

    /**
     * @return 调用函数
     */
    public List<String> getImpFunctions() {
        return impFunctions;
    }

    /**
     * @return 动态链接导出表（包含函数和对象）
     */
    public List<String> getExportFunctions() {
        return exportFunctions;
    }

}
