package com.ipsim.interfaces;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.ipsim.exceptions.CodeGenerationException;
import com.ipsim.exceptions.LexicalException;
import com.ipsim.exceptions.SemanticException;
import com.ipsim.exceptions.SyntacticException;

public abstract class Processor {
    
    private DataPath datapath = null;
    private ControlPath controlpath = null;
    public abstract List<String> binaryParser(String binaryInstruction);
    public abstract String compile(File file) throws IOException, LexicalException, SyntacticException, SemanticException, CodeGenerationException;
    public abstract void executeProgram();
    public abstract void executeStep();
    public abstract String getName();
    public void setDatapath(DataPath datapath) {
        this.datapath = datapath;
    }
    public void setControlpath(ControlPath controlpath) {
        this.controlpath = controlpath;
    }
    public DataPath getDatapath() {
        return datapath;
    }
    public ControlPath getControlpath() {
        return controlpath;
    }
}
