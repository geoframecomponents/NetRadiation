/*
 * GNU GPL v3 License
 *
 * Copyright 2015 Marialaura Bancheri
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package netRadTest;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.geotools.coverage.grid.GridCoverage2D;
import org.jgrasstools.gears.io.rasterreader.OmsRasterReader;
import org.jgrasstools.gears.io.rasterwriter.OmsRasterWriter;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;

import netRadiation.*;
import org.junit.Test;


/**
 * @author Marialaura Bancheri
 */
public class TestNetRadRasterCase {
	
	GridCoverage2D netRadDataGrid = null;

	@Test
	public void componentTest() throws Exception {


		OmsRasterReader upwellingReader = new OmsRasterReader();
		upwellingReader.file = "resources/Input/upwelling.asc";
		upwellingReader.fileNovalue = -9999.0;
		upwellingReader.geodataNovalue = Double.NaN;
		upwellingReader.process();
		GridCoverage2D upwelling = upwellingReader.outRaster;


		OmsRasterReader downReader = new OmsRasterReader();
		downReader.file = "resources/Input/downwelling.asc";
		downReader.fileNovalue = -9999.0;
		downReader.geodataNovalue = Double.NaN;
		downReader.process();
		GridCoverage2D downwelling = downReader.outRaster;


		OmsRasterReader swrbReader = new OmsRasterReader();
		swrbReader.file = "resources/Input/mapTotal.asc";
		swrbReader.fileNovalue = -9999.0;
		swrbReader.geodataNovalue = Double.NaN;
		swrbReader.process();
		GridCoverage2D swrb = downReader.outRaster;



		NetRadiationRasterCase netRad = new NetRadiationRasterCase();
		netRad.alfa=0.26;
		netRad.inShortwaveGrid = swrb;
		netRad.inDownwellingGrid= downwelling;
		netRad.inUpwellingGrid= upwelling;



		netRad.process();
		
		netRadDataGrid  = netRad.outNetRadGrid;



		OmsRasterWriter writerNetraster = new OmsRasterWriter();
		writerNetraster .inRaster = netRadDataGrid;
		writerNetraster .file = "resources/Output/netRad.asc";
		writerNetraster.process();






	}

}
