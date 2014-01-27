package dice.state;

/** The class that will contain all information about the
 * current state of the game.
 * @author Craig Wilkinson
 */
public class WorldState {
    private RobotState opponentDefender;
    private RobotState opponentAttacker;
    private RobotState ourDefender;
    private RobotState ourAttacker;

    public WorldState() {
        
    }

    public RobotState getOpponentDefender() {
    	return opponentDefender;
    }

    public RobotState getOpponentAttacker() {
    	return opponentAttacker;
    }

    public RobotState getOurDefender() {
    	return ourDefender;
    }

    public RobotState getOurAttacker() {
    	return ourAttacker;
    }

}
