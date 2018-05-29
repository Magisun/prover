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

        this.params = new PredicateGeneratorParams(generatorParams);
        this.constantSpace = new NameSpace("c");
        this.predicateSpace = new NameSpace("P", 2);

        this.constantPool = new Constant[params.constants];
        for(int i = 0; i < params.constants; i++) {
            this.constantPool[i] = new Constant(constantSpace.getNextName());
        }

        this.predicatePool = new ImmutablePair[params.predicates.max];
        for(int i = 0; i < params.predicates.max; i++) {
            this.predicatePool[i] = ImmutablePair.from(
                    predicateSpace.getNextName(),
                    ThreadLocalRandom.current().nextInt(
                            params.predicateArguments.min,
                            params.predicateArguments.max +1));
        }
    }


    static {

        prover = SnarkWrapper.getInstance();
    }


    @Override
    public ProblemSet generate(int total) {

        List<Pair<List<Formula>, Boolean>> generated = CollectionUtils.newEmptyList();

        for (int i = 0; i < total; i++){

            generated.add(generateProblem());
        }

        return new PredicateProblemSet(generated, params, predicateSpace, constantSpace);
    }


    private Pair<List<Formula>, Boolean> generateProblem(){

        List<Formula> clauses = CollectionUtils.newEmptyList();

        int totalClauses = params.clauses.max > params.clauses.min ?
                ThreadLocalRandom.current().nextInt(params.clauses.min, params.clauses.max) : params.clauses.max;

        for(int i = 0; i < totalClauses; i++){

            clauses.add(generateRandomClause());
        }


       // Formula goalNeg = CommonUtils.pickRandom(clauses);

        if(prover.prove(new HashSet<>(clauses), Logic.getFalseFormula()).isPresent()){

            return ImmutablePair.from(clauses, true);
        } else {

            return ImmutablePair.from(clauses, false);
        }
    }

    private Formula generateRandomEquality() {
        List<Value> constants = getRandomConstants(2);

        Value[] args = new Value[2];
        constants.toArray(args);

        return new Predicate("=", args);
    }

    private Formula generateRandomClause(){

        int totalLiteralsInClause = ThreadLocalRandom.current().nextInt(1, params.clauseWidth + 1 );
        int equalities = ThreadLocalRandom.current().nextInt(Math.min(params.equalities.max,
                totalLiteralsInClause) + 1);

        List<Formula> clauseLiterals = CollectionUtils.newEmptyList();

        for(int i = 0; i < totalLiteralsInClause - equalities; i++){
            boolean negated = ThreadLocalRandom.current().nextBoolean();

            if(negated) {
                clauseLiterals.add(new Not(generateRandomClause()));
            } else {
                clauseLiterals.add(generateRandomLiteral());
            }
        }

        for(int i = 0; i < equalities; i++) {
            boolean negated = ThreadLocalRandom.current().nextBoolean();

            if(negated) {
                clauseLiterals.add(new Not(generateRandomEquality()));
            } else {
                clauseLiterals.add(generateRandomEquality());
            }
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
                                  .nextInt(params.predicates.max)];

        List<Value> arguments = getRandomConstants(predicateInfo.second());

        Value[] args = new Value[arguments.size()];
        arguments.toArray(args);

        return new Predicate(predicateInfo.first(), args);
    }

    private List<Value> getRandomConstants(int num_constants) {
        List<Value> constants = CollectionUtils.newEmptyList();

        for(int i = 0; i < num_constants; i++) {
            constants.add(this.constantPool[ThreadLocalRandom.current().nextInt(params.constants)]);
        }

        return constants;
    }
}



