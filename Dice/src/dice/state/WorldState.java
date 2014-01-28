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
    private Ball ball;

    // populate the world. First all robots and the
    // ball must be created
    public WorldState(RobotState opponentDefender, RobotState opponentAttacker, RobotState ourDefender, RobotState ourAttacker, Ball ball) {
        
        this.opponentDefender = opponentDefender;
        this.opponentAttacker = opponentAttacker;
        this.ourDefender = ourDefender;
        this.ourAttacker = ourAttacker;
        this.ball = ball;
    }

    public void update(RobotState opponentDefender, RobotState opponentAttacker, RobotState ourDefender, RobotState ourAttacker, Ball ball) {
        
        this.opponentDefender = opponentDefender;
        this.opponentAttacker = opponentAttacker;
        this.ourDefender = ourDefender;
        this.ourAttacker = ourAttacker;
        this.ball = ball;
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
