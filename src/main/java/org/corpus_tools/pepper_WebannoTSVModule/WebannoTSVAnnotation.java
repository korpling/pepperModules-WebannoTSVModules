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

/**
 *
 * @author Amir Zeldes
 */
public class WebannoTSVAnnotation {


    public enum WebannoTSVAnnotationType{
    	SPAN, RELATION;

    }
    
	private final WebannoTSVAnnotationType type;
	private final String nodeName;
	private final String annoName;

    
    WebannoTSVAnnotation(WebannoTSVAnnotationType type, String nodeName, String annoName) {
		this.type = type;
                this.nodeName = nodeName;
                this.annoName = annoName;
        
    }
    
    
    public WebannoTSVAnnotationType getType() {
        return this.type;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getAnnoName() {
        return annoName;
    }

    @Override
    public String toString() {
        return type + ": " + nodeName + "@" + annoName;
    }

    
}

