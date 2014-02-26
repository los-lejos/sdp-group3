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
	
	private static final int tireDiameterMm = 62;
	private static int trackWidthMm = 136; // Actual measured - 119, this works better
	
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);

	private DifferentialPilot pilot;
	private final AttackKickerThread kickerThread;
	
	private double travelSpeed;
	private double rotateSpeed;
	
	public AttackRobot() {
		super(leftLightSensor, rightLightSensor, ballSensor);
		
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		
		travelSpeed = pilot.getMaxTravelSpeed() * 0.5;
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.3;
		
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		
		kickerThread = new AttackKickerThread(conn);
    	kickerThread.start();
	}

	@Override
	public void stop() {
		pilot.stop();
	}

	@Override
	public boolean isMoving() {
		return pilot.isMoving();
	}

	@Override
	public void rotate(int heading) {
		pilot.rotate(heading, true);
	}

	@Override
	public void move(int distance) {
		pilot.travel(distance, true);
	}
	
	@Override
	public void moveLat(int power) {
		throw new UnsupportedOperationException("Lateral movement is not possible for the attacker");
	}
	
	@Override
	public void kick() {
    	kickerThread.setKickerState(KickerState.KICK);
    	this.hasBall = false;
    }
    
	@Override
    public void grab() {
    	kickerThread.setKickerState(KickerState.GRAB);
    }
    
	@Override
    public void cleanup() {
    	kickerThread.setKickerState(KickerState.EXIT);
    }

	@Override
	public void setTrackWidth(int width) {
		trackWidthMm = width;
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		travelSpeed = pilot.getMaxTravelSpeed() * 0.5;
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.3;
		
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
	}
}
