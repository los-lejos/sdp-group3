import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;


public class KickerTest {

	public static void main(String[] args) throws InterruptedException {
/*
		RegulatedMotor kickMotor = Motor.A;
		
		
//		kickMotor.setSpeed(kickMotor.getMaxSpeed());
		kickMotor.setSpeed(100);
		kickMotor.forward();
		
		
		Motor.C.setSpeed(200);
		Motor.C.forward();
		Thread.sleep(1000);
		Motor.C.stop();
*/
		NXTMotor mamkamu = new NXTMotor(MotorPort.B);
		
		mamkamu.setPower(200);
		mamkamu.backward();
		Thread.sleep(1000);
		
		//Button.waitForAnyPress();
		
		// IC2 Motor test
		// DO NOT SEND 0x03 STATE TO ANY REGISTER!
//		I2CPort I2Cport; //Create a I2C port
//		I2Cport = SensorPort.S4; //Assign port
//		I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
//		//Initialize port in standard mode
//		I2CSensor I2Csensor = new I2CSensor(I2Cport);
//		//Creates an I2CSensor
//
//		byte forward = (byte)1;
//		byte backward = (byte)2;
//		byte off = (byte)0;
//		byte speed = (byte)255;
//		I2Csensor.setAddress(0xB4);
//		I2Csensor.sendData(0x01,backward);
//		I2Csensor.sendData(0x02,speed);
//		Thread.sleep(500);
//		//I2Csensor.sendData(0x01,forward);
//		//Thread.sleep(600);
//		I2Csensor.sendData(0x01,off);
//		I2Csensor.sendData(0x02,off);
	}
	
}
