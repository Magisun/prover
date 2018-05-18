package com.naveensundarg.shadow.prover.generators;

public class PredicateGeneratorParams extends GeneratorParams {

    public int numConstants;
    public int minConstants;
    public int maxConstants;
    public int numPredicates;
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
        other.minConstants = this.minConstants;
        other.maxConstants = this.maxConstants;
        other.numPredicates = this.numPredicates;
        other.numEqualities = this.numEqualities;
    }
}
