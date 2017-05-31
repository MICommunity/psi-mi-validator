package psidev.psi.mi.validator.extension;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

/**
 * Created by anjali on 31/05/17.
 */
public class MISchemaW3Validator {

    /*
    * This test for checking if xmls under a directory is valid against a schema xsd
    *
    * provide your own directory or file(uncomment) and xsd for checking
    * */
    @Test
    @Ignore
    public void validateXmlFilesAgainstXsd(){
        Document document;
        int index = 1;

        String currentFile=null;
        try {
            Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
          //  v.setSchemaSources(org.xmlunit.builder.Input.fromFile("/home/anjali/Documents/delete/300xsd.xsd").build());
            v.setSchemaSources(org.xmlunit.builder.Input.fromURI("https://raw.githubusercontent.com/HUPO-PSI/miXML/master/3.0/src/MIF300.xsd").build());
            //  File file = new File("/home/anjali/Documents/delete/psi30_large_files/unassigned1304.xml");

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder parser = builderFactory.newDocumentBuilder();

            File directory = new File("/home/xyz/Documents/delete/pmidMIF30");
            File[] fList = directory.listFiles();

            for (File file : fList) {


                currentFile=file.getPath();
                // File file = new File("/home/anjali/Documents/delete/10353244.xml");
                document = parser.parse(file);

                ValidationResult result = v.validateInstance(org.xmlunit.builder.Input.fromDocument(document).build());
                // boolean valid = result.isValid();
                Iterable<ValidationProblem> problems = result.getProblems();

                index++;

                if(!result.isValid()){
                    System.out.println("at ----> File Name: " + file.getPath());
                }

                for (ValidationProblem validationProblem : problems) {
                    /*if (!validationProblem.getMessage().contains("is not a valid value for 'date'") && !validationProblem.getMessage().contains("of attribute 'releaseDate' on element 'source' is not valid with respect to its type")
                            && !validationProblem.getMessage().contains("Invalid content was found starting with element 'organism'") && !validationProblem.getMessage().contains("Invalid content was found starting with element 'bindingFeaturesList'")) {
                        System.out.println("File Name: " + file.getPath() + " \n Error: " + validationProblem.getMessage());
                    }*/
                    System.out.println("  Error: " + validationProblem.getMessage());
                    validationProblem=null;
                }



                result=null;
                problems=null;
                document=null;

            }
        }catch(Throwable exp){
            System.out.println("Files Processed"+index+" currentFile:"+currentFile);
            exp.printStackTrace();
        }
    }
}
