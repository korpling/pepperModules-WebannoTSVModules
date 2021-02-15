![SaltNPepper project](./gh-site/img/SaltNPepper_logo2010.png)
# WebannoTSVModules
This repository provides modules supporting the WebAnno TSV format (see https://webanno.github.io/webanno/) in the Pepper converter framework (see https://u.hu-berlin.de/saltnpepper). So far, only an Importer module for WebAnno TSV format, version 3, has been developed. In the future, an exporter may also be added.

Pepper is a pluggable framework to convert a variety of linguistic formats (like [TigerXML](http://www.ims.uni-stuttgart.de/forschung/ressourcen/werkzeuge/TIGERSearch/doc/html/TigerXML.html), the [EXMARaLDA format](http://www.exmaralda.org/), [PAULA](http://www.sfb632.uni-potsdam.de/paula.html) etc.) into each other. Furthermore Pepper uses Salt (see https://github.com/korpling/salt), the graph-based meta model for linguistic data, which acts as an intermediate model to reduce the number of mappings to be implemented. This means converting data from a format _A_ to a format _B_ consists of two steps. First the data is mapped from format _A_ to Salt and second from Salt to format _B_. This detour reduces the number of Pepper modules from _n<sup>2</sup>-n_ (in the case of a direct mapping) to _2n_ to handle  n formats.

![n:n mappings via SaltNPepper](./gh-site/img/puzzle.png)

In Pepper there are three different types of modules:
* importers (to map a format _A_ to a Salt model)
* manipulators (to map a Salt model to a Salt model, e.g. to add additional annotations, to rename things to merge data etc.)
* exporters (to map a Salt model to a format _B_).

For a simple Pepper workflow you need at least one importer and one exporter. This project currently supplies an importer for WebAnno TSV format, version 3.

## Requirements
Since the module provided here is a plugin for Pepper, you need an instance of the Pepper framework. If you do not already have a running Pepper instance, click on the link below and download the latest stable version (not a SNAPSHOT):

> Note:
> Pepper is a Java based program, therefore you need to have at least Java 7 (JRE or JDK) on your system. You can download Java from https://www.oracle.com/java/index.html or http://openjdk.java.net/ .


## Install module
If this Pepper module is not yet contained in your Pepper distribution, you can easily install it. Just open a command line and enter one of the following program calls:

**Windows**
```
pepperStart.bat 
```

**Linux/Unix**
```
bash pepperStart.sh 
```

Then type in command *is* and the path from where to install the module:
```
pepper> update de.hu_berlin.german.korpling.saltnpepper::pepperModules-WebannoTSVModules::https://repo1.maven.org/maven2/
```

## Usage
To use this module in your Pepper workflow, put the following lines into the workflow description file. Note the fixed order of xml elements in the workflow description file: &lt;importer/>, &lt;manipulator/>, &lt;exporter/>. The WebannoTSVImporter is an importer module, which can be addressed by one of the following alternatives.
A detailed description of the Pepper workflow can be found on the [Pepper project site](https://u.hu-berlin.de/saltnpepper). 

### a) Identify the module by name

```xml
<importer name="WebannoTSVImporter" path="PATH_TO_CORPUS"/>
```

### b) Identify the module by formats

```xml
<importer formatName="WebannoTSV" formatVersion="3.0" path="PATH_TO_CORPUS"/>
```

### c) Use properties

```xml
<importer name="WebannoTSVImporter" path="PATH_TO_CORPUS">
  <property key="PROPERTY_NAME">PROPERTY_VALUE</property>
</importer>
```

## Contribute
Since this Pepper module is under a free license, please feel free to fork it from github and improve the module. If you think that others can benefit from your improvements, don't hesitate to make a pull request, so that your changes can be merged.
If you have found any bugs, or have some feature request, please open an issue on github. If you need any help, please write an e-mail to saltnpepper@lists.hu-berlin.de .

## Funders
This project module was developed at Georgetown University. 

## License
  Copyright 2016 Amir Zeldes, Georgetown University.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.


# <a name="details1">WebannoTSVImporter</a>

## Properties

|name of property			|possible values		|default value|	
|---------------------|-------------------|-------------|
|WebannoTSV.namespace			    |String           |webanno|
|WebannoTSV.lowerTypes  |true,false       |false|
|WebannoTSV.tokAnnos  |String       | |

### namespace

The name of the default namespace or Salt layer to add to all imported nodes,
edges and annotations. Default is "webanno".

### lowerTypes

If `true`, relation annotation types, which WebAnno capitalizes automatically,
will be lower-cased. For example, if you have an edge annotation `coref`, the
WebAnno TSV export function will rename it to `Coref`. By setting this property
to `true`, the name will be `coref` again. Default is `false`.

### tokAnnos

Supplies a semicolon-separated list of annotation names which should be attached directly to token, without creating span nodes above the covered area. Example value:
`pos;lemma` (if annotations called pos and lemma are present, they are attached directly to their token, without creating a non-terminal node above the token).
