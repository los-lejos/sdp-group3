package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class DefenseRobot extends Robot {
    
    public DefenseRobot() {
    	TIRE_DIAMETER_MM = 56;
    	TRACK_WIDTH_MM = 114;
    	LEFT_MOTOR = Motor.C;
    	RIGHT_MOTOR = Motor.A;
    	LEFT_LIGHT_SENSOR = new LightSensor(SensorPort.S4);
    	RIGHT_LIGHT_SENSOR = new LightSensor(SensorPort.S1);
    	FRONT_SENSOR = new UltrasonicSensor(SensorPort.S2);
    	PILOT = new DifferentialPilot(TIRE_DIAMETER_MM, TRACK_WIDTH_MM, LEFT_MOTOR, RIGHT_MOTOR, true);
    	robotMoveSpeed = PILOT.getMaxTravelSpeed() * 0.4;
    	PILOT.setTravelSpeed(robotMoveSpeed);
    }

}
