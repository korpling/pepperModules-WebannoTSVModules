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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.impl.PepperMapperImpl;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleDataException;
import org.corpus_tools.pepper_WebannoTSVModule.WebannoTSVAnnotation.WebannoTSVAnnotationType;
import org.corpus_tools.peppermodules.conll.tupleconnector.TupleConnectorFactory;
import org.corpus_tools.peppermodules.conll.tupleconnector.TupleReader;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SLayer;
import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright 2016 GU.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 * @author Amir Zeldes
 */
public class WebannoTSV2SaltMapper extends PepperMapperImpl {

	private String namespace;
	private String tokAnnos;
	private boolean lowercaseTypes;
	private SLayer layer;

	/**
	 * Mapper for WebannoTSV format to Salt
	 * 
	 */

	public WebannoTSV2SaltMapper() {
		setProperties(new WebannoTSVImporterProperties());

	}

	private static final Logger logger = LoggerFactory.getLogger(WebannoTSV2SaltMapper.class);

	@Override
	public DOCUMENT_STATUS mapSCorpus() {

		return (DOCUMENT_STATUS.COMPLETED);
	}

	/**
	 * {@inheritDoc PepperMapper#setDocument(SDocument)}
	 * 
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument() {

		getDocument().setDocumentGraph(SaltFactory.createSDocumentGraph());

		// assign customizationn values from importer properties
		this.namespace = (String) getProperties().getProperties().getProperty(WebannoTSVImporterProperties.NAMESPACE,
				"webanno");
		this.tokAnnos = (String) getProperties().getProperties().getProperty(WebannoTSVImporterProperties.TOK_ANNOS,
				"");

		String[] tokAnnoList;
		tokAnnoList = this.tokAnnos.split(";");

		if (getProperties().getProperty(WebannoTSVImporterProperties.LOWER_TYPES).getValue() instanceof Boolean) {
			this.lowercaseTypes = (Boolean) getProperties().getProperty(WebannoTSVImporterProperties.LOWER_TYPES)
					.getValue();
		} else {
			this.lowercaseTypes = Boolean
					.valueOf((String) getProperties().getProperty(WebannoTSVImporterProperties.LOWER_TYPES).getValue());
		}
		if (this.namespace != null) {
			this.layer = SaltFactory.createSLayer();
			this.layer.setName(this.namespace);
			this.layer.setGraph(getDocument().getDocumentGraph());
		}

		// to get the exact resource which is processed now, call
		// getResources(), make sure, it was set in createMapper()
		URI resource = getResourceURI();

		// we record, which file currently is imported to the debug stream,
		// in this dummy implementation the resource is null
		logger.debug("Importing the file {}.", resource.toFileString());

		TupleReader tupleReader = TupleConnectorFactory.fINSTANCE.createTupleReader();
		// try reading the input file
		try {

			tupleReader.setSeperator("\t");
			tupleReader.setFile(new File(this.getResourceURI().toFileString()));
			tupleReader.readFile();
		} catch (IOException e) {
			String errorMessage = "Input file could not be read. Aborting conversion of file " + this.getResourceURI()
					+ ".";
			logger.error(errorMessage);
			throw new PepperModuleDataException(this, errorMessage);
		}

		STextualDS sTextualDS = SaltFactory.createSTextualDS();
		sTextualDS.setGraph(getDocument().getDocumentGraph());

		LinkedHashMap<String, WebannoTSVMarkable> spanAnnoMap = new LinkedHashMap<>();
		LinkedList<WebannoTSVEdge> pointingRelationList = new LinkedList<>();
		ArrayList<String> fieldValues = new ArrayList<>();

		Collection<String> tuple = null;
		int numOfTuples = tupleReader.getNumOfTuples();
		int tupleSize;
		int fieldNum = 1;
		float processedTuples = 0;

		// list to keep track of declared annotation names
		ArrayList<WebannoTSVAnnotation> annotations = new ArrayList<>();
		String[] declParts;
		String[] annoSpecs;
		String nodeName;
		WebannoTSVAnnotationType nodeType;

		// variables to keep track of tokens and text
		// using a StringBuilder for the iteratively updated raw text
		int stringBuilderCharBufferSize = tupleReader.characterSize(2) + numOfTuples;
		StringBuilder primaryText = new StringBuilder(stringBuilderCharBufferSize);
		String tokID;
		String textOffset; // TODO: properly decode text offsets to reconstruct
							// whitespace preserving STextualDS
		String tokText;
		String markID;
		String annoVal;
		String annoField;
		int annoIndex;

		// iteration over all data rows (the complete input file)
		for (int rowIndex = 0; rowIndex < numOfTuples; rowIndex++) {
			try {
				tuple = tupleReader.getTuple();
			} catch (IOException e) {
				String errorMessage = String.format("line %d of input file could not be read. Abort conversion of file "
						+ this.getResourceURI() + ".", rowIndex + 1);
				throw new PepperModuleDataException(this, errorMessage);
			}

			tupleSize = tuple.size();
			fieldValues.clear();

			if (tupleSize == 1) {
				String fieldValue = tuple.iterator().next();
				if (fieldNum == 1 && fieldValue.startsWith("#T_")) { // annotation
																		// declaration
																		// section
					if (fieldValue.startsWith("#T_RL")) {
						nodeType = WebannoTSVAnnotationType.RELATION;
					} else {
						nodeType = WebannoTSVAnnotationType.SPAN;
					}

					declParts = fieldValue.split("=");
					if (declParts.length > 1) {
						annoSpecs = declParts[1].split("\\|");
						if (annoSpecs.length > 1) {
							nodeName = annoSpecs[0];
							nodeName = nodeName.replace("webanno.custom.", ""); // remove
																				// webanno.custom
																				// prefixes
							nodeName = nodeName.replaceAll("de.tudarmstadt.ukp.dkpro\\..+\\.", ""); // remove
																									// built
																									// in
																									// type
																									// prefixess
							if (this.lowercaseTypes) {
								nodeName = nodeName.toLowerCase();
							}

							if (nodeType == WebannoTSVAnnotationType.SPAN) {
								for (String anno : Arrays.copyOfRange(annoSpecs, 1, annoSpecs.length)) {
									annotations.add(new WebannoTSVAnnotation(nodeType, nodeName, anno));
								}
							} else if (nodeType == WebannoTSVAnnotationType.RELATION) {
								for (String anno : Arrays.copyOfRange(annoSpecs, 1, annoSpecs.length - 1)) {
									annotations.add(
											new WebannoTSVAnnotation(nodeType, nodeName, anno, annoSpecs.length - 2));
								}
								// Variable to hold type of edge target node -
								// this may be redundant
								// String targetNodeName =
								// annoSpecs[annoSpecs.length-1];
							}
						}
					}
				}
			} else if (tupleSize > 2) { // Token row

				// Extend the text - currently adding space after each token
				// TODO: get actual whitespace from #text comment

				// Create the token and index it in the token list
				Iterator<String> iter = tuple.iterator();
				tokID = iter.next();
				textOffset = iter.next();
				tokText = iter.next();

				// create token and add to token list
				SToken sToken = SaltFactory.createSToken();
				sToken.setGraph(getDocument().getDocumentGraph());

				// update primary text string builder (sTextualDS.sText will be
				// set after
				// completely reading the input file)
				int tokenTextStartOffset = primaryText.length();
				primaryText.append(tokText).append(" ");
				int tokenTextEndOffset = primaryText.length() - 1;

				// create textual relation
				STextualRelation sTextualRelation = SaltFactory.createSTextualRelation();
				sTextualRelation.setSource(sToken);
				sTextualRelation.setTarget(sTextualDS);
				sTextualRelation.setStart(tokenTextStartOffset);
				sTextualRelation.setEnd(tokenTextEndOffset);
				sTextualRelation.setGraph(getDocument().getDocumentGraph());

				annoIndex = 0;
				while (iter.hasNext()) {
					annoField = iter.next();
					if (!annoField.equals("_")) { // Underscore designates empty
													// annotation in WebannoTSV

						if (annoIndex < annotations.size()) {
							WebannoTSVAnnotation currentAnno = annotations.get(annoIndex);
							String[] sepAnnos;
							String[] sepRels;
							if (currentAnno.getType() == WebannoTSVAnnotationType.SPAN) { // Span
																							// annotation
								sepAnnos = annoField.split("\\|");
								for (String sepAnno : sepAnnos) {
									if (sepAnno.contains("[")) {
										String[] temp = sepAnno.split("\\[");
										annoVal = temp[0];
										markID = temp[1].substring(0, temp[1].length() - 1);
									} else { // Single token annotation span
												// without annotation ID, use
												// tokID as markID
										annoVal = sepAnno;
										markID = tokID;
									}

									WebannoTSVMarkable mark;
									if (spanAnnoMap.containsKey(markID)) {
										mark = spanAnnoMap.get(markID);
									} else {
										mark = new WebannoTSVMarkable();
										spanAnnoMap.put(markID, mark);
									}
									mark.addToken(sToken);
									mark.addAnnotation(namespace, currentAnno.getAnnoName(), annoVal);
									// Check if this is a single-token
									// annotation
									if (Arrays.asList(tokAnnoList).contains(currentAnno.getAnnoName())) {
										mark.setIsTokAnno(true);
									}
									mark.setNodeName(currentAnno.getNodeName());
								}
								if (annoIndex < annotations.size()) {
									annoIndex++;
								}
							} else if (currentAnno.getType() == WebannoTSVAnnotationType.RELATION) {
								// Edge annotation, consists of at least two
								// columns:
								// annotation value(s), and finally
								// source_target
								// use numSisters to determine how many columns
								// to chomp
								int numCols = currentAnno.getNumSisters();

								// collect as many additional fields as we have
								// additional columns
								for (int j = 1; j < numCols; j++) {
									annoField += "\t" + iter.next();
								}

								String relField;
								if (iter.hasNext()) {
									relField = iter.next();
								} else {
									throw new PepperModuleDataException(this,
											"Insufficient columns found. Annotation field is: " + annoField
													+ " and currentAnnos is: " + currentAnno.getAnnoName()
													+ " which has n sisters: " + currentAnno.getNumSisters());
								}

								sepRels = relField.split("\\|");
								String sepAnnoField[];
								sepAnnoField = annoField.split("\t");

								// iterate over values and assign to appropriate
								// anno objects
								for (int j = 0; j < numCols; j++) {
									String edgeAnnoValues = sepAnnoField[j]; // contains
																				// pipe-separated
																				// instances
																				// of
																				// a
																				// single
																				// edge
																				// anno
																				// type
									if (j == 0) {
										// do nothing, currentAnno is already
										// correct
									} else {
										// advance to second and subsequent edge
										// annos
										annoIndex++;
										currentAnno = annotations.get(annoIndex);
									}
									sepAnnos = edgeAnnoValues.split("\\|");
									if (sepAnnos.length < sepRels.length) {
										logger.warn("Ignored relation because there were more annotations than edges: "
												+ edgeAnnoValues + "<>" + relField + "\n");
									} else {
										for (int i = 0; i < sepAnnos.length; i++) {
											if (i >= sepRels.length) {
												throw new PepperModuleDataException(this,
														"Missing edge information for: " + edgeAnnoValues + ":"
																+ relField + "\n");
											}
											String edgeAnnoValue = sepAnnos[i]; // contains
																				// a
																				// single
																				// edge
																				// anno
																				// label
																				// of
																				// a
																				// single
																				// type
											String edgeSourceTarget = sepRels[i];
											String source = "0";
											String target = "0";
											String position;
											if (edgeSourceTarget.contains("[") && edgeSourceTarget.contains("_")) { // relation
																													// contains
																													// explicit
																													// source
																													// or
																													// target
																													// node
																													// ID
												String[] position_source_target = edgeSourceTarget.split("\\[");
												position = position_source_target[0];
												String[] source_target = position_source_target[1].split("_");
												source = source_target[0];
												target = source_target[1].substring(0, source_target[1].length() - 1);
											} else { // this is a link between
														// two single token
														// spans, just use token
														// IDs
												position = edgeSourceTarget;
											}
											if (target.equals("0")) {
												target = tokID;
											}
											if (source.equals("0")) {
												source = position;
											}

											WebannoTSVEdge rel = new WebannoTSVEdge(source, target,
													currentAnno.getNodeName(), currentAnno.getAnnoName(),
													edgeAnnoValue);
											pointingRelationList.add(rel);
										}
									}
								}
								// Only increment annoIndex once for these two
								// or more columns,
								// which correspond to one edge annotation type
								if (annoIndex < annotations.size()) {
									annoIndex++;
								}
							}
						}
					} else {
						if (annoIndex < annotations.size()) {
							annoIndex++; // moving annoIndex for empty anno
											// column with "_"
						}
					}
				}
			}

			// Finished reading an input tuple
			processedTuples++;
			if ((int) ((processedTuples / numOfTuples) * 100) % 10 == 0) {
				addProgress((double) (processedTuples / numOfTuples));
			}

		}

		// ### file is completely read now ###

		// delete last char of primary text (a space character) and set it as
		// text for STextualDS
		primaryText.deleteCharAt(primaryText.length() - 1);
		sTextualDS.setText(primaryText.toString());

		// generate SSpans and attach annotations from HashMap
		// save SSpans in HashMap by markID to link with SPointingRelations
		// later
		HashMap<String, SStructuredNode> spanIDMap = new HashMap<>();

		for (Map.Entry<String, WebannoTSVMarkable> entry : spanAnnoMap.entrySet()) {
			String ID = entry.getKey();
			WebannoTSVMarkable mark = entry.getValue();

			// make SSpan above tokens covered by this WebannoTSVMarkable
			ArrayList<SToken> toksToCover = mark.getTokens();
			if (mark.isIsTokAnno()) { // handle token annotations

				SToken thisTok = toksToCover.get(0);
				// add annotations if not already present
				for (SAnnotation anno : mark.getAnnotations()) {
					if (thisTok.getAnnotation(anno.getNamespace(), anno.getName()) == null) {
						thisTok.addAnnotation(anno);
					}
				}
				spanIDMap.put(ID, thisTok);
			} else {
				SSpan sSpan = getDocument().getDocumentGraph().createSpan(toksToCover);
				if (this.namespace != null) {
					if (sSpan == null) {
						throw new PepperModuleDataException(this,
								"Null span detected, created from markable object: " + mark.toString());
					}
					sSpan.addLayer(this.layer);
				}
				if (mark.getNodeName() != null) {
					sSpan.setName(mark.getNodeName());
				}

				// remember SSpan object belonging to this markID
				spanIDMap.put(ID, sSpan);

				// add annotations if not already present
				for (SAnnotation anno : mark.getAnnotations()) {
					if (sSpan.getAnnotation(anno.getNamespace(), anno.getName()) == null) {
						sSpan.addAnnotation(anno);
					}
				}
			}
		}

		// create SPointingRelations between SSpans if found
		for (WebannoTSVEdge edge : pointingRelationList) {
			// Check that source and target are not identical
			// Note that self-links are valid in WebAnno, but not in Salt
			if (!edge.getSourceID().equals(edge.getTargetID())) {
				SPointingRelation sRel = SaltFactory.createSPointingRelation();
				if (spanIDMap.containsKey(edge.getSourceID())) {
					sRel.setSource(spanIDMap.get(edge.getSourceID()));
				} else {
					throw new PepperModuleDataException(this,
							"Input error: relation with missing source element: " + edge.getSourceID() + "\n");
				}
				if (spanIDMap.containsKey(edge.getTargetID())) {
					sRel.setTarget(spanIDMap.get(edge.getTargetID()));
				} else {
					throw new PepperModuleDataException(this,
							"Input error: relation with missing target element: " + edge.getTargetID() + "\n");
				}
				sRel.setType(edge.getType());
				SAnnotation relAnno = SaltFactory.createSAnnotation();
				if (namespace != null) {
					relAnno.setNamespace(namespace);
					sRel.addLayer(this.layer);
				}
				relAnno.setName(edge.getAnnoName());
				relAnno.setValue(edge.getAnnoValue());
				sRel.addAnnotation(relAnno);
				getDocument().getDocumentGraph().addRelation(sRel);
			}
		}

		return (DOCUMENT_STATUS.COMPLETED);

	}

}
