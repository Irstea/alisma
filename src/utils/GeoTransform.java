package utils;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
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

	//GeometryFactory gf = new GeometryFactory();
	//Coordinate c;
	
	String WKT_lambert93 = "PROJCS[\"RGF93 / Lambert-93\",\n" + 
			"    GEOGCS[\"RGF93\",\n" + 
			"        DATUM[\"Reseau_Geodesique_Francais_1993\",\n" + 
			"            SPHEROID[\"GRS 1980\",6378137,298.257222101,\n" + 
			"                AUTHORITY[\"EPSG\",\"7019\"]],\n" + 
			"            TOWGS84[0,0,0,0,0,0,0],\n" + 
			"            AUTHORITY[\"EPSG\",\"6171\"]],\n" + 
			"        PRIMEM[\"Greenwich\",0,\n" + 
			"            AUTHORITY[\"EPSG\",\"8901\"]],\n" + 
			"        UNIT[\"degree\",0.01745329251994328,\n" + 
			"            AUTHORITY[\"EPSG\",\"9122\"]],\n" + 
			"        AUTHORITY[\"EPSG\",\"4171\"]],\n" + 
			"    UNIT[\"metre\",1,\n" + 
			"        AUTHORITY[\"EPSG\",\"9001\"]],\n" + 
			"    PROJECTION[\"Lambert_Conformal_Conic_2SP\"],\n" + 
			"    PARAMETER[\"standard_parallel_1\",49],\n" + 
			"    PARAMETER[\"standard_parallel_2\",44],\n" + 
			"    PARAMETER[\"latitude_of_origin\",46.5],\n" + 
			"    PARAMETER[\"central_meridian\",3],\n" + 
			"    PARAMETER[\"false_easting\",700000],\n" + 
			"    PARAMETER[\"false_northing\",6600000],\n" + 
			"    AUTHORITY[\"EPSG\",\"2154\"],\n" + 
			"    AXIS[\"X\",EAST],\n" + 
			"    AXIS[\"Y\",NORTH]]";
	
	String WKT_wgs84 = "GEOGCS[\"WGS 84\",\n" + 
			"    DATUM[\"WGS_1984\",\n" + 
			"        SPHEROID[\"WGS 84\",6378137,298.257223563,\n" + 
			"            AUTHORITY[\"EPSG\",\"7030\"]],\n" + 
			"        AUTHORITY[\"EPSG\",\"6326\"]],\n" + 
			"    PRIMEM[\"Greenwich\",0,\n" + 
			"        AUTHORITY[\"EPSG\",\"8901\"]],\n" + 
			"    UNIT[\"degree\",0.01745329251994328,\n" + 
			"        AUTHORITY[\"EPSG\",\"9122\"]],\n" + 
			"    AUTHORITY[\"EPSG\",\"4326\"]]";



	/**
	 * 
	 * @param point
	 * @return double[]
	 */
	public double[] wgs84toLambert93(double point[]) {
		return transform(point, "wgs84-lambert");
	}
	
	/**
	 * 
	 * @param point
	 * @return double[]
	 */
	public double[] lambert93ToWgs84 (double point[]) {
		return transform(point, "lambert-wgs84");
	}
	/**
	 * Transforme un point XY fourni en WGS84 vers le Lambert93
	 * @param point : double[X, Y]
	 * @param sens : wgs84-lambert|lambert-wgs84
	 * @return double[X, Y]
	 */
	public double[] transform(double [] point, String sens) {
		double[] newPoint = new double[2];
		MathTransform mathTransform;
		if (!Double.isNaN(point[0]) && !Double.isNaN(point[1])) {
			GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory( null );
			Coordinate coord = new Coordinate( point[0], point[1] );
			Point pointgeom = geometryFactory.createPoint( coord );
			try {
				CoordinateReferenceSystem wgsCRS = CRS.parseWKT(WKT_wgs84);
				CoordinateReferenceSystem lambertCRS = CRS.parseWKT(WKT_lambert93);
				if (sens.equals("wgs84-lambert")) {
					mathTransform = CRS.findMathTransform(wgsCRS, lambertCRS, false);
				} else {
					mathTransform = CRS.findMathTransform(lambertCRS, wgsCRS, false);
				}
				Point p1 = (Point) JTS.transform(pointgeom, mathTransform);
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
