package com.ipsim.processor.neander3.cpu;
import com.ipsim.processor.neander3.assembler.*;
import com.ipsim.processor.neander3.assembler.NeanderLexicalAnalyzer3.Token;

import com.ipsim.interfaces.Processor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.ipsim.exceptions.CodeGenerationException;
import com.ipsim.exceptions.LexicalException;
import com.ipsim.exceptions.SemanticException;
import com.ipsim.exceptions.SyntacticException;

import com.ipsim.interfaces.DataPath;

public class NeanderProcessor3 extends Processor {

    public NeanderControlpath3 controlpath;
    public NeanderDatapath3 datapath;
    public NeanderProcessor3() {
        controlpath = new NeanderControlpath3();
        datapath = new NeanderDatapath3();
    }

    public String getName() {
        return "NeanderProcessor";
    }
    public List<String> binaryParser(String binaryInstruction) {
        String opcode = binaryInstruction.substring(0, 4);

        List<String> result = new ArrayList<>();
        result.add(opcode);
        return result;
    }

    @Override
    public String compile(File file) throws IOException, LexicalException, SyntacticException, SemanticException, CodeGenerationException {

        
        String input = new String(Files.readAllBytes(file.toPath()));
        // Lexical analysis
        NeanderLexicalAnalyzer3 lexicalAnalyzer = new NeanderLexicalAnalyzer3();
        List<Token> tokens = lexicalAnalyzer.analyze(input);

        // Syntactic analysis
        NeanderSyntacticAnalyzer3 syntacticAnalyzer = new NeanderSyntacticAnalyzer3();
        syntacticAnalyzer.analyze(tokens);

        // Semantic analysis
        NeanderSemanticAnalyzer3 semanticAnalyzer = new NeanderSemanticAnalyzer3(syntacticAnalyzer.getSymbolTable());
        semanticAnalyzer.analyze(tokens);

        // Code generation
        NeanderCodeGenerator3 codeGenerator = new NeanderCodeGenerator3(semanticAnalyzer.getSymbolTable());
        String binaryCode = codeGenerator.generate(tokens);
        System.out.println("Generated binary code: " + binaryCode);
        datapath.loadProgram(binaryCode);
        return binaryCode;
    }
    
    /**
     * The getDatapath method returns the datapath
     * @return DataPath
     */
    public DataPath getDatapath() {
        return datapath;
    }
    /**
     * The executeProgram method executes the program
     */
    public void executeProgram() {
        // Reset the datapath
        datapath.init();
        // Execute the program
        while (!datapath.isProgramEnd()) {
            int programCounter = datapath.getProgramCounter();
            int instruction = datapath.getMemory().read(programCounter);
            String binaryInstruction = String.format("%8s", Integer.toBinaryString(instruction)).replace(' ', '0');
            String operation = controlpath.returnOperation(binaryInstruction.substring(0, 4));
            List<Object> arguments = new ArrayList<>();
    
            // Check if the operation has arguments
            if (operation.equals("STA") || operation.equals("LDA") || operation.equals("ADD") ||
                operation.equals("OR") || operation.equals("AND") || operation.equals("JMP") ||
                operation.equals("JN") || operation.equals("JZ")) {
                // Extract the address from the instruction
                int address = datapath.getMemory().read(programCounter + 1);
                arguments.add(address);
                // Increment the program counter
                datapath.incrementProgramCounter();
            }else {
                arguments.add(0);
            }
            // Execute the operation
            datapath.execute(operation, arguments);
            // If the operation is not a jump instruction, increment the program counter
            if(!operation.equals("JMP") && !operation.equals("JN") && !operation.equals("JZ")) {
                datapath.incrementProgramCounter();
            }
        }
    }    
}
