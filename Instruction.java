import java.util.*;
import java.io.*;

public class Instruction {
	int id;
	Vector instr;  			//['load' R1, 5]
	Vector decoded; 		//['load' 0, 5]
	int executed; 			//5
	int memory;
	int destination;
	// writeback						
	ArrayList<String> pipe = new ArrayList<String>();

	int stage;
	int stalled;
	boolean isStalled;
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
		this.id = Main.counter + 1;
		this.instr = instr;
		this.stage = 0; // initialize to fetch
		this.stalled = -1;
		this.isStalled = false;
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
		// if previously not stalled
		if(!this.isStalled){
			this.isStalled = true;
			this.stalled = this.stage;
			this.stage = -1;
		}
		// else retain stall state
	}

	public void restore(){
		this.stage = this.stalled + 1;
		this.isStalled = false;
		this.stalled = -1;
	}

	public int getStalled(){
		return this.stalled;
	}

	public boolean checkDependencies(){
		boolean ready = true;
		boolean flag;

		for(Vector dependency : Main.hazards){
			System.out.println(dependency);
			System.out.println("ID: " + this.id);

			flag = false;
			if((int) dependency.get(0) == this.id){
				// check if the dependency is still in the process queue

				System.out.println("-------------");
				for(Instruction instr : Main.processQueue){
					System.out.print(instr.instr);
					System.out.print(" " + instr.id + " " + this.id);
					System.out.println();
					if(instr.id != this.id && instr.id == (int) dependency.get(1)){
						System.out.println("They equal");
						ready = false;
						flag = true;
						break;
					}
				}
				System.out.println("-------------");

				if(flag) break;
			}

			if((int) dependency.get(1) == this.id){
				// check if the dependency is still in the process queue

				System.out.println("-------------");
				for(Instruction instr : Main.processQueue){
					if(instr.id != this.id && instr.id == (int) dependency.get(0)){
						System.out.println("They equal");
						ready = false;
						flag = true;
						break;
					}
				}
				System.out.println("-------------");

				if(flag) break;
			}
		}

		return ready;
	}



}