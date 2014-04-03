package robot;

import java.io.IOException;
import java.util.Arrays;

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

	protected final BluetoothDiceConnection conn;
	
    private IssuedInstruction currentInstruction, newInstruction;
    
    private MovementController movementController;
    private KickerController kicker;
    private final BallSensorController ballSensor;
    
    private boolean isRunning = true;
    
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
				System.out.println(newInstruction.getType() + " - " + Arrays.toString(newInstruction.getParameters()));
				
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
				this.kicker.open();
				this.sendReleasedBallMessage();
			}
			// If the ball is in front of the kicker + kicker is open, try to grab
			else if(this.ballSensor.isBallNearby() && !this.kicker.getHasBall() && !this.kicker.isMoving() && this.kicker.isOpen()) {
				this.kicker.grab();
				this.sendCaughtBallMessage();
				
				// Reset so that we gather some measurements to make sure we have the ball
				this.ballSensor.resetMeasurements();
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
		this.kicker.kill();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Exiting");
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
			case RobotInstructions.MOVE_AND_KICK:
				distance = instructionParams[0];
				speed = instructionParams[1];
				this.movementController.setTravelSpeed(speed);
				this.movementController.move(distance);
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.kicker.kick();
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
			case RobotInstructions.OPEN_KICKER:
				this.kicker.open();
				break;
			case RobotInstructions.CLOSE_KICKER:
				this.kicker.kick();
				this.sendReleasedBallMessage();
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
