package net.mmly.openminemap.util;

import java.util.Arrays;
import java.util.Collections;

public class PolygonTriangulator {

    //  Coded by Darel Rex Finley, 2022; hereby given to the public domain. If you use this code in your own project,
    //  and you want to give me credit, please say, "Based on code by D. R. Finley." But that is not required. :)

    //triangulate a list of [x, y] points representing a polygon into a set of [[x1, y1], [x2, y2], [x3, y3]] triangles for drawing
    // [[p1x, p1y], [p2x, p2y], ...]  ->  [[[t1x1, t1y1], [t1x2, t1y2], [t1x3, t1y3]], [[t2x1, t2y1], [t2x2, t2y2], [t2x3, t2y3]], ...]

    public static double[][][] triangulate(double[][] polygon) {

        //System.out.println(Arrays.toString(polygon[0]));
        //System.out.println(Arrays.toString(polygon[polygon.length - 1]));

        if (Arrays.equals(polygon[0], polygon[polygon.length - 1])) {
            polygon = Arrays.copyOfRange(polygon, 0, polygon.length - 1);
        }

        if (isClockwise(polygon)) {
            Collections.reverse(Arrays.asList(polygon));
        }
        //System.out.println(polygon.length);

        int corners = polygon.length;
        double[][][] triangles = new double[polygon.length - 2][3][2];

        polygon = toXY(polygon);
        double[] X = polygon[0];
        double[] Y = polygon[1];

        int i;
        int j;
        int k;
        int l;

        while (corners >= 3) {
            //  Crawl around the edge of the polygon, looking for three consecutive corners that form a usable triangle.
            i = 0;
            while (i < corners) {
                j = (i + 1) % corners;
                k = (j + 1) % corners;
                //  Verify that the candidate triangle is inside, not outside the
                //  polygon. Adapted from: alienryderflex.com/point_left_of_ray
                if ((X[i] != X[j] || Y[i] != Y[j]) && (Y[k] - Y[i]) * (X[j] - X[i]) >= (X[k] - X[i]) * (Y[j] - Y[i])) {
                    //  Verify that the candidate triangle's interior does not contain any corners of the polygon.
                    for (l = 0; l < corners; l++) {
                        if (pointInsideTriangle(X[i], Y[i], X[j], Y[j], X[k], Y[k], X[l], Y[l])) l = corners;
                    }
                    if (l == corners) {   //  A usable triangle has been found.
                        //  Here, you would add the triangle to your list, but in this example code, we will just draw it instead.
                        triangles[corners - 3] = new double[][] {{X[i], Y[i]}, {X[j], Y[j]}, {X[k], Y[k]}};
                        //  Reduce the remaining polygon to exclude the just-drawn triangle.
                        corners--;
                        for (l = j; l < corners; l++) {
                            X[l] = X[l + 1];
                            Y[l] = Y[l + 1];
                        }
                        //  Set a timeout to continue the process.
                        //animate(X, Y, corners);
                        //  Now exit the function; the timeout will trigger it again.

                        break;
                        //return;
                    }
                }
                //  The three corners (i,j,k) did not make a usable triangle. Move forward around the polygon to keep looking.
                i++;
            }

            if (corners >= 3 && !(i < corners)) {
                return null;
            }

            //code will get here with corners ranging from [2] <-> [2+]

        }

        return triangles;
        //  The process has ended; alert the user if it didn't successfully triangulate the entire polygon.
    }

    private static boolean isClockwise(double[][] polygon) {
        int sum = 0;
        for (int i = 0; i < polygon.length; i++) {
            sum += (int) ((polygon[i][0] + polygon[i + 1 == polygon.length ? 0 : i + 1][0]) * (polygon[i][1] + polygon[i + 1 == polygon.length ? 0 : i + 1][1]));
        }
        return !(sum < 0);
    }

    private static double[][] flipArray(double[][] polygon) {
        return null;
    }

    private static double[][] toXY(double[][] polygonArray) {
        double[] X = new double[polygonArray.length];
        double[] Y = new double[polygonArray.length];

        for (int i = 0; i < polygonArray.length; i++) {
            X[i] = polygonArray[i][0];
            Y[i] = polygonArray[i][1];
        }

        return new double[][] {X, Y};
    }

    private static boolean pointInsideTriangle(double Ax, double Ay, double Bx, double By, double Cx, double Cy, double x, double y) {
        boolean inside = false;

        if ((x!=Ax || y!=Ay) && (x!=Bx || y!=By) && (x!=Cx || y!=Cy)) {
            if ((Ay<y && By>=y || By<y && Ay>=y) && Ax+(y-Ay)/(By-Ay)*(Bx-Ax)<x) inside = !inside;
            if ((By<y && Cy>=y || Cy<y && By>=y) && Bx+(y-By)/(Cy-By)*(Cx-Bx)<x) inside = !inside;
            if ((Cy<y && Ay>=y || Ay<y && Cy>=y) && Cx+(y-Cy)/(Ay-Cy)*(Ax-Cx)<x) inside = !inside;
        }

        return inside;
    }

}