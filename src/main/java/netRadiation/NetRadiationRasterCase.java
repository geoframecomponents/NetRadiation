package netRadiation;

import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import javax.media.jai.iterator.RandomIterFactory;
import javax.media.jai.iterator.WritableRandomIter;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.annotations.Unit;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.feature.SchemaException;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.jgrasstools.gears.utils.RegionMap;
import org.jgrasstools.gears.utils.coverage.CoverageUtilities;

import com.vividsolutions.jts.geom.Coordinate;

public class NetRadiationRasterCase extends JGTModel {
	
	
	@Description("The map of the the interpolated SWRB at the top of the atmosphere.")
	@In
	public GridCoverage2D inShortwaveGrid;

	@Description("The Hashmap with the time series of the shortwave radiation values")
	@In
	@Unit ("W/m2")
	public HashMap<Integer, double[]> inShortwaveValues;
	

	@Description("The map of the the interpolated SWRB at the top of the atmosphere.")
	@In
	public GridCoverage2D inDownwellingGrid;

	@Description("The Hashmap with the time series of the Downwelling values")
	@In
	@Unit ("W/m2")
	public HashMap<Integer, double[]> inDownwellingValues;
	
	
	@Description("The map of the the interpolated SWRB at the top of the atmosphere.")
	@In
	public GridCoverage2D inUpwellingGrid;

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
	
	@Description("The clearness index map")
	@Out
	public GridCoverage2D outNetRadGrid;

	/**
	 * Process.
	 *
	 * @throws Exception the exception
	 */
	@Execute
	public void process() throws Exception { 

		checkNull(inShortwaveGrid);
		
		
		WritableRaster SWRBMap=mapsReader(inShortwaveGrid);
		WritableRaster DWMap=mapsReader(inDownwellingGrid);
		WritableRaster UWMap=mapsReader(inUpwellingGrid);
		
		
		// get the dimension of the maps
		RegionMap regionMap = CoverageUtilities.getRegionParamsFromGridCoverage(inShortwaveGrid);
        int cols = regionMap.getCols();
        int rows = regionMap.getRows();

		// create the output maps with the right dimensions
		WritableRaster outNetWritableRaster= CoverageUtilities.createDoubleWritableRaster(cols, rows, null, null, null);
		WritableRandomIter NetIter = RandomIterFactory.createWritable(outNetWritableRaster, null);
		
		// iterate over the entire domain and compute for each pixel the SWE
		for( int r = 1; r < rows - 1; r++ ) {
            for( int c = 1; c < cols - 1; c++ ) {


            	
			double shortWave=SWRBMap.getSampleDouble(c, r, 0);
			if(shortWave<0) shortWave=0;
			
			double downwelling = DWMap.getSampleDouble(c, r, 0);
			if(downwelling<0) downwelling=0;

			double upwelling=UWMap.getSampleDouble(c, r, 0);
			if(upwelling<0) upwelling=0;

			double netRad=(shortWave<=0)?0:(1-alfa)*(shortWave)+downwelling-upwelling;
			netRad=(netRad<0)?0:netRad;
			
			
			NetIter.setSample(c, r, 0, netRad);
			
            }

		}


		CoverageUtilities.setNovalueBorder(outNetWritableRaster);
		outNetRadGrid = CoverageUtilities.buildCoverage("NETRAD", outNetWritableRaster, 
				regionMap, inShortwaveGrid.getCoordinateReferenceSystem());
		
	}
	
	/**
	 * Maps reader transform the GrifCoverage2D in to the writable raster and
	 * replace the -9999.0 value with no value.
	 *
	 * @param inValues: the input map values
	 * @return the writable raster of the given map
	 */
	private WritableRaster mapsReader ( GridCoverage2D inValues){	
		RenderedImage inValuesRenderedImage = inValues.getRenderedImage();
		WritableRaster inValuesWR = CoverageUtilities.replaceNovalue(inValuesRenderedImage, -9999.0);
		inValuesRenderedImage = null;
		return inValuesWR;
	}


}
