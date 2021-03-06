package dice.state;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Craig Wilkinson
 * a path is just a list of points
 * When constructing a path, at least two
 * points must be specified before running
 * the getCoordinateAtX function for obvious reasons
 */
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

        if (points.size() > 1) {
            for (int i = 0; i < points.size() - 1; i++) {
                Vector2 first = points.get(i);
                Vector2 second = points.get(i+1);
                
                if (points.get(i) == null) {
                	System.err.println(i + " is null.");
                } else if (points.get(i+1) == null) {
                	System.err.println((i+1) + " is null.");
                }

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
        }

        return result;
    }

    public double getGradient(Vector2 start, Vector2 end) {
        return (end.Y - start.Y) / (end.X - start.X);
    }

}
