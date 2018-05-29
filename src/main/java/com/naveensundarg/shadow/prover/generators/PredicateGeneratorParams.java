package com.naveensundarg.shadow.prover.generators;

public class PredicateGeneratorParams extends GeneratorParams {

    /**
     * Specifies the number of constants (0-arity functions) in the pool.
     * <p>
     * See GeneratorParams.atoms for a description of name pooling.
     */
    public int constants;

    /**
     * Specifies the min & max arity for predicate.
     */
    public Range predicateArguments;

    /**
     * Specifies the number of predicates in the pool.
     */
    public Range predicates;

    /**
     * Equalities are inserted into generated clauses; this is used to specify
     * limits to those insertions.
     */
    public Range equalities;


    public PredicateGeneratorParams() {
        super();

        this.constants = 0;

        this.predicateArguments = this.predicates =
                this.equalities = new Range(0, 0);
    }

    public PredicateGeneratorParams(PredicateGeneratorParams other) {
        super(other);

        this.constants = other.constants;
        this.predicateArguments = other.predicateArguments;
        this.predicates = other.predicates;
        this.equalities = other.equalities;
    }

    public void sanityCheck() {
        assert equalities.max <= clauseWidth;

        assert predicateArguments.max <= constants;
    }
}
