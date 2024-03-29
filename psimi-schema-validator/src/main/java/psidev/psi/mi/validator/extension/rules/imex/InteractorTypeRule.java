package psidev.psi.mi.validator.extension.rules.imex;

import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.validator.extension.MiContext;
import psidev.psi.mi.validator.extension.rules.AbstractMIRule;
import psidev.psi.mi.validator.extension.rules.RuleUtils;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This rule will check that each interactor type is not set to 'small molecule' or 'nucleic acid'
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/11</pre>
 */

public class InteractorTypeRule extends AbstractMIRule<Interactor> {

        public InteractorTypeRule(OntologyManager ontologyMaganer) {
        super( ontologyMaganer, Interactor.class );

        // describe the rule.
        setName( "Interactor type check" );

        setDescription( "Interactor's type cannot be set to 'nucleic acid' or 'small molecule' as it is currently outside " +
                "of the remit of IMEx." );

        addTip( "The possible interactor types can be found at https://www.ebi.ac.uk/ols4/ontologies/mi/terms?obo_id=MI:0313" );
    }

    @Override
    public boolean canCheck(Object t) {
        return t instanceof Interactor;
    }

    /**
     * check that each interactor has at least name or a short label.
     *
     * @param interactor to check on.
     * @return a collection of validator messages.
     * @exception psidev.psi.tools.validator.ValidatorException if we fail to retrieve the MI Ontology.
     */
    public Collection<ValidatorMessage> check( Interactor interactor ) throws ValidatorException {

        // list of messages to return
        List<ValidatorMessage> messages = Collections.EMPTY_LIST;

        if (interactor.getInteractorType() == null){
            MiContext context = RuleUtils.buildContext( interactor, "interactor" );

            messages=Collections.singletonList( new ValidatorMessage( "The interactor does not have an interactor type and it is required by IMEx.",
                    MessageLevel.ERROR,
                    context,
                    this ) );
        }
        else {
            if( RuleUtils.isNucleicAcid(ontologyManager, interactor) || RuleUtils.isSmallMolecule(ontologyManager, interactor)) {
                MiContext context = RuleUtils.buildContext( interactor, "interactor" );

                messages=Collections.singletonList( new ValidatorMessage( "'nucleic acids' and 'small molecules' are currently outside of the remit of IMEx and " +
                        "should be removed from the record.",
                        MessageLevel.WARN,
                        context,
                        this ) );
            }
        }

        return messages;
    }

    public String getId() {
        return "R42";
    }
}
