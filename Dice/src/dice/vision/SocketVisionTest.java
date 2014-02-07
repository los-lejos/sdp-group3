package dice.vision;

import dice.state.WorldState;

public class SocketVisionTest {
	public static void main(String[] args) {
		WorldState worldState = WorldState.init();
        new SocketVisionReader(worldState, null);
    }
}
