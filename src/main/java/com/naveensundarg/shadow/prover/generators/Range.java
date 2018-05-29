package com.naveensundarg.shadow.prover.generators;

import org.json.JSONObject;

/**
 * A minimalist range class; this class is strictly for representing integer ranges.
 */
public class Range {

    public final int min;
    public final int max;


    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }


    public JSONObject toJSON() {
        JSONObject container = new JSONObject();

        container.put("min", min);
        container.put("max", max);

        return container;
    }
}
