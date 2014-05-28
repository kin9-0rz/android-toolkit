package parser.elf;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Datasym {

	public Datasym(ByteBuffer data, int roDataOff, int roDataSize, int addralign) {
		int off = roDataOff;
		while(off<roDataOff+roDataSize)
		{
			int beginOff = off;
			while(data.get(off++) != 0x0);
			if(off-1>beginOff)
			{
				
				try {
					String na = new String(Arrays.copyOfRange(data.array(), beginOff, off-1),"utf-8");
//					System.out.println(na);	
					symbols.add(na);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<String> getRoDataStrings()
	{
		return symbols;
	}
	List<String> symbols = new ArrayList<String>();

}
