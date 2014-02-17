package robot;

import java.io.IOException;
import java.util.Arrays;

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
 * run() method contains main robot loop.
 */

public abstract class Robot {
	
	private static final int LIGHT_SENSOR_CUTOFF = 40;
	private static final int FRONT_SENSOR_CUTOFF = 8;
	private final LightSensor LEFT_LIGHT_SENSOR;
	private final LightSensor RIGHT_LIGHT_SENSOR;
	private final UltrasonicSensor BALL_SENSOR;
	private final BluetoothDiceConnection conn;

    private IssuedInstruction currentInstruction, newInstruction;
    private MovementThread movementThread;
    private TerminatorThread arnold;
    
    protected boolean hasBall;
    
    public Robot(LightSensor LEFT_LIGHT_SENSOR, LightSensor RIGHT_LIGHT_SENSOR, UltrasonicSensor BALL_SENSOR) {
    	arnold = new TerminatorThread();
		arnold.start();
    	
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
				movementThread.exit();
				arnold.exit();
			}
		});
    }

	public void run() {
		movementThread = new MovementThread(this, conn);
		movementThread.start();

		while(arnold.isAlive() && movementThread.isAlive()) {
			if(currentInstruction != newInstruction) {
				System.out.println(Arrays.toString(newInstruction.getCompletedResponse()));
				currentInstruction = newInstruction;
				movementThread.setInstruction(currentInstruction);
			}
			
			if (rightSensorOnBoundary() || leftSensorOnBoundary()) {
				// Provisional: just stop and wait
				this.movementThread.stopMovement();
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
		}

		arnold.exit();
		
		try {
			arnold.join();
			System.out.println("Joined arnold.");
		} catch (InterruptedException e) {
			System.out.println("Couldn't join arnold.");
			//e.printStackTrace();
		}
		
		movementThread.exit();
		
		try {
			movementThread.join();
			System.out.println("Joined movementThread.");
		} catch (InterruptedException e) {
			System.out.println("Couldn't join movementThread.");
			//e.printStackTrace();
		}
		
		System.out.println("Exiting");
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
    
    abstract boolean isMoving();
    abstract void rotate(int heading);
    abstract void move(int distance);
    abstract void moveLat(int power);
    abstract void grab();
    abstract void stop();
    abstract void kick();
    abstract void cleanup();
	
}
