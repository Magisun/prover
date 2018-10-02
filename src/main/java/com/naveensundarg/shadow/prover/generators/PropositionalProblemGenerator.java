package com.naveensundarg.shadow.prover.generators;


import com.naveensundarg.shadow.prover.core.Logic;
import com.naveensundarg.shadow.prover.core.Prover;
import com.naveensundarg.shadow.prover.core.SnarkWrapper;
import com.naveensundarg.shadow.prover.representations.formula.Atom;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.formula.Not;
import com.naveensundarg.shadow.prover.representations.formula.Or;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.ImmutablePair;
import com.naveensundarg.shadow.prover.utils.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PropositionalProblemGenerator implements Generator {

    static Prover prover;

    private final GeneratorParams params;
    public final NameSpace atomSpace;


    public PropositionalProblemGenerator(GeneratorParams generatorParams) {

        this.params = new GeneratorParams(generatorParams);
        this.atomSpace = new NameSpace("a");

        for(int i = 0; i < this.params.atoms; i++) {
            this.atomSpace.getNextName();
        }
    }


    static {

        prover = SnarkWrapper.getInstance();
    }

    @Override
    public ProblemSet generate(int total) {

        int positiveExamples = 0;
        Set<Pair<List<Formula>, Boolean>> generated = new HashSet<>();

        while(generated.size() < total) {
            Pair<List<Formula>, Boolean> problem = generateProblem();

            if(generated.contains(problem)) {
                continue;
            }

            if(problem.second() && positiveExamples < total/2) {
                generated.add(problem);
                positiveExamples++;
            } else if(!problem.second() && (generated.size() - positiveExamples) < total/2) {
                generated.add(problem);
            }
        }

        List<Pair<List<Formula>, Boolean>> gen_list = Arrays.asList(generated.toArray(new Pair[0]));

        return new PropositionalProblemSet(gen_list, new GeneratorParams(params), atomSpace);
    }


    private Pair<List<Formula>, Boolean> generateProblem() {

        List<Formula> clauses = CollectionUtils.newEmptyList();

        int totalClauses = ThreadLocalRandom.current().nextInt(params.clauses.min, params.clauses.max + 1);

        for (int i = 0; i < totalClauses; i++) {

            clauses.add(generateRandomClause());
        }


        // Formula goalNeg = CommonUtils.pickRandom(clauses);

        if (prover.prove(new HashSet<>(clauses), Logic.getFalseFormula()).isPresent()) {

            return ImmutablePair.from(clauses, true);

        } else {

            return ImmutablePair.from(clauses, false);
        }

    }

    private Formula generateRandomClause() {

        int totalLiteralsInClause = ThreadLocalRandom.current().nextInt(1, params.clauseWidth + 1);

        List<Formula> clauseLiterals = CollectionUtils.newEmptyList();

        for (int i = 0; i < totalLiteralsInClause; i++) {

            clauseLiterals.add(generateRandomLiteral());
        }

        return new Or(clauseLiterals);


    }

    private Formula generateRandomLiteral() {

        Atom atom = generateRandomAtom();
        if (ThreadLocalRandom.current().nextBoolean()) {

            return atom;

        } else {

            return new Not(atom);
        }

    }


    private Atom generateRandomAtom() {

        return new Atom(atomSpace.indexToName(ThreadLocalRandom.current().nextInt(0, params.atoms)));
    }


}



