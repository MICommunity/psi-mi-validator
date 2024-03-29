package psidev.psi.mi.validator.extension.rules.psimi;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.validator.extension.MiContext;
import psidev.psi.mi.validator.extension.rules.AbstractMIRule;
import psidev.psi.mi.validator.extension.rules.RuleUtils;
import psidev.psi.mi.validator.extension.rules.dependencies.ValidatorRuleException;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccess;
import psidev.psi.tools.ontology_manager.interfaces.OntologyTermI;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.utilities.ols.web.service.client.OLSClient;
import uk.ac.ebi.pride.utilities.ols.web.service.config.OLSWsConfigProd;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This rule is checking that database cross references are valid
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Aug-2010</pre>
 */

public class DatabaseAccessionRule extends AbstractMIRule<Xref> {

    public static final Log log = LogFactory.getLog( DatabaseAccessionRule.class );


    public static void closeGeneralCacheAdministrator(){
        admin.remove();
    }

    public String getId() {
        return "R12";
    }

    /**
     * The cache configuration file
     */
    private static final String cacheConfig = "olsontology-oscache.properties";

    /**
     * The cache for OLS
     */
    //protected GeneralCacheAdministrator admin;

    private static ThreadLocal<GeneralCacheAdministrator> admin = new
            ThreadLocal<GeneralCacheAdministrator>() {
                @Override
                protected GeneralCacheAdministrator initialValue() {
                    // setting up the cache for OLS
                    Properties cacheProps;
                    InputStream is = this.getClass().getClassLoader().getResourceAsStream( cacheConfig );
                    cacheProps = new Properties();
                    try {
                        cacheProps.load( is );
                    } catch ( IOException e ) {
                        log.error( "Failed to load cache configuration properties for database cross reference checking: " + cacheConfig, e );
                    }
                    finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if ( cacheProps.isEmpty() ) {
                        log.warn( "Using default cache configuration for database cross reference checking!" );
                        return new GeneralCacheAdministrator();
                    } else {
                        log.info( "Using custom cache configuration from file: " + cacheConfig );
                        return new GeneralCacheAdministrator( cacheProps );
                    }
                }
            };


    /**
     * The OLS olsClient interface
     */
    private OLSClient olsClient;

    /**
     * MI ontology name
     */
    private final String ontologyId = "MI";

    /**
     * The comment before the regular expression of each database in OLS
     */
    private final String regularExpressionComment = "id-validation-regexp:&quot;";

    /**
     * The OLS regular expression escape
     */
    private final String regularExpressionEscape = "&quot;";

    /**
     * Create a new DatabaseAccessionRule
     * @param ontologyMaganer
     */
    public DatabaseAccessionRule(OntologyManager ontologyMaganer) {
        super( ontologyMaganer, Xref.class );

        // describe the rule.
        setName( "Database cross reference Check" );
        setDescription( "Checks that the each database cross reference is using a valid database accession which matches the regular expression of the database." );
        addTip( "You can find all the regular expressions matching each database at https://www.ebi.ac.uk/ols4/ontologies/mi/terms?iri=http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FMI_0000" );

        // setting up OLS client
        try {
            olsClient = new OLSClient(new OLSWsConfigProd());
        } catch ( Exception e ) {
            log.error( "Exception setting up OLS olsClient client! The database cross reference check will not be effective.", e );
        }
    }

    /**
     *
     * @param termAccession
     * @return the cross references of this term in OLS.
     */
    private Pattern getTermXRefUncached(String termAccession){
        if (termAccession == null) { return null; }
        final Map metadata;
        Identifier identifier = new Identifier(termAccession, Identifier.IdentifierType.OBO);
        metadata = olsClient.getTermXrefs( identifier, ontologyId );
        if (metadata != null){
            for ( Object k : metadata.keySet() ) {
                final String key = (String) k;
                // That's the only way OLS provides cross references, all keys are different so we are fishing out keywords :(
                if( key != null && key.contains( "id-validation-regexp" )) {
                    String regularExpression = (String) metadata.get( k );
                    if( regularExpression != null ) {
                        return Pattern.compile(regularExpression);
                    }
                }
            }
        }

        return null;
    }

    /**
     * The method is synchronized to avoid concurrent access/modification when accessing the cache
     * @param myKey : the key to find in the cache
     * @return the object associated with this key in the cache.
     * @throws com.opensymphony.oscache.base.NeedsRefreshException : the key doesn't exist in the cache or is outdated
     */
    private Object getFromCache( String myKey ) throws NeedsRefreshException {
        return DatabaseAccessionRule.admin.get().getFromCache( myKey );
    }

    /**
     * The method will put the key associated with an object in the cache of this OlsOntology
     * The method is synchronized to avoid concurrent access/modification when accessing the cache
     * @param myKey : the key to put in the cache
     * @param result : the associated object to put in the cache
     */
    private void putInCache( String myKey, Object result ) {
        DatabaseAccessionRule.admin.get().putInCache(myKey, result);
    }

    /**
     * Cancel any update for this key in the cache
     * The method is synchronized to avoid concurrent access/modification when accessing the cache
     * @param myKey : the key to find in the cache
     */
    private void cancelUpdate(String myKey){
        DatabaseAccessionRule.admin.get().cancelUpdate( myKey );
    }

    /**
     *
     * @param key
     * @param termAccession
     * @return
     */
    private Pattern getTermXRefs( String key, String termAccession) {
        if (key == null || termAccession == null) { return null; }

        Pattern metadata=null;

        try {
            metadata = (Pattern) getFromCache(key);
            if ( log.isDebugEnabled() ) log.debug( "Using cached terms for key: " + key );
        } catch (NeedsRefreshException e) {
            boolean updated = false;
            // if not found in cache, use uncached method and store result in cache
            try {
                metadata = this.getTermXRefUncached( termAccession );
                if ( log.isDebugEnabled() ) log.debug( "Storing uncached terms for key: " + key );
                putInCache( key, metadata );
                updated = true;
            } finally {
                if ( !updated ) {
                    // It is essential that cancelUpdate is called if the cached content could not be rebuilt
                    cancelUpdate( key );
                }
            }
        }

        return metadata;
    }

    /**
     *
     * @param term
     * @return The Pattern for the database represented by this term
     */
    private Pattern getDatabaseRegularExpression( OntologyTermI term ){
        if (term == null) { return null;}

        // create a unique string for this olsClient
        // generate from from method specific ID, the ontology ID and the input parameter
        final String myKey = ontologyId + '_' + term.getTermAccession();

        return getTermXRefs(myKey, term.getTermAccession());
    }

    /**
     *
     * @param annotation
     * @return the regular expression of the database contained in the annotation (format = id-validation-regexp:&quot;regular_expression&quot;).
     * Null if the annotation String is not in this specific format
     */
    private String extractRegularExpressionFromOLS( String annotation){

        if (annotation != null){
            if (annotation.contains(regularExpressionComment)){
                int indexOfRegularExpression = Math.max(0, annotation.indexOf(regularExpressionComment)) + regularExpressionComment.length();

                if (indexOfRegularExpression < annotation.length()){
                    String regularExpression = annotation.substring(indexOfRegularExpression);

                    regularExpression = regularExpression.replaceAll(regularExpressionEscape, "");

                    return regularExpression;
                }
            }
        }

        return null;
    }

    /**
     * Checks that the database accession is matching the regular expression required by its database
     * @param access : the MI ontology access
     * @param databaseAc : the database Ac
     * @param accession : the database accession
     * @param messages : the validator messages
     */
    private void checkDatabaseCrossReference(OntologyAccess access, String databaseAc, String accession, List<ValidatorMessage> messages, Xref xRef){
        OntologyTermI databaseTerm = access.getTermForAccession(databaseAc);

        // a database cross reference must have a non null identifier
        if (databaseTerm != null) {
            Pattern databasePattern = getDatabaseRegularExpression(databaseTerm);

            // the regular expression of the database is not known (not in OLS)
            if (databasePattern != null){
                // All database accessions must match the regular expression of its database
                if (!databasePattern.matcher(accession).matches()){
                    MiContext context = RuleUtils.buildContext(xRef, "database xref");
                    messages.add( new ValidatorMessage( "The database accession does not match the regular expression of this database (" + databaseTerm.getPreferredName() + "): " + databasePattern.pattern(),
                            MessageLevel.WARN,
                            context,
                            this ) );
                }
            }
            // If the regular expression is not known, do nothing
        }
    }

    @Override
    public Collection<ValidatorMessage> check(Xref xref) throws ValidatorException {

        // list of messages to return
        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        checkCrossReference(xref, messages);

        return messages;
    }

    /**
     * Checks that the cross references in the object XRef are valid
     * @param xRef
     * @param messages
     */
    private void  checkCrossReference(Xref xRef, List<ValidatorMessage> messages){
        // if the XRef object is not null
        if (xRef != null){

            OntologyAccess access = this.ontologyManager.getOntologyAccess("MI");

            // if the ontology access for MI is not null
            if (access != null){
                if (xRef.getDatabase() != null){

                    // MI identifier of the database
                    String dbAc = xRef.getDatabase().getMIIdentifier();
                    // accession
                    String accession = xRef.getId();

                    if (dbAc != null){
                        checkDatabaseCrossReference(access, dbAc, accession, messages, xRef);
                    }
                }
            }
            else {
                throw new ValidatorRuleException("The rule which checks if a database accession is valid cannot be applied because there is no ontology access to the PSI-MI ontology");
            }
        }

    }
}
