package com.naveensundarg.shadow.prover.generators;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class GeneratorParams {

    public int maxAtoms;
    public int maxLiteralsInClause;
    // TODO: Make into a range
    public int clauses;


    public GeneratorParams copy() {
        GeneratorParams params = new GeneratorParams();

        copyTo(params);

        return params;
    }

    protected void copyTo(GeneratorParams other) {
        other.maxAtoms = this.maxAtoms;
        other.maxLiteralsInClause = this.maxLiteralsInClause;
        other.clauses = this.clauses;
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

