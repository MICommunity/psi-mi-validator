/**
 * Copyright (c) 2006 The European Bioinformatics Institute, and others.
 * All rights reserved.
 */
package uk.ac.ebi.intact.services.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.tools.validator.ValidatorMessage;

import java.util.List;

/**
 * This class is the model of the information reported after the validations
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class PsiReport {

    private static final Log log = LogFactory.getLog(PsiReport.class);

    public static final String VALID = "valid";
    public static final String INVALID = "invalid";
    public static final String WARNINGS = "warnings";
    public static final String NOT_RUN = "not run";

    /**
     * Name of the file/report
     */
    private String name;

    /**
     * The status of the xml syntax validation (valid|invalid)
     */
    private String syntaxStatus;

    /**
     * Report of the validation. If failed, contains the stacktrace
     */
    private List<ValidatorMessage> syntaxReport;

    /**
     * The status of the semantics validation (valid|warnings|invalid)
     */
    private String semanticsStatus;

    /**
     * Report of the validation.
     */
    private String semanticsReport;

    /**
     * Collection of ValidatorMessages after the semantic validation
     */
    private List<ValidatorMessage> validatorMessages;

    /**
     * String containing the HTML View of the PSI file shown to the user, after transforming it
     * using XSLT
     */
    private String htmlView;

    /**
     * Constructor
     *
     * @param name of the file/report
     */
    public PsiReport(String name) {
        this.name = name;
    }

    // ACCESSOR METHODS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSyntaxValid() {
        return VALID.equals(syntaxStatus) || WARNINGS.equals(syntaxStatus);
    }

    public boolean isSyntaxWarning() {
        return WARNINGS.equals(syntaxStatus);
    }

    public boolean isSyntaxInvalid() {
        return INVALID.equals(syntaxStatus);
    }

    public String getSyntaxStatus() {
        return syntaxStatus;
    }

    public void setSyntaxStatus(String syntaxStatus) {
        this.syntaxStatus = syntaxStatus;
    }

    public List<ValidatorMessage> getSyntaxReport() {
        return syntaxReport;
    }

    public void setSyntaxReport(List<ValidatorMessage> syntaxReport) {
        this.syntaxReport = syntaxReport;
    }

    public boolean hasSemanticStatus() {
        return semanticsStatus != null;
    }

    public boolean isSemanticValid() {
        return VALID.equals(semanticsStatus);
    }

    public boolean isSemanticInvalid() {
        return INVALID.equals(semanticsStatus);
    }

    public String getSemanticsStatus() {
        return semanticsStatus;
    }

    public boolean isSemanticReportAvailable() {
        return semanticsStatus != null;
    }

    public void setSemanticsStatus(String semanticsStatus) {
        this.semanticsStatus = semanticsStatus;
    }

    public String getSemanticsReport() {
        return semanticsReport;
    }

    public void setSemanticsReport(String semanticsReport) {
        this.semanticsReport = semanticsReport;
    }

    public String getHtmlView() {
        return htmlView;
    }

    public void setHtmlView(String htmlView) {
        this.htmlView = htmlView;
    }

    public List<ValidatorMessage> getValidatorMessages() {
        return validatorMessages;
    }

    public int getMessageCount() {
        return validatorMessages.size();
    }

    public void setValidatorMessages(List<ValidatorMessage> validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    private int interactionCount;

    public void setInteractionCount( int interactionCount ) {
        this.interactionCount = interactionCount;
    }

    public int getInteractionCount() {
        return interactionCount;
    }

    public int getNumberOfSyntaxMessages(){
        if (this.syntaxReport == null){
            return 0;
        }

        return this.syntaxReport.size();
    }

    public int getNumberOfSemanticMessages(){
        if (this.validatorMessages == null){
            return 0;
        }

        return this.validatorMessages.size();
    }
}
