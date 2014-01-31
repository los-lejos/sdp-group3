package robot;

import java.io.IOException;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
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

	protected static int TIRE_DIAMETER_MM;
	protected static int TRACK_WIDTH_MM;
	protected static int KICK_MOTOR_SPEED;
	protected static NXTRegulatedMotor LEFT_MOTOR;
	protected static NXTRegulatedMotor RIGHT_MOTOR;
	protected static NXTRegulatedMotor KICK_MOTOR;
	protected static LightSensor LEFT_LIGHT_SENSOR;
	protected static LightSensor RIGHT_LIGHT_SENSOR;
    protected static UltrasonicSensor FRONT_SENSOR;
    protected static DifferentialPilot PILOT;
    protected static OdometryPoseProvider POSE_PROVIDER;
    protected static Navigator NAVIGATOR;

    double robotMoveSpeed;
    Pose pose;
    IssuedInstruction currentInstruction, newInstruction;
    byte instructionType;
    byte[] instructionParameters;
    boolean quit;

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
				// TODO: Handle boundary problem. Reverse? Notify DICE? Sync location?
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
			float x = instructionParameters[0];
			float y = instructionParameters[1];
			NAVIGATOR.goTo(x, y);
		} else if (instructionType == RobotInstructions.KICK_TOWARDS) {
			float heading = instructionParameters[0];
			NAVIGATOR.rotateTo(heading);
			// TODO: Do some kicking. Steal code from MS2.
		} else if (instructionType == RobotInstructions.SYNC_LOCATION) {
			float x = instructionParameters[0];
			float y = instructionParameters[1];
			float heading = instructionParameters[2];
			pose = new Pose(x, y, heading);
		}
	}

    public boolean rightSensorOnBoundary() {
    	return RIGHT_LIGHT_SENSOR.getLightValue() >= LIGHT_SENSOR_CUTOFF;
    }

    public boolean leftSensorOnBoundary() {
    	return LEFT_LIGHT_SENSOR.getLightValue() >= LIGHT_SENSOR_CUTOFF;
    }

    public boolean objectAtFrontSensor() {
    	return FRONT_SENSOR.getDistance() <= FRONT_SENSOR_CUTOFF;
    }

    public void setMoveSpeed(double speed) {
    	robotMoveSpeed = speed;
    }

    public DifferentialPilot getPilot() {
    	return PILOT;
    }

}
