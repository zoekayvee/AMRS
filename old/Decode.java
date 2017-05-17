import java.util.*;
import java.io.*;

public class Decode {
	Vector decoded;

	public Decode (Instruction instruction) {
		this.decoded = instruction;

		// Checks if source is not an integer
		if (decoded.getOperand2() instanceof Integer == false) {
			// Identifies source's register
			String source = (String) decoded.getOperand2();
			String sourceReg = (String) source.substring(1);
			int sourceRegNo = Integer.parseInt(sourceReg);
			decoded.setOperand2(Main.r[sourceRegNo].getValue());
		}		
	}

	public Instruction getDecoded() {
		return this.decoded;
	}
}
