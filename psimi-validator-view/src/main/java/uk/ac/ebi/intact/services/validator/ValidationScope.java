package uk.ac.ebi.intact.services.validator;

/**
 * Validation scope for PSI-MI.
 * <p/>
 * Given that 'A > B' where A is broader than B, so that the right operand is included in the left one.
 * <b/>
 * We have IMEx > MIMIx > CV > Syntax.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public enum ValidationScope {

    SYNTAX, CV_ONLY, MIMIX, IMEX, CUSTOMIZED, PSI_MI
    /**
     * Only run XML syntax via a SAX parser.
     */
    //SYNTAX( "syntax" ),

    /**
     * Only run the Cv mapping (incl, XML syntax).
     */
    //CV_ONLY( "cv-only" ),

    /**
     * Run the MIMIx rules (incl. XML syntax and Cv Mapping).
     */
    //MIMIX( "mimix" ),

    /**
     * Run the IMEx rules (incl. MIMIx rule, XML syntax and Cv Mapping).
     */
    //IMEX( "imex" );

    /*private String name;

    ValidationScope( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ValidationScope forName( String scope ) {
        if ( SYNTAX.name.equalsIgnoreCase( scope ) ) {
            return SYNTAX;
        } else if ( CV_ONLY.name.equalsIgnoreCase( scope ) ) {
            return CV_ONLY;
        } else if ( MIMIX.name.equalsIgnoreCase( scope ) ) {
            return MIMIX;
        } else if ( IMEX.name.equalsIgnoreCase( scope ) ) {
            return IMEX;
        } else {
            throw new IllegalArgumentException( "That validation scope (" + scope + ") didn't match any of the supported ones." );
        }
    }

    public boolean isBroader( ValidationScope scope ) {
        return ( this.compareTo( scope ) > 0 );
    }

    public boolean isSame( ValidationScope scope ) {
        return ( this.compareTo( scope ) == 0 );
    }

    public boolean isNarrower( ValidationScope scope ) {
        return ( this.compareTo( scope ) < 0 );
    }

    public boolean involveSemanticValidation() {
        return isBroader( SYNTAX );
    } */
}
