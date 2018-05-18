package com.naveensundarg.shadow.prover.generators;

import com.naveensundarg.shadow.prover.utils.CollectionUtils;

import java.util.Map;

public class NameSpace {

    private String prefix;
    private int cur_index;
    private Map<String, Integer> name_map;


    public NameSpace(String prefix) {
        this.prefix = prefix;
        this.cur_index = 1;
        this.name_map = CollectionUtils.newMap();
    }


    public String getNextName() {
        StringBuilder builder = new StringBuilder();

        builder.append(prefix);
        builder.append(Integer.toString(this.cur_index));

        String name = builder.toString();
        name_map.put(name, this.cur_index++);

        return name;
    }

    public Integer getNameIndex(String name) {
        return name_map.get(name);
    }
}
