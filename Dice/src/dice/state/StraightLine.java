package dice.state;

public class StraightPath implements Path {
    private Vector2 startPos;
    private Vector2 endPos;

    public StraightPath(Vector2 startPos, Vector2 endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Override
    public Vector2 getCoordinateAt(double x) {
        double gradient = (endPos.Y - startPos.Y) / (endPos.X - startPos.X);

        // y = mx + c mothafuckas
        double y = gradient * x + startPos.X;

        return new Vector2(x, y);
    }
}
