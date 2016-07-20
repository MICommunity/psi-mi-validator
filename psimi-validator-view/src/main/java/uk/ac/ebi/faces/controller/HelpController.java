package uk.ac.ebi.faces.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import uk.ac.ebi.intact.services.validator.ValidationScope;
import uk.ac.ebi.intact.services.validator.context.ValidatorWebContent;
import uk.ac.ebi.intact.services.validator.context.ValidatorWebContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The HelpController will list all the Imex rules and MIMIx rules in the user's guide
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Jul-2010</pre>
 */
@Controller( "helpController" )
@Scope( "conversation.access" )
@ViewController( viewIds = {"/help.xhtml"})
public class HelpController extends BaseController {

    private static final Log log = LogFactory.getLog(HelpController.class);

    /**
     *
     * @return the list of ObjectRules executed during an 'IMEx' validation with this order :
     * IMEx rules first, MIMIx rules and then PSI-MI rules
     */
    public List<ObjectRule> getImexRules(){
        ValidatorWebContext validatorContext = ValidatorWebContext.getInstance();

        ValidatorWebContent validatorContent = validatorContext.getValidatorWebContent();

        Collection<ObjectRule> rules = validatorContent.getPsiMiValidators().get(ValidationScope.IMEX).getAllRules();

        List<ObjectRule> imexRules = extractImexRulesFrom(rules);
        imexRules.addAll(extractMimixRulesFrom(rules));
        imexRules.addAll(extractObjectRulesFrom(rules));

        return imexRules;
    }

    /**
     *
     * @return the list of ObjectRules for IMEx
     */
    public List<ObjectRule> getImexRulesOnly(){
        ValidatorWebContext validatorContext = ValidatorWebContext.getInstance();

        ValidatorWebContent validatorContent = validatorContext.getValidatorWebContent();

        Collection<ObjectRule> rules = validatorContent.getPsiMiValidators().get(ValidationScope.IMEX).getAllRules();

        List<ObjectRule> imexRules = extractImexRulesFrom(rules);

        return imexRules;
    }

    /**
     *
     * @return the list of ObjectRules executed during an 'PSI-MI' validation
     */
    public List<ObjectRule> getPsiMiRules(){
        ValidatorWebContext validatorContext = ValidatorWebContext.getInstance();

        ValidatorWebContent validatorContent = validatorContext.getValidatorWebContent();

        List<ObjectRule> rules = new ArrayList(validatorContent.getPsiMiValidators().get(ValidationScope.PSI_MI).getAllRules());

        return rules;
    }

    /**
     *
     * @return the list of ObjectRules for MIMIx
     */
    public List<ObjectRule> getMimixRulesOnly(){
        ValidatorWebContext validatorContext = ValidatorWebContext.getInstance();

        ValidatorWebContent validatorContent = validatorContext.getValidatorWebContent();

        Collection<ObjectRule> rules = validatorContent.getPsiMiValidators().get(ValidationScope.MIMIX).getAllRules();

        List<ObjectRule> mimixRules = extractMimixRulesFrom(rules);

        return mimixRules;
    }

    /**
     *
     * @return the list of ObjectRules executed during a 'MIMIx' validation with this order :
     * MIMIx rules first and then PSI-MI rules
     */
    public List<ObjectRule> getMimixRules(){
        ValidatorWebContext validatorContext = ValidatorWebContext.getInstance();

        ValidatorWebContent validatorContent = validatorContext.getValidatorWebContent();

        Collection<ObjectRule> rules = validatorContent.getPsiMiValidators().get(ValidationScope.MIMIX).getAllRules();

        List<ObjectRule> mimixRules = extractMimixRulesFrom(rules);
        mimixRules.addAll(extractObjectRulesFrom(rules));

        return mimixRules;
    }

    /**
     * Convert the collection of rules into a list
     * @param rules : the collection of rules
     * @return
     */
    public List<CvRule> getCvRules(Collection<CvRule> rules){
        ValidatorWebContext validatorContext = ValidatorWebContext.getInstance();

        ValidatorWebContent validatorContent = validatorContext.getValidatorWebContent();

        List<CvRule> cvRules = new ArrayList<CvRule>();

        for (CvRule rule : rules){
            cvRules.add(rule);
        }
        return cvRules;
    }

    /**
     * Extract a list of rules with the scope 'IMEx' from the set of rules
     * @param rules : the set of rules to sort
     * @return
     */
    private List<ObjectRule> extractImexRulesFrom(Collection<ObjectRule> rules){
        List<ObjectRule> imexRules = new ArrayList<ObjectRule>();

        if (rules != null){
            for (ObjectRule rule : rules){
                if (ValidationScope.IMEX.toString().equalsIgnoreCase(rule.getScope())){
                    imexRules.add(rule);
                }
            }
        }

        return imexRules;
    }

    /**
     * Extract a list of rules with the scope 'MIMIx' from the set of rules
     * @param rules : the set of rules to sort
     * @return
     */
    private List<ObjectRule> extractMimixRulesFrom(Collection<ObjectRule> rules){
        List<ObjectRule> mimixRules = new ArrayList<ObjectRule>();

        if (rules != null){
            for (ObjectRule rule : rules){
                if (ValidationScope.MIMIX.toString().equalsIgnoreCase(rule.getScope())){
                    mimixRules.add(rule);
                }
            }
        }

        return mimixRules;
    }

    /**
     * Extract a list of rules without neither the scope 'IMEx' nor the scope 'MIMIx' from the set of rules
     * @param rules : the set of rules to sort
     * @return
     */
    private List<ObjectRule> extractObjectRulesFrom(Collection<ObjectRule> rules){
        List<ObjectRule> simpleRules = new ArrayList<ObjectRule>();

        if (rules != null){
            for (ObjectRule rule : rules){
                if (!ValidationScope.MIMIX.toString().equalsIgnoreCase(rule.getScope()) && !ValidationScope.IMEX.toString().equalsIgnoreCase(rule.getScope())){
                    simpleRules.add(rule);
                }
            }
        }

        return simpleRules;
    }
}
