package robot;
import java.io.IOException;

import lejos.nxt.Button;
import robot.communication.BluetoothCommunicationException;
import robot.communication.BluetoothDiceConnection;
import robot.communication.IssuedInstruction;
import robot.communication.OnNewInstructionHandler;

/*
 * @author Joris S. Urbaitis
 */

public class Main {
	
	private static boolean quit = false;
	private static IssuedInstruction currentInstruction, newInstruction;
	
	public static void main(String[] args) {
		final BluetoothDiceConnection conn = new BluetoothDiceConnection(new OnNewInstructionHandler() {
			@Override
			public void onNewInstruction(IssuedInstruction instruction) {
				newInstruction = instruction;
			}

			@Override
			public void onExitRequested() {
				quit = true;
			}
		});
		
		conn.openConnection();
		conn.start();
		
		while(!quit) {
			if(currentInstruction != newInstruction) {
				System.out.println("Getting new instruction");
				currentInstruction = newInstruction;
				
				// Respond that the instruction has been completed
				try {
					conn.send(currentInstruction.getCompletedResponse());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (BluetoothCommunicationException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Exiting");

		conn.closeConnection();
	}
}