package com.naveensundarg.shadow.prover.generators;

import com.naveensundarg.shadow.prover.utils.CollectionUtils;

import java.util.Map;

public class NameSpace {

    private String prefix;
    private int curIndex;
    private Map<String, Integer> nameMap;


    public NameSpace(String prefix) {
        this.prefix = prefix;
        this.curIndex = 1;
        this.nameMap = CollectionUtils.newMap();
    }

    public NameSpace(String prefix, int startAt) {
        this(prefix);

        this.curIndex = startAt;
    }


    public String getNextName() {
        StringBuilder builder = new StringBuilder();

        builder.append(prefix);
        builder.append(Integer.toString(this.curIndex));

        String name = builder.toString();
        nameMap.put(name, this.curIndex++);

        return name;
    }

    public Integer nameToIndex(String name) {
        if(!name.startsWith(prefix)) {
            throw new IllegalArgumentException("Specified string does not start with this" +
                    " NameSpace's prefix (" + prefix + ")");
        }

        return nameMap.get(name);
    }

    public String indexToName(Integer index) {
        return prefix + index;
    }
}
