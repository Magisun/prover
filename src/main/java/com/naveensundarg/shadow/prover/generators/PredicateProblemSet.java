package com.naveensundarg.shadow.prover.generators;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.formula.Not;
import com.naveensundarg.shadow.prover.representations.formula.Or;
import com.naveensundarg.shadow.prover.representations.formula.Predicate;
import com.naveensundarg.shadow.prover.utils.Pair;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class PredicateProblemSet implements ProblemSet {

    private static final long VERSION = 1;

    private List<Pair<List<Formula>, Boolean>> problems;
    private PredicateGeneratorParams params;
    private NameSpace predicateSpace, constantSpace;

    public PredicateProblemSet(List<Pair<List<Formula>, Boolean>> problems, PredicateGeneratorParams params,
                               NameSpace predicateSpace, NameSpace constantSpace) {
        this.problems = problems;
        this.params = new PredicateGeneratorParams(params);
        this.predicateSpace = predicateSpace;
        this.constantSpace = constantSpace;
    }

    @Override
    public void writeToWriter(Writer writer) {
        JSONObject container = new JSONObject();

        container.put("type", PropositionalProblemSet.class.getSimpleName());
        container.put("version", VERSION);

        //container.put("signature", collectSignature(params));
        container.put("problems", collectProblems());

        container.put("parameters", params.toJSON());

        container.write(writer);
    }

    private JSONArray collectProblems() {
        JSONArray container = new JSONArray();

        for(Pair<List<Formula>, Boolean> problem : problems) {
            container.put(collectProblem(problem));
        }

        return container;
    }

    private JSONObject collectProblem(Pair<List<Formula>, Boolean> problem) {
        JSONObject container = new JSONObject();

        container.put("consistent", problem.second() ? 0 : 1);

        {
            JSONArray outer_matrix = new JSONArray();

            for(Formula formula : problem.first()) {
                outer_matrix.put(collectFormula((Or) formula));
            }

            JSONArray empty_inner_matrix = emptyClause();

            for(int i = 0; i < params.clauses.max - problem.first().size(); i++) {
                outer_matrix.put(empty_inner_matrix);
            }

            container.put("formulae", outer_matrix);
        }

        return container;
    }

    private JSONArray collectFormula(Or formula) {
        JSONArray inner_matrix = new JSONArray();

        JSONArray title_row = new JSONArray();
        for(Formula generic_form : formula.getArguments()) {
            Predicate predicate;
            int value = 1;

            if(generic_form instanceof Not) {
                predicate = (Predicate) ((Not) generic_form).getArgument();
                value = -1;
            } else if(generic_form instanceof Predicate) {
                predicate = (Predicate) generic_form;
            } else {
                throw new IllegalArgumentException("Subformula from PredicateProblemGenerator was invalid.");
            }

            if(predicate.getName() != "=") {
                value *= predicateSpace.nameToIndex(predicate.getName());
            }

            title_row.put(value);
        }

        inner_matrix.put(title_row);

        for(int i = 0; i < params.predicateArguments.max; i++) {
            JSONArray row = new JSONArray();

            for(Formula generic_form : formula.getArguments()) {
                Predicate predicate;

                if(generic_form instanceof Not) {
                    predicate = (Predicate) ((Not) generic_form).getArgument();
                } else {
                    predicate = (Predicate) generic_form;
                }

                if(predicate.getArguments().length <= i) {
                    row.put(0);

                    continue;
                }

                row.put(constantSpace.nameToIndex(predicate.getArguments()[i].getName()));
            }

            inner_matrix.put(row);
        }

        return inner_matrix;
    }

    private JSONArray emptyClause() {
        JSONArray inner_matrix = new JSONArray();

        for(int i = 0; i < params.predicateArguments.max + 1; i++) {
            JSONArray row = new JSONArray();

            for(int j = 0; j < params.clauseWidth; j++) {
                row.put(0);
            }

            inner_matrix.put(row);
        }

        return inner_matrix;
    }

    @Override
    public void readFromReader(Reader reader) {
        throw new NotImplementedException("Problem set parsing not implemented.");
    }
}
