package dice;

import java.io.IOException;

import dice.communication.RobotCommunication;
import dice.communication.RobotCommunicationCallback;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;

/*
 * @author Joris S. Urbaitis
 */

public class Main {
	public static void main (String[] args) {
		RobotCommunication.getInstance().init();
		
		RobotInstruction test = new RobotInstruction(
			RobotInstruction.MOVE_TO, (byte)4, (byte)4, RobotType.ATTACKER,
			new RobotCommunicationCallback() {
				@Override
				public void onError() {
					System.out.println("Error");
				}

				@Override
				public void onTimeout() {
					System.out.println("Timeout");
				}

				@Override
				public void onDone() {
					System.out.println("Done");
				}
		});

		RobotCommunication.getInstance().sendInstruction(test);

		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RobotCommunication.getInstance().close();
	}
}
