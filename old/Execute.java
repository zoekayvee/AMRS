import java.util.*;
import java.io.*;

public class Execute {
	static Flag ZF = new Flag();
	static Flag NF = new Flag();
	static Flag UF = new Flag();
	static Flag OF = new Flag();

	public Execute (Instruction instruction) {
		switch ((String) instruction.getOpcode()) {
				case "LOAD" :	load(instruction.getInstructionVector());
								break;
				case "ADD" 	:	add(instruction.getInstructionVector());
								break;
				case "SUB" 	:	sub(instruction.getInstructionVector());
								break;
				case "CMP" 	: 	cmp(instruction.getInstructionVector());
								break;
		}
	}

	public static void load(Vector instruction) {
		System.out.println("LOAD");
		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);
		
		Main.r[destReg].setValue((int) instruction.get(2));

		System.out.printf("R[%d]: %d\n", destReg, Main.r[destReg].getValue());
	}

	public static void add(Vector instruction) {
		System.out.println("ADD");
		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);
		
		int addendA = (int) instruction.get(2);

		int sum = Main.r[destReg].getValue() + addendA;

		if (sum > 99) {
			sum = 99;
			OF.setValue(true);
		} else if (sum < -99) {
			sum = -99;
			UF.setValue(true);
		}

		Main.r[destReg].setValue(sum);

		System.out.printf("R[%d]: %d\n", destReg, Main.r[destReg].getValue());		

	}

	public static void sub(Vector instruction) {
		System.out.println("SUB");
		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);
		
		int subtrahend = (int) instruction.get(2);

		int difference = Main.r[destReg].getValue() - subtrahend;
		
		if (difference > 99) {
			difference = 99;
			OF.setValue(true);
		} else if (difference < -99) {
			difference = -99;
			UF.setValue(true);
		}

		Main.r[destReg].setValue(difference);

		System.out.printf("R[%d]: %d\n", destReg, Main.r[destReg].getValue());
	
	}

	public static void cmp(Vector instruction) {
		System.out.println("CMP");

		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);
		
		int subtrahend = (int) instruction.get(2);

		int difference = Main.r[destReg].getValue() - subtrahend;
		
		if (difference == 0) {
			ZF.setValue(true);
		} else if (difference < 0) {
			NF.setValue(true);
		}

		System.out.println("Zero Flag: " + ZF.getValue());
		System.out.println("Negative Flag: " + NF.getValue());

	}
}
