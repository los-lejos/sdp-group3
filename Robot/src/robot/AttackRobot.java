package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;

/*
 * @author Owen Gillespie
 */

public class AttackRobot extends Robot {
	
	private static final int tireDiameterMm = 62;
	private static final int trackWidthMm = 144;
	private static final int kickSpeed = 800;
	private static final NXTRegulatedMotor kickMotor = Motor.B;
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	private final DifferentialPilot pilot;
	private final OdometryPoseProvider poseProvider;
	private final Navigator navigator;
	
	public AttackRobot() {
		super(leftLightSensor, rightLightSensor, ballSensor);
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, true);
		poseProvider = new OdometryPoseProvider(pilot);
		navigator = new Navigator(pilot, poseProvider);
		kickMotor.setSpeed(kickSpeed);
	}

	@Override
	void moveTo(int heading, int distance) {
		poseProvider.setPose(new Pose()); // Reset to origin + 0 heading
		int x, y;
		
		// Assuming heading is in range 0-359.
		if (heading == 0) {
			x = distance;
			y = 0;
		} else if (heading > 0 && heading < 90) {
			x = (int) (Math.sin(heading) * distance);
			y = (int) (Math.cos(heading) * distance);
		} else if (heading == 90) {
			x = 0;
			y = heading;
		} else if (heading > 90 && heading < 180) {
			x = - (int) (Math.sin(heading) * distance);
			y = (int) (Math.cos(heading) * distance);
		} else if (heading == 180) {
			x = -distance;
			y = 0;
		} else if (heading > 180 && heading < 270) {
			x = - (int) (Math.cos(heading) * distance);
			y = - (int) (Math.sin(heading) * distance);
		} else if (heading == 270) {
			x = 0;
			y = -distance;
		} else {
			x = - (int) (Math.sin(heading) * distance);
			y = - (int) (Math.cos(heading) * distance);
		}
		
		navigator.goTo(x, y);
	}

	@Override
	void rotateTo(int heading) {
		poseProvider.setPose(new Pose()); // Reset to origin + 0 heading
		navigator.rotateTo(heading);
	}

	@Override
	void kick() {
		if (this.hasBall()) {
			this.unsetHasBall();
			kickMotor.rotate(-40);
		}
	}

}
