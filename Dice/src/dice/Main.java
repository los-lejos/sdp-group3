package dice;

import java.io.IOException;

import dice.communication.RobotCommunication;

/*
 * @author Joris S. Urbaitis
 */

public class Main {
	public static void main (String[] args) {
		RobotCommunication.getInstance().init();
		

		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RobotCommunication.getInstance().close();
	}
}
