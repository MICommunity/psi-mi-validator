package psidev.psi.mi.validator.extension.rules.mimix;

import org.junit.Assert;
import org.junit.Test;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.validator.extension.rules.AbstractRuleTest;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.ValidatorMessage;

import java.util.Collection;

/**
 * ExperimentHostOrgRule Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 2.0.0
 * @version $Id$
 */
public class HostOrganismRuleTest extends AbstractRuleTest {

    public HostOrganismRuleTest() throws OntologyLoaderException {
        super( HostOrganismRuleTest.class.getResourceAsStream( "/config/ontologies.xml" ) );
    }

    @Test
    public void check_valid_taxid() throws Exception {
        final OrganismRule rule = new OrganismRule( ontologyMaganer );

        final Organism organism = buildOrganism( 9606 );
        final Collection<ValidatorMessage> messages = rule.check( organism );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 0, messages.size() );
    }

    @Test
    public void check_invalid_positive_taxid() throws Exception {
        final OrganismRule rule = new OrganismRule( ontologyMaganer );

        final Organism organism = buildOrganism( Integer.MAX_VALUE );
        final Collection<ValidatorMessage> messages = rule.check( organism );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
    }

    @Test
    public void check_invalid_taxid_one() throws Exception {
        final OrganismRule rule = new OrganismRule( ontologyMaganer );

        final Organism organism = buildOrganism( 1 );
        final Collection<ValidatorMessage> messages = rule.check( organism );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
    }
}
