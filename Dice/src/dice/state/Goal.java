package dice.state;

public class Goal {
	private Vector2 topPost;
	private Vector2 bottomPost;

	public Goal(Vector2 top, Vector2 bottom) {
		topPost = top;
		bottomPost = bottom;
    }

    public double getHeight() {
    	return bottomPost.Y - topPost.Y;
    }
    
    public Vector2 getTopPost() {
    	return topPost;
    }

    public Vector2 getBottomPost() {
    	return bottomPost;
    }

    public Vector2 getGoalCenter() {
        double centerY = (topPost.Y + bottomPost.Y) / 2.0;
        return new Vector2(getTopPost().X, centerY);
    }

}
