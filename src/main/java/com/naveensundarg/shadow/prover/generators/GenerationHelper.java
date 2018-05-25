package com.naveensundarg.shadow.prover.generators;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.Pair;

import java.io.*;
import java.util.List;

public class GenerationHelper {

    public static void writeProblemSet(ProblemSet pset, GeneratorParams params, String file) {
        File outputFile = new File(file);
        FileWriter outputWriter = null;
        try {
            outputWriter = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        pset.writeToWriter(outputWriter, params);

        try {
            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ProblemSet genPropositionalProblemSet(GeneratorParams params, int num) {
        PropositionalProblemGenerator generator = new PropositionalProblemGenerator(params);

        List<Pair<List<Formula>, Boolean>> problems = generator.generate(num);

        return new PropositionalProblemSet(problems);
    }

    public static GeneratorParams makePropositionalParameters(int number) {
        GeneratorParams params = new GeneratorParams();

        params.maxAtoms = number;
        params.maxLiteralsInClause = number;
        params.clauses = number;

        return params;
    }

    public static void genPropositionalJSON(int number, int totalCases, String fileName) {
        GeneratorParams params = makePropositionalParameters(number);

        ProblemSet pset = genPropositionalProblemSet(params, totalCases);

        writeProblemSet(pset, params, fileName);
    }

}
