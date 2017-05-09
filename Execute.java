import java.util.*;
import java.io.*;

public class Execute {
	private static Register[] r;

	public Execute (Register[] r) {
		r = this.r;
	}

	public static void load(Vector instruction) {
		System.out.println("LOAD");
		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);
		
		// Checks if source is already an integer
		if (instruction.get(2) instanceof Integer) {
			r[destReg].setValue((int) instruction.get(2));
		} else {
			// Identifies source's register
			String source = (String) instruction.get(2);
			String sourceReg = (String) source.substring(1);
			int sourceRegNo = Integer.parseInt(sourceReg);
			Register sourceR = r[sourceRegNo];
			r[destReg].setValue(sourceR.getValue());
		}

		System.out.printf("R[%d]: %d\n", destReg, r[destReg].getValue());

	}

	public static void add(Vector instruction) {
		System.out.println("ADD");
		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);
		
		int addendA;
		int addendB;
		
		// Checks if source is an integer
		if (instruction.get(2) instanceof Integer) {
			addendA = (int) instruction.get(2);
		} else {
			// Identifies source's register
			String source = (String) instruction.get(2);
			String sourceReg = (String) source.substring(1);
			int sourceRegNo = Integer.parseInt(sourceReg);
			addendA = r[sourceRegNo].getValue();
		}

		int sum = r[destReg].getValue() + addendA;

		if (sum > 99) {
			sum = 99;
			OF.setValue(true);
		} else if (sum < -99) {
			sum = -99;
			UF.setValue(true);
		}

		r[destReg].setValue(sum);

		System.out.printf("R[%d]: %d\n", destReg, r[destReg].getValue());	
	
	}

	public static void sub(Vector instruction) {
		int subtrahend;

		System.out.println("SUB");
		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);
		
		// Checks if source is already an integer
		if (instruction.get(2) instanceof Integer) {
			subtrahend = (int) instruction.get(2);
		} else {
			// Identifies source's register
			String source = (String) instruction.get(2);
			String sourceReg = (String) source.substring(1);
			int sourceRegNo = Integer.parseInt(sourceReg);
			subtrahend = r[sourceRegNo].getValue();
		}

		int difference = r[destReg].getValue() - subtrahend;
		
		if (difference > 99) {
			difference = 99;
			OF.setValue(true);
		} else if (difference < -99) {
			difference = -99;
			UF.setValue(true);
		}

		r[destReg].setValue(difference);

		System.out.printf("R[%d]: %d\n", destReg, r[destReg].getValue());
	
	}

	public static void cmp(Vector instruction) {
		System.out.println("CMP");

		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);
		
		int minuend;
		int subtrahend;
		
		// Checks if source is already an integer
		if (instruction.get(1) instanceof Integer) {
			minuend = (int) instruction.get(1);
		} else {
			// Identifies source's register
			String source = (String) instruction.get(1);
			String sourceReg = (String) source.substring(1);
			int sourceRegNo = Integer.parseInt(sourceReg);
			minuend = r[sourceRegNo].getValue();
		}

		if (instruction.get(2) instanceof Integer) {
			subtrahend = (int) instruction.get(2);
		} else {
			// Identifies source's register
			String source = (String) instruction.get(2);
			String sourceReg = (String) source.substring(1);
			int sourceRegNo = Integer.parseInt(sourceReg);
			subtrahend = r[sourceRegNo].getValue();
		}

		int difference = minuend - subtrahend;
		
		if (difference == 0) {
			ZF.setValue(true);
		} else if (difference < 0) {
			NF.setValue(true);
		}

		System.out.println("Zero Flag: " + ZF.getValue());
		System.out.println("Negative Flag: " + NF.getValue());

	}
}