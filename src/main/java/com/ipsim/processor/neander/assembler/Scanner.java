/*package assembler.neanderAsm;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import assembler.neanderAsm.Token.TokenType;
public class Scanner {
    private char[] content;
    private int    state;
    private int    pos;
    public Scanner(String fileName) {

        try{

            String fileContent = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            System.out.println("DEBUG");
            System.out.println(fileContent);
            content = fileContent.toCharArray();
            pos=0;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Token nextToken() {
        char currentChar;
        if(isEOF()) {
            return null;
        }
        state=0;
        while(true) {
            switch (state) {
                case 0:
                    currentChar = nextChar();
                    if(isChar(currentChar)) {
                        state = 1;   
                    }else if(isDigit(currentChar)) {
                        state = 3;
                    }else if(isSpace(currentChar)) {
                        state = 0;
                    }
                    break;
                case 1:
                    if(isChar(currentChar) || isDigit(currentChar)) {
                        state = 1;

                    }else {
                        state = 2;
                    }
                    break;
                case 2:
                    Token token = new Token(Token.TokenType., )

            
                default:
                    break;
            }
        }
    }
    private Boolean isDigit(char c) {

        return c>='0' && c<='9';
    }
    private Boolean isChar(char c) {

        return (c>= 'a' && c<='z')||(c>='A' && c<='Z');
    }
    private Boolean isSpace(char c) {

        return c==' ' || c=='\t' || c=='\n'|| c=='\r'; 
    }
    private char nextChar() {

        return content[pos++];
    }

    private void back() {
        pos--;

    }
    private Boolean isEOF() {
        return pos == content.length;
    }
}
*/