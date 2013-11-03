package sharpc2b;

import edu.asu.sharpc2b.transform.BlocklyGenerator;
import edu.asu.sharpc2b.transform.SharpLiterals;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;
import edu.asu.sharpc2b.transform.IriUtil;
import edu.asu.sharpc2b.transform.OwlUtil;
import edu.asu.sharpc2b.transform.SharpOperators;

import java.io.File;

/**
 * Goal
 *
 * @Mojo( name = "define-operators" )
 * @goal define-operators
 * @phase generate-sources
 * @requires DependencyResolution compile
 */
public class DefineOperatorsPlugin
        extends AbstractMojo
{

    //=====================================================================================================
    // Maven parameters
    //=====================================================================================================

    /**
     * Location in the classpath to find Excel file containing Operator definitions.
     *
     * @parameter default-value="./target/generated-sources"
     */
    private File operatorDefinitionFile;

    /**
     * Location in the classpath to find Excel file containing Operator definitions.
     *
     * @parameter default-value="./target/generated-sources"
     */
    private String hedSourceOntologyPath;

    /**
     * File in which to save the output OWL Ontology containing Operator and Expression definitions..
     *
     * @parameter default-value="./target/generated-sources"
     */
    private File outputOntologyFile;

    /**
     * File in which to save the Blockly configuration files
     *
     * @parameter default-value="./target/generated-sources"
     */
    private File outputBlocklyDir;

    /**
     * The IRI to give the saved output Ontology.
     *
     * @parameter default-value="./target/generated-sources"
     */
    private String outputOntologyIriString;

    /**
     * The underlying classes were written to look for resource files (such as OWL files) in the classpath.
     * This is an alternate file system directory under which to search for resource files.
     *
     * @parameter default-value="./target/generated-sources"
     */
    private File alternateResourceDir;

    //=====================================================================================================
    // Maven parameter Getters and Setters
    //=====================================================================================================

    public File getOperatorDefinitionFile ()
    {
        return operatorDefinitionFile;
    }

    public void setOperatorDefinitionFile (final File operatorDefinitionFile)
    {
        this.operatorDefinitionFile = operatorDefinitionFile;
    }

    public File getOutputOntologyFile ()
    {
        return outputOntologyFile;
    }

    public void setOutputOntologyFile (final File outputOntologyFile)
    {
        this.outputOntologyFile = outputOntologyFile;
    }

    public String getOutputOntologyIriString ()
    {
        return outputOntologyIriString;
    }

    public void setOutputOntologyIriString (final String outputOntologyIriString)
    {
        this.outputOntologyIriString = outputOntologyIriString;
    }

    public File getAlternateResourceDir ()
    {
        return alternateResourceDir;
    }

    public File getOutputBlocklyDir() {
        return outputBlocklyDir;
    }

    public void setOutputBlocklyDir( File outputBlocklyDir ) {
        this.outputBlocklyDir = outputBlocklyDir;
    }

    public String getHedSourceOntologyPath() {
        return hedSourceOntologyPath;
    }

    public void setHedSourceOntologyPath( String hedSourceOntologyPath ) {
        this.hedSourceOntologyPath = hedSourceOntologyPath;
    }

    public void execute ()
            throws MojoExecutionException, MojoFailureException
    {
        final SharpOperators converter;
        final SharpLiterals literalProcessor;

        final OWLOntologyManager oom;
        final PrefixOWLOntologyFormat oFormat;

//        OWLOntology ontT;
        final OWLOntology operatorOntology;
        OWLOntology hedOntology = null;

        System.out.println( "getOperatorDefinitionFile() = '" + getOperatorDefinitionFile() + "'" );
        System.out.println( "getOutputOntologyFile() = '" + getOutputOntologyFile() + "'" );
        System.out.println( "getOutputOntologyIriString() = '" + getOutputOntologyIriString() + "'" );
        System.out.println(
                "getOperatorDefinitionFile().exists() = '" + getOperatorDefinitionFile().exists() + "'" );
        System.out.println( "getAlternateResourceDir() = '" + getAlternateResourceDir() + "'" );

        oom = OwlUtil.createSharpOWLOntologyManager();

        OWLOntologyDocumentSource s0 = new StreamDocumentSource( SharpOperators.class.getResourceAsStream( "/ontologies/editor_models/skos-core.owl" ) );
        OWLOntologyDocumentSource s1 = new StreamDocumentSource( SharpOperators.class.getResourceAsStream( "/ontologies/editor_models/skos-ext.owl" ) );
        OWLOntologyDocumentSource s2 = new StreamDocumentSource( SharpOperators.class.getResourceAsStream( "/ontologies/editor_models/expr-core.owl" ) );
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        config.setMissingOntologyHeaderStrategy(
                OWLOntologyLoaderConfiguration.MissingOntologyHeaderStrategy.IMPORT_GRAPH );

        OWLOntologyDocumentSource hed = new FileDocumentSource( new File( getHedSourceOntologyPath() ) );
        try {
            hedOntology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument( hed );
        } catch ( OWLOntologyCreationException e ) {
            e.printStackTrace();
        }


        PrefixManager pm;

        OWLOntology ontology;
        try {
            oom.loadOntologyFromOntologyDocument( s0, config );
            oom.loadOntologyFromOntologyDocument( s1, config );
            oom.loadOntologyFromOntologyDocument( s2, config );
            operatorOntology = oom.createOntology( IRI.create( getOutputOntologyIriString() ) );
        } catch ( OWLOntologyCreationException oce ) {
            oce.printStackTrace();
            return;
        }


        oFormat = IriUtil.getDefaultSharpOntologyFormat();
        oFormat.setPrefix( "a:", operatorOntology.getOntologyID().getOntologyIRI().toString() + "#" );


        converter = new SharpOperators();
        converter.addSharpOperators( operatorDefinitionFile, operatorOntology );

        literalProcessor = new SharpLiterals();
        literalProcessor.addSharpLiteralExpressions( hedOntology, operatorOntology );

        try
        {
            oom.saveOntology( operatorOntology, oFormat, IRI.create( getOutputOntologyFile() ) );
        }
        catch (OWLOntologyStorageException e)
        {
            e.printStackTrace();
            throw new MojoFailureException(
                    "Failed to save output ontology to File = " + getOutputOntologyFile(), e );
        }



        BlocklyGenerator blockly = new BlocklyGenerator();

        blockly.processOperators( outputBlocklyDir, operatorOntology );
    }






    public static void main( String... args ) throws MojoFailureException, MojoExecutionException {
        DefineOperatorsPlugin def = new DefineOperatorsPlugin();

        def.setHedSourceOntologyPath( "/home/davide/Projects/Git/HeD-editor/sharp-editor/editor-models/generated-models/src/main/resources/knowledgedocument.xsd.ttl" );
        def.setOperatorDefinitionFile( new File( "/home/davide/Projects/Git/HeD-editor/sharp-editor/editor-models/generated-models/src/main/resources/SharpOperators.xlsx" ) );
        def.setOutputOntologyIriString( "http://asu.edu/sharpc2b/ops-set" );
        def.setOutputBlocklyDir( new File( "/home/davide/Projects/Git/HeD-editor/sharp-editor/editor-models/generated-models/target/generated-sources/blockly" ) );
        def.setOutputOntologyFile( new File( "/home/davide/Projects/Git/HeD-editor/sharp-editor/editor-models/generated-models/target/generated-sources/ontologies/editor_models/sharp_operators.ofn" ) );

        def.execute();
    }

}
