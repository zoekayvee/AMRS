import java.util.*;
import java.io.*;

public class Instruction {
	Vector instr;  			//['load' R1, 5]
	Vector decoded; 		//['load' 0, 5]
	int executed; 			//5
	// memory
	// writeback						

	int stage;
	int stalled;
	Object opcode;
	Object operand1;
	Object operand2;

	/*
		Stages:
		-1 - Stall
		0 - Fetch
		1 - Decode
		2 - Execute
		3 - Memory
		4 - Writeback
	*/
		
	public Instruction(Vector instr) {
		this.instr = instr;
		this.stage = 0; // initialize to fetch
		this.stalled = -1;
		this.initialize();
	}

	public void initialize(){
		this.opcode = this.instr.get(0);
		this.operand1 = this.instr.get(1);
		this.operand2 = this.instr.get(2);
	} 

	public Object getOpcode(){
		return this.opcode;
	}

	public Vector getInstructionVector(){
		return this.instr;
	}

	public void setOperand2(Object operand2) {
		this.operand2 = operand2;
	}

	public Object getOperand2(){
		return this.operand2;
	}

	public void setOperand1(Object operand1) {
		this.operand1 = operand1;
	}

	public Object getOperand1(){
		return this.operand1;
	}

	public void nextStage() {
		this.stage++;
	}

	public int getStage() {
		return this.stage;
	}

	public void stall(){
		this.stalled = this.stage;
		this.stage = -1;
	}

	public void restore(){
		this.stage = this.stalled + 1;
		this.stalled = -1;
	}

	public int getStalled(){
		return this.stalled;
	}
}