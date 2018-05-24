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

    private static final long VERSION = 1;

    private List<Pair<List<Formula>, Boolean>> pset;

    public PropositionalProblemSet() {
        this.pset = CollectionUtils.newEmptyList();
    }

    public PropositionalProblemSet(List<Pair<List<Formula>, Boolean>> pset) {
        this.pset = pset;
    }


    // TODO: Add accessors


    @Override
    public void writeToWriter(Writer writer, GeneratorParams params) {
        JSONObject container = new JSONObject();

        container.put("type", PropositionalProblemSet.class.getSimpleName());
        container.put("version", VERSION);

        container.put("signature", collectSignature(params));
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

            for(Formula formula : problem.first()) {
                JSONArray formulaRepresentation = new JSONArray();

                Set<Formula> literals = Arrays.stream(((Or) formula).getArguments()).collect(Collectors.toSet());
                for(int i = 0; i < params.maxAtoms; i++) {
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

            container.put("formulae", formulae);


            problemArray.put(container);
        }


        return problemArray;
    }

    private JSONObject collectSignature(GeneratorParams params) {
        JSONObject map = new JSONObject();

        for(int i = 0; i < params.maxAtoms; i++) {
            map.put(Integer.toString(i), 0);
        }

        return map;
    }

    @Override
    public void readFromReader(Reader reader) {
        throw new NotImplementedException("Problem set parsing not implemented.");
    }
}
