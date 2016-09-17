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
public class WebannoTSVEdge {

    private String sourceID;
    private String targetID;
    private String type;
    private String annoName;
    private String annoValue;
    
    WebannoTSVEdge() {

    }

    public WebannoTSVEdge(String sourceID, String targetID, String type, String annoName, String annoValue) {

        this.sourceID = sourceID;
        this.targetID = targetID;
        this.type = type;
        this.annoName = annoName;
        this.annoValue = annoValue;

    }

    public String getSourceID() {
        return sourceID;
    }

    public String getTargetID() {
        return targetID;
    }

    public String getType() {
        return type;
    }

    public String getAnnoName() {
        return annoName;
    }

    public String getAnnoValue() {
        return annoValue;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAnnoName(String annoName) {
        this.annoName = annoName;
    }

    public void setAnnoValue(String annoValue) {
        this.annoValue = annoValue;
    }
    
    
    
}
