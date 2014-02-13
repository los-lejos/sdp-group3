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
	
	protected boolean interrupted = false;
	
	private Object instructionLock = new Object();
	private IssuedInstruction currentInstruction, newInstruction;
	
	private final BluetoothDiceConnection conn;
	
	private int heading, distance;
    private State currentState = State.READY;
    private Robot robot;
    private String latDirection;
    
    public MovementThread(Robot robot, BluetoothDiceConnection conn) {
    	this.conn = conn;
    	this.robot = robot;
    }

    public void exit() {
    	interrupted = true;
    	this.currentState = State.EXIT;
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
		interrupted = true;
		currentState = State.READY;
		
		synchronized(instructionLock) {
			this.newInstruction = instruction;
		}
	}
	
	private void handleInstruction(IssuedInstruction instruction) {
		byte instructionType = instruction.getType();
		byte[] instructionParameters = instruction.getParameters();
		
		if (instructionType == RobotInstructions.MOVE_TO) {
			if (instructionParameters.length == 3) {
				byte headingA = instructionParameters[0];
				byte headingB = instructionParameters[1];
				heading = (10 * headingA) + headingB;
				distance = instructionParameters[2];
				
				System.out.println("MOVE_TO");
				System.out.println("Heading: " + heading);
				System.out.println("Distance: " + distance);
				
				currentState = State.MOVE_TO;
			} else {
				System.out.println("Error: wrong parameters for MOVE_TO");
			}
		} else if (instructionType == RobotInstructions.KICK_TOWARD) {
			if (instructionParameters.length == 2) {
				byte headingA = instructionParameters[0];
				byte headingB = instructionParameters[1];
				heading = (10 * headingA) + headingB;
				
				System.out.println("KICK_TOWARD");
				System.out.println("Heading: " + heading);
				
				currentState = State.KICK_TOWARD;
				
				// Notify DICE that we no longer have the ball
				byte[] releaseBallResponse = {RobotInstructions.RELEASED_BALL, 0, 0, 0};
				try {
					conn.send(releaseBallResponse);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (BluetoothCommunicationException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Error: wrong parameters for KICK_TOWARD");
			}
		} else if (instructionType == RobotInstructions.LAT_MOVE_TO) {
			distance = instructionParameters[0];
			
			currentState = State.MOVE_LAT;
		}
	}
	
	@Override
	public void run() {
		while(currentState != State.EXIT) {
			if(currentState == State.KICK_TOWARD) {
				robot.rotate(heading);
				while(robot.isMoving() && !interrupted);
				
				if(!interrupted) {
					// kick
					robot.kick();
				} else {
					robot.stop();
				}
			} else if(currentState == State.MOVE_TO) {
				robot.rotate(heading);
				while(robot.isMoving() && !interrupted);
				
				if(!interrupted) {
					robot.move(distance);
				} else {
					robot.stop();
				}
			} else if (currentState == State.MOVE_LAT) {
				while(robot.isMoving() && !interrupted);
				
				if(!interrupted) {
					try {
						((DefenceRobot) robot).travelSidewise(90, distance, latDirection);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
