package com.ipsim.processor.neander.cpu;

import com.ipsim.processor.neander.assembler.*;
import com.ipsim.processor.neander.assembler.LexicalAnalyzer.Token;
import com.ipsim.processor.neander.assembler.SyntacticAnalyzer.SyntacticException;
import com.ipsim.interfaces.Processor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.ipsim.processor.neander.assembler.LexicalAnalyzer.LexicalException;
import com.ipsim.processor.neander.assembler.SemanticAnalyzer.SemanticException;
import com.ipsim.processor.neander.assembler.CodeGenerator.CodeGenerationException;
import com.ipsim.interfaces.DataPath;

public class NeanderProcessor extends Processor {
    @Override
    public void getOperation() {
        // Implementation of the inherited abstract method
    }
    public NeanderControlpath controlpath;
    public NeanderDatapath datapath;
    public NeanderProcessor() {
        controlpath = new NeanderControlpath();
        datapath = new NeanderDatapath();
    }

    public void run() {
        System.out.println("Running Neander Processor");
    }
    public void getOperation(String binaryInstruction) {
        System.out.println("Operation: " + controlpath.returnOperation(binaryInstruction));
    }

    @Override
    public String getName() {
        return "NeanderProcessor";
    }

    public List<String> binaryParser(String binaryInstruction) {
        // Extrai o opcode (primeiros 4 bits)
        String opcode = binaryInstruction.substring(0, 4);

        // Cria uma lista para retornar a substring
        List<String> result = new ArrayList<>();
        result.add(opcode);
        return result;
    }

    public void readAssembly(File assemblyFile) {
        System.out.println("Reading assembly instruction: " + assemblyFile);
    }
    @Override
    public String compile(File file) throws IOException, LexicalException, SyntacticException, SemanticException, CodeGenerationException {

        
        String input = new String(Files.readAllBytes(file.toPath()));
        // Análise léxica
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        List<Token> tokens = lexicalAnalyzer.analyze(input);

        // Análise sintática
        SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer();
        syntacticAnalyzer.analyze(tokens);
        System.out.println("Syntactic analysis passed.");

        // Análise semântica
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(tokens);
        System.out.println("Semantic analysis passed.");

        // Geração de código
        CodeGenerator codeGenerator = new CodeGenerator(semanticAnalyzer.getSymbolTable());
        String binaryCode = codeGenerator.generate(tokens);
        System.out.println("Generated binary code: " + binaryCode);
        datapath.loadProgram(binaryCode);
        return binaryCode;
    }
    public DataPath getDatapath() {
        return datapath;
    }
    public void executeProgram() {
        // Resetando o datapath antes da execução
        datapath.init();
        while (!datapath.isProgramEnd()) {
            int programCounter = datapath.getProgramCounter();
            int instruction = datapath.getMemory().read(programCounter);
            String binaryInstruction = String.format("%8s", Integer.toBinaryString(instruction)).replace(' ', '0');
            String operation = controlpath.returnOperation(binaryInstruction.substring(0, 4));
            List<Object> arguments = new ArrayList<>();
    
            if (operation.equals("STA") || operation.equals("LDA") || operation.equals("ADD") ||
                operation.equals("OR") || operation.equals("AND") || operation.equals("JMP") ||
                operation.equals("JN") || operation.equals("JZ")) {
                int address = datapath.getMemory().read(programCounter + 1);
                arguments.add(address);
                datapath.incrementProgramCounter();
            }else {
                arguments.add(0);
            }
    
            datapath.execute(operation, arguments);
            if(!operation.equals("JMP") && !operation.equals("JN") && !operation.equals("JZ")) {
                datapath.incrementProgramCounter();
            }
        }
    }    
}
