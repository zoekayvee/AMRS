import java.util.*;
import java.io.*;

public class Computer {

	private Register[] register;
	private Flag ZF;
	private Flag NF;
	private Flag UF;
	private Flag OF;

	public Computer() {
		register = new Register[33];
		ZF = new Flag();
		NF = new Flag();
		UF = new Flag();
		OF = new Flag();

		// initialize registers
		for (int i = 1; i<=32; i++) {
			register[i] = new Register(i);
		}
	}

	// Method to print register values
	public void printRegisters(){
		for (int i = 1; i<=32; i++) {
			System.out.print("R" + i + ": " + register[i].getValue() + "\t");
			if(i%8==0 && i!=0){
				System.out.println();
			}
		}
	}

	// Stage of decode, retrieves values and places in a Vector
	public Vector Decode(Instruction instruction) {
		Vector decode = new Vector();
		decode = instruction.getInstructionVector();

		System.out.println(decode);

		String source = (String) decode.get(1);
		String sourceReg = (String) source.substring(1);
		int sourceRegNo = Integer.parseInt(sourceReg);
		register[1].getValue();
		decode.set(1, register[sourceRegNo].getValue());
		instruction.setDestination(sourceRegNo);

		// Checks if source is not an integer
		if (decode.get(2) instanceof Integer == false) {
			// Identifies source's register
			source = (String) decode.get(2);
			sourceReg = (String) source.substring(1);
			sourceRegNo = Integer.parseInt(sourceReg);
			decode.set(2, register[sourceRegNo].getValue());
		}

		return decode;
	}

	// Calculates a value based on the opcode
	public int Execute(Instruction instruction) {
		int value = 0;
		switch ((String) instruction.getOpcode()) {
				case "LOAD" :	value = load(instruction.getDecoded());
								break;				
				case "ADD" 	:	value = add(instruction.getDecoded());
								break;
				case "SUB" 	:	value = sub(instruction.getDecoded());
								break;
				case "CMP" 	: 	value = cmp(instruction.getDecoded());
								break;
		}

		// Checks for over/underflows
		if (value > 99) {
			value = 99;
			OF.setValue(true);
		} else if (value < -99) {
			value = -99;
			UF.setValue(true);
		}

		return value;
	}

	public int load(Vector decoded) {
		return (Integer) decoded.get(2);
	}

	public int add(Vector decoded) {
		return (Integer) decoded.get(1) + (Integer) decoded.get(2);
	}

	public int sub(Vector decoded) {
		return (Integer) decoded.get(1) - (Integer) decoded.get(2);
	}

	public int cmp(Vector decoded) {
		int difference = (int) decoded.get(1) - (int) decoded.get(2);
		
		if (difference == 0) {
			ZF.setValue(true);
		} else if (difference < 0) {
			NF.setValue(true);
		}

		return 0;
	}

	public boolean Memory(Instruction instruction) {
		return true;
	}

	// Places executed value in designated register (unless CMP Opcode)
	public void Writeback(Instruction instruction) {
		if ((String) instruction.getOpcode() != "CMP") {
			register[instruction.destination].setValue(instruction.executed);
		}
	}

}