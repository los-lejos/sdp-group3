package dice.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import dice.Log;

import shared.RobotInstructions;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;

/*
 * @author Joris S. Urbaitis
 */

public class BluetoothRobotConnection extends Thread {

	private static final byte[] HANDSHAKE_MESSAGE = {-1, -2, -3, -4};
	private static final byte[] HANDSHAKE_RESPONSE = {-4, -3, -2, -1};
	
	private static final byte[] EXIT_MESSAGE = {-1, -1, -1, -1};

	private RobotEventListener eventListener;
	
	private boolean connected = false;
	private boolean isRunning = true;

	private NXTInfo nxtInfo;
	private NXTConnector nxtConn;
	
	private InputStream in;
	private OutputStream out;

	public BluetoothRobotConnection(RobotType robot, RobotEventListener eventListener) {
		this.setDaemon(true);
		
		this.eventListener = eventListener;
		
		if(robot == RobotType.DEFENDER) {
			nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, "OptimusPrime", "0016530A553F");
		} else {
			nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, "Ball-E", "0016530A5C22");
		}
		
		nxtConn = new NXTConnector();
	}
	
	public void send(RobotInstruction instruction) throws IOException, BluetoothCommunicationException {
        if (!connected) {
        	throw new BluetoothCommunicationException("Can't send message. Not connected to " + nxtInfo.name);
        }

    	// Send to the robot
    	this.send(instruction.getInstruction());
	}

	private void send(byte[] msg) throws IOException {
		out.write(msg);
		out.flush();
	}
	
	@Override
	public void run() {
		if (!connected) {
			Log.logError("Cannot start bluetooth communications thread without connecting");
        } else {
        	while(isRunning) {
    			try {
    				this.receiveMessages();
    			} catch(IOException e) {
    				Log.logError("Error (" + nxtInfo.name + "): " + e.getMessage());
    				this.terminate();
    			} catch (BluetoothCommunicationException e) {
    				Log.logError("Error (" + nxtInfo.name + "): " + e.getMessage());
    				this.terminate();
    			}
    		}
    		
        	try {
				in.close();
				out.close();
				nxtConn.close();
			} catch (IOException e) {
				Log.logError("Error during termination (" + nxtInfo.name + "): " + e.getMessage());
			}
			
        }
	}
	
	private void receiveMessages() throws BluetoothCommunicationException, IOException {
		byte[] res = new byte[RobotInstructions.LENGTH];
		in.read(res);
		
		if(Arrays.equals(res, EXIT_MESSAGE)) {
			// If we weren't the ones who initiated the termination
			if(this.connected) {
				Log.logError(this.nxtInfo.name + " disconnecting");
				this.terminate();
			}
		} else {	
			byte instructionId = res[0];
			
			if(instructionId == RobotInstructions.CAUGHT_BALL) {
				eventListener.onBallCaught();
			} else if(instructionId == RobotInstructions.RELEASED_BALL) {
				eventListener.onBallReleased();
			}
		}
	}

	public void openConnection() throws BluetoothCommunicationException {
		Log.logError("Attempting to connect to robot " + nxtInfo.name);
		boolean connected = nxtConn.connectTo(nxtInfo, NXTComm.PACKET);
		
		if (!connected) {
			throw new BluetoothCommunicationException("Failed to connect to " + nxtInfo.name + " (is it switched on?)");
		}
		
		Log.logInfo("Connected to robot " + nxtInfo.name);

	    out = nxtConn.getOutputStream();
	    in = nxtConn.getInputStream();
	    
	    this.connected = true;
	}
	
	public void handshake() throws BluetoothCommunicationException {
		Log.logError("Sending handshake to " + nxtInfo.name);

		try {
			this.send(HANDSHAKE_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] response = new byte[HANDSHAKE_RESPONSE.length];
		try {
			in.read(response);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(Arrays.equals(response, HANDSHAKE_RESPONSE)) {
			Log.logError("Handshake completed with " + nxtInfo.name);
		}
		else {
			throw new BluetoothCommunicationException("Handshake failed with " + nxtInfo.name);
		}
	}
	
	public void closeConnection() throws IOException {
		if(!this.connected) return;

		this.send(EXIT_MESSAGE);
		this.interrupt();
	}
	
	private void terminate() {
		Log.logError(this.nxtInfo.name + " bluetooth connection terminating");
		
		isRunning = false;
		connected = false;
	}
}
