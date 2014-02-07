package dice.state;

import java.util.List;
import java.util.ArrayList;

// a path is just a list of points
public class Path {
    List<Vector2> points;

    public Path() {
        points = new ArrayList<Vector2>();
    }

    public Path(List<Vector2> points) {
        this.points = new ArrayList<Vector2>(points);
    }

    public void addPoint(Vector2 point) {
        points.add(point);
    }

    public Vector2 getCoordinateAtX(double x) {
        Vector2 result = null;

        for (int i = 0; i < points.size(); i++) {
            Vector2 first = points.get(i);
            Vector2 second = points.get(i+1);

            // if the point is between the section of
            // the path
            if ( (x > first.X && x < second.X) ||
                 (x < first.X && x > second.X) ) {

                double m = getGradient(first, second);
                double y = first.X + (x - first.X) * m;

                // just return the first one found in
                // the path
                return new Vector2(x, y);
            }
        }

        return result;
    }

    public double getGradient(Vector2 start, Vector2 end) {
        return (end.Y - start.Y) / (end.X - start.X);
    }

}
