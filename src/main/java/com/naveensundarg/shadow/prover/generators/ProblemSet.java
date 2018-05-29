package com.naveensundarg.shadow.prover.generators;

import java.io.Reader;
import java.io.Writer;

public interface ProblemSet {

    void writeToWriter(Writer writer);

    void readFromReader(Reader reader);
}
