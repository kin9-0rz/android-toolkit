package parser.elf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dynamic {

	public Dynamic(ByteBuffer data, int dynamicOff, int dynstrOff, int dynamicSize)
	{
		this.dynamicData = data;
		this.dynamicOff = dynamicOff;
		this.dynstrOff = dynstrOff;
		this.dynamicSize = dynamicSize;
		for(int i=0; i<dynamicSize; i=i+8)
		{
			int off = dynamicOff +i;
			if(data.getInt(off) ==1)
			{
				int noff = dynstrOff+data.getInt(off+4);
				int k=0;
				while(data.get(noff+(k++)) != 0x0);
				String na = new String(Arrays.copyOfRange(data.array(), noff, noff+k-1));
//				System.out.println(na);
				impLib.add(na);
			}
		}
	}
	
	public List<String> getImpLib()
	{
		return impLib;
	}
	
	private List<String> impLib = new ArrayList<String>();
	
	private ByteBuffer dynamicData;
	private int dynamicOff;
	private int dynstrOff;
	private int dynamicSize;

}
