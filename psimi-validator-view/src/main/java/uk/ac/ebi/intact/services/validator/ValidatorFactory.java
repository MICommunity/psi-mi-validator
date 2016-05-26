package uk.ac.ebi.intact.services.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.validator.extension.MiValidator;
import psidev.psi.tools.cvrReader.mapping.jaxb.CvMapping;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.preferences.UserPreferences;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.intact.services.validator.context.ValidatorWebContent;
import uk.ac.ebi.intact.services.validator.context.ValidatorWebContext;
import uk.ac.ebi.intact.services.validator.context.ValidatorWebContextException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * The factory which returns a new instance of the Validator depending on the ValidationScope and Validator scope
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Jun-2010</pre>
 */

public class ValidatorFactory {

    /**
     * Logging, logging!
     */
    private static final Log log = LogFactory.getLog(ValidatorFactory.class);
    //private static final String psiParOntology = "config/psi_par/ontologies.xml";
    //private static final String psiMiOntology = "config/psi_mi/ontologies.xml";
    private static final String localPsiParOntology = "validator/config/psi_par/ontologies-local.xml";
    private static final String localPsiMiOntology = "validator/config/psi_mi/ontologies-local.xml";
    private static final String psiMiCvMapping = "config/psi_mi/cv-mapping.xml";
    private static final String psiParCvMapping = "config/psi_par/cv-mapping.xml";
    private static final String mimixRules = "config/psi_mi/mimix-rules.xml";
    private static final String imexRules = "config/psi_mi/imex-rules.xml";
    private static final String psimiRules = "config/psi_mi/object-rules.xml";

    /**
     *
     * @param scope
     * @param dataModel
     * @return A new instance of the validator which has reloaded the ontologies and rules from the configuration files
     * @throws ValidatorWebContextException
     */
    public MiValidator getReInitialisedValidator(ValidationScope scope, DataModel dataModel) throws ValidatorWebContextException {
        if (dataModel == null){
            throw new IllegalArgumentException("The dataModel cannot be null.");
        }

        if ( log.isInfoEnabled() ) {
            log.info( "Model: " + dataModel.name() );
            log.info( "Selected validation scope: " + scope );
        }

        if( dataModel.equals( DataModel.PSI_PAR )){
            if( scope == null ) {
                // set default value
                log.warn( "The application didn't get a valid validation scope (null), setting PAR default to CV Mapping." );
                scope = ValidationScope.CV_ONLY;
            }
            return createPsiParValidator(scope);
        }
        else if (dataModel.equals( DataModel.PSI_MI)){
            if (scope == null){
                // set default value
                log.warn( "The application didn't get a valid validation scope (null), setting MI default to MIMIx." );
                scope = ValidationScope.MIMIX;
            }
            return createPsiMiValidator(scope);
        }
        else {
            throw new IllegalStateException( "Unknown data model: " + dataModel );

        }
    }

    /**
     *
     * @param scope
     * @param dataModel
     * @return A new instance of the validator which uses the OntologyManager instance of the ValidatorWebContext
     * @throws ValidatorWebContextException
     */
    public MiValidator getValidator(ValidationScope scope, DataModel dataModel) throws ValidatorWebContextException {
        if (dataModel == null){
            throw new IllegalArgumentException("The dataModel cannot be null.");
        }

        if ( log.isInfoEnabled() ) {
            log.info( "Model: " + dataModel.name() );
            log.info( "Selected validation scope: " + scope );
        }

        if( dataModel.equals( DataModel.PSI_PAR )){
            if( scope == null ) {
                // set default value
                log.warn( "The application didn't get a valid validation scope (null), setting PAR default to CV Mapping." );
                scope = ValidationScope.CV_ONLY;
            }
            return getPsiParValidator(scope);
        }
        else if (dataModel.equals( DataModel.PSI_MI)){
            if (scope == null){
                // set default value
                log.warn( "The application didn't get a valid validation scope (null), setting MI default to MIMIx." );
                scope = ValidationScope.MIMIX;
            }
            return getPsiMiValidator(scope);
        }
        else {
            throw new IllegalStateException( "Unknown data model: " + dataModel );

        }
    }

    /**
     *
     * @param customizedRules
     * @return A new instance of the validator which uses the OntologyManager instance of the ValidatorWebContext
     * @throws ValidatorWebContextException
     */
    public MiValidator getValidator(List<ObjectRule> customizedRules) throws ValidatorWebContextException {

        if ( log.isInfoEnabled() ) {
            log.info( "Model: " + DataModel.PSI_MI.toString());
            log.info( "Selected " + customizedRules.size() + " customized rules." );
        }

        return getCustomizedPsiMiValidator(customizedRules);
    }

    /**
     * Set up the user preferences
     * @param validator
     */
    private void setUpUserPreferences(MiValidator validator){
        // set work directory
        UserPreferences preferences = new UserPreferences();
        preferences.setKeepDownloadedOntologiesOnDisk(true);
        preferences.setWorkDirectory(new File(System.getProperty("java.io.tmpdir")));
        preferences.setSaxValidationEnabled(true);

        validator.setUserPreferences(preferences);
    }

    /**
     * This method will re-load the ontologies and rules from the configuration files
     * @param scope
     * @return a new Validator initialized for a PSI-PAR validation with the specific scope
     * @throws ValidatorWebContextException
     */
    private MiValidator createPsiParValidator(ValidationScope scope) throws ValidatorWebContextException {
        InputStream ontologyCfg = null;
        InputStream cvMappingCfg = null;
        InputStream ruleCfg = null;
        try {
            // We read the configuration file, included inside the jar
            ontologyCfg = ValidatorFactory.class.getClassLoader().getResourceAsStream( localPsiParOntology );
            cvMappingCfg = null;
            ruleCfg = null;

            switch( scope ) {
                case SYNTAX:
                    break;

                case CV_ONLY:
                    cvMappingCfg = MiValidator.class.getClassLoader().getResourceAsStream( psiParCvMapping );

                    if (cvMappingCfg == null){
                        throw new IllegalStateException("The file containing the Cv-mapping rules '" + psiParCvMapping + " has not been loaded and it is needed to" +
                                "validate the file with the scope " + scope.toString());
                    }
                    break;

                default:
                    throw new IllegalStateException( "Unsupported validation scope: '"+ scope +"', the application is not correctly configured." );
            }

            log.info( "Is the Cv mapping file null: " + ( cvMappingCfg == null ) );

            // we instantiate the MI25 validator
            MiValidator validator = new MiValidator(ontologyCfg, cvMappingCfg, ruleCfg);

            setUpUserPreferences(validator);

            return validator;

        } catch (Throwable t) {
            throw new ValidatorWebContextException("An error occured while configuring the PAR validator.", t);
        }
        finally {
            if (ontologyCfg != null){
                try {
                    ontologyCfg.close();
                } catch (IOException e) {
                    log.error("An error occurred while closing the ontology config file", e);
                }
            }
            if (cvMappingCfg != null){
                try {
                    cvMappingCfg.close();
                } catch (IOException e) {
                    log.error( "An error occurred while closing the cv mapping config file", e );
                }
            }
            if (ruleCfg != null){
                try {
                    ruleCfg.close();
                } catch (IOException e) {
                    log.error( "An error occurred while closing the object rule config file", e );
                }
            }
        }
    }

    /**
     * This method will re-use the ontologies and rules from the ValidatorWebContext
     * @param scope
     * @return a new Validator initialized for a PSI-PAR validation with the specific scope
     * @throws ValidatorWebContextException
     */
    private MiValidator getPsiParValidator(ValidationScope scope) throws ValidatorWebContextException {
        ValidatorWebContext context = ValidatorWebContext.getInstance();

        if (context == null){
            throw new ValidatorWebContextException("It is not possible to get the pre-instantiated psi-par environment because" +
                    " the ValidatorWebContext is not initialized yet.");
        }

        try {
            ValidatorWebContent validatorContent = context.getValidatorWebContent();

            // We get the pre-instantiated ontologyManager and object rules
            MiValidator validator = validatorContent.getPsiParValidators().get(scope);
            if (validator == null){
                throw new IllegalStateException( "Unsupported validation scope: '"+ scope +"', the application is not correctly configured." );
            }

            setUpUserPreferences(validator);

            return validator;

        } catch (Throwable t) {

            throw new ValidatorWebContextException("An error occured while configuring the PAR validator.", t);
        }
    }

    /**
     * This method will re-use the ontologies and rules from the ValidatorWebContext
     * @param scope
     * @return a new Validator initialized for a PSI-MI validation with the specific scope
     * @throws ValidatorWebContextException
     */
    private MiValidator getPsiMiValidator(ValidationScope scope) throws ValidatorWebContextException {
        ValidatorWebContext context = ValidatorWebContext.getInstance();

        if (context == null){
            throw new ValidatorWebContextException("It is not possible to get the pre-instantiated psi-mi environment because" +
                    " the ValidatorWebContext is not initialized yet.");
        }

        try {
            ValidatorWebContent validatorContent = context.getValidatorWebContent();

            // We get the pre-instantiated ontologyManager and object rules
            MiValidator validator = validatorContent.getPsiMiValidators().get(scope);
            if (validator == null){
                throw new IllegalArgumentException( "Unsupported validation scope: '"+ scope +"', the application is not correctly configured." );
            }

            setUpUserPreferences(validator);

            return validator;

        } catch (Throwable t) {

            throw new ValidatorWebContextException("An error occured while configuring the MI validator.", t);
        }
    }

    private MiValidator getCustomizedPsiMiValidator(List<ObjectRule> customizedRules) throws ValidatorWebContextException {
        ValidatorWebContext context = ValidatorWebContext.getInstance();

        if (context == null){
            throw new ValidatorWebContextException("It is not possible to get the pre-instantiated psi-mi environment because" +
                    " the ValidatorWebContext is not initialized yet.");
        }

        try {
            ValidatorWebContent validatorContent = context.getValidatorWebContent();

            // We get the pre-instantiated ontologyManager and object rules
            OntologyManager ontologymanager = validatorContent.getPsiMiOntologyManager();
            CvMapping cvMapping = validatorContent.getPsiMiCvMapping();

            // we instantiate the MI25 validator
            MiValidator validator = new MiValidator(ontologymanager, cvMapping, customizedRules);

            setUpUserPreferences(validator);

            return validator;

        } catch (Throwable t) {

            throw new ValidatorWebContextException("An error occured while configuring the MI validator.", t);
        }
    }

    /**
     * This method will re-load the ontologies and rules from the configuration files
     * @param scope
     * @return a new Validator initialized for a PSI-MI validation with the specific scope
     * @throws ValidatorWebContextException
     */
    private MiValidator createPsiMiValidator(ValidationScope scope) throws ValidatorWebContextException {

        InputStream ontologyCfg = null;
        InputStream cvMappingCfg = null;
        InputStream ruleCfg = null;
        try {
            // We read the configuration file, included inside the jar
            ontologyCfg = ValidatorFactory.class.getClassLoader().getResourceAsStream( localPsiMiOntology );

            switch( scope ) {
                case SYNTAX:
                    break;

                case CV_ONLY:
                    cvMappingCfg = MiValidator.class.getClassLoader().getResourceAsStream( psiMiCvMapping );

                    if (cvMappingCfg == null){
                        throw new IllegalStateException("The file containing the Cv-mapping rules '" + psiMiCvMapping + " has not been loaded and it is needed to" +
                                "validate the file with the scope " + scope.toString());
                    }
                    break;
                case PSI_MI:
                    cvMappingCfg = MiValidator.class.getClassLoader().getResourceAsStream( psiMiCvMapping );

                    if (cvMappingCfg == null){
                        throw new IllegalStateException("The file containing the Cv-mapping rules '" + psiMiCvMapping + " has not been loaded and it is needed to" +
                                "validate the file with the scope " + scope.toString());
                    }

                    ruleCfg = MiValidator.class.getClassLoader().getResourceAsStream( psimiRules );

                    if (ruleCfg == null){
                        throw new IllegalStateException("The file containing the PSI-MI rules '" + psimiRules + " has not been loaded and it is needed to" +
                                "validate the file with the scope " + scope.toString());
                    }
                    break;
                case MIMIX:
                    cvMappingCfg = MiValidator.class.getClassLoader().getResourceAsStream( psiMiCvMapping );

                    if (cvMappingCfg == null){
                        throw new IllegalStateException("The file containing the Cv-mapping rules '" + psiMiCvMapping + " has not been loaded and it is needed to" +
                                "validate the file with the scope " + scope.toString());
                    }

                    ruleCfg = MiValidator.class.getClassLoader().getResourceAsStream( mimixRules );

                    if (ruleCfg == null){
                        throw new IllegalStateException("The file containing the mimix rules '" + mimixRules + " has not been loaded and it is needed to" +
                                "validate the file with the scope " + scope.toString());
                    }
                    break;

                case IMEX:
                    cvMappingCfg = MiValidator.class.getClassLoader().getResourceAsStream( psiMiCvMapping );

                    if (cvMappingCfg == null){
                        throw new IllegalStateException("The file containing the Cv-mapping rules '" + psiParCvMapping + " has not been loaded and it is needed to" +
                                "validate the file with the scope " + scope.toString());
                    }

                    ruleCfg = MiValidator.class.getClassLoader().getResourceAsStream( imexRules );

                    if (ruleCfg == null){
                        throw new IllegalStateException("The file containing the imex rules '" + imexRules + " has not been loaded and it is needed to" +
                                "validate the file with the scope " + scope.toString());
                    }

                    break;

                default:
                    throw new IllegalArgumentException( "Unsupported validation scope: '"+ scope +"', the application is not correctly configured." );
            }

            // we instantiate the MI25 validator
            MiValidator validator = new MiValidator(ontologyCfg, cvMappingCfg, ruleCfg);

            setUpUserPreferences(validator);

            return validator;

        }catch (Throwable t) {

            throw new ValidatorWebContextException("An error occured while configuring the MI validator.", t);
        }
        finally {
            if (ontologyCfg != null){
                try {
                    ontologyCfg.close();
                } catch (IOException e) {
                    log.error("An error occurred while closing the ontology config file", e);
                }
            }
            if (cvMappingCfg != null){
                try {
                    cvMappingCfg.close();
                } catch (IOException e) {
                    log.error( "An error occurred while closing the cv mapping config file", e );
                }
            }
            if (ruleCfg != null){
                try {
                    ruleCfg.close();
                } catch (IOException e) {
                    log.error( "An error occurred while closing the object rule config file", e );
                }
            }
        }
    }

    /**
     *
     * @param dataModel
     * @return The default Validation scope for the specific data model
     */
    public ValidationScope getDefaultValidationScope(DataModel dataModel){
        if (dataModel == null){
            throw new IllegalArgumentException("The dataModel cannot be null.");
        }

        if( dataModel.equals( DataModel.PSI_PAR )){
            return ValidationScope.CV_ONLY;
        }
        else if (dataModel.equals( DataModel.PSI_MI)){
            return ValidationScope.MIMIX;
        }
        else {
            throw new IllegalStateException( "Unknown data model: " + dataModel );

        }
    }

    /**
     *
     * @return The ontology configuration file for PSI-PAR
     */
    public static String getPsiParOntology() {
        return localPsiParOntology;
    }

    /**
     *
     * @return The ontology configuration file for PSI-MI
     */
    public static String getPsiMiOntology() {
        return localPsiMiOntology;
    }

    /**
     *
     * @return The Cv mapping configuration file for PSI-MI
     */
    public static String getPsiMiCvMapping() {
        return psiMiCvMapping;
    }

    /**
     *
     * @return The Cv mapping configuration file for PSI-PAR
     */
    public static String getPsiParCvMapping() {
        return psiParCvMapping;
    }

    /**
     *
     * @return the object rules configuration file for MIMIx
     */
    public static String getMimixRules() {
        return mimixRules;
    }

    /**
     *
     * @return the object rules configuration file for IMEx
     */
    public static String getImexRules() {
        return imexRules;
    }
}
