package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class AttackRobot {
	
	private static final int TIRE_DIAMETER_MM = 56;
	private static final int TRACK_WIDTH_MM = 114;
	private static final int LIGHT_SENSOR_CUTOFF = 40;
	private static final int FRONT_SENSOR_CUTOFF = 12;
	private static final NXTRegulatedMotor LEFT_MOTOR = Motor.C;
	private static final NXTRegulatedMotor RIGHT_MOTOR = Motor.A;
	private static final LightSensor RIGHT_LIGHT_SENSOR = new LightSensor(SensorPort.S4);
    private static final LightSensor LEFT_LIGHT_SENSOR = new LightSensor(SensorPort.S1);
    private static final UltrasonicSensor FRONT_SENSOR = new UltrasonicSensor(SensorPort.S2);
    private static final DifferentialPilot PILOT = new DifferentialPilot(TIRE_DIAMETER_MM, TRACK_WIDTH_MM, LEFT_MOTOR, RIGHT_MOTOR, true);
    
    private static double robotMoveSpeed;
    
    public AttackRobot() {
    	robotMoveSpeed = PILOT.getMaxTravelSpeed() * 0.4;
    	PILOT.setTravelSpeed(robotMoveSpeed);
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
