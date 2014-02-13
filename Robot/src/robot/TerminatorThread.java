package robot;

import lejos.nxt.Button;

public class TerminatorThread extends Thread {
	
	public void run() {
		while (Button.readButtons() == 0);
	}

}
