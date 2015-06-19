# SMT (SPARQL Model Transformation)

SMT is a Java application that provides the possibility to transform RDF datasets using SPARQL. It can load RDF files, store them in an internal Jena TDB database and transform them with SPARQL rules. The output can afterwards be stored in files again.

SMT provides a graphical interface both for models and for rules.

## Concept
Different RDF files can be loaded into the internal triplestore. The one model can be loaded into the *LHS* (left-hand-side) of the transformation and one to the *RHS* (if necessary). For TGG a further model can be loaded into the context graph *CS*. All transformation are going to be applied to these three graphs *LHS*, *RHS* and *CS*. Thus, the rules can be written independantly from the models they should be applied to.