package uk.ac.ebi.pride.utilities.netCDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.ma2.IndexIterator;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import uk.ac.ebi.pride.utilities.netCDF.core.Metadata;
import uk.ac.ebi.pride.utilities.netCDF.core.MsScan;
import uk.ac.ebi.pride.utilities.netCDF.core.SpectrumType;
import uk.ac.ebi.pride.utilities.netCDF.utils.NetCDFConstants;
import uk.ac.ebi.pride.utilities.netCDF.utils.Tuple;
import uk.ac.ebi.pride.utilities.netCDF.utils.netCDFParsingException;

import java.io.File;

import java.io.IOException;
import java.util.*;


/*
  This class allow to read netCDF files for metabolomics experiments. Its aim is to be able to read
  every file and
 */
public class NetCDFFile {
    /**
     * The netCDFFile source file.
     */
    private File sourceFile;

    private static final Logger logger = LoggerFactory.getLogger(NetCDFFile.class);

    private NetcdfFile inputFile;

    private int totalScans = 0;

    private double massValueScaleFactor = 1;

    private double intensityValueScaleFactor = 1;

    private Variable massValueVariable;

    private Variable intensityValueVariable;

    private Variable scanIndexVariable;

    private Variable scanTimeVariable;

    private SortedMap<Integer, Tuple> indexElement = new TreeMap<Integer, Tuple>();

    public NetCDFFile(File sourceFile) throws netCDFParsingException, IOException {
        this.sourceFile = sourceFile;
        // Open NetCDF-file
        try {
            inputFile = NetcdfFile.open(this.sourceFile.getPath());
        } catch (IOException e) {
            throw new  netCDFParsingException("Some errors creating the netCDF file", e.fillInStackTrace());
        }

        readMsRunAttributes();

        indexScans();
    }

    private void readMsRunAttributes() throws IOException, netCDFParsingException {

        // Find mass_values and intensity_values variables
        massValueVariable = inputFile.findVariable(NetCDFConstants.MASS_VALUES);
        intensityValueVariable = inputFile.findVariable(NetCDFConstants.INTENSITY_VALUES);
        scanIndexVariable = inputFile.findVariable(NetCDFConstants.SCAN_INDEXES);
        scanTimeVariable = inputFile.findVariable(NetCDFConstants.SCAN_ACQUISITION_TIME);

        if (massValueVariable == null || intensityValueVariable == null || scanIndexVariable == null || scanTimeVariable == null) {
            throw new netCDFParsingException("Could not find variable mass_values and probably not spectra file");
        }
        assert(massValueVariable.getRank() == 1);

        Attribute massScaleFacAttr = massValueVariable.findAttribute(NetCDFConstants.SCALE_FACTOR);

        if (massScaleFacAttr != null) massValueScaleFactor = massScaleFacAttr.getNumericValue().doubleValue();

        Attribute intScaleFacAttr = intensityValueVariable.findAttribute(NetCDFConstants.SCALE_FACTOR);
        if (intScaleFacAttr != null) intensityValueScaleFactor = intScaleFacAttr.getNumericValue().doubleValue();


        // Read number of scans

        totalScans = scanIndexVariable.getShape()[0];
        logger.debug("Number of Scans: " + totalScans);
    }

    private void indexScans() throws IOException {
        // Read scan start position. An extra element is required, because
        // element totalScans+1 is used to
        // find the stop position for last scan

        Array scanIndexArray = scanIndexVariable.read();

        IndexIterator scanIndexIterator = scanIndexArray.getIndexIterator();

        while (scanIndexIterator.hasNext()) {
            Tuple<Long, Float> scan = new Tuple<Long, Float>(new Long((Integer)scanIndexIterator.next()), null);
            indexElement.put(indexElement.size(), scan);
        }

        // Calc stop position for the last scan
        // This defines the end index of the last scan
        indexElement.put(totalScans, new Tuple<Long, Float>(massValueVariable.getSize(), null));

        // Start scan RT

        Array scanTimeArray = null;
        scanTimeArray = scanTimeVariable.read();
        IndexIterator scanTimeIterator = scanTimeArray.getIndexIterator();
        int ind = 0;
        while (scanTimeIterator.hasNext()) {
            Float sTime = null;
            if (scanTimeVariable.getDataType().getPrimitiveClassType() == float.class) {
                sTime = (Float) (scanTimeIterator.next());
            }
            if (scanTimeVariable.getDataType().getPrimitiveClassType() == double.class) {
                sTime = ((Double) scanTimeIterator.next()).floatValue();
            }

            Tuple<Long,Float> scan = indexElement.get(ind);
            if(scan != null)
                scan.setValue(sTime);
            else
                scan = new Tuple<Long, Float>(null, sTime);

            indexElement.put(ind, scan);

            ind++;
        }


        // Calculate number of good scans, some of them are wrong

        int numberOfGoodScans = 0;
        for (int i = 0; i < indexElement.size(); i++) {
            if (indexElement.get(i).getKey() != null && (Long) indexElement.get(i).getKey() >= 0) {
                numberOfGoodScans++;
            }
        }

        // Is there need to fix something?
        if (numberOfGoodScans < totalScans) {

            // Fix scan_acquisition_time
            // - calculate average delta time between present scans
            double sumDelta = 0;
            int n = 0;
            for (int i = 0; i < totalScans; i++) {
                // Is this a present scan?
                if ((Long)indexElement.get(i).getKey() >= 0) {
                    // Yes, find next present scan
                    for (int j = i + 1; j < totalScans; j++) {
                        if ((Long)indexElement.get(j).getKey() >= 0) {
                            sumDelta += ((Float) indexElement.get(j).getValue() - (Float) indexElement.get(i).getValue())/ ((double) (j - i));
                            n++;
                            break;
                        }
                    }
                }
            }
            double avgDelta = sumDelta / (double) n;
            // - fill missing scan times using nearest good scan and avgDelta
            for (int i = 0; i < totalScans; i++) {
                // Is this a missing scan?
                if ((Long) indexElement.get(i).getKey() < 0) {
                    // Yes, find nearest present scan
                    int nearestI = Integer.MAX_VALUE;
                    for (int j = 1; 1 < 2; j++) {
                        if ((i + j) < totalScans) {
                            if ((Long) indexElement.get(i + j).getKey() >= 0) {
                                nearestI = i + j;
                                break;
                            }
                        }
                        if ((i - j) >= 0) {
                            if ((Long) indexElement.get(i - j).getKey() >= 0) {
                                nearestI = i + j;
                                break;
                            }
                        }

                        // Out of bounds?
                        if (((i + j) >= totalScans) && ((i - j) < 0)) {
                            break;
                        }
                    }

                    if (nearestI != Integer.MAX_VALUE) {

                        Tuple<Long, Float> scan = indexElement.get(i);
                        scan.setValue((float) ((Float) indexElement.get(nearestI).getValue() + (i - nearestI) * avgDelta));
                        indexElement.put(i, scan);

                    } else {
                        if (i > 0) {
                            Tuple<Long, Float> scan = indexElement.get(i);
                            scan.setValue((Float)indexElement.get(i-1).getValue());
                            indexElement.put(i, scan);
                        } else {
                            Tuple<Long, Float> scan = indexElement.get(i);
                            scan.setValue(new Float(0));
                            indexElement.put(i, scan);
                        }
                    }
                }
            }

            // Fix scanStartPositions by filling gaps with next good value
            for (int i = 0; i < totalScans; i++) {
                if ((Long)indexElement.get(i).getKey() < 0) {
                    for (int j = i + 1; j < (totalScans + 1); j++) {
                        if ((Long)indexElement.get(j).getKey() >= 0) {
                            Tuple<Long, Float> scan = indexElement.get(i);
                            scan.setKey((Long) indexElement.get(j).getKey());
                            indexElement.put(i, scan);
                            break;
                        }
                    }
                }
            }
        }
    }

    public MsScan readNextScan(int scanIndex) throws IOException, netCDFParsingException, InvalidRangeException {

//        // Set a simple MS function, always MS level 1 for netCDF data
//        final MsFunction msFunction = MSDKObjectBuilder.getMsFunction(1);

        // Scan number
        final Integer scanNumber = scanIndex + 1;

        MsScan scan = new MsScan();

        // Extract and store the data points
        Map<Float, Float> dataPoints = extractDataPoints(scanIndex);

        scan.setDataPoints(dataPoints);


        scan.setSpectrumType(SpectrumType.detectType(scan.getDataPoints()));

        // TODO set correct separation type from global netCDF file attributes

        scan.setRetentionTime((Float)indexElement.get(scanIndex).getValue());

        scan.setId(scanIndex);

        scan.setMsLevel(1);

        return scan;

    }

    private  Map<Float, Float> extractDataPoints(int scanIndex) throws IOException, InvalidRangeException {

        // Find the Index of mass and intensity values
        Map<Float, Float> dataPoints = new HashMap<Float, Float>();

        final int scanStartPosition[] = { ((Long) indexElement.get(scanIndex).getKey()).intValue()};
        if(scanIndex == 4975){
            System.out.println("wait");}
        final int scanLength[] = { ((Long) indexElement.get(scanIndex + 1).getKey()).intValue() - ((Long) indexElement.get(scanIndex).getKey()).intValue() };
        final Array massValueArray = massValueVariable.read(scanStartPosition, scanLength);
        final Array intensityValueArray = intensityValueVariable .read(scanStartPosition, scanLength);

        final Index massValuesIndex = massValueArray.getIndex();
        final Index intensityValuesIndex = intensityValueArray.getIndex();

        // Get number of data points
        final int arrayLength = massValueArray.getShape()[0];

        // Load the data points

        for (int i = 0; i < arrayLength; i++) {
            final Index massIndex0 = massValuesIndex.set0(i);
            final Index intensityIndex0 = intensityValuesIndex.set0(i);
            dataPoints.put(new Float(massValueArray.getDouble(massIndex0) * massValueScaleFactor), (float) (intensityValueArray
                    .getDouble(intensityIndex0) * intensityValueScaleFactor));
        }

        return dataPoints;

    }

    public void close() throws IOException {
        inputFile.close();
    }

    public int getNumberScans() {
        return totalScans;
    }

    public Collection<Integer> getScanIdentifiers(){
        List<Integer> scanIds = new ArrayList<Integer>(indexElement.keySet());
        scanIds.remove(scanIds.size()-1);
        return scanIds;
    }

    /**
     * Returns all the metadata related with the experiments as key-value pairs.
     *
     * @return
     */
    public Metadata getMetadata(){

        HashMap<String, String> globalAttr = new HashMap<String, String>();
        for (Attribute attr: inputFile.getGlobalAttributes())
            globalAttr.put(attr.getName(), attr.getStringValue());

        return new Metadata(globalAttr);
    }
}
