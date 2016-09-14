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

import org.geotools.data.simple.SimpleFeatureCollection;
import org.jgrasstools.gears.io.shapefile.OmsShapefileFeatureReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.hortonmachine.utils.HMTestCase;

import netRadiation.*;



/**
 * Test the {@link ClearnessIndexPointCase} module.
 * 
 * @author Marialaura Bancheri
 */
public class TestNetRadPointCase extends HMTestCase {

	
	public TestNetRadPointCase() throws Exception {


		String startDate = "2004-06-14 00:00" ;
		String endDate = "2005-01-01 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		String inPathToSWRB ="resources/Input/DIRETTA.csv";
		String inPathToDownwelling ="resources/Input/downwelling.csv";
		String inPathToUpwelling ="resources/Input/upwelling.csv";
		String pathToNet= "resources/Output/NetRad.csv";


		OmsTimeSeriesIteratorReader DirectSWRBreader = getTimeseriesReader(inPathToSWRB, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader DownReader = getTimeseriesReader(inPathToDownwelling, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader UpReader = getTimeseriesReader(inPathToUpwelling, fId, startDate, endDate, timeStepMinutes);




		OmsTimeSeriesIteratorWriter writerNetRad = new OmsTimeSeriesIteratorWriter();


		
		writerNetRad.file = pathToNet;
		writerNetRad.tStart = startDate;
		writerNetRad.tTimestep = timeStepMinutes;
		writerNetRad.fileNovalue="-9999";

		 

		NetRadiation netRad = new NetRadiation();
		netRad.alfa=0;

		while( DirectSWRBreader.doProcess  ) { 

	
			DirectSWRBreader.nextRecord();	
			HashMap<Integer, double[]> id2ValueMap = DirectSWRBreader.outData;
			netRad.inShortwaveValues= id2ValueMap;

			

			DownReader.nextRecord();
			id2ValueMap = DownReader.outData;
			netRad.inDownwellingValues = id2ValueMap;
			
			UpReader.nextRecord();
			id2ValueMap = UpReader.outData;
			netRad.inUpwellingValues = id2ValueMap;
			

			netRad.pm = pm;

			netRad.process();
			
			
			 HashMap<Integer, double[]> outHM = netRad.outHMnetRad;
	            
				writerNetRad.inData = outHM;
				writerNetRad.writeNextLine();
				
				
				
				if (pathToNet != null) {
					writerNetRad.close();
				}

	        
			
		}
		

		DirectSWRBreader.close();
		DownReader.close();
		UpReader.close();


	}

	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = startDate;
		reader.tTimestep = timeStepMinutes;
		reader.tEnd = endDate;
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}

}
