import java.util.*;
import java.io.*;

public class Decode {

	public Decode() {
		
	}

	public Decode (Instruction instruction) {

		// Checks if source is already an integer
		if (instruction.get(2) instanceof Integer) {
			return instruction;
		} else {
			// Identifies source's register
			String source = (String) instruction.get(2);
			String sourceReg = (String) source.substring(1);
			int sourceRegNo = Integer.parseInt(sourceReg);
			instruction.setOperand2(r[sourceRegNo].getValue());
		}

		return instruction;		
	}
}