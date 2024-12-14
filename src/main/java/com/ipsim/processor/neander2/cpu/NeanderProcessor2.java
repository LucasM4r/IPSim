package com.ipsim.processor.neander2.cpu;
import com.ipsim.processor.neander2.assembler.*;
import com.ipsim.processor.neander2.assembler.NeanderLexicalAnalyzer2.Token;

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

public class NeanderProcessor2 extends Processor {

    public NeanderControlpath2 controlpath;
    public NeanderDatapath2 datapath;
    public NeanderProcessor2() {
        controlpath = new NeanderControlpath2();
        datapath = new NeanderDatapath2();
    }

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

    @Override
    public String compile(File file) throws IOException, LexicalException, SyntacticException, SemanticException, CodeGenerationException {

        
        String input = new String(Files.readAllBytes(file.toPath()));
        // Lexical analysis
        NeanderLexicalAnalyzer2 lexicalAnalyzer = new NeanderLexicalAnalyzer2();
        List<Token> tokens = lexicalAnalyzer.analyze(input);

        // Syntactic analysis
        NeanderSyntacticAnalyzer2 syntacticAnalyzer = new NeanderSyntacticAnalyzer2();
        syntacticAnalyzer.analyze(tokens);

        // Semantic analysis
        NeanderSemanticAnalyzer2 semanticAnalyzer = new NeanderSemanticAnalyzer2(syntacticAnalyzer.getSymbolTable());
        semanticAnalyzer.analyze(tokens);

        // Code generation
        NeanderCodeGenerator2 codeGenerator = new NeanderCodeGenerator2(semanticAnalyzer.getSymbolTable());
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
