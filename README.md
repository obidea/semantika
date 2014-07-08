OBDA Semantika
==============

Semantika is a robust, high-performance RDB-to-RDF connector and data access middleware API for Java and SQL. The library provides an easy interface for enabling semantic search over an existing database. The way it works is non-intrusive and risk-free towards your valuable data. No data replication or data migration or extra hardware is required.

Feature
-------
* Support most features in SPARQL 1.1 Query language.
* Data semantic mapping based on R2RML
* Support R2RML native syntax
* Data provider based on JDBC system. Full support on MySQL, PostgreSQL and H2
* Connection pool enabled
* Support domain modelling in OWL2 QL
* Built-in reasoner when domain model is supplied
* Open source under Apache License 2.0

API Overview
------------

### System Setup

An instance of `com.obidea.semantika.app.ApplicationManager` is created by loading a configuration file, i.e., `application.cfg.xml` in your classpath. [Please refer to our wiki page for more details about Semantika configuration settings](https://github.com/obidea/semantika-api/wiki/1.-XML-Configuration-File).
```java
ApplicationManager manager = new ApplicationFactory()
             .configure("application.cfg.xml")
             .createApplicationManager();
```

### SPARQL Query Answer

The `ApplicationManager` then creates `SparqlQueryEngine` which is a thread-safe object that is initiated once to serve SPARQL query answering.
```java
SparqlQueryEngine queryEngine = manager.createQueryEngine(); 
queryEngine.start();
QueryResult result = queryEngine.evaluate(sparql);
// do something with the result
// ...
queryEngine.stop();
```

In addition, the query engine allows you to manage result fetching for efficient data retrieval. The example below shows you how to create a simple paging where each page contains 100 items.

```java
int offset = 0;
int limit = 100;
int maxPage = 10;
int pageNum = 1;
while (pageNum <= maxPage) {
   QueryResult result = queryEngine.createQuery(sparql)
                                    .setFirstResult(offset)
                                    .setMaxResults(limit).evaluate();
   // do something with the result
   // ...
   offset += limit;
   pageNum++;
}
```

### RDB2RDF Export

The `ApplicationManager` can also create `RdfMaterializerEngine` which is a thread-safe object that is initiated once to serve RDB2RDF data exporting.

```java
RdfMaterializerEngine exporter = manager.createMaterializerEngine().useNTriples();
exporter.start();
exporter.materialize(fout);
exporter.stop();
```

Starting from Semantika 1.5, the application manager can receive R2RML mapping model and does the same RDB2RDF data exporting.

License
-------
This software is licensed under the Apache 2 license, quoted below.

```
Copyright (c) 2013-2014 Josef Hardi <josef.hardi@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
