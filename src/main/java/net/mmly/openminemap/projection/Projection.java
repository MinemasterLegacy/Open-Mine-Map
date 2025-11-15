package net.mmly.openminemap.projection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.*;

public class Projection {
    static void _validate_geographic_coordinates(double lat, double lon) throws CoordinateValueError {
        if(!(-90 <= lat && lat <= 90)) {
            throw new CoordinateValueError(lat, lon);
        }
        if(!(-180 <= lon && lon <= 180)) {
            throw new CoordinateValueError(lat, lon);
        }
    } //done

    static void _validate_minecraft_coordinates(double x, double z) throws CoordinateValueError {
        if (!(-25000000 <= x && x <= 25000000)) {
            throw new CoordinateValueError(x, z);
        }
        if (!(-15000000 <= z && z <= 15000000)) {
            throw new CoordinateValueError(x, z);
        }
    } //done

    static GeographicProjection _orient_projection(GeographicProjection base, Orientation orientation) {
        if (base.upright()) {
            if (orientation == Orientation.UPRIGHT) {
                return base;
            }
            base = new UprightOrientation(base);
        }

        if (orientation == Orientation.SWAPPED) {
            return new InvertedOrientation(base);
        } else if (orientation == Orientation.UPRIGHT) {
            base = new UprightOrientation(base);
        }

        return base;
    }

    static ModifiedAirOcean _projection = new ModifiedAirOcean();
    static GeographicProjection _upright_proj = _orient_projection(_projection, Orientation.UPRIGHT);
    static ScaleProjection _scale_proj = new ScaleProjection(_upright_proj, 7318261.522857145, 7318261.522857145);

    public static double[] from_geo(double lat, double lon) throws CoordinateValueError {
        _validate_geographic_coordinates(lat, lon);
        return _scale_proj.from_geo(lon, lat);
    }

    public static double[] to_geo(double x, double z) throws CoordinateValueError {
        _validate_minecraft_coordinates(x, z);
        return _scale_proj.to_geo(x, z);
    }

    static double[][] load_conformal_data() {
        File conformal_file = new File(System.getProperty("user.dir") + File.separator + "openminemap/conformal2.txt");
        //System.out.println(conformal_file.getAbsolutePath());
        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
        InputStream stream;
        int available = -1;

        try {
            stream = manager.getResource(Identifier.of("openminemap", "conformal2.txt")).get().getInputStream();
            //System.out.println(stream.available());
            //System.out.println(new BufferedReader(new InputStreamReader(stream, "UTF-8")).readLine());
            available = stream.available();
        } catch (IOException e) {
            System.out.println("Required conformal correction file not found: openminemap:conformal2.txt");
            throw new RuntimeException(e);
            //System.out.println("error: "+e.getMessage());
        }


        if (available > 0) {
            try {
                //BufferedReader reader = new BufferedReader(new FileReader(stream));
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                double[][] x = new double[33153][2];
                String text;
                String[] vPair;
                for (int i = 0; i < 33153; i++) {
                    text = reader.readLine();
                    text = text.substring(1, text.length()-2);
                    vPair = text.split(", ");
                    x[i][0] = Double.valueOf(vPair[0]);
                    x[i][1] = Double.valueOf(vPair[1]);
                }
                reader.close();
                return x;
            } catch (Exception r) {
                //r.printStackTrace();
                System.out.println("Failed to load conformal data from: openminemap:conformal2.txt");
            }
        } else {
            System.out.println("Required conformal correction file not found: openminemap:conformal2.txt");
        }
        return null;
    }

    static double[][] get_conformal_json() {
        return load_conformal_data();
    }

    public Projection() {

    }

}

enum Orientation {
    NONE,
    UPRIGHT,
    SWAPPED
}

class InvertableVectorField {
    final double ROOT3 = Math.sqrt(3);
    int side_length;
    double[][] vector_x;
    double[][] vector_y;

    InvertableVectorField(double[][] vector_x, double[][] vector_y) {
        this.side_length = vector_x.length - 1;
        this.vector_x = vector_x;
        this.vector_y = vector_y;
    } //done

    double[] get_interpolated_vector(double x, double y) {

        x *= this.side_length; // +
        y *= this.side_length; // +

        double v = 2 * y / this.ROOT3;
        double u = x - v * 0.5;

        int u1 = Math.max(0, Math.min((int) u, this.side_length - 1)); //+
        int v1 = Math.max(0, Math.min((int) v, this.side_length - u1 - 1)); //+

        int flip = 1; // +
        double y3;
        double x3;
        double valx1;
        double valy1;
        double valx2;
        double valy2;
        double valx3;
        double valy3;

        if(y < -this.ROOT3 * (x - u1 - v1 - 1) || v1 == this.side_length - u1 - 1) { //problems with vectorx and vector y
            valx1 = this.vector_x[u1][v1]; // +
            valy1 = this.vector_y[u1][v1]; // +
            valx2 = this.vector_x[u1][v1 + 1]; // +
            valy2 = this.vector_y[u1][v1 + 1]; // +
            valx3 = this.vector_x[u1 + 1][v1]; // +
            valy3 = this.vector_y[u1 + 1][v1]; // +

            y3 = 0.5 * this.ROOT3 * v1; // +
            x3 = (u1 + 1) + 0.5 * v1; // +
        } else {
            valx1 = this.vector_x[u1][v1 + 1]; // +
            valy1 = this.vector_y[u1][v1 + 1]; // +
            valx2 = this.vector_x[u1 + 1][v1]; // +
            valy2 = this.vector_y[u1 + 1][v1]; // +
            valx3 = this.vector_x[u1 + 1][v1 + 1]; // +
            valy3 = this.vector_y[u1 + 1][v1 + 1]; // +

            flip = -1; // +
            y = -y; // +

            y3 = -(0.5 * this.ROOT3 * (v1 + 1)); // +
            x3 = (u1 + 1) + 0.5 * (v1 + 1); // +
        }
        //System.out.println(valx1);

        double w1 = -(y - y3) / this.ROOT3 - (x - x3); // +
        double w2 = 2 * (y - y3) / this.ROOT3; // +
        double w3 = 1 - w1 - w2; // +

        double val_x = valx1 * w1 + valx2 * w2 + valx3 * w3; // +
        double val_y = valy1 * w1 + valy2 * w2 + valy3 * w3; // +

        double dfdx = (valx3 - valx1) * this.side_length; // +
        double dfdy = this.side_length * flip * (2 * valx2 - valx1 - valx3) / this.ROOT3; // +
        double dgdx = (valy3 - valy1) * this.side_length; // +
        double dgdy = this.side_length * flip * (2 * valy2 - valy1 - valy3) / this.ROOT3; // +

        return new double[] {val_x, val_y, dfdx, dfdy, dgdx, dgdy}; // +
    } //done

    double[] apply_newtons_method(double expected_f, double expected_g, double x_est, double y_est, int iterations) {
        double[] itv;
        double val_x;
        double val_y;
        double dfdx;
        double dfdy;
        double dgdx;
        double dgdy;
        double f;
        double g;
        double determinant;

        for (int i = 0; i < iterations; i++) {
            itv = this.get_interpolated_vector(x_est, y_est); //something here
            val_x = itv[0];
            val_y = itv[1];
            dfdx = itv[2];
            dfdy = itv[3];
            dgdx = itv[4];
            dgdy = itv[5];

            f = val_x - expected_f;
            g = val_y - expected_g;

            determinant = 1.0 / (dfdx * dgdy - dfdy * dgdx);

            x_est -= determinant * (dgdy * f - dfdy * g);
            y_est -= determinant * (-dgdx * f + dfdx * g);
        }
        return new double[] {x_est, y_est};
    } //done

} //done

class UprightOrientation extends ProjectionTransform{
    UprightOrientation(GeographicProjection input_projection) {
        super(input_projection);
    } //done

    @Override
    double[] to_geo(double x, double y) {
        return this.input.to_geo(x, -y);
    } //done

    @Override
    double[] from_geo(double lon, double lat) {
        //System.out.println("ll: "+lon+", "+lat);
        double[] coords = this.input.from_geo(lon, lat);
        return new double[] {coords[0], -coords[1]};
    } //done

    @Override
    boolean upright() {
        return !this.input.upright();
    } //done

    @Override
    double[] bounds() {
        double[] bounds = this.input.bounds();
        return new double[] {bounds[0], -bounds[3], bounds[2], -bounds[1]};
    } //done
} //done

class InvertedOrientation extends ProjectionTransform{
    InvertedOrientation(GeographicProjection input_projection) {
        super(input_projection);
    } //done

    double[] to_geo(double x, double y) {
        return this.input.to_geo(x, y);
    } //done

    double[] from_geo(double lon, double lat) {
        return this.input.from_geo(lon, lat);
    } //done

    double[] bounds() {
        double[] bounds = this.input.bounds();
        return new double[] {bounds[1], bounds[0], bounds[3], bounds[2]};
    } //done
} //done

class ScaleProjection extends ProjectionTransform{
    double scale_x;
    double scale_y;
    ScaleProjection(GeographicProjection input_projection, double scale_x, double scale_y) {
        super(input_projection);
        this.scale_x = scale_x;
        this.scale_y = scale_y;
    } //done

    @Override
    double[] to_geo(double x, double y) {
        return this.input.to_geo(x / this.scale_x, y / this.scale_y);
    } //done

    @Override
    double[] from_geo(double lon, double lat) {
        double[] coords = this.input.from_geo(lon, lat);
        return new double[] {coords[0] * this.scale_x, coords[1] * this.scale_y};
    } //done

    @Override
    boolean upright() {
        if (this.scale_y < 0) {
            return !this.input.upright();
        } else {
            return this.input.upright();
        }
    } //done

    @Override
    double[] bounds() {
        double[] bounds = this.input.bounds();
        return new double[] {bounds[0] * this.scale_x, bounds[1] * this.scale_y, bounds[2] * this.scale_x, bounds[3] * this.scale_y};
    } //done

    @Override
    double meters_per_unit() {
        double base_mpu = this.input.meters_per_unit();
        double scale_factor = Math.sqrt((this.scale_x * this.scale_x + this.scale_y * this.scale_y) / 2);
        return (base_mpu / scale_factor);
    } //done
} //done

abstract class GeographicProjection {
    final double EARTH_CIRCUMFERENCE = 40075017.0F;
    final double EARTH_POLAR_CIRCUMFERENCE = 40008000.0F;

    double[] to_geo(double x, double y) {
        return new double[] {x, y};
    } //done

    double[] from_geo(double lon, double lat) {
        //System.out.println("a: "+lon+", "+lat);
        return new double[] {lon, lat};
    } //done

    double meters_per_unit() {
        return 100000.0F;
    } //done

    double[] bounds() {
        double[] coords = new double[] {this.from_geo(-180, 0)[0], this.from_geo(0, -90)[1], this.from_geo(180, 0)[0], this.from_geo(0, 90)[1]};
        if (coords[0] > coords[2]) {
            double placeholder = coords[0];
            coords[0] = coords[2];
            coords[2] = placeholder;
        }
        if (coords[1] > coords[3]) {
            double placeholder = coords[1];
            coords[1] = coords[3];
            coords[3] = placeholder;
        }
        return coords;
    } //done

    boolean upright() {
        double north_y = this.from_geo(0, 90)[1];
        double south_y = this.from_geo(0, -90)[1];
        return north_y <= south_y;
    } //done
} //done

abstract class ProjectionTransform extends GeographicProjection {
    GeographicProjection input;
    ProjectionTransform(GeographicProjection input_projection) {
        this.input = input_projection;
    } //done

    @Override
    boolean upright() {
        return this.input.upright();
    } //done

    @Override
    double[] bounds() {
        return this.input.bounds();
    } //done

    @Override
    double meters_per_unit() {
        return  this.input.meters_per_unit();
    } //done
} //done

class Airocean extends GeographicProjection {
    final double ARC = 2 * Math.asin(Math.sqrt(5 - Math.sqrt(5)) / Math.sqrt(10));
    final double TO_RADIANS = Math.PI / 180;
    final double ROOT3 = Math.sqrt(3);

    final double[][] _VERT_RAW = {
            {10.536199, 64.700000},
            {-5.245390, 2.300882},
            {58.157706, 10.447378},
            {122.300000, 39.100000},
            {-143.478490, 50.103201},
            {-67.132330, 23.717925},
            {36.521510, -50.103200},
            {112.867673, -23.717930},
            {174.754610, -2.300882},
            {-121.842290, -10.447350},
            {-57.700000, -39.100000},
            {-169.463800, -64.700000},
    };
    final int[][] ISO = {
            {2, 1, 6},
            {1, 0, 2},
            {0, 1, 5},
            {1, 5, 10},
            {1, 6, 10},
            {7, 2, 6},
            {2, 3, 7},
            {3, 0, 2},
            {0, 3, 4},
            {4, 0, 5},
            {5, 4, 9},
            {9, 5, 10},
            {10, 9, 11},
            {11, 6, 10},
            {6, 7, 11},
            {8, 3, 7},
            {8, 3, 4},
            {8, 4, 9},
            {9, 8, 11},
            {7, 8, 11},
            {11, 6, 7},
            {3, 7, 8},
    };
    final int[][] CENTER_MAP_RAW = {
            {-3, 7},
            {-2, 5},
            {-1, 7},
            {2, 5},
            {4, 5},
            {-4, 1},
            {-3, -1},
            {-2, 1},
            {-1, -1},
            {0, 1},
            {1, -1},
            {2, 1},
            {3, -1},
            {4, 1},
            {5, -1},
            {-3, -5},
            {-1, -5},
            {1, -5},
            {2, -7},
            {-4, -7},
            {-5, -5},
            {-2, -7}
    };
    final int[] FLIP_TRIANGLE = {
            1, 0, 1, 0, 0,
            1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
            1, 1, 1, 0, 0,
            1, 0
    };
    final int[][] FACE_ON_GRID = {
            {-1, -1, 0, 1, 2, -1, -1, 3, -1, 4, -1},
            {-1, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14},
            {20, 19, 15, 21, 16, -1, 17, 18, -1, -1, -1},
    };
    final double Z = Math.sqrt(5 + 2 * Math.sqrt(5)) / Math.sqrt(15);
    final double EL = Math.sqrt(8) / Math.sqrt(5 + Math.sqrt(5));
    final double EL6 = EL / 6;
    final double DVE = Math.sqrt(3 + Math.sqrt(5)) / Math.sqrt(5 + Math.sqrt(5));
    final double R = -3 * EL6 / DVE;

    final double[] OUT_OF_BOUNDS = {Double.NaN, Double.NaN};
    int newton = 5;

    double[][] CENTER_MAP;
    double[][] VERT;
    double[][] CENTROID;
    double[][][] ROTATION_MATRIX;
    double[][][] INVERSE_ROTATION_MATRIX;

    Airocean() {
        this._initialize_vertices();
        this._initialize_centers();
        this._initialize_matrices();
    }

    void _initialize_vertices() {
        this.VERT = new double[_VERT_RAW.length][2];
        double lon;
        double lat;
        int i = 0;
        for (double[] vertex : this._VERT_RAW) {
            lon = vertex[0] * this.TO_RADIANS;
            lat = (90 - vertex[1]) * this.TO_RADIANS;
            this.VERT[i] = new double[] {lon, lat};
            i++;
        }
    } //done

    void _initialize_centers() {
        this.CENTER_MAP = new double[this.CENTER_MAP_RAW.length][3];
        double x;
        double y;
        int i = 0;
        for (int[] center : this.CENTER_MAP_RAW) {
            x = center[0] * 0.5 * this.ARC;
            y = center[1] * this.ARC * this.ROOT3 / 12;
            this.CENTER_MAP[i] = new double[] {x, y};
            i++;
        }
    } //done

    void _initialize_matrices() {
        CENTROID = new double[22][3];
        ROTATION_MATRIX = new double[22][3][3];
        INVERSE_ROTATION_MATRIX = new double[22][3][3];

        double[] a;
        double[] b;
        double[] c;
        double x_sum;
        double y_sum;
        double z_sum;
        double mag;
        double c_lon;
        double c_lat;
        double[] v;

        for (int i = 0; i < 22; i++) {
            a = this._cart(this.VERT[this.ISO[i][0]][0], this.VERT[this.ISO[i][0]][1]);
            b = this._cart(this.VERT[this.ISO[i][1]][0], this.VERT[this.ISO[i][1]][1]);
            c = this._cart(this.VERT[this.ISO[i][2]][0], this.VERT[this.ISO[i][2]][1]);

            x_sum = a[0] + b[0] + c[0];
            y_sum = a[1] + b[1] + c[1];
            z_sum = a[2] + b[2] + c[2];

            mag = Math.sqrt(x_sum * x_sum + y_sum * y_sum + z_sum * z_sum);

            this.CENTROID[i][0] = x_sum / mag;
            this.CENTROID[i][1] = y_sum / mag;
            this.CENTROID[i][2] = z_sum / mag;

            c_lon = Math.atan2(y_sum, x_sum);
            c_lat = Math.atan2(Math.sqrt(x_sum * x_sum + y_sum * y_sum), z_sum);

            v = this._y_rot(this.VERT[this.ISO[i][0]][0] - c_lon, this.VERT[this.ISO[i][0]][1], -c_lat);

            this.ROTATION_MATRIX[i] = _produce_zyz_rotation_matrix(-c_lon, -c_lat, (Math.PI / 2) - v[0]);
            this.INVERSE_ROTATION_MATRIX[i] = _produce_zyz_rotation_matrix(v[0] - (Math.PI / 2), c_lat, c_lon);
        }
    } //done

    static double[] _cart(double longitude, double phi) {
        double sin_phi = Math.sin(phi);
        return new double[] {sin_phi * Math.cos(longitude), sin_phi * Math.sin(longitude), Math.cos(phi)};
    } //done

    double[] _y_rot(double longitude, double phi, double rot) {
        double[] c = this._cart(longitude, phi);

        double x = c[0];
        double new_x = c[2] * Math.sin(rot) + x * Math.cos(rot);
        double new_z = c[2] * Math.cos(rot) - x * Math.sin(rot);

        double mag = Math.sqrt(new_x * new_x + c[1] * c[1] + new_z * new_z);
        new_x /= mag;
        double c_y = c[1] / mag;
        new_z /= mag;

        return new double[] {
                Math.atan2(c_y, new_x),
                Math.atan2(Math.sqrt(new_x * new_x + c_y * c_y), new_z)
        };
    } //done

    static double[][] _produce_zyz_rotation_matrix(double a, double b, double c) { //this sets values in 'out', which may be supposed to set values in 'ROTATION_MATRIX' and 'INVERSE_ROTATION_MATRIX'; function may need to be modified to accommodate
        double sin_a = Math.sin(a);
        double cos_a = Math.cos(a);
        double sin_b = Math.sin(b);
        double cos_b = Math.cos(b);
        double sin_c = Math.sin(c);
        double cos_c = Math.cos(c);

        double[][] out = new double[3][3];

        out[0][0] = cos_a * cos_b * cos_c - sin_c * sin_a;
        out[0][1] = -sin_a * cos_b * cos_c - sin_c * cos_a;
        out[0][2] = cos_c * sin_b;

        out[1][0] = sin_c * cos_b * cos_a + cos_c * sin_a;
        out[1][1] = cos_c * cos_a - sin_c * cos_b * sin_a;
        out[1][2] = sin_c * sin_b;

        out[2][0] = -sin_b * cos_a;
        out[2][1] = sin_b * sin_a;
        out[2][2] = cos_b;

        return out;
    } //done

    int _find_triangle(double x, double y, double z) {
        double min_dist = Double.MAX_VALUE; //was originally infinity
        int face = 0;
        double x_d;
        double y_d;
        double z_d;
        double dist_sq;

        for (int i = 0; i < 20; i++) {
            x_d = this.CENTROID[i][0] - x;
            y_d = this.CENTROID[i][1] - y;
            z_d = this.CENTROID[i][2] - z;

            dist_sq = x_d * x_d + y_d * y_d + z_d * z_d;
            if (dist_sq < min_dist) {
                if (dist_sq < 0.1) {
                    return i;
                }
                face = i;
                min_dist = dist_sq;

            }
        }

        return face;
    } //done

    int _find_triangle_grid(double x, double y) {
        double x_p = x / this.ARC;
        double y_p = y / (this.ARC * this.ROOT3);
        int row;
        double x_r;
        double y_r;
        int g_x;
        int g_y;
        int col;

        if (y_p > -0.25) {
            if (y_p < 0.25) {
                row = 1;
            } else if (y_p <= 0.75) {
                row = 0;
                y_p = 0.5 - y_p;
            } else {
                return -1;
            }
        } else if (y_p >= -0.75) {
            row = 2;
            y_p = -y_p - 0.5;
        } else {
            return -1;
        }

        y_p += 0.25;

        x_r = x_p - y_p;
        y_r = x_p + y_p;

        g_x = (int) Math.floor(x_r);
        g_y = (int) Math.floor(y_r);

        col = 2 * g_x + (g_y == g_x ? 0 : 1) + 6;

        if (col < 0 || col >= 11) {
            return -1;
        }

        return this.FACE_ON_GRID[row][col];
    } //done

    double[] _triangle_transform(double x, double y, double z) {
        double s = this.Z / z;

        double x_p = s * x;
        double y_p = s * y;

        double a = Math.atan((2 * y_p / this.ROOT3 - this.EL6) / this.DVE);
        double b = Math.atan((x_p - y_p / this.ROOT3 - this.EL6) / this.DVE);
        double c = Math.atan((-x_p - y_p / this.ROOT3 - this.EL6) / this.DVE);

        return new double[] {0.5 * (b - c), (2 * a - b - c) / (2 * this.ROOT3)};
    } //done

    double[] _inverse_triangle_transform_newton(double x_pp, double y_pp) {
        double tan_a_off = Math.tan(this.ROOT3 * y_pp + x_pp);
        double tan_b_off = Math.tan(2 * x_pp);

        double a_numer = tan_a_off * tan_a_off + 1;
        double b_numer = tan_b_off * tan_b_off + 1;

        double tan_a = tan_a_off;
        double tan_b = tan_b_off;
        double tan_c = 0.0;

        double a_denom = 1.0;
        double b_denom = 1.0;

        double f;
        double f_p;

        for (int i = 0; i < 5; i++) {
            f = tan_a + tan_b + tan_c - this.R;
            f_p = a_numer * a_denom * a_denom + b_numer * b_denom * b_denom + 1;

            tan_c -= f / f_p;

            a_denom = 1 / (1 - tan_c * tan_a_off);
            b_denom = 1 / (1 - tan_c * tan_b_off);

            tan_a = (tan_c + tan_a_off) * a_denom;
            tan_b = (tan_c + tan_b_off) * b_denom;
        }

        double y_p = this.ROOT3 * (this.DVE * tan_a + this.EL6) / 2;
        double x_p = this.DVE * tan_b + y_p / this.ROOT3 + this.EL6;

        double x_p_over_z = x_p / this.Z;
        double y_p_over_z = y_p / this.Z;

        double z = 1 / Math.sqrt(1 + x_p_over_z * x_p_over_z + y_p_over_z * y_p_over_z);

        return new double[] {z * x_p_over_z, z * y_p_over_z, z};
    } //done

    double[] _inverse_triangle_transform(double x, double y) {
        return this._inverse_triangle_transform_newton(x, y);
    } //done

    @Override
    double[] from_geo(double lon, double lat) {
        //System.out.println("a: "+lon+", "+lat);
        lat = 90 - lat;

        double lon_rad = lon * this.TO_RADIANS;
        double lat_rad = lat * this.TO_RADIANS;

        double sin_phi = Math.sin(lat_rad);

        double x = Math.cos(lon_rad) * sin_phi;
        double y = Math.sin(lon_rad) * sin_phi;
        double z = Math.cos(lat_rad);

        int face = this._find_triangle(x, y, z);

        double[][] rotation_matrix = this.ROTATION_MATRIX[face];
        double x_p = (x * rotation_matrix[0][0] +
                y * rotation_matrix[0][1] +
                z * rotation_matrix[0][2]);
        double y_p = (x * rotation_matrix[1][0] +
                y * rotation_matrix[1][1] +
                z * rotation_matrix[1][2]);
        double z_p = (x * rotation_matrix[2][0] +
                y * rotation_matrix[2][1] +
                z * rotation_matrix[2][2]);

        double[] out = this._triangle_transform(x_p, y_p, z_p);

        double out_x = out[0];
        double out_y = out[1];
        //System.out.println("c: "+out_x+", "+out_y);

        if (this.FLIP_TRIANGLE[face] != 0) {
            out_x = -out_x;
            out_y = -out_y;
        }

        double orig_x = out_x;
        if (((face == 15 && orig_x > out_y * this.ROOT3) || face == 14) && orig_x > 0) {
            out_x = 0.5 * orig_x - 0.5 * this.ROOT3 * out_y;
            out_y = 0.5 * this.ROOT3 * orig_x + 0.5 * out_y;
            face += 6;
        }

        out_x += this.CENTER_MAP[face][0];
        out_y += this.CENTER_MAP[face][1];

        //System.out.println("b: "+out_x+", "+out_y);
        return new double[] {out_x, out_y};
    } //done

    @Override
    double[] to_geo(double x, double y) {
        int face = this._find_triangle_grid(x, y);

        if (face == -1) {
            return this.OUT_OF_BOUNDS;
        }

        x -= this.CENTER_MAP[face][0];
        y -= this.CENTER_MAP[face][1];

        if (face == 14 && x > 0) {
            return this.OUT_OF_BOUNDS;
        } else if (face == 20 && -y * this.ROOT3 > x) {
            return this.OUT_OF_BOUNDS;
        } else if (face == 15 && x > 0 && x > y * this.ROOT3) {
            return this.OUT_OF_BOUNDS;
        } else if (face == 21 && (x < 0 || -y * this.ROOT3 > x)) {
            return this.OUT_OF_BOUNDS;
        }

        if (this.FLIP_TRIANGLE[face] != 0) {
            x = -x;
            y = -y;
        }

        double[] itt = this._inverse_triangle_transform(x, y);
        double x_3d = itt[0];
        double y_3d = itt[1];
        double z_3d = itt[2];

        double[][] inverse_rotation_matrix = this.INVERSE_ROTATION_MATRIX[face];
        double x_p = (x_3d * inverse_rotation_matrix[0][0] +
                y_3d * inverse_rotation_matrix[0][1] +
                z_3d * inverse_rotation_matrix[0][2]);
        double y_p = (x_3d * inverse_rotation_matrix[1][0] +
                y_3d * inverse_rotation_matrix[1][1] +
                z_3d * inverse_rotation_matrix[1][2]);
        double z_p = (x_3d * inverse_rotation_matrix[2][0] +
                y_3d * inverse_rotation_matrix[2][1] +
                z_3d * inverse_rotation_matrix[2][2]);

        double lon = Math.atan2(y_p, x_p) / this.TO_RADIANS;
        double lat = 90 - Math.acos(z_p) / this.TO_RADIANS;

        return new double[] {lat, lon};
    } //done
} //done

class ConformalEstimate extends Airocean {
    final double VECTOR_SCALE_FACTOR = 1 / 1.1473979730192934;
    final int side_length = 256;
    InvertableVectorField inverse;

    ConformalEstimate() {
        double[][] xs = new double[(side_length + 1)][(side_length + 1)];
        double[][] ys = new double[(side_length + 1)][(side_length + 1)];

        double[][] conformal_data = Projection.get_conformal_json();
        //System.out.println(conformal_data[0][0]);
        double[] entry;

        int counter = 0;
        for (int v = 0; v < side_length + 1; v++) {
            for (int u = 0; u < side_length + 1 - v; u++) {
                if (counter < conformal_data.length) {
                    entry = conformal_data[counter];
                    xs[u][v] = entry[0] * this.VECTOR_SCALE_FACTOR;
                    ys[u][v] = entry[1] * this.VECTOR_SCALE_FACTOR;
                } else {
                    xs[u][v] = (double) u / side_length * this.VECTOR_SCALE_FACTOR;
                    ys[u][v] = (double) v / side_length * this.VECTOR_SCALE_FACTOR;
                }
                counter += 1;
            }
        }

        this.inverse = new InvertableVectorField(xs, ys);
    } //done

    @Override
    double[] _triangle_transform(double x, double y, double z) {
        double[] c = super._triangle_transform(x, y, z);

        double orig_x = c[0];
        double orig_y = c[1];

        c[0] /= this.ARC;
        c[1] /= this.ARC;

        c[0] += 0.5;
        c[1] += this.ROOT3 / 6;

        double[] corrected = this.inverse.apply_newtons_method(orig_x, orig_y, c[0], c[1], 5);

        c[0] = corrected[0] - 0.5;
        c[1] = corrected[1] - this.ROOT3 / 6;

        c[0] *= this.ARC;
        c[1] *= this.ARC;

        return new double[] {c[0], c[1]};

    } //done

    @Override
    double[] _inverse_triangle_transform(double x, double y) {
        x /= this.ARC;
        y /= this.ARC;

        x += 0.5;
        y += this.ROOT3 / 6;

        double[] corrected = this.inverse.get_interpolated_vector(x, y);

        return super._inverse_triangle_transform(corrected[0], corrected[1]);
    } //done

    @Override
    double meters_per_unit() {
        return ((40075017 / (2 * Math.PI)) / this.VECTOR_SCALE_FACTOR);
    } //done
}

class ModifiedAirOcean extends ConformalEstimate {

    double THETA = -150 * TO_RADIANS;
    double SIN_THETA = Math.sin(THETA);
    double COS_THETA = Math.cos(THETA);

    double BERING_X = -0.3420420960118339;
    double BERING_Y = -0.322211064085279;
    double ARCTIC_Y = -0.2;

    double ALEUTIAN_Y = -0.5000446805492526;
    double ALEUTIAN_XL = -0.5149231279757507;
    double ALEUTIAN_XR = -0.45;

    double ARCTIC_M;
    double ARCTIC_B;
    double ALEUTIAN_M;
    double ALEUTIAN_B;

    ModifiedAirOcean() {
        super();
        this.ARCTIC_M = ((this.ARCTIC_Y - this.ROOT3 * this.ARC / 4) /
                (this.BERING_X - (-0.5 * this.ARC)));
        this.ARCTIC_B = this.ARCTIC_Y - this.ARCTIC_M * this.BERING_X;

        this.ALEUTIAN_M = ((this.BERING_Y - this.ALEUTIAN_Y) /
                (this.BERING_X - this.ALEUTIAN_XR));
        this.ALEUTIAN_B = this.BERING_Y - this.ALEUTIAN_M * this.BERING_X;
    }

    boolean _is_eurasian_part(double x, double y) {
        //System.out.println(x);
        if (x > 0) {
            return false;
        }
        if (x < -0.5 * this.ARC) {
            return true;
        }

        if (y > this.ROOT3 * this.ARC / 4) {
            return x < 0;
        }

        if (y < this.ALEUTIAN_Y) {
            return y < (this.ALEUTIAN_Y + this.ALEUTIAN_XL) - x;
        }

        if (y > this.BERING_Y) {
            if (y < this.ARCTIC_Y) {
                return x < this.BERING_X;
            }
            return y < this.ARCTIC_M * x + this.ARCTIC_B;
        }
        return y > this.ALEUTIAN_M * x + this.ALEUTIAN_B;
    } //done

    @Override
    double[] from_geo(double lon, double lat) {
        double[] c = super.from_geo(lon, lat);
        double x = c[0];
        double y = c[1];

        boolean easia = this._is_eurasian_part(x, y);
        //System.out.println(easia);

        y -= 0.75 * this.ARC * this.ROOT3;

        if (easia) {
            x += this.ARC;

            double t = x;
            x = this.COS_THETA * x - this.SIN_THETA * y;
            y = this.SIN_THETA * t + this.COS_THETA * y;
        } else {
            x -= this.ARC;
        }

        c[0] = y;
        c[1] = -x;

        return new double[] {c[0], c[1]};
    } //done

    @Override
    double[] to_geo(double x, double y) {
        boolean easia;
        if (y < 0) {
            easia = x > 0;
        } else if (y > this.ARC / 2) {
            easia = x > -this.ROOT3 * this.ARC / 2;
        } else {
            easia = y * -this.ROOT3 < x;
        }

        double t = x;
        x = -y;
        y = t;

        if (easia) {
            t = x;
            x = this.COS_THETA * x + this.SIN_THETA * y;
            y = this.COS_THETA * y - this.SIN_THETA * t;
            x -= this.ARC;
        } else {
            x += this.ARC;
        }

        y += 0.75 * this.ARC * this.ROOT3;

        if (easia != this._is_eurasian_part(x, y)) {
            return OUT_OF_BOUNDS;
        }
        return super.to_geo(x, y);
    } //done

    @Override
    double[] bounds() {
        return new double[] {
                -1.5 * this.ARC * this.ROOT3,
                -1.5 * this.ARC,
                3 * this.ARC,
                this.ROOT3 * this.ARC
        };
    } //done

}