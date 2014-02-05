package robot.communication;

/*
 * @author Joris S. Urbaitis
 */

public class IssuedInstruction {

	private byte type;
	private byte[] response;
	private byte param1;
	private byte param2;
	private byte param3;
	
	public IssuedInstruction(byte[] instruction) {
		this.response = instruction;
		this.type = instruction[1];
		this.param1 = instruction[2];
		this.param2 = instruction[3];
		this.param3 = instruction[4];
	}
	
	public byte[] getCompletedResponse() {
		return response;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public byte[] getParameters() {
		byte[] parameters = {this.param1, this.param2, this.param3};
		return parameters;
	}
	
}
