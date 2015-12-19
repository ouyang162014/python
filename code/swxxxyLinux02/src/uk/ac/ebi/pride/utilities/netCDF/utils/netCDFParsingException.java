package uk.ac.ebi.pride.utilities.netCDF.utils;

public class netCDFParsingException extends Exception {

	private static final long serialVersionUID = 1L;

	public netCDFParsingException() {
		
	}

	public netCDFParsingException(String arg0) {
		super(arg0);
	}

	public netCDFParsingException(Throwable arg0) {
		super(arg0);
	}

	public netCDFParsingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
