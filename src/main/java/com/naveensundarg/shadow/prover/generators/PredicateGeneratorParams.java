package com.naveensundarg.shadow.prover.generators;

public class PredicateGeneratorParams extends GeneratorParams {

    // TODO: Make into a range
    public int numConstants;
    public int minArguments;
    public int maxArguments;
    // TODO: Make into a range
    public int numPredicates;
    // TODO: Make into a range
    public int numEqualities;


    @Override
    public GeneratorParams copy() {
        PredicateGeneratorParams params = new PredicateGeneratorParams();

        super.copyTo(params);
        this.copyTo(params);

        return params;
    }

    protected void copyTo(PredicateGeneratorParams other) {
        other.numConstants = this.numConstants;
        other.minArguments = this.minArguments;
        other.maxArguments = this.maxArguments;
        other.numPredicates = this.numPredicates;
        other.numEqualities = this.numEqualities;
    }
}
