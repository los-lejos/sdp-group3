package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

/*
 * @author Owen Gillespie
 */

public class AttackRobot extends Robot {
	
	private static final int tireDiameterMm = 48;
	private static final int trackWidthMm = 114;
	private static final int kickSpeed = 50; // TODO placeholder value
	private static final NXTRegulatedMotor kickMotor = Motor.B;
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	private final DifferentialPilot pilot;
	
	public AttackRobot() {
		super(kickSpeed, kickMotor, leftLightSensor, rightLightSensor, ballSensor);
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, true);
	}

	@Override
	void moveTo(int heading, int distance) {
		// TODO Auto-generated method stub
	}

	@Override
	void rotateTo(int heading) {
		// TODO Auto-generated method stub
	}

	@Override
	void kick() {
		// TODO Auto-generated method stub
	}

}
