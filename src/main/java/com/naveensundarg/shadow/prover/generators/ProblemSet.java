package com.naveensundarg.shadow.prover.generators;

import java.io.Reader;
import java.io.Writer;

public interface ProblemSet {

    void writeToWriter(Writer writer, GeneratorParams params);

    void readFromReader(Reader reader);
}
