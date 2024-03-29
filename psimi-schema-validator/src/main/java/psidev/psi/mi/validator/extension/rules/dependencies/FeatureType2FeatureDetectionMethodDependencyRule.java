package psidev.psi.mi.validator.extension.rules.dependencies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.validator.extension.MiContext;
import psidev.psi.mi.validator.extension.MiValidatorContext;
import psidev.psi.mi.validator.extension.rules.AbstractMIRule;
import psidev.psi.mi.validator.extension.rules.RuleUtils;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccess;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Rule that allows to check whether the feature type specified matches the feature detection method.
 *
 * Rule Id = 13. See http://docs.google.com/Doc?docid=0AXS9Q1JQ2DygZGdzbnZ0Ym5fMHAyNnM3NnRj&hl=en_GB&pli=1
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id: FeatureType2FeatureDetectionMethodDependencyRule.java 56 2010-01-22 15:37:09Z marine.dumousseau@wanadoo.fr $
 * @since 2.0
 */
public class FeatureType2FeatureDetectionMethodDependencyRule extends AbstractMIRule<FeatureEvidence> {

    private static final Log log = LogFactory.getLog( InteractionDetectionMethod2BiologicalRoleDependencyRule.class );

    private DependencyMapping mapping;

    /**
     *
     * @param ontologyManager
     */
    public FeatureType2FeatureDetectionMethodDependencyRule( OntologyManager ontologyManager ) {
        super( ontologyManager, FeatureEvidence.class );
        MiValidatorContext validatorContext = MiValidatorContext.getCurrentInstance();

        OntologyAccess mi = ontologyManager.getOntologyAccess( "MI" );
        String fileName = validatorContext.getValidatorConfig().getFeatureType2FeatureDetectionMethod();

        try {
            URL resource = FeatureType2FeatureDetectionMethodDependencyRule.class
                    .getResource( fileName );

            mapping = new DependencyMapping();

            mapping.buildMappingFromFile( mi, resource );

        } catch (IOException e) {
            throw new ValidatorRuleException("We can't build the map containing the dependencies from the file " + fileName, e);
        } catch (ValidatorException e) {
            throw new ValidatorRuleException("We can't build the map containing the dependencies from the file " + fileName, e);
        }
        // describe the rule.
        setName( "Dependency Check : Participant's feature type and feature detection method" );
        setDescription( "Checks that each association participant's feature type - feature detection method is valid and respects IMEx curation rules.");
        addTip( "Search the possible terms for feature type and feature detection method on https://www.ebi.ac.uk/ols4/ontologies/mi" );
        addTip( "Look at the file https://github.com/MICommunity/PSI-MI-Validator/blob/master/psimi-schema-validator/src/main/resources/featureType2FeatureDetectionMethod.tsv for the possible dependencies feature type - feature detection method" );
    }

    /**
     * For each participants of the interaction, collect all respective feature detection methods and feature types and
     * check if the dependencies are correct.
     *
     * @param feature to check on.
     * @return a collection of validator messages.
     *         if we fail to retreive the MI Ontology.
     */
    public Collection<ValidatorMessage> check( FeatureEvidence feature) throws ValidatorException {

        Collection<ValidatorMessage> messages = Collections.EMPTY_LIST;

        Collection<CvTerm> method = feature.getDetectionMethods();

        if (feature.getType() != null){
            CvTerm featureType = feature.getType();

            if (!method.isEmpty()){
                messages = new ArrayList<ValidatorMessage>(method.size());

                for (CvTerm met: method){
                    MiContext context = RuleUtils.buildContext(feature, "feature");
                    context.addAssociatedContext(RuleUtils.buildContext(featureType, "feature type"));
                    context.addAssociatedContext(RuleUtils.buildContext(met, "feature detection method"));
                    messages.addAll(mapping.check(featureType, met, context, this)) ;
                }
            }
        }

        return messages;
    }

    public String getId() {
        return "R52";
    }
}
