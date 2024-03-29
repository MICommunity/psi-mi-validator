package psidev.psi.mi.validator.extension.rules.psimi;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.tab.utils.MitabUtils;
import psidev.psi.mi.jami.xml.utils.PsiXmlUtils;
import psidev.psi.mi.validator.extension.MiContext;
import psidev.psi.mi.validator.extension.rules.AbstractMIRule;
import psidev.psi.mi.validator.extension.rules.RuleUtils;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccess;
import psidev.psi.tools.ontology_manager.interfaces.OntologyTermI;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Check that a database is well formatted
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/03/13</pre>
 */

public class DatabaseXrefSyntaxRule extends AbstractMIRule<Xref> {

    public DatabaseXrefSyntaxRule(OntologyManager ontologyManager) {
        super(ontologyManager, Xref.class);
        setName( "Database cross reference syntax check" );

        setDescription( "Check that each database cross reference has a non empty database and a non empty database accession. Checks that if MI identifiers are provided for database and qualifiers, they are valid MI identifiers." );
    }

    @Override
    public Collection<ValidatorMessage> check(Xref xref) throws ValidatorException {
        if (xref != null){
            // list of messages to return
            List<ValidatorMessage> messages = Collections.EMPTY_LIST;
            final OntologyAccess access = ontologyManager.getOntologyAccess("MI");

            String id = xref.getId();
            CvTerm database = xref.getDatabase();
            CvTerm qualifier = xref.getQualifier();

            if (id == null ||
                    id.trim().length() == 0 ||
                    PsiXmlUtils.UNSPECIFIED.equals(id) ||
                    MitabUtils.UNKNOWN_ID.equals(id)){
                MiContext xrefContext = RuleUtils.buildContext(xref, "database xref");
                messages = new ArrayList<ValidatorMessage>();
                messages.add( new ValidatorMessage( "Database xrefs must have a valid and non empty id.'",
                        MessageLevel.ERROR,
                        xrefContext,
                        this ) );
            }

            if (database == null ||
                    PsiXmlUtils.UNSPECIFIED.equals(database.getShortName()) ||
                    MitabUtils.UNKNOWN_ID.equals(database.getShortName())){
                MiContext xrefContext = RuleUtils.buildContext(xref, "database xref");

                if (messages.isEmpty()){
                    messages = new ArrayList<ValidatorMessage>();
                }
                messages.add( new ValidatorMessage( "Database xrefs must have a valid and non empty database.'",
                        MessageLevel.ERROR,
                        xrefContext,
                        this ) );
            }
            else if (database != null && database.getMIIdentifier() != null){
                final OntologyTermI dbTerm = access.getTermForAccession(database.getMIIdentifier());

                if (dbTerm == null){
                    MiContext context = RuleUtils.buildContext(xref, "database xref");
                    if (messages.isEmpty()){
                        messages = new ArrayList<ValidatorMessage>();
                    }
                    messages.add( new ValidatorMessage( "The database MI identifier "+database.getMIIdentifier()+"  does not exist in the PSI-MI ontology. The valid MI terms for databases are available here: https://www.ebi.ac.uk/ols4/ontologies/mi/terms?obo_id=MI:0444",
                            MessageLevel.ERROR,
                            context,
                            this ) );
                }
                else {
                    Collection<OntologyTermI> parents = access.getAllParents(dbTerm);

                    boolean foundParent = false;

                    for (OntologyTermI p : parents){
                        if ("MI:0444".equals(p.getTermAccession())){
                            foundParent = true;
                            break;
                        }
                    }

                    if (!foundParent){
                        MiContext context = RuleUtils.buildContext(xref, "database xref");
                        if (messages.isEmpty()){
                            messages = new ArrayList<ValidatorMessage>();
                        }
                        messages.add( new ValidatorMessage( "The MI identifier "+database.getMIIdentifier()+" is not a valid MI identifier for databases. The valid MI terms for databases are available here: https://www.ebi.ac.uk/ols4/ontologies/mi/terms?obo_id=MI:0444",
                                MessageLevel.ERROR,
                                context,
                                this ) );
                    }
                }
            }

            if (qualifier != null && qualifier.getMIIdentifier() != null){
                final OntologyTermI dbTerm = access.getTermForAccession(qualifier.getMIIdentifier());

                if (dbTerm == null){
                    MiContext context = RuleUtils.buildContext(xref, "database xref");
                    if (messages.isEmpty()){
                        messages = new ArrayList<ValidatorMessage>();
                    }
                    messages.add( new ValidatorMessage( "The cross reference type MI identifier "+qualifier.getMIIdentifier()+"  does not exist in the PSI-MI ontology. The valid MI terms for cross reference types are available here: https://www.ebi.ac.uk/ols4/ontologies/mi/terms?obo_id=MI:0353",
                            MessageLevel.ERROR,
                            context,
                            this ) );
                }
                else {
                    Collection<OntologyTermI> parents = access.getAllParents(dbTerm);

                    boolean foundParent = false;

                    for (OntologyTermI p : parents){
                        if ("MI:0353".equals(p.getTermAccession())){
                            foundParent = true;
                            break;
                        }
                    }

                    if (!foundParent){
                        MiContext context = RuleUtils.buildContext(xref, "database xref");
                        if (messages.isEmpty()){
                            messages = new ArrayList<ValidatorMessage>();
                        }
                        messages.add( new ValidatorMessage( "The MI identifier "+qualifier.getMIIdentifier()+" is not a valid MI identifier for cross reference types. The valid MI terms for cross reference types are available here: https://www.ebi.ac.uk/ols4/ontologies/mi/terms?obo_id=MI:0353",
                                MessageLevel.ERROR,
                                context,
                                this ) );
                    }
                }
            }

            return messages;
        }
        return Collections.EMPTY_LIST;
    }

    public String getId() {
        return "R17";
    }
}