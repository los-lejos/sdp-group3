package robot;

import java.io.IOException;

import robot.communication.BluetoothCommunicationException;
import robot.communication.BluetoothDiceConnection;
import robot.communication.IssuedInstruction;
import shared.RobotInstructions;

public class MovementThread extends Thread {
	
	private enum State {
		READY, MOVE_TO, KICK_TOWARD, EXIT, MOVE_LAT                                                                     
	}
	
	private final BluetoothDiceConnection conn;
	private final Robot robot;
	
	private State currentState = State.READY;
	private boolean interrupted = false;
	
	private final Object instructionLock = new Object();
	private IssuedInstruction currentInstruction, newInstruction;

	private int heading, distance;

    public MovementThread(Robot robot, BluetoothDiceConnection conn) {
    	this.setDaemon(true);
    	
    	this.conn = conn;
    	this.robot = robot;
    }

    public void exit() {
    	synchronized(instructionLock) {
	    	this.currentState = State.EXIT;
	    	this.currentInstruction = null;
	    	this.newInstruction = null;
	    	this.interrupted = true;
    	}
    }

    public void stopMovement() {
    	synchronized(instructionLock) {
        	this.currentState = State.READY;
        	this.currentInstruction = null;
        	this.newInstruction = null;
        	this.interrupted = true;
    	}
    }
	
	public void setInstruction(IssuedInstruction instruction) {
		// If we're doing something, stop.
		robot.stop();
		interrupted = true;
		currentState = State.READY;
		
		synchronized(instructionLock) {
			this.newInstruction = instruction;
		}
	}
	
	protected void handleInstruction(IssuedInstruction instruction) {
		updateStateForInstruction(instruction);
		validateParameters();
	}
	
	private void validateParameters() {

		if(heading > 180) {
			heading -= 360;
		} else if(heading < -180) {
			heading += 360;
		}
		
		assert (heading >= -180) && (heading <= 180);
	}
	
	private void updateStateForInstruction(IssuedInstruction instruction) {
		byte instructionType = instruction.getType();
		byte[] instructionParams = instruction.getParameters();
		
		// Reset state
		heading = 0;
		distance = 0;
		currentState = State.READY;
		try {
			switch(instructionType) {
			case RobotInstructions.MOVE_TO:
				heading = (10 * instructionParams[0]) + instructionParams[1];
				distance = instructionParams[2];
				// Convert from centimeters to millimeters
				distance *= 10;
				currentState = State.MOVE_TO;
				break;
			case RobotInstructions.KICK_TOWARD:
				heading = (10 * instructionParams[0]) + instructionParams[1];
				System.out.println("KICK_TOWARD");
				System.out.println("Heading: " + heading);
				currentState = State.KICK_TOWARD;
				break;
			case RobotInstructions.LAT_MOVE_TO:
				distance = instructionParams[0];
				currentState = State.MOVE_LAT;
				break;
			case RobotInstructions.SET_TRACK_WIDTH:
				int mm = instructionParams[0]*10 + instructionParams[1];
				robot.setTrackWidth(mm);
				break;
			case RobotInstructions.SET_TRAVEL_SPEED:
				robot.setTravelSpeed(instructionParams[0]);
				break;
			case RobotInstructions.SET_ROTATE_SPEED:
				robot.setRotateSpeed(instructionParams[0]);
				break;
			default: 
				System.out.println("Unknown instruction: " + instructionType);
				break;
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: wrong params for instruction: " + instructionType);
			currentState = State.READY;
		}
	}
	
	public void run() {		
		while(currentState != State.EXIT) {
			if(currentState == State.KICK_TOWARD) {
				robot.rotate(heading);
				while(robot.isMoving() && !interrupted);
				if(!interrupted) {
					robot.getKicker().kick();
					while(robot.getKicker().isMoving() && !interrupted);
				} else {
					robot.stop();
				}
			} else if(currentState == State.MOVE_TO) {
				robot.rotate(heading);
				while(robot.isMoving() && !interrupted);
				if(!interrupted) {
					robot.move(distance);
					while(robot.isMoving() && !interrupted);
				} else {
					robot.stop();
				}
			} else if (currentState == State.MOVE_LAT) {
				if(!interrupted) {
					robot.moveLat(distance);
				} else {
					robot.stop();
				}
			}
			
			synchronized(this.instructionLock) {
				interrupted = false;
				
				if(currentInstruction != null) {
					// Respond that the instruction has been completed
					try {
						conn.send(currentInstruction.getCompletedResponse());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (BluetoothCommunicationException e) {
						e.printStackTrace();
					}
					
					if(this.newInstruction == this.currentInstruction) {
						this.newInstruction = null;
					}
					
					this.currentInstruction = null;
					this.currentState = State.READY;
				}
				
				if(this.newInstruction != currentInstruction) {
					this.currentInstruction = this.newInstruction;
					this.handleInstruction(this.currentInstruction);
				}
			}
		}
	}
}
