package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import robot.navigation.HolonomicPilot;

/*
 * @author Owen Gillespie
 */

public class DefenceRobot extends Robot {
	
	private static final int tireDiameterMm = 48; // TODO placeholder value
	private static final NXTRegulatedMotor kickMotor = Motor.B;
	private static final float kickSpeed = kickMotor.getMaxSpeed();
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor forwardMotor = Motor.C;
	private static final NXTRegulatedMotor lateralMotor = Motor.A;
	private final HolonomicPilot pilot;
    
    public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor, ballSensor);
    	pilot = new HolonomicPilot(tireDiameterMm, forwardMotor, lateralMotor);
    	kickMotor.setSpeed(kickSpeed);
    }

	@Override
	public void moveTo(int heading, int distance) {
		// Probably going to be...
		pilot.travel(distance, heading, true);
	}

	@Override
	protected void kickToward(int heading) {
		if (this.hasBall()) {
			kickMotor.rotate(50);
			this.hasBall = false;
		} else {
			System.out.println("Bad KICK attempt.");
		}
	}
	
	@Override
	protected void grab() {
		if (!this.hasBall()) {
			kickMotor.rotate(-40);
			this.hasBall = true;
		} else {
			System.out.println("Bad GRAB attempt.");
		}
	}
}
