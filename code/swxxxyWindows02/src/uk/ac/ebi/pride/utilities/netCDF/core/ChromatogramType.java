package uk.ac.ebi.pride.utilities.netCDF.core;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 25/09/15
 */
public enum ChromatogramType {
        /**
         * Total ion current (TIC) chromatogram. This is the summed intensity of all
         * ions in each spectrum.
         */
        TIC,

        /**
         * Base peak chromatogram (BPC). This is the intensity of the most intense
         * ion in each spectrum.
         */
        BPC,

        /**
         * Extracted-ion chromatogram (XIC). Also referred to as EIC or
         * reconstructed-ion chromatogram (RIC). This is the intensity of one or
         * more m/z values in each spectrum.
         */
        XIC,

        /**
         * Single ion current (SIC) chromatogram. Only a narrow range of m/z
         * values are detected in the analysis and a chromatogram similar to the XIC
         * is generated.
         */
        SIC,

        /**
         * Multiple Reaction Monitoring (MRM) or Selected Reaction Monitoring (SRM).
         * A specific product ion of a specific parent ion is detected.
         */
        MRM_SRM,

        /**
         * Unknown chromatogram type.
         */
        UNKNOWN;
}
