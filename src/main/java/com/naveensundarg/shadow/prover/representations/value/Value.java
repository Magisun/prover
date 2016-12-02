package com.naveensundarg.shadow.prover.representations.value;

import com.naveensundarg.shadow.prover.representations.Expression;

import java.util.Map;
import java.util.Set;

/**
 * Created by naveensundarg on 4/11/16.
 */
public abstract class Value extends Expression  implements  Comparable{

    protected String name;


    public  String getName(){
        return name;
    }
    public abstract int arity();
    public abstract Value[] getArguments();

    public abstract boolean isVariable();
    public abstract boolean isConstant();
    public abstract boolean isCompound();

    public boolean occurs(Variable x){
        return false;
    }

    public abstract Set<Variable> variablesPresent();

    public abstract Value apply(Map<Variable, Value> substitution);
    public abstract Value replace(Value value1, Value value2);

    public abstract Set<Value> subValues();

 }
