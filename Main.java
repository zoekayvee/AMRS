import java.util.*;
import java.io.*;

public class Main{
	static Register[] r = new Register[32];
	static Execute execute = new Execute(r);
	static Flag ZF = new Flag();
	static Flag NF = new Flag();
	static Flag UF = new Flag();
	static Flag OF = new Flag();
	
	public static void main(String[] args) {
		LexicalAnalyzer parse = new LexicalAnalyzer();

		if (parse.error.getValue()) {
			System.exit(0);
		}
		
		for (int i = 0; i<32; i++) {
			r[i] = new Register(i);
		}

		for (Vector instruction : parse.instructions) {
			switch ((String) instruction.get(0)) {
				case "LOAD" :	load(instruction);
								break;
				case "ADD" 	:	add(instruction);
								break;
				case "SUB" 	:	sub(instruction);
								break;
				case "CMP" 	: 	cmp(instruction);
								break;
			}
		}
	}
}