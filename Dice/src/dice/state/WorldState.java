package dice.state;


/** The class that will contain all information about the
 * current state of the game.
 * @author Craig Wilkinson
 */
public class WorldState {

	public enum PitchZone {
		OUR_ATTACK_ZONE,
		OUR_DEFEND_ZONE,
		OPP_ATTACK_ZONE,
		OPP_DEFEND_ZONE
    }

    public enum BallPossession {
    	NONE,
    	OUR_DEFENDER,
    	OUR_ATTACKER,
    	OPP_DEFENDER,
    	OPP_ATTACKER
    }

    private GameObject opponentDefender;
    private GameObject opponentAttacker;
    private GameObject ourDefender;
    private GameObject ourAttacker;
    private GameObject ball;
    
    // pitch geometry
    // These represent only the x values of the lines
    // it may be a better idea to use lines here, since the vision
    // may be skewed, or the table rotated slightly
    // o----f----s----t----e
    // |    |    |    |    |
    // |    |    |    |    |
    // o----f----s----t----e
    private double origin;
    private double firstDivision;
    private double secondDivision;
    private double thirdDivision;
    private double end;
    private double width;

    // lines which represent the outer edge
    private Line top;
    private Line topRight;
    private Line right;
    private Line bottomRight;
    private Line bottom;
    private Line bottomLeft;
    private Line left;
    private Line topLeft;

    private BallPossession possession;
    
    // Utility to create and return a new WorldState
    public static WorldState init() {
    	GameObject opponentDefender = new GameObject(0, 0, 0.0);
		GameObject ourAttacker = new GameObject(0, 0, 0.0);
		GameObject opponentAttacker = new GameObject(0, 0, 0.0);
		GameObject ourDefender = new GameObject(0, 0, 0.0);
		GameObject ball = new GameObject(0, 0, 0.0);
		return new WorldState(opponentDefender, opponentAttacker, ourDefender, ourAttacker, ball);
    }

    public void updateState(Vector2 a, double aAngle, Vector2 b, double bAngle,
    		Vector2 c, double cAngle, Vector2 d, double dAngle, Vector2 ball) {
        opponentDefender.setPos(a);
        opponentDefender.setRotation(aAngle);
        
        ourAttacker.setPos(b);
        ourAttacker.setRotation(bAngle);
        
        opponentAttacker.setPos(c);
        opponentAttacker.setRotation(cAngle);
        
        ourDefender.setPos(d);
        ourDefender.setRotation(dAngle);

        ball.setPos(ball);
    }


    // populate the world. First all robots and the
    // ball must be created
    public WorldState(GameObject opponentDefender, GameObject opponentAttacker, GameObject ourDefender, GameObject ourAttacker, GameObject ball) {
        
        this.opponentDefender = opponentDefender;
        this.opponentAttacker = opponentAttacker;
        this.ourDefender = ourDefender;
        this.ourAttacker = ourAttacker;
        this.ball = ball;
    }

    // do this once at the beginning, so we have an "accurate"
    // representation of the pitch divisions (the pitch may be nudged
    // slightly).
    public void calibratePitch(Line top, Line topRight, Line right
                               Line bottomRight, Line bottom,
                               Line bottomLeft, Line left, Line topLeft) {
        this.top = top;
        this.topRight = topRight;
        this.right = right;
        this.bottomRight = bottomRight;
        this.bottom = bottom;
        this.bottomLeft = bottomLeft;
        this.left = left;
        this.topLeft = topLeft;
    }

    // pitch geometry getters
    public double getOrigin() {
    	return origin;
    }

    public double getFirstDivision() {
    	return firstDivision;
    }

    public double getSecondDivision() {
    	return secondDivision;
    }

    public double getThirdDivision() {
    	return thirdDivision;
    }

    public double getEnd() {
    	return end;
    }


    // pitch cell centers
    public Vector2 getCellCenter(PitchZone zone) {
        double y = width / 2.0;
        double x;

        switch (zone) {
            case OUR_DEFEND_ZONE:
                x = origin + firstDivision / 2.0;
                break;
            case OPP_ATTACK_ZONE:
                x = firstDivision + secondDivision / 2.0;
                break;
        	case OUR_ATTACK_ZONE:
                x = secondDivision + thirdDivision / 2.0;
                break;
            case OPP_DEFEND_ZONE:
                x = thirdDivision + end / 2.0;
                break;
            default:
                x = -1;

        }

        return new Vector2(x, y);
    }

    
    // getters for the various robots and ball
    public GameObject getOpponentDefender() {
    	return opponentDefender;
    }

    public GameObject getOpponentAttacker() {
    	return opponentAttacker;
    }

    public GameObject getOurDefender() {
    	return ourDefender;
    }

    public GameObject getOurAttacker() {
    	return ourAttacker;
    }

    public GameObject getBall() {
    	return ball;
    }

    public PitchZone getBallZone() {
        PitchZone ballZone;
        double ballX = ball.getPos().X;

        if (ballX >= origin && ballX <= firstDivision)
            ballZone = PitchZone.OUR_DEFEND_ZONE;
        else if (ballX <= secondDivision)
            ballZone = PitchZone.OPP_ATTACK_ZONE;
        else if (ballX <= thirdDivision)
            ballZone = PitchZone.OUR_ATTACK_ZONE;
        else if (ballX <= end)
            ballZone = PitchZone.OPP_DEFEND_ZONE;
        else
            return null;
        
        return ballZone;
    }

    public BallPossession getBallPossession() {
    	return possession;
    }

    public void setBallPossession(BallPossession newPossession) {
        this.possession = newPossession;
    }
}
