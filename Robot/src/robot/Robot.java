package robot;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.UltrasonicSensor;
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
 * eg. Instruction handling, sensors, message passing.
 * run() method contains main robot loop.
 */

public abstract class Robot {
	
	private enum State {
		READY, MOVE_TO, KICK_TOWARD, EXIT
	}

	private static final int LIGHT_SENSOR_CUTOFF = 40;
	private static final int FRONT_SENSOR_CUTOFF = 8;

	private final LightSensor LEFT_LIGHT_SENSOR;
	private final LightSensor RIGHT_LIGHT_SENSOR;
	private final UltrasonicSensor BALL_SENSOR;

    private IssuedInstruction currentInstruction, newInstruction;
    private final BluetoothDiceConnection conn;
    private byte instructionType;
    private byte[] instructionParameters;
    private Thread movementThread;
    private int heading, distance;
    private State currentState;
    private boolean quit;
    protected boolean hasBall, interrupted;
    
    public Robot(LightSensor LEFT_LIGHT_SENSOR, LightSensor RIGHT_LIGHT_SENSOR, UltrasonicSensor BALL_SENSOR) {
    	this.LEFT_LIGHT_SENSOR = LEFT_LIGHT_SENSOR;
    	this.RIGHT_LIGHT_SENSOR = RIGHT_LIGHT_SENSOR;
    	this.BALL_SENSOR = BALL_SENSOR;
    	conn = new BluetoothDiceConnection(new OnNewInstructionHandler() {
			@Override
			public void onNewInstruction(IssuedInstruction instruction) {
				newInstruction = instruction;
			}

			@Override
			public void onExitRequested() {
				quit = true;
			}
		});
    }

	public void run() {
		// Try waiting for a Bluetooth connection
		try {
			conn.openConnection();
		} catch (BluetoothCommunicationException e1) {
			// This is likely a timeout
			System.out.println("Error: " + e1.getMessage());
			System.out.println("Exiting");
			return;
		}
		
		conn.start();
		
		currentState = State.READY;
		movementThread = new MovementThread();
		movementThread.run();

		while(!quit) {
			if(currentInstruction != newInstruction) {
				System.out.println("Getting new instruction");
				currentInstruction = newInstruction;
				handleInstruction(currentInstruction);
				
				// Respond that the instruction has been completed
				try {
					conn.send(currentInstruction.getCompletedResponse());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (BluetoothCommunicationException e) {
					e.printStackTrace();
				}
			}
			
			if (rightSensorOnBoundary() || leftSensorOnBoundary()) {
				// Provisional: just stop and wait
				currentState = State.READY;
				interrupted = true;
				stop();
				interrupted = false;
				System.out.println("Boundary detected! Waiting for further instructions.");
			}
			
			if (objectAtFrontSensor()) {
				grab();
				
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

			if(Button.readButtons() != 0) {
				quit = true;
			}
		}

		System.out.println("Exiting");
		
		// Kill movement thread
		currentState = State.EXIT;
		
		// Wait for thread to die
		try {
			movementThread.join();
		} catch (InterruptedException e1) {
			System.out.println("EXCEPTION when waiting for movement thread to exit.");
			// e1.printStackTrace();
		}
		
		try {
			conn.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
	}
	
	public void handleInstruction(IssuedInstruction instruction) {
		instructionType = instruction.getType();
		instructionParameters = instruction.getParameters();
		
		// If we're doing something, stop.
		currentState = State.READY;
		interrupted = true;
		stop();
		interrupted = false;
		
		if (instructionType == RobotInstructions.MOVE_TO) {
			if (instructionParameters.length == 3) {
				byte headingA = instructionParameters[0];
				byte headingB = instructionParameters[1];
				heading = (10 * headingA) + headingB;
				distance = instructionParameters[2];
				currentState = State.MOVE_TO;
			} else {
				System.out.println("Error: wrong parameters for MOVE_TO");
			}
		} else if (instructionType == RobotInstructions.KICK_TOWARD) {
			if (instructionParameters.length == 2) {
				byte headingA = instructionParameters[0];
				byte headingB = instructionParameters[1];
				heading = (10 * headingA) + headingB;
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
		}
	}

    private boolean rightSensorOnBoundary() {
    	return RIGHT_LIGHT_SENSOR.getLightValue() >= LIGHT_SENSOR_CUTOFF;
    }

    private boolean leftSensorOnBoundary() {
    	return LEFT_LIGHT_SENSOR.getLightValue() >= LIGHT_SENSOR_CUTOFF;
    }

    private boolean objectAtFrontSensor() {
    	return BALL_SENSOR.getDistance() <= FRONT_SENSOR_CUTOFF;
    }
    
    protected boolean hasBall() {
    	return hasBall;
    }
    
    abstract void moveTo(int heading, int distance);
    abstract void kickToward(int heading);
    abstract void grab();
    abstract void stop();
    
    private class MovementThread extends Thread {
    	
    	public void run() {
    		boolean running = true;
    		
    		while (running) {
    			if (currentState == State.KICK_TOWARD) {
    				kickToward(heading);
    				currentState = State.READY;
    			} else if (currentState == State.MOVE_TO) {
    				moveTo(heading, distance);
    				currentState = State.READY;
    			}
    			
    			if (currentState == State.EXIT) {
    				stop();
    				break;
    			}
    		}
    	}
    }
    
}
