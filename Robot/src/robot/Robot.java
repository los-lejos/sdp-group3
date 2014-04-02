package robot;

import java.io.IOException;

import lejos.nxt.Button;
import robot.communication.BluetoothCommunicationException;
import robot.communication.BluetoothDiceConnection;
import robot.communication.IssuedInstruction;
import robot.communication.OnNewInstructionHandler;
import shared.RobotInstructions;

/*
 * @author Joris Urbaitis
 * @author Owen Gillespie
 */

/*
 * Super class for fields/methods common to both robots.
 * run() method contains main robot loop.
 */

public class Robot {
	
	private static final int KICKER_RESET_DELAY = 10000; // 10 seconds

	protected final BluetoothDiceConnection conn;
	
    private IssuedInstruction currentInstruction, newInstruction;
    
    private MovementController movementController;
    private KickerController kicker;
    private final BallSensorController ballSensor;
    
    private boolean isRunning = true;
    
    private long prevKickerResetTime = System.currentTimeMillis();
    
    public Robot(
    		KickerController kicker, MovementController movementController,
    		BallSensorController ballSensor) {
    	this.movementController = movementController;
    	this.kicker = kicker;
    	this.ballSensor = ballSensor;
    	
    	conn = new BluetoothDiceConnection(new OnNewInstructionHandler() {
			@Override
			public void onNewInstruction(IssuedInstruction instruction) {
				newInstruction = instruction;
			}

			@Override
			public void onExitRequested() {
				isRunning = false;
			}
		});
    }

	public void run() {
		// Start Bluetooth
		try {
			conn.openConnection();
		} catch (BluetoothCommunicationException e1) {
			// This is likely a timeout
			System.out.println("Error: " + e1.getMessage());
			return;
		}
		
		conn.start();
		
		this.kicker.init();

		while(isRunning && Button.readButtons() == 0) {
			if(currentInstruction != newInstruction) {
				//System.out.println(newInstruction.getType() + " - " + Arrays.toString(newInstruction.getParameters()));
				
				currentInstruction = newInstruction;
				
				// If we have encountered an instruction beginning with '0'
				// assume error and terminate
				if(currentInstruction.getType() <= 0) {
					System.out.println("Instruction type 0");
				} else {
					this.handleInstruction(currentInstruction);
				}
			}
			
			// Update the sensor
			this.ballSensor.takeReading();
			
			// If we tried to catch the ball but didn't, restore kicker
			if(this.kicker.getHasBall() && !this.ballSensor.isDetectingBallInKicker() && !this.kicker.isMoving()) {
				prevKickerResetTime = System.currentTimeMillis();
				this.kicker.open();
				this.sendReleasedBallMessage();
			}
			// If the ball is in front of the kicker, try to grab
			else if(this.ballSensor.isBallNearby() && !this.kicker.getHasBall() && !this.kicker.isMoving()) {
				this.kicker.grab();
				this.sendCaughtBallMessage();
				
				// Reset so that we gather some measurements to make sure we have the ball
				this.ballSensor.resetMeasurements();
			}
			// Periodically reset kicker to open
			else if (this.kickerResetElapsed() && !this.kicker.getHasBall() && !this.kicker.isMoving()) {
				prevKickerResetTime = System.currentTimeMillis();
				this.kicker.open();
			}
		}

		try {
			conn.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
		
		this.movementController.cleanup();
		this.kicker.cleanup();
		
		System.out.println("Exiting");
	}

	private boolean kickerResetElapsed() {
		long currTime = System.currentTimeMillis();
		
		if (currTime - prevKickerResetTime > KICKER_RESET_DELAY) {
			return true;
		}
		
		return false;
	}

	private void handleInstruction(IssuedInstruction instruction) {
		byte instructionType = instruction.getType();
		int[] instructionParams = instruction.getParameters();
		
		if(instructionParams == null) { return; }
		
		int heading, distance, speed;

		try {
			switch(instructionType) {
			case RobotInstructions.MOVE:
				distance = instructionParams[0];
				speed = instructionParams[1];
				this.movementController.setTravelSpeed(speed);
				this.movementController.move(distance);
				break;
			case RobotInstructions.ROTATE:
				heading = instructionParams[0];
				speed = instructionParams[1];
				this.movementController.setRotateSpeed(speed);
				this.movementController.rotate(heading);
				break;
			case RobotInstructions.KICK:
				this.kicker.kick();
				this.sendReleasedBallMessage();
				break;
			case RobotInstructions.LAT_MOVE:
				distance = instructionParams[0];
				this.movementController.moveLat(distance);
				break;
			case RobotInstructions.SET_TRACK_WIDTH:
				int mm = instructionParams[0];
				this.movementController.setTrackWidth(mm);
				break;
			case RobotInstructions.SET_TRAVEL_SPEED:
				this.movementController.setTravelSpeed(instructionParams[0]);
				break;
			case RobotInstructions.SET_ROTATE_SPEED:
				this.movementController.setRotateSpeed(instructionParams[0]);
				break;
			default: 
				System.out.println("Unknown instruction: " + instructionType);
				break;
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: wrong params for instruction: " + instructionType);
		}
	}
	
	private void sendCaughtBallMessage() {
		// Notify DICE that we have the ball
		byte[] hasBallResponse = {RobotInstructions.CAUGHT_BALL, 0, 0, 0};
		
		try {
			conn.send(hasBallResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
	}
	
	private void sendReleasedBallMessage() {
		// Notify DICE that we have the ball
		byte[] hasBallResponse = {RobotInstructions.RELEASED_BALL, 0, 0, 0};
		
		try {
			conn.send(hasBallResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
	}
}
