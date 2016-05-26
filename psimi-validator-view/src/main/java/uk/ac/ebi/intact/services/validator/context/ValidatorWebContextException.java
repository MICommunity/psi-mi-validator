package uk.ac.ebi.intact.services.validator.context;

/**
 * The exception thrown when there is a problem related with the ValidatorWebContent or ValidatorWebContext
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-Jul-2010</pre>
 */

public class ValidatorWebContextException extends Exception{
    public ValidatorWebContextException() {
        super();
    }

    public ValidatorWebContextException(String message) {
        super(message);
    }

    public ValidatorWebContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidatorWebContextException(Throwable cause) {
        super(cause);
    }
}
