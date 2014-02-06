package dice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import dice.communication.RobotCommunication;
import dice.communication.RobotCommunicationCallback;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;

/*
 * @author Joris S. Urbaitis
 * @author Robin Scott
 *
 */

public class Main {
	public static void main (String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Ready");
		
		String[] cmd = null;
		do {
			System.out.print("> ");
			
			// Split on whitespace
			try {
				cmd = br.readLine().split("\\s+");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(cmd[0].equals("connect")) {
				execConnect(cmd);
			}
			else if(cmd[0].equals("send")) {
				execSend(cmd);
			}
			else if(cmd[0].equals("help")) {
				System.out.println("Robots are specified as 'a' for attacker/OptimusPrime and 'd' for defender/Ball-E");
				System.out.println("List of commands:");
				System.out.println("connect <robot> - starts up a bluetooth connection with the robot");
				System.out.println("send <robot> <instruction type> <param1> <param2> - sends an instruction to the robot. Parameters are bytes between -127 and 126");
				System.out.println("vision <options> - starts up vision system. Enter 'vision -h' for options formatting");
			}
			else if(cmd[0].equals("vision")) {
				startVision(cmd);		
			} 
			else if(!cmd[0].equals("quit")) {
				System.out.println("Unrecognized command");
			}
			
		} while(cmd == null || !cmd[0].equals("quit"));

		System.out.println("Exiting");
		RobotCommunication.getInstance().close();
	}
	
	private static void execConnect(String[] cmd) {
		RobotType type = getRobotTypeFromCommand(cmd);
		
		if(type != null) {
			RobotCommunication.getInstance().init(type);
		}
	}
	
	private static void execSend(String[] cmd) {
		RobotType type = getRobotTypeFromCommand(cmd);
		
		if(type != null) {
			byte instructionType = Byte.parseByte(cmd[2]);
			byte param1 = Byte.parseByte(cmd[3]);
			byte param2 = Byte.parseByte(cmd[4]);
			byte param3 = Byte.parseByte(cmd[5]);
			final String display = "" + instructionType + param1 + param2 + param3;
			
			RobotCommunicationCallback callback = new RobotCommunicationCallback() {
				@Override
				public void onError() {
					System.out.println("\nError occured while sending instruction:\n" + display);
				}
	
				@Override
				public void onTimeout() {
					System.out.println("\nTimeout occured while sending instruction:\n" + display);
				}
	
				@Override
				public void onDone() {
					System.out.println("\nRobot sent completion response for instruction:\n" + display);
				}
			};
			
			RobotInstruction instruction = new RobotInstruction(instructionType, param1, param2, param3, type, callback);
			RobotCommunication.getInstance().sendInstruction(instruction);
		}
	}
	
	private static RobotType getRobotTypeFromCommand(String[] cmd) {
		if(cmd[1].equals("d")) {
			return RobotType.DEFENDER;
		} else if(cmd[1].equals("a")) {
			return RobotType.ATTACKER;
		}
		
		System.out.println("Invalid robot type. Accepted are 'a' for attacker and 'd' for defender");
		
		return null;
	}
	
	private static void startVision(String[] cmd) {
		String options = Arrays.toString(cmd);               
		options = options.substring(1, options.length()-1).replaceAll(",", "");
		String pythonCmd = "python vision/vision.py " + options;
		String s = null;
		
		try {
			Process p = Runtime.getRuntime().exec(pythonCmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// read the output
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
			// read any errors
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
			
		} catch (IOException e) {
			System.out.println("exception occured");
			e.printStackTrace();
		}

	}
}
