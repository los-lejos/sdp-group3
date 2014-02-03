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
	
	private static final int tireDiameterMm = 48;
	private static final int kickSpeed = 50; // TODO placeholder value
	private static final NXTRegulatedMotor kickMotor = Motor.B;
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor forwardMotor = Motor.C;
	private static final NXTRegulatedMotor lateralMotor = Motor.A;
	private final HolonomicPilot pilot;
    
    public DefenceRobot() {
    	super(kickSpeed, kickMotor, leftLightSensor, rightLightSensor, ballSensor);
    	pilot = new HolonomicPilot(tireDiameterMm, forwardMotor, lateralMotor);
    }

	@Override
	public void moveTo(int heading, int distance) {
		// TODO Auto-generated method stub
	}

	@Override
	public void rotateTo(int heading) {
		// TODO Auto-generated method stub
	}

	@Override
	public void kick() {
		// TODO Auto-generated method stub
	}
    
    

}
