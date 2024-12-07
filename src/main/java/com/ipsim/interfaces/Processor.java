package com.ipsim.interfaces;
import java.util.List;
import java.io.File;
import java.io.IOException;
import com.ipsim.processor.neander.assembler.LexicalAnalyzer.LexicalException;
import com.ipsim.processor.neander.assembler.SyntacticAnalyzer.SyntacticException;
import com.ipsim.processor.neander.assembler.SemanticAnalyzer.SemanticException;
import com.ipsim.processor.neander.assembler.CodeGenerator.CodeGenerationException;

public abstract class Processor {
    
    DataPath datapath = null;
    ControlPath controlpath = null;
    public abstract void run();
    public abstract void getOperation();
    public abstract List<String> binaryParser(String binaryInstruction);
    public abstract String getName();
    public abstract String compile(File file) throws IOException, LexicalException, SyntacticException, SemanticException, CodeGenerationException;
    public abstract DataPath getDatapath();
    public abstract void executeProgram();
}
