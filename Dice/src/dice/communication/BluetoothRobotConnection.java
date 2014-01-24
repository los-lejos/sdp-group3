package dice.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;

/*
 * @author Joris S. Urbaitis
 */

public class BluetoothRobotConnection extends Thread {
	
	private static final byte[] HANDSHAKE_MESSAGE = {1, 2, 3, 4};
	private static final byte[] HANDSHAKE_RESPONSE = {4, 3, 2, 1};
	
	private static final byte INSTRUCTION_CALLBACK_MAX = 4;
	private RobotCommunicationCallback[] instructionCallbacks;
	private byte currentInstructionCallback = -1;
	
	private boolean connected = false;
	private boolean isRunning = true;

	private NXTInfo nxtInfo;
	private NXTConnector nxtConn;
	
	private InputStream in;
	private OutputStream out;
	
	private RobotInstruction instructionToSend;
	

	public BluetoothRobotConnection(String deviceName, String deviceMacAddress) {
		nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, deviceName, deviceMacAddress);
		nxtConn = new NXTConnector();
		
		instructionCallbacks = new RobotCommunicationCallback[INSTRUCTION_CALLBACK_MAX];
	}
	
	public void send(RobotInstruction instruction) throws IOException, BluetoothCommunicationException {
        if (!connected) {
        	throw new BluetoothCommunicationException("Can't send message. Not connected to " + nxtInfo.name);
        }
        
        synchronized(instructionCallbacks) {
	        currentInstructionCallback++;
	        if(currentInstructionCallback >= INSTRUCTION_CALLBACK_MAX) {
	        	currentInstructionCallback = 0;
	        }
	        
	        // If current slot is filled, time it out and replace
	        if(instructionCallbacks[currentInstructionCallback] != null) {
	        	instructionCallbacks[currentInstructionCallback].onTimeout();
	        }
	        
	        synchronized(instructionToSend) {
	        	instructionToSend = instruction;
	        	instructionToSend.getInstruction()[0] = currentInstructionCallback;
	        	instructionCallbacks[currentInstructionCallback] = instructionToSend.getCallback();
	        }
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
    				this.sendMessages();
    			} catch(IOException e) {
    				e.printStackTrace();
    			} catch (BluetoothCommunicationException e) {
    				e.printStackTrace();
    			}
    		}
    		
    		this.closeConnection();
        }
	}
	
	private void receiveMessages() throws BluetoothCommunicationException, IOException {
		synchronized(instructionCallbacks) {
			if(in.available() >= RobotInstruction.LENGTH) {
				byte[] res = new byte[4];
				in.read(res);
				
				byte instructionId = res[0];
				RobotCommunicationCallback callback = instructionCallbacks[instructionId];
				if(callback != null) {
					callback.onDone();
				}
				else {
					throw new BluetoothCommunicationException("No callback for instruction ID " + instructionId);
				}
			}
		}
	}
	
	private void sendMessages() throws IOException {
		synchronized(instructionToSend) {
			if(instructionToSend != null) {
				out.write(instructionToSend.getInstruction());
				out.flush();
				instructionToSend = null;
			}
		}
	}

	public void openConnection() throws BluetoothCommunicationException {
		System.out.println("Attempting to connect to robot " + nxtInfo.name);
		boolean connected = nxtConn.connectTo(nxtInfo, NXTComm.PACKET);
		
		if (!connected) {
			throw new BluetoothCommunicationException("Failed to connect to " + nxtInfo.name);
		}

	    out = nxtConn.getOutputStream();
	    in = nxtConn.getInputStream();
	}
	
	public void handshake() throws BluetoothCommunicationException {
		System.out.println("Sending handshake to " + nxtInfo.name);
		
		try {
			out.write(HANDSHAKE_MESSAGE);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] response = null;
		try {
			// This blocks, should probably be wrapped
			in.read(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(response == HANDSHAKE_RESPONSE) {
			System.out.println("Handshake completed with " + nxtInfo.name);
		}
		else {
			throw new BluetoothCommunicationException("Handshake failed with " + nxtInfo.name);
		}
		
		connected = true;
	}
	
	public void closeConnection() {
		try {
			isRunning = false;
			in.close();
			out.close();
			nxtConn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connected = false;
		}
	}

}
