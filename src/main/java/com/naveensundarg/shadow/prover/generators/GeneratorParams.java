package com.naveensundarg.shadow.prover.generators;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class GeneratorParams {

    /**
     * Specifies the number of atoms (0-arity predicates) in the pool.
     * <p>
     * This indirectly specifies the maximum number of atoms:
     * You cannot have more atoms in a clause than exist in the pool.
     * <p>
     * For example, specifying atoms as N will give the generator N atoms
     * to choose from when constructing clauses:
     * a_1, a_2, a_3, ..., a_n
     * <p>
     * These atoms make up the "atom pool," from which all atoms used in
     * clauses are drawn.
     */
    public int atoms;

    /**
     * Clause width, specified to be the maximum number of literals in a clause.
     */
    public int clauseWidth;

    /**
     * Specifies the minimum and maximum number of clauses in a given problem.
     */
    public Range clauses;


    public GeneratorParams() {
        this.atoms = 0;
        this.clauseWidth = 0;
        this.clauses = new Range(0, 0);
    }

    public GeneratorParams(int atoms, int clauseWidth, int minClauses, int maxClauses) {
        this.atoms = atoms;
        this.clauseWidth = clauseWidth;
        this.clauses = new Range(minClauses, maxClauses);
    }

    public GeneratorParams(GeneratorParams other) {
        this.atoms = other.atoms;
        this.clauseWidth = other.clauseWidth;
        this.clauses = other.clauses;
    }

    public JSONObject toJSON() {
        Field[] fields = this.getClass().getFields();
        String[] fieldNames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }

        return new JSONObject(this, fieldNames);
    }
}

