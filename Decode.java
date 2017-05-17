import java.util.*;
import java.io.*;

public class Decode {
	Vector decoded;

	public Decode (Instruction instruction) {
		this.decoded = instruction.instr;

		String source = "" + decoded.get(1);
		// System.out.println("----" + source);
		String sourceReg = "" + source.charAt(1);
		int sourceRegNo = Integer.parseInt(sourceReg);
		decoded.set(1, Main.r[sourceRegNo].getValue());

		// Checks if source is not an integer
		if (decoded.get(2) instanceof Integer == false) {
			// Identifies source's register
			source = (String) decoded.get(2);
			sourceReg = (String) source.substring(1);
			sourceRegNo = Integer.parseInt(sourceReg);
			decoded.set(2, Main.r[sourceRegNo].getValue());
		}		
	}

	public Vector getDecoded() {
		return this.decoded;
	}
}
