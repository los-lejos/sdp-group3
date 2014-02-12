package dice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VisionThread extends Thread {

	private Process p;
	
	public VisionThread(Process p) {
		this.p = p;
	}
	
	@Override
	public void run() {
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String s = null;

		try {
			// read the input
			while ((s = stdInput.readLine()) != null) {
				Log.logError(s);
			}
			
		} catch (IOException e) {
			Log.logError("exception occured");
			e.printStackTrace();
		}
	}
	
}
