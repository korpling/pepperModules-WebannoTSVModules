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

import java.util.ArrayList;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;

/**
 *
 * @author Amir Zeldes
 */
public class WebannoTSVMarkable {
    private ArrayList<SToken> tokens;
    private ArrayList<SAnnotation> annotations;
    private String nodeName;

    public String getNodeName() {
        return nodeName;
    }
    
    WebannoTSVMarkable(){

        this.tokens = new ArrayList<>();
        this.annotations = new ArrayList<>();
             
    }

    public WebannoTSVMarkable(ArrayList<SToken> tokens) {
        this.tokens = tokens;
    }

    public ArrayList<SAnnotation> getAnnotations() {
        return annotations;
    }
    

    public ArrayList<SToken> getTokens() {
        return tokens;
    }

    public void addAnnotation(String namespace, String annoName, String annoValue){
        SAnnotation sAnno = SaltFactory.createSAnnotation();
        sAnno.setNamespace(namespace);
        sAnno.setName(annoName);
        sAnno.setValue(annoValue);
        this.annotations.add(sAnno);
    }
    
    public void addToken(SToken tok){
        if (!this.tokens.contains(tok)){
            this.tokens.add(tok);
        }
    }

    void setNodeName(String name) {
        this.nodeName = name;
    }
    
}
