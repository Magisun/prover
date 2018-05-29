package com.naveensundarg.shadow.prover.generators;

import com.naveensundarg.shadow.prover.representations.formula.Atom;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.formula.Not;
import com.naveensundarg.shadow.prover.representations.formula.Or;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Pair;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PropositionalProblemSet implements ProblemSet {

    private static final long VERSION = 2;

    private List<Pair<List<Formula>, Boolean>> pset;
    private GeneratorParams params;
    private NameSpace atomSpace;

    public PropositionalProblemSet() {
        this.pset = CollectionUtils.newEmptyList();
    }

    public PropositionalProblemSet(List<Pair<List<Formula>, Boolean>> pset,
                                   GeneratorParams params,
                                   NameSpace atomSpace) {
        this.pset = pset;
        this.params = new GeneratorParams(params);
        this.atomSpace = atomSpace;
    }


    // TODO: Add accessors


    @Override
    public void writeToWriter(Writer writer) {
        JSONObject container = new JSONObject();

        container.put("type", PropositionalProblemSet.class.getSimpleName());
        container.put("version", VERSION);

        //container.put("signature", collectSignature(params));
        container.put("problems", collectProblems(params));

        container.put("parameters", params.toJSON());

        container.write(writer);
    }

    private JSONArray collectProblems(GeneratorParams params) {
        JSONArray problemArray = new JSONArray();


        for(Pair<List<Formula>, Boolean> problem : pset) {
            JSONObject container = new JSONObject();


            container.put("consistent", problem.second() ? 0 : 1);


            JSONArray formulae = new JSONArray();

            // Add all generated formulae to the matrix
            for(Formula formula : problem.first()) {
                JSONArray formulaRepresentation = new JSONArray();

                Set<Formula> literals = Arrays.stream(((Or) formula).getArguments()).collect(Collectors.toSet());
                for(int i = 0; i < params.atoms; i++) {
                    Atom atom = new Atom(Names.NAMES[i]);
                    Not negatedAtom = new Not(atom);

                    if(literals.contains(atom)) {
                        formulaRepresentation.put(1);
                    } else if(literals.contains(negatedAtom)) {
                        formulaRepresentation.put(-1);
                    } else {
                        formulaRepresentation.put(0);
                    }
                }


                formulae.put(formulaRepresentation);
            }

            // Add additional empty clauses to pad out the matrix if needed
            if(problem.first().size() < params.clauses.max) {
                JSONArray emptyFormula = new JSONArray();

                for(int i = 0; i < params.atoms; i++) {
                    emptyFormula.put(0);
                }

                for(int i = 0; i < params.clauses.max - problem.first().size(); i++) {
                    formulae.put(emptyFormula);
                }
            }

            container.put("formulae", formulae);


            problemArray.put(container);
        }


        return problemArray;
    }

    private JSONObject collectSignature(GeneratorParams params) {
        JSONObject map = new JSONObject();

        for(int i = 0; i < params.atoms; i++) {
            map.put(Integer.toString(i), 0);
        }

        return map;
    }

    @Override
    public void readFromReader(Reader reader) {
        throw new NotImplementedException("Problem set parsing not implemented.");
    }
}
