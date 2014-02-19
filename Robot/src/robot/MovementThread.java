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
		interrupted = true;
		currentState = State.READY;
		
		synchronized(instructionLock) {
			this.newInstruction = instruction;
		}
	}
	
	protected void handleInstruction(IssuedInstruction instruction) {
		byte instructionType = instruction.getType();
		byte[] instructionParameters = instruction.getParameters();
		
		if (instructionType == RobotInstructions.MOVE_TO) {
			if (instructionParameters.length == 3) {
				heading = instructionParameters[0];
				distance = instructionParameters[1];
				
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
			
			System.out.println("MOVE_LAT");
			System.out.println("Power: " + distance);
			currentState = State.MOVE_LAT;
		} else if (instructionType == 0) {
			System.out.println("Zero instruction, assuming disconnected.");
			boolean joined = false;
			while (joined == false) {
				try {
					this.join();
					joined = true;
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.exit();
		}
	}
	
	public void run() {		
		while(currentState != State.EXIT) {
			if(currentState == State.KICK_TOWARD) {
				robot.rotate(heading);
				while(robot.isMoving() && !interrupted);
				
				if(!interrupted) {
					robot.kick();
					while(robot.isMoving() && !interrupted);
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
		
		System.out.println("Out of movement loop");
	}
}
