package robot;

import lejos.nxt.Button;

public class TerminatorThread extends Thread {
	
	private boolean exit = false;
	
	public void run() {
		while (Button.readButtons() == 0 || exit);
	}
	
	public void exit() {
		this.exit = true;
		System.out.println("Hasta la vista");
	}

}
