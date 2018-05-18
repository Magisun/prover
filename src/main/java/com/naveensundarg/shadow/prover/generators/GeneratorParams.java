package com.naveensundarg.shadow.prover.generators;

public class GeneratorParams {

        public int maxAtoms;
        public int maxLiteralsInClause;
        public int clauses;


        public GeneratorParams copy() {
            GeneratorParams params = new GeneratorParams();

            copyTo(params);

            return params;
        }

        protected void copyTo(GeneratorParams other) {
            other.maxAtoms = this.maxAtoms;
            other.maxLiteralsInClause = this.maxLiteralsInClause;
            other.clauses = this.clauses;
        }
 }

