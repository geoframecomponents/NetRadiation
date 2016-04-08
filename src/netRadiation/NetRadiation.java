package netRadiation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;


import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.annotations.Unit;


import org.geotools.feature.SchemaException;
import org.jgrasstools.gears.libs.modules.JGTModel;


import com.vividsolutions.jts.geom.Coordinate;

public class NetRadiation extends JGTModel {

	@Description("The Hashmap with the time series of the direct shortwave radiation values")
	@In
	@Unit ("W/m2")
	public HashMap<Integer, double[]> inShortwaveDirectValues;
	
	@Description("The Hashmap with the time series of the diffuse shortwave radiation values")
	@In
	@Unit ("W/m2")
	public HashMap<Integer, double[]> inShortwaveDiffuseValues;

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

		checkNull(inShortwaveDirectValues);


		// reading the ID of all the stations 
		Set<Entry<Integer, double[]>> entrySet = inShortwaveDirectValues.entrySet();

		for (Entry<Integer, double[]> entry : entrySet) {
			Integer ID = entry.getKey();

			double direct=inShortwaveDirectValues.get(ID)[0];
			if(direct<0) direct=0;
			
			double diffuse=inShortwaveDiffuseValues.get(ID)[0];
			if(diffuse<0) diffuse=0;
			
			double downwelling = inDownwellingValues.get(ID)[0];
			if(downwelling<0) downwelling=0;
			
			double upwelling=inUpwellingValues.get(ID)[0];
			if(upwelling<0) upwelling=0;
			
			double netRad=(direct<=0)?0:(1-alfa)*(direct+diffuse)+downwelling-upwelling;
			netRad=(netRad<0)?0:netRad;
			
			/**Store results in Hashmaps*/
			storeResult((Integer)ID,netRad);

		}


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
