package dice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import dice.communication.BluetoothRobotCommunicator;
import dice.communication.RobotCommunicationCallback;
import dice.communication.RobotCommunicator;
import dice.communication.RobotEventListener;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;
import dice.state.WorldState.BallPossession;
import dice.strategy.StrategyEvaluator;
import dice.strategy.StrategyEvaluator.StrategyType;
import dice.vision.SocketVisionReader;

/*
 * @author Joris S. Urbaitis
 * @author Robin Scott
 *
 */

public class Main {
	public static void main (String[] args) {
		Main program = new Main();
		program.init();
		program.run();
		program.cleanup();
	}
	
	private BufferedReader br;
	
	private RobotCommunicator attackerComms, defenderComms;
	private StrategyEvaluator strategy;
	private WorldState worldState;
	private SocketVisionReader visionReader;
	
	private RobotEventListener attackerEventListener = new RobotEventListener() {
		@Override
		public void onBallCaught() {
			synchronized(worldState) {
				worldState.setBallPossession(BallPossession.OUR_ATTACKER);
			}
		}

		@Override
		public void onBallReleased() {
			synchronized(worldState) {
				worldState.setBallPossession(BallPossession.NONE);
			}
		}
	};
	
	private RobotEventListener defenderEventListener = new RobotEventListener() {
		@Override
		public void onBallCaught() {
			synchronized(worldState) {
				worldState.setBallPossession(BallPossession.OUR_DEFENDER);
			}
		}

		@Override
		public void onBallReleased() {
			synchronized(worldState) {
				worldState.setBallPossession(BallPossession.NONE);
			}
		}
	};

	public void init() {
		Log.init();
		br = new BufferedReader(new InputStreamReader(System.in));
		
		worldState = WorldState.init();
        worldState.setSide(WorldState.Side.LEFT);
		this.attackerComms = new BluetoothRobotCommunicator();
		this.defenderComms = new BluetoothRobotCommunicator();

		strategy = new StrategyEvaluator();
		strategy.setType(StrategyType.M3_DEFENDER);
		strategy.setCommunicator(RobotType.ATTACKER, attackerComms);
		strategy.setCommunicator(RobotType.DEFENDER, defenderComms);
		
		this.visionReader = new SocketVisionReader(worldState, strategy);
	}
	
	public void cleanup() {
		Log.close();
		if(this.attackerComms != null) {
			this.attackerComms.close();
		}
		
		if(this.defenderComms != null) {
			this.defenderComms.close();
		}
		
		this.visionReader.stop();
	}
	
	public void run() {
		Log.logInfo("Ready");
		String[] cmd = null;
		
		do {
			Log.logPrint("> ");
			
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
				Log.logInfo("Robots are specified as 'a' for attacker/OptimusPrime and 'd' for defender/Ball-E");
				Log.logInfo("List of commands:");
				Log.logInfo("connect <robot> - starts up a bluetooth connection with the robot");
				Log.logInfo("send <robot> <instruction type> <param1> <param2> - sends an instruction to the robot. Parameters are bytes between -127 and 126");
				Log.logError("vision <options> - starts up vision system. Enter 'vision -h' for options formatting");
			}
			else if(cmd[0].equals("vision")) {
				startVision(cmd);		
			} 
			else if(!cmd[0].equals("quit")) {
				Log.logError("Unrecognized command");
			}
			
		} while(cmd == null || !cmd[0].equals("quit"));
		
		// send the quit command to both robots
		execSend(new String[] {"0", "0", "10", "0", "0", "0"});
		execSend(new String[] {"0", "0", "10", "0", "0", "0"});

		Log.logInfo("Exiting");
	}
	
	private void execConnect(String[] cmd) {
		RobotType type = getRobotTypeFromCommand(cmd);
		
		if(type != null) {
			if(type == RobotType.ATTACKER) {
				this.attackerComms.init(RobotType.ATTACKER, this.attackerEventListener);
			} else {
				this.defenderComms.init(RobotType.DEFENDER, this.defenderEventListener);
			}
		}
	}
	
	private void execSend(String[] cmd) {
		if(cmd.length < 5) {
			Log.logInfo("Not enough parameters specified");
			return;
		}
		
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
					Log.logError("\nError occured while sending instruction:\n" + display);
				}
	
				@Override
				public void onTimeout() {
					Log.logError("\nTimeout occured while sending instruction:\n" + display);
				}
	
				@Override
				public void onDone() {
					Log.logError("\nRobot sent completion response for instruction:\n" + display);
				}
			};
			
			RobotInstruction instruction = new RobotInstruction(instructionType, param1, param2, param3);
			instruction.setCallback(callback);
			
			if(type == RobotType.ATTACKER) {
				this.attackerComms.sendInstruction(instruction);
			} else {
				this.defenderComms.sendInstruction(instruction);
			}
		}
	}
	
	private RobotType getRobotTypeFromCommand(String[] cmd) {
		if(cmd[1].equals("d")) {
			return RobotType.DEFENDER;
		} else if(cmd[1].equals("a")) {
			return RobotType.ATTACKER;
		}
		
		Log.logError("Invalid robot type. Accepted are 'a' for attacker and 'd' for defender");
		
		return null;
	}
	
	private void startVision(String[] cmd) {
		String options = Arrays.toString(cmd);               
		options = options.substring(1, options.length()-1).replaceAll(",", "");
		String pythonCmd = "python vision/vision.py " + options;
		String s = null;
		
		try {
			Process p = Runtime.getRuntime().exec(pythonCmd);
			//BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// read the output
			//while ((s = stdInput.readLine()) != null) {
			//	Log.logError(s);
			//}
			// read any errors
			//while ((s = stdError.readLine()) != null) {
			//	Log.logError(s);
			//}
			
		} catch (IOException e) {
			Log.logError("exception occured");
			e.printStackTrace();
		}

	}
}
