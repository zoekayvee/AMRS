import java.util.*;
import java.io.*;

public class Decode {
	Instruction decoded;

	public Decode (Instruction instruction) {
		this.decoded = instruction;

		// Checks if source is not an integer
		if (decoded.getOperand2() instanceof Integer == false) {
			// Identifies source's register
			String source = (String) decoded.get(2);
			String sourceReg = (String) source.substring(1);
			int sourceRegNo = Integer.parseInt(sourceReg);
			decoded.setOperand2(r[sourceRegNo].getValue());
		}		
	}

	public Instruction getDecoded() {
		return this.decoded;
	}
}
