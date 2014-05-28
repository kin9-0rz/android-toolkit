package parser.elf;

import java.nio.ByteBuffer;


public class Program {
    private static final int P_TYPE = 0x00;
    private static final int P_OFFSET = 0x04;
    private static final int P_VADDR = 0x08;
    private static final int P_PADDR = 0x0C;
    private static final int P_FILESZ = 0x10;
    private static final int P_MEMSZ = 0x14;
    private static final int P_FLAGS = 0x18;
    private static final int P_ALIGN = 0x1c;
    ByteBuffer procData;
    int phoff;
    int phentsize;
    int phnum;

    Program(ByteBuffer data, int phoff) {
        System.out.println("\nprogram:");
        System.out.println("p_type:" + Integer.toHexString(data.getInt(phoff + P_TYPE)));
        System.out.println("p_offset:" + Integer.toHexString(data.getInt(phoff + P_OFFSET)));
        System.out.println("p_vaddr:" + Integer.toHexString(data.getInt(phoff + P_VADDR)));
        System.out.println("p_paddr:" + Integer.toHexString(data.getInt(phoff + P_PADDR)));
        System.out.println("p_filesz:" + Integer.toHexString(data.getInt(phoff + P_FILESZ)));
        System.out.println("p_flages:" + Integer.toHexString(data.getInt(phoff + P_FLAGS)));
    }

    Program(ByteBuffer data, int phoff, int phentsize, int phnum) {
        this.procData = data;
        this.phoff = phoff;
        this.phentsize = phentsize;
        this.phnum = phnum;
        for (int i = 0; i < phnum; i++) {
            int off = phoff + i * phentsize;
//			System.out.printf("\nprogram:%d\n", i);
//			System.out.println("p_type:"+Integer.toHexString(data.getInt(off+P_TYPE)));
//			System.out.println("p_offset:"+Integer.toHexString(data.getInt(off+P_OFFSET)));
//			System.out.println("p_vaddr:"+Integer.toHexString(data.getInt(off+P_VADDR)));
//			System.out.println("p_paddr:"+Integer.toHexString(data.getInt(off+P_PADDR)));
//			System.out.println("p_filesz:"+Integer.toHexString(data.getInt(off+P_FILESZ)));
//			System.out.println("p_memsz:"+Integer.toHexString(data.getInt(off+P_MEMSZ)));
//			System.out.println("p_flages:"+Integer.toHexString(data.getInt(off+P_FLAGS)));

        }
    }
}
