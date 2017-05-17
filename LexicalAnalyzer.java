import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
	boolean parsed;
	String file;
	int lineNum = 0;
	Flag OF = new Flag();
	Flag UF = new Flag();
	ArrayList<Vector> instructions = new ArrayList<Vector>();
	Flag error = new Flag(false);
	
	public LexicalAnalyzer(String fileinput) {
		try {
			this.file = fileinput;
			FileReader readCode = new FileReader(this.file);
			BufferedReader inputCode = new BufferedReader(readCode);		
			String line = null;
			String delims = (" ");


			// Retrieves the rest of the data
			while ((line = inputCode.readLine())!= null) {
				lineNum++;

				Boolean spaceFound = false;
				for(int i=0; i<line.length(); i++){
					if(!spaceFound){
						spaceFound = (line.charAt(i) == ' ') ? true : false;
					}else if(line.charAt(i) == ' '){
						System.out.printf("Error at Line %d\n", lineNum);
						error.toggle();
						break;
					}else{
						spaceFound = false;
					}
				}

				if (error.getValue() == true) {
					break;
				}

				String[] tokens = line.split(delims);
				Vector lexemes = new Vector();

				for (int i = 0; i < tokens.length; i++){
				// System.out.println("\"" + tokens[i] + "\"");
					lexemes.add(tokens[i]);
				}

				if (errorChecker(lexemes)){
					instructions.add(lexemes);
				} else {
					System.out.printf("Error at Line %d\n", lineNum);
					error.toggle();
					break;
				}

			}

			inputCode.close();
			parsed = true;
		}catch(Exception e) {
			parsed = false;
			System.out.println(e.getMessage());
		}

	}

	public Boolean errorChecker(Vector lexemes) {

		ArrayList<String> opcodes = new ArrayList<String>();
		opcodes.add("LOAD");
		opcodes.add("ADD");
		opcodes.add("SUB");
		opcodes.add("CMP");
		
		// OPCODE CHECKER
		if(!opcodes.contains(lexemes.get(0))){
			return false;
		}
		String firstArg = (String) lexemes.get(1);

		// FIRST ARGUMENT
		if(firstArg.charAt(0) == 'R'){
			//check if register within range
			String registerNum = firstArg.substring(1, firstArg.length()-1);
			int regNum = Integer.parseInt(registerNum);
			if(regNum > 32 || regNum == 0){
				return false;
			}else{
				// remove comma
				firstArg = "R" + registerNum;
				lexemes.set(1, firstArg);
			}
		}

		// REMAINING ARGUMENTS
		int i;
		for(i=2; i<lexemes.size(); i++){
			String intPattern = "(-)?([0-9]+)";
			String lexeme = (String) lexemes.get(i);

			// if middle argument
			if(i < lexemes.size()-1){
				int lastInd = lexeme.length()-1;
				if(lexeme.charAt(lastInd) == ','){
					// remove comma
					lexeme = lexeme.substring(0, lexeme.length()-1);
					lexemes.set(i, lexeme);
				}else{
					return false;
				}
			}

			// if register found
			if(lexeme.charAt(0) == 'R'){
				//check if register within range
				String registerNum = lexeme.substring(1);
				int regNum = Integer.parseInt(registerNum);
				if(regNum > 32 || regNum == 0){
					return false;
				}else{
					continue;
				}
			}

			// if immediate value
			else if(lexeme.matches(intPattern)){
				int immediate = Integer.parseInt(lexeme);
				
				if (immediate > 99) {
					immediate = 99;
					OF.setValue(true);
				} else if (immediate < -99){
					immediate = -99;
					UF.setValue(true);
				}

				lexemes.set(i, immediate);
				continue;
				
			}

			else return false;
		}

		return true;

		// check lexemes per line
		// look for errors
		// return false if error found
		// true otherwise

		/*
			Errors to consider:
			- Spelling
			- Order (ex. register be4 immediate)
			- Within range (Registers: 1-32, Numbers: -99 to 99)
			- if opcode is valid( Load, add sub, cmp)
			- look for comma if valid ba sya for the position ng token
		*/

		// TO-DO:
		// remove comma per lexeme
	}







}