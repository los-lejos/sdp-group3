package dice.state;

public class Line {
    private Vector2 startPoint;
    private Vector2 endPoint;

    public Line(Vector2 startPoint, Vector2 endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    // may return Infinity
    public double getGradient() {
        return (endPoint.Y - startPoint.Y) /
               (endPoint.X - startPoint.X);
    }

    public double getYIntersect() {
        return startPoint.Y - (getGradient() * startPoint.X);
    }

    public Vector2 intersect(Line otherLine) {
        Vector2 result = null;

        // avoid parallel lines
        if (this.getGradient() != otherLine.getGradient()) {
            double c1 = this.getYIntersect();
            double c2 = otherLine.getYIntersect();
            double m1 = this.getGradient();
            double m2 = otherLine.getGradient();

            double x = (c2 - c1) / (m1 - m2);
            double y = m1 * x + c1;

            result = new Vector2(x, y);
        }

        return result;
    }

    // answer in radians
    public double angleOfIncidence(Line otherLine) {
        double alpha = Math.atan(getGradient());
        double beta = Math.atan(otherLine.getGradient());

        return beta - alpha;
    }

}
