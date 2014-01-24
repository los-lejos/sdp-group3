package dice;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Main {
	public static void main(String[] args) {
		BTConnection btc = Bluetooth.waitForConnection();
		
		InputStream in = btc.openInputStream();
		OutputStream out = btc.openOutputStream();
		
		byte[] message = new byte[4];
		try {
			in.read(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(message);
		
		byte[] response = { 4, 3, 2, 1 };
		try {
			out.write(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			btc.close();
		}
	}
}