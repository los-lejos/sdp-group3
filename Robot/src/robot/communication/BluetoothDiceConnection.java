package robot.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/*
 * @author Joris S. Urbaitis
 */

public class BluetoothDiceConnection extends Thread {

	private static final byte[] HANDSHAKE_MESSAGE = {1, 2, 3, 4};
	private static final byte[] HANDSHAKE_RESPONSE = {4, 3, 2, 1};
	
	private static final byte[] EXIT_MESSAGE = {-1, -1, -1, -1};

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
		byte[] res = new byte[4];
		in.read(res);
		
		// Debug logging
		String debug = "Incoming:\n";
		for(int i = 0; i < res.length; i++) {
			debug += "" + res[i] + " ";
		}
		
		System.out.println(debug);
		
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

	public void openConnection() {
		System.out.println("Waiting for Bluetooth connection");
		btc = Bluetooth.waitForConnection();
		System.out.println("Received Bluetooth connection");

		in = btc.openInputStream();
		out = btc.openOutputStream();
		
		connected = true;
	}

	public void closeConnection() {
		isRunning = false;
		connected = false;
	}
}

