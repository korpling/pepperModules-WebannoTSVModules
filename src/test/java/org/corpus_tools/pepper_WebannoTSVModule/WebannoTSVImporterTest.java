/**
 * Copyright 2016 GU.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.corpus_tools.pepper_WebannoTSVModule;

import org.corpus_tools.pepper.modules.PepperModuleProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
	public void testCustomAnnotation()
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
          assertEquals(122, dg.getPointingRelations().size());          
          // checks that all relations are contained
          assertEquals(2008, dg.getRelations().size());
        }
        
        @Test
	public void testDepNER()
	{
	  getFixture().setResourceURI(URI.createFileURI("src/test/resources/PosDep.tsv"));
          PepperModuleProperties props = new PepperModuleProperties();
          props.setPropertyValue("WebannoTSV.tokAnnos", "PosValue");
          props.setPropertyValue("WebannoTSV.lowerTypes", false);
          getFixture().setProperties(props);
	  getFixture().mapSDocument();
	  
	  SDocumentGraph dg = getFixture().getDocument().getDocumentGraph();

          // check token count
	  assertEquals(86, dg.getTokens().size());
          // check that POS annotations were correctly attached to tokens
          assertNotNull(dg.getTokens().get(0).getAnnotation("webanno","PosValue"));
          // checks that all nodes are contained
          assertEquals(92, dg.getNodes().size());
          // checks that all spans (subclass of nodes) are contained
          assertEquals(5, dg.getSpans().size());
          // checks that all structures (subclass of nodes) are contained
          assertEquals(0, dg.getStructures().size());
          // checks that all pointing relations (subclass of relations) are
          // contained
          assertEquals(158, dg.getPointingRelations().size());          
          assertEquals(true, (dg.getPointingRelations().size()>0));          
          // checks that all relations are contained
          assertEquals(254, dg.getRelations().size());
        }
}