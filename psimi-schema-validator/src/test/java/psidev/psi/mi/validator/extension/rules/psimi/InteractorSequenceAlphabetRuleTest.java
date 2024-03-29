/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package psidev.psi.mi.validator.extension.rules.psimi;

import junit.framework.Assert;
import org.junit.Test;
import psidev.psi.mi.jami.model.Protein;
import psidev.psi.mi.validator.extension.rules.AbstractRuleTest;
import psidev.psi.mi.validator.extension.rules.RuleUtils;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.ValidatorMessage;

import java.util.Collection;

/**
 * InteractorSequenceAlphabetRule Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class InteractorSequenceAlphabetRuleTest extends AbstractRuleTest {

    public InteractorSequenceAlphabetRuleTest() throws OntologyLoaderException {
        super( InteractorSequenceAlphabetRuleTest.class.getResourceAsStream( "/config/ontologies.xml" ) );
    }

    @Test
    public void check_protein_error() throws Exception {
        final Protein interactor = buildProtein( "P12345" );
        updateInteractorType( interactor, RuleUtils.PROTEIN_MI_REF );
        interactor.setSequence( "ZZZ" );
        InteractorSequenceAlphabetRule rule = new InteractorSequenceAlphabetRule( ontologyMaganer );
        final Collection<ValidatorMessage> messages = rule.check( interactor );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
    }

    @Test
    public void check_protein_ok() throws Exception {
        final Protein interactor = buildProtein( "P12345" );
        updateInteractorType( interactor, RuleUtils.PROTEIN_MI_REF );
        interactor.setSequence( "QDCEGHI" );
        InteractorSequenceAlphabetRule rule = new InteractorSequenceAlphabetRule( ontologyMaganer );
        final Collection<ValidatorMessage> messages = rule.check( interactor );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 0, messages.size() );
    }

    @Test
    public void check_dna_error() throws Exception {
        final Protein interactor = buildProtein( "P12345" );
        updateInteractorType( interactor, RuleUtils.DNA_MI_REF );
        interactor.setSequence( "TGCXabc" );
        InteractorSequenceAlphabetRule rule = new InteractorSequenceAlphabetRule( ontologyMaganer );
        final Collection<ValidatorMessage> messages = rule.check( interactor );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        printMessages( messages );
    }

    @Test
    public void check_dna_ok() throws Exception {
        final Protein interactor = buildProtein( "P12345" );
        updateInteractorType( interactor, RuleUtils.DNA_MI_REF );
        interactor.setSequence( "AAAAAAAAAATTCGCGCGCG" );
        InteractorSequenceAlphabetRule rule = new InteractorSequenceAlphabetRule( ontologyMaganer );
        final Collection<ValidatorMessage> messages = rule.check( interactor );
        Assert.assertNotNull( messages );
        printMessages( messages );
        Assert.assertEquals( 0, messages.size() );
    }

    @Test
    public void check_smallmolecule_ok() throws Exception {
        final Protein interactor = buildProtein( "P12345" );
        updateInteractorType( interactor, RuleUtils.SMALL_MOLECULE_MI_REF );
        interactor.setSequence( null );
        InteractorSequenceAlphabetRule rule = new InteractorSequenceAlphabetRule( ontologyMaganer );
        final Collection<ValidatorMessage> messages = rule.check( interactor );
        Assert.assertNotNull( messages );
        printMessages( messages );
        Assert.assertEquals( 0, messages.size() );
    }

    @Test
    public void check_polysaccharide_ok() throws Exception {
        // polysaccharide is a polymer but here wasn't given a sequence, we expect one INFO message. (not anymore as we have a uniprot identity!)
        // TODO find out why using the OBO flat file loader fails to load MI:0904 !!
        final Protein interactor = buildProtein( "P12345" );
        updateInteractorType( interactor, RuleUtils.POLYSACCHARIDE_MI_REF );
        interactor.setSequence( null );
        InteractorSequenceAlphabetRule rule = new InteractorSequenceAlphabetRule( ontologyMaganer );
        final Collection<ValidatorMessage> messages = rule.check( interactor );
        Assert.assertNotNull( messages );
        printMessages( messages );
        Assert.assertEquals( 0, messages.size() );
    }
}