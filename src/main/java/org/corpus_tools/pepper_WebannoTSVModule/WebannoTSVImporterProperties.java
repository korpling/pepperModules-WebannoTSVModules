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
package org.corpus_tools.pepper_WebannoTSVModule;

import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;

/**
 *
 * @author Amir Zeldes
 */
public class WebannoTSVImporterProperties extends PepperModuleProperties {

	public static final String PREFIX = "WebannoTSV.";

	public final static String NAMESPACE = PREFIX + "namespace";
	public final static String LOWER_TYPES = PREFIX + "lowerTypes";
	public final static String TOK_ANNOS = PREFIX + "tokAnnos";

	public WebannoTSVImporterProperties() {
		this.addProperty(new PepperModuleProperty<String>(NAMESPACE, String.class,
				"Specifies a namespace to assign to all imported annotations.", "webanno", false));
		this.addProperty(new PepperModuleProperty<Boolean>(LOWER_TYPES, Boolean.class,
				"States whether to automatically lower-case all node and edge types, since these are capitalized automatically by Webanno.",
				false, false));
		this.addProperty(new PepperModuleProperty<String>(TOK_ANNOS, String.class,
				"Supplies a semicolon-separated list of annotation names which should be attached directly to token, without creating span nodes above the covered area",
				"", false));
	}

}
