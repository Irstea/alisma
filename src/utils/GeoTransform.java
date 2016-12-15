package utils;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Classe permettant de realiser des transformations geographiques
 * @author quinton
 *
 */
public class GeoTransform {

	GeometryFactory gf = new GeometryFactory();
	Coordinate c;
	
	String WKT_lambert93 = "PROJCS[\"RGF93 / Lambert-93\","
			+ "GEOGCS[\"RGF93\","
			+ "DATUM[\"Reseau_Geodesique_Francais_1993\","
			+ "SPHEROID[\"GRS 1980\",6378137,298.257222101,"
			+ "AUTHORITY[\"EPSG\",\"7019\"]],"
			+ "TOWGS84[0,0,0,0,0,0,0],"
			+ "AUTHORITY[\"EPSG\",\"6171\"]],"
			+ "PRIMEM[\"Greenwich\",0,"
			+ "AUTHORITY[\"EPSG\",\"8901\"]],"
			+ "UNIT[\"degree\",0.0174532925199433,"
			+ "AUTHORITY[\"EPSG\",\"9122\"]],"
			+ "AUTHORITY[\"EPSG\",\"4171\"]],"
			+ "PROJECTION[\"Lambert_Conformal_Conic_2SP\"],"
			+ "PARAMETER[\"standard_parallel_1\",49],"
			+ "PARAMETER[\"standard_parallel_2\",44],"
			+ "PARAMETER[\"latitude_of_origin\",46.5],"
			+ "PARAMETER[\"central_meridian\",3],"
			+ "PARAMETER[\"false_easting\",700000],"
			+ "PARAMETER[\"false_northing\",6600000],"
			+ "UNIT[\"metre\",1,"
			+ "AUTHORITY[\"EPSG\",\"9001\"]],"
			+ "AXIS[\"X\",EAST],"
			+ "AXIS[\"Y\",NORTH],"
			+ "AUTHORITY[\"EPSG\",\"2154\"]]";
	
	String WKT_wgs84 = "GEOGCS[\"WGS 84\","
			+ "DATUM[\"WGS_1984\","
			+ "SPHEROID[\"WGS 84\",6378137,298.257223563,"
			+ "AUTHORITY[\"EPSG\",\"7030\"]],"
			+ "AUTHORITY[\"EPSG\",\"6326\"]],"
			+ "PRIMEM[\"Greenwich\",0,"
			+ "AUTHORITY[\"EPSG\",\"8901\"]],"
			+ "UNIT[\"degree\",0.0174532925199433,"
			+ "AUTHORITY[\"EPSG\",\"9122\"]],"
			+ "AUTHORITY[\"EPSG\",\"4326\"]]";


	/**
	 * Transforme un point XY fourni en WGS84 vers le Lambert93
	 * @param point : double[X, Y]
	 * @return double[X, Y]
	 */
	public double[] wgs84toLambert93(double point[]) {
		double[] newPoint = new double[2];

		if (!Double.isNaN(point[0]) && !Double.isNaN(point[1])) {
			c = new Coordinate(point[0], point[1]);
			Point p = gf.createPoint(c);
			try {
				CoordinateReferenceSystem sourceCRS = CRS.parseWKT(WKT_wgs84);
				CoordinateReferenceSystem targetCRS = CRS.parseWKT(WKT_lambert93);
				MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS, true);
				Point p1 = (Point) JTS.transform(p, mathTransform);
				newPoint[0] = p1.getX();
				newPoint[1] = p1.getY();
			} catch (NoSuchAuthorityCodeException e) {
				e.printStackTrace();
			} catch (FactoryException e) {
				e.printStackTrace();
			} catch (MismatchedDimensionException e) {
				e.printStackTrace();
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		return newPoint;
	}

}
