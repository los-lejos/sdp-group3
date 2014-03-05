package dice.misc;

import dice.communication.RobotCommunicationCallback;

public class UsefulCallback implements RobotCommunicationCallback {
	
	public boolean cont = false;
	@Override
	public void onError() {
		System.out.println("Error thingy");
	}

	@Override
	public void onTimeout() {
		System.out.println("Timeout thingy");
	}

	@Override
	public void onDone() {
		this.cont = true;
		System.out.println("cont set to true");
	}
}