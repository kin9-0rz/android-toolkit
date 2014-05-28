package parser.elf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RelPlt {

	public RelPlt(ByteBuffer data, int relpltOff, int relpltSize, List<String> symbols)
	{
		this.relpltData = data;
		this.relpltOff = relpltOff;
		this.relpltSize = relpltSize;
//		System.out.println("import函数");
		for(int i=0; i<relpltSize; i = i+8)
		{
			int off = relpltOff + i;
			int index = data.getInt(off+4)>>8;
//		    System.out.println(symbols.get(index));
//			System.out.println("r_offset:"+Integer.toHexString(data.getInt(off)));
//			System.out.println("r_info:"+Integer.toHexString(data.getInt(off+4)));
		    impfuctions.add(symbols.get(index));
		}
		
	}
	public List<String> getImpFuctions()
	{
		return impfuctions;
	}
	
	ByteBuffer relpltData;
	int relpltOff;
	int relpltSize;
	private List<String> impfuctions = new ArrayList<String>();
	
}
