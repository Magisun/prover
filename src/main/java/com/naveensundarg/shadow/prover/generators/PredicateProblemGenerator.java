package com.naveensundarg.shadow.prover.generators;


import com.naveensundarg.shadow.prover.core.Logic;
import com.naveensundarg.shadow.prover.core.Prover;
import com.naveensundarg.shadow.prover.core.SnarkWrapper;
import com.naveensundarg.shadow.prover.representations.formula.*;
import com.naveensundarg.shadow.prover.representations.value.Constant;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.ImmutablePair;
import com.naveensundarg.shadow.prover.utils.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PredicateProblemGenerator implements Generator {

    private static Prover prover;

    private final PredicateGeneratorParams params;
    private final NameSpace constantSpace, predicateSpace;
    private final Constant[] constantPool;
    private final Pair<String, Integer>[] predicatePool;


    public PredicateProblemGenerator(PredicateGeneratorParams generatorParams){

        this.params = (PredicateGeneratorParams) generatorParams.copy();
        this.constantSpace = new NameSpace("c");
        this.predicateSpace = new NameSpace("P");

        this.constantPool = new Constant[params.numConstants];
        for(int i = 0; i < params.numConstants; i++) {
            this.constantPool[i] = new Constant(constantSpace.getNextName());
        }

        this.predicatePool = new ImmutablePair[params.numPredicates];
        for(int i = 0; i < params.numPredicates; i++) {
            this.predicatePool[i] = ImmutablePair.from(
                    predicateSpace.getNextName(),
                    ThreadLocalRandom.current().nextInt(params.minArguments, params.maxArguments +1));
        }
    }


    static {

        prover = SnarkWrapper.getInstance();
    }


    @Override
    public List<Pair<List<Formula>, Boolean>> generate(int total) {

        List<Pair<List<Formula>, Boolean>> generated = CollectionUtils.newEmptyList();

        for (int i = 0; i < total; i++){

            generated.add(generateProblem());
        }

        return generated;
    }


    private Pair<List<Formula>, Boolean> generateProblem(){

        List<Formula> clauses = CollectionUtils.newEmptyList();

        for(int i = 0; i< params.clauses; i++){

            clauses.add(generateRandomClause());
        }


       // Formula goalNeg = CommonUtils.pickRandom(clauses);

        if(prover.prove(new HashSet<>(clauses), Logic.getFalseFormula()).isPresent()){

            return ImmutablePair.from(clauses, true);
        } else {

            return ImmutablePair.from(clauses, false);
        }
    }

    private Formula generateRandomClause(){

        int totalLiteralsInClause = ThreadLocalRandom.current().nextInt(1, params.maxLiteralsInClause + 1 );
        List<Formula> clauseLiterals = CollectionUtils.newEmptyList();

        for(int i = 0; i< totalLiteralsInClause; i++){

            clauseLiterals.add(generateRandomLiteral());
        }

        return new Or(clauseLiterals);
    }

    private Formula generateRandomLiteral(){
        Predicate predicate = generateRandomPredicate();

        if(ThreadLocalRandom.current().nextBoolean()){

            return predicate;
        } else {

            return new Not(predicate);
        }
    }

    private Predicate generateRandomPredicate() {
        Pair<String, Integer> predicateInfo =
                this.predicatePool[ThreadLocalRandom.current()
                                  .nextInt(params.numPredicates)];

        List<Value> arguments = getRandomConstants(predicateInfo.second());

        return new Predicate(predicateInfo.first(), (Value[])arguments.toArray());
    }

    private List<Value> getRandomConstants(int num_constants) {
        List<Value> constants = CollectionUtils.newEmptyList();

        for(int i = 0; i < num_constants; i++) {
            constants.add(this.constantPool[ThreadLocalRandom.current().nextInt(params.numConstants)]);
        }

        return constants;
    }
}



