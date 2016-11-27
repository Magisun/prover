package com.naveensundarg.shadow.prover.representations.formula;

import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Created by naveensundarg on 11/24/16.
 */
public class Ought extends Formula{


    Value agent;
    Value time;
    Formula precondition;
    Formula ought;

    Set<Formula> subFormulae;
    Set<Variable> variables;


    public Ought(Value agent, Value time, Formula formula, Formula ought) {


        this.agent = agent;
        this.time = time;
        this.precondition = formula;
        this.subFormulae = CollectionUtils.setFrom(formula.subFormulae());
        this.subFormulae.add(this);
        this.ought = ought;

        this.variables = CollectionUtils.setFrom(formula.variablesPresent());
        if (agent instanceof Variable) {
            variables.add((Variable) agent);
        }

        if (time instanceof Variable) {
            variables.add((Variable) time);

        }
    }

    public Formula getPrecondition(){
        return precondition;
    }


    public Formula getOught() {
        return ought;
    }

    public Value getAgent() {
        return agent;
    }

    public Value getTime() {
        return time;
    }

    public Set<Formula> getSubFormulae() {
        return subFormulae;
    }

    public Set<Variable> getVariables() {
        return variables;
    }

    @Override
    public Set<Formula> subFormulae() {
        return subFormulae;
    }

    @Override
    public Set<Variable> variablesPresent() {
        return variables;
    }

    @Override
    public Formula apply(Map<Variable, Value> substitution) {
        return null;
    }

    @Override
    public Formula shadow(int level) {
        return new Atom("#"+toString()+"#");
    }

    @Override
    public Formula applyOperation(UnaryOperator<Formula> operator) {
        return null;
    }

    @Override
    public int getLevel() {
        return 2;
    }


    @Override
    public String toString() {
        return "(Ought! "
                + agent + " "
                + time + " "+
                precondition + " " +
                 ought + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Belief belief = (Belief) o;

        if (!agent.equals(belief.agent)) return false;
        if (!time.equals(belief.time)) return false;
        return precondition.equals(belief.formula);

    }

    @Override
    public int hashCode() {
        int result = agent.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + precondition.hashCode();
        return result;
    }
}
