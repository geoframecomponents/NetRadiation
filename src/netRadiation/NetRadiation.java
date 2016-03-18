package netRadiation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.annotations.Unit;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class NetRadiation extends JGTModel {

	@Description("The Hashmap with the time series of the Shortwave values")
	@In
	@Unit ("W/m2")
	public HashMap<Integer, double[]> inShortwaveValues;

	@Description("The Hashmap with the time series of the Downwelling values")
	@In
	@Unit ("W/m2")
	public HashMap<Integer, double[]> inDownwellingValues;

	@Description("The Hashmap with the time series of the Upwelling values")
	@In
	@Unit ("W/m2")
	public HashMap<Integer, double[]> inUpwellingValues;


	@Description("albedo")
	@In
	@Unit ("-")
	public double alfa;


	@Description("The shape file with the station measuremnts")
	@In
	public SimpleFeatureCollection inStations;

	@Description("The name of the field containing the ID of the station in the shape file")
	@In
	public String fStationsid;

	@Description(" The vetor containing the id of the station")
	Object []idStations;

	@Description("the linked HashMap with the coordinate of the stations")
	LinkedHashMap<Integer, Coordinate> stationCoordinates;

	@Description("the output hashmap withe the direct radiation")
	@Out
	public HashMap<Integer, double[]> outHMnetRad= new HashMap<Integer, double[]>();

	/**
	 * Process.
	 *
	 * @throws Exception the exception
	 */
	@Execute
	public void process() throws Exception { 


		// starting from the shp file containing the stations, get the coordinate
		//of each station
		stationCoordinates = getCoordinate(inStations, fStationsid);

		//create the set of the coordinate of the station, so we can 
		//iterate over the set
		Set<Integer> stationCoordinatesIdSet = stationCoordinates.keySet();


		// trasform the list of idStation into an array
		idStations= stationCoordinatesIdSet.toArray();


		// iterate over the list of the stations
		for (int i=0;i<idStations.length;i++){

			double shortwave=inShortwaveValues.get(idStations[i])[0];
			
			double downwelling=inShortwaveValues.get(idStations[i])[0];
			
			double upwelling=inShortwaveValues.get(idStations[i])[0];
			
			double netRad=(1-alfa)*shortwave+downwelling-upwelling;
			
			/**Store results in Hashmaps*/
			storeResult((Integer)idStations[i],netRad);

		}


		}

		/**
		 * Gets the coordinate given the shp file and the field name in the shape with the coordinate of the station.
		 *
		 * @param collection is the shp file with the stations
		 * @param idField is the name of the field with the id of the stations 
		 * @return the coordinate of each station
		 * @throws Exception the exception in a linked hash map
		 */
		private LinkedHashMap<Integer, Coordinate> getCoordinate(SimpleFeatureCollection collection, String idField)
				throws Exception {
			LinkedHashMap<Integer, Coordinate> id2CoordinatesMap = new LinkedHashMap<Integer, Coordinate>();
			FeatureIterator<SimpleFeature> iterator = collection.features();
			Coordinate coordinate = null;
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					int stationNumber = ((Number) feature.getAttribute(idField)).intValue();
					coordinate = ((Geometry) feature.getDefaultGeometry()).getCentroid().getCoordinate();
					id2CoordinatesMap.put(stationNumber, coordinate);
				}
			} finally {
				iterator.close();
			}

			return id2CoordinatesMap;
		}


		/**
		 * Store result in given hashpmaps.
		 *
		 * @param downwellingALLSKY: the downwelling radiation in all sky conditions
		 * @param upwelling: the upwelling radiation
		 * @param longwave: the longwave radiation
		 * @throws SchemaException 
		 */
		private void storeResult(Integer ID,double netRad) 
				throws SchemaException {

			outHMnetRad.put(ID, new double[]{netRad});

		}

	}
