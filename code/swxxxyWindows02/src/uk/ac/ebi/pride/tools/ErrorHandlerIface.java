package uk.ac.ebi.pride.tools;

import org.xml.sax.ErrorHandler;

import java.util.List;
import java.io.IOException;

/**
 * An extension of the org.xml.sax.ErrorHandler that can keep track of
 * the generated error messages produced during the validation process.
 *
 * @author Florian Reisinger
 *         Date: 30-Apr-2009
 * @since 1.0
 *
 * @see org.xml.sax.ErrorHandler
 */
public interface ErrorHandlerIface extends ErrorHandler {

    /**
     * Convenience method to retrieve all accumulated error message.
     *
     * @return boolean to indicate if there have been errors recorded.
     */
    public boolean noErrors();

    /**
     * Returns a List of the error messages accumulated during
     * the validation process.
     * Note: while the returned List can take any kind of Object,
     * it is recommened that it will return something that can be
     * printed (e.g. the its toString() method should return
     * something sensible).
     * The default implementation (ValidationErrorHandler) uses
     * a List of String objects.
     *
     * @return a List of the recored error messages.
     */
    public List getErrorMessages();

    /**
     * Extension of the ErrorHandler method to be able to also
     * record IOExceptions accuring during the validation process.
     *
     * @param exception IOException to record a message for.
     */
    public void fatalError(IOException exception);

}
