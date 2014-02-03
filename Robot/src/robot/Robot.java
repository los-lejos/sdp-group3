package robot;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
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
 * run() method contains main robot loop.
 */

public abstract class Robot {

	private static final int LIGHT_SENSOR_CUTOFF = 40;
	private static final int FRONT_SENSOR_CUTOFF = 12;

	protected final int KICK_SPEED;
	protected final NXTRegulatedMotor KICK_MOTOR;
	private final LightSensor LEFT_LIGHT_SENSOR;
	private final LightSensor RIGHT_LIGHT_SENSOR;
	private final UltrasonicSensor BALL_SENSOR;

    private IssuedInstruction currentInstruction, newInstruction;
    private byte instructionType;
    private byte[] instructionParameters;
    private boolean quit;
    
    public Robot(int KICK_SPEED, NXTRegulatedMotor KICK_MOTOR,
    			 LightSensor LEFT_LIGHT_SENSOR, LightSensor RIGHT_LIGHT_SENSOR,
    			 UltrasonicSensor BALL_SENSOR) {
    	this.KICK_SPEED = KICK_SPEED;
    	this.KICK_MOTOR = KICK_MOTOR;
    	this.LEFT_LIGHT_SENSOR = LEFT_LIGHT_SENSOR;
    	this.RIGHT_LIGHT_SENSOR = RIGHT_LIGHT_SENSOR;
    	this.BALL_SENSOR = BALL_SENSOR;
    }

	public void run() {
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
				// TODO Handle boundary problem. Reverse? Notify DICE?
			}
			
			if (objectAtFrontSensor()) {
				// TODO Grab ball here
			}

			if(Button.readButtons() != 0) {
				quit = true;
			}
		}

		System.out.println("Exiting");
		conn.closeConnection();
	}
	
	public void handleInstruction(IssuedInstruction instruction) {
		byte instructionType = instruction.getType();
		byte[] instructionParameters = instruction.getParameters();
		
		if (instructionType == RobotInstructions.MOVE_TO) {
			int heading = instructionParameters[0];
			int distance = instructionParameters[1];
			moveTo(heading, distance);
		} else if (instructionType == RobotInstructions.TURN_TO) {
			int heading = instructionParameters[0];
			rotateTo(heading);
		} else if (instructionType == RobotInstructions.KICK) {
			kick();
		}
	}
    
    abstract void moveTo(int heading, int distance);
    abstract void rotateTo(int heading);
    abstract void kick();

    private boolean rightSensorOnBoundary() {
    	return RIGHT_LIGHT_SENSOR.getLightValue() >= LIGHT_SENSOR_CUTOFF;
    }

    private boolean leftSensorOnBoundary() {
    	return LEFT_LIGHT_SENSOR.getLightValue() >= LIGHT_SENSOR_CUTOFF;
    }

    private boolean objectAtFrontSensor() {
    	return BALL_SENSOR.getDistance() <= FRONT_SENSOR_CUTOFF;
    }
    
}
