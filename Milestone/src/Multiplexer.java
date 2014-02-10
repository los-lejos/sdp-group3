import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;


public class Multiplexer {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		//IC2 Motor test
		//DO NOT SEND 0x03 STATE TO ANY REGISTER!
		
		I2CPort I2Cport; //Create a I2C port
		I2Cport = SensorPort.S1; //Assign port
		I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
		//Initialise port in standard mode
		
		I2CSensor I2Csensor = new I2CSensor(I2Cport);
		//Creates an I2CSensor

		byte forward = (byte)1;
		byte backward = (byte)2;
		byte off = (byte)0;
		byte speed = (byte)200;
		
		//I2Csensor.setAddress(0xB4);
		I2Csensor.setAddress(0x5A); 
		I2Csensor.sendData(0x01,backward);
		I2Csensor.sendData(0x02,speed);
		Thread.sleep(500);
		
		I2Csensor.sendData(0x01,forward);
		Thread.sleep(600);
		
		I2Csensor.sendData(0x01,off);
		I2Csensor.sendData(0x02,off);
	}

}
