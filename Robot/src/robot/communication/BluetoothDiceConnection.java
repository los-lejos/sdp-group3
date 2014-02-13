package robot.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import shared.RobotInstructions;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

/*
 * @author Joris S. Urbaitis
 */

public class BluetoothDiceConnection extends Thread {

	private static final byte[] HANDSHAKE_MESSAGE = {-1, -2, -3, -4, -5};
	private static final byte[] HANDSHAKE_RESPONSE = {-4, -3, -2, -1, -0};
	
	private static final byte[] EXIT_MESSAGE = {-1, -1, -1, -1, -1};

	private boolean connected = false;
	private boolean isRunning = true;
	
	private OnNewInstructionHandler instructionHandler;

	private BTConnection btc;
	
	private Object sendingLock = new Object();
	
	private InputStream in;
	private OutputStream out;
	
	public BluetoothDiceConnection(OnNewInstructionHandler instructionHandler) {
		this.instructionHandler = instructionHandler;
	}

	public void send(byte[] msg) throws IOException, BluetoothCommunicationException {
		if (!connected) {
        	throw new BluetoothCommunicationException("Can't send message. Not connected to Dice");
        }
		
		synchronized(sendingLock) {
			out.write(msg);
			out.flush();
		}
	}
	
	@Override
	public void run() {
		if (!connected) {
        	System.out.println("Cannot start bluetooth communications thread without connecting");
        } else {
        	while(isRunning) {
    			try {
    				this.receiveMessages();
    			} catch(IOException e) {
    				e.printStackTrace();
    				this.terminate();
    			}
    		}
    		
        	try {
				in.close();
				out.close();
				btc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	private void receiveMessages() throws IOException {
		byte[] res = new byte[RobotInstructions.LENGTH];
		in.read(res);

		if(Arrays.equals(res, HANDSHAKE_MESSAGE)) {
			try {
				this.send(HANDSHAKE_RESPONSE);
			} catch (BluetoothCommunicationException e) {
				e.printStackTrace();
			}
		} else if(Arrays.equals(res, EXIT_MESSAGE)) {
			try {
				this.send(EXIT_MESSAGE);
			} catch (BluetoothCommunicationException e) {
				e.printStackTrace();
			}
			
			this.instructionHandler.onExitRequested();
		} else {
			IssuedInstruction instruction = new IssuedInstruction(res);
			this.instructionHandler.onNewInstruction(instruction);
		}
	}

	public void openConnection() throws BluetoothCommunicationException {
		System.out.println("Waiting for Bluetooth connection");
		btc = Bluetooth.waitForConnection(30000, NXTConnection.PACKET);
		
		if(btc == null) {
			throw new BluetoothCommunicationException("Timed out while waiting for a Bluetooth connection");
		} else {
			System.out.println("Received Bluetooth connection");

			in = btc.openInputStream();
			out = btc.openOutputStream();
			
			connected = true;
		}
	}

	public void closeConnection() throws IOException, BluetoothCommunicationException {
		System.out.println("Sending exit message to Dice");
		try {
			this.send(EXIT_MESSAGE);
		} catch(IOException e) {
			System.out.println("Failed to send exit message.");
		}
		
		this.terminate();
	}
	
	private void terminate() {
		System.out.println("Closing connection to Dice");
		isRunning = false;
		connected = false;
	}
}

