package dice;
import java.io.*;
import lejos.pc.*;
import lejos.pc.comm.*;

public class Main {
	public static void main (String[] args) {
		try {
			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		
			NXTInfo[] nxtInfo = nxtComm.search("NXT");
			
			for(NXTInfo info : nxtInfo) {
				System.out.println(info.name);
			}
		} catch (NXTCommException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
