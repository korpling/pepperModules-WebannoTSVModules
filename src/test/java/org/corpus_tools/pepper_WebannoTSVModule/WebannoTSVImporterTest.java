package org.corpus_tools.pepper_WebannoTSVModule;

import static org.junit.Assert.assertEquals;
import org.corpus_tools.pepper.testFramework.PepperImporterTest;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for testing the  * {@link WebannoTSVImporter} class. 
 * Please note, that the test class is derived from {@link PepperImporterTest}.
 * 
 * @author Amir Zeldes
 */
public class WebannoTSVImporterTest {

    
	private WebannoTSV2SaltMapper fixture = null;

        WebannoTSV2SaltMapper getFixture() {
		return fixture;
	}

	private void setFixture(WebannoTSV2SaltMapper fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() {
		this.setFixture(new WebannoTSV2SaltMapper());
		
		//URI propertiesLoc = URI.createFileURI("./src/test/resources/WebannoTSVImporter.properties");
		//getFixture().getProperties().setPropertyValues(new File(propertiesLoc.toFileString()));
		SDocument sDoc = SaltFactory.createSDocument();
		SaltFactory.createIdentifier(sDoc, "doc1");
		getFixture().setDocument(sDoc);
		getFixture().getDocument().setDocumentGraph(SaltFactory.createSDocumentGraph());

	}
        
        
        @Test
	public void testSentenceAnnotation()
	{
	  getFixture().setResourceURI(URI.createFileURI("src/test/resources/GUM_webanno_test.tsv"));
	  getFixture().mapSDocument();
	  
	  SDocumentGraph dg = getFixture().getDocument().getDocumentGraph();

          // check token count
	  assertEquals(1017, dg.getTokens().size());
          // checks that all nodes are contained
          assertEquals(1299, dg.getNodes().size());
          // checks that all spans (subclass of nodes) are contained
          assertEquals(281, dg.getSpans().size());
          // checks that all structures (subclass of nodes) are contained
          assertEquals(0, dg.getStructures().size());
          // checks that all pointing relations (subclass of relations) are
          // contained
          assertEquals(117, dg.getPointingRelations().size());          
          // checks that all relations are contained
          assertEquals(2003, dg.getRelations().size());
        }

}