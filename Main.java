import java.util.*;
import java.io.*;

public class Main{
	static Register[] r = new Register[32];
	// static Execute execute = new Execute(r);
	static Flag ZF = new Flag();
	static Flag NF = new Flag();
	static Flag UF = new Flag();
	static Flag OF = new Flag();
	static int instrSetSize;
	static int counter = 0;
	static int stalls = 0;
	static int instrDone = 0;
	static ArrayList<Instruction> processQueue = new ArrayList<Instruction>();

	static ArrayList<Vector> hazards = new ArrayList<Vector>();
	static ArrayList<Vector> dependencies = new ArrayList<Vector>();

	/*
		STATUS for pipeline
		-1 - Stall
		0 - Inactive
		1 - Active

	*/

	static int fetch = 0;
	static int decode = 0;
	static int execute = 0;
	static int memory = 0;
	static int writeback = 0;

	static ArrayList<Integer> fetchpipeline = new ArrayList<Integer>();
	static ArrayList<Integer> decodepipeline = new ArrayList<Integer>();
	static ArrayList<Integer> executepipeline = new ArrayList<Integer>();
	static ArrayList<Integer> memorypipeline = new ArrayList<Integer>();
	static ArrayList<Integer> writebackpipeline = new ArrayList<Integer>();
	
	public static void setDependencies(ArrayList<Vector> instructions){
		/*
			Vector contains 3 elements
			0 - instruction 1 ID (int)
			1 - instruction 2 ID (int)
			2 - type of hazard (string)

			Note: The id of instruction is its position in the instructions set

			Example of a vector
			< 1, 4, RAW >
		*/
		int i, j;

 		for (i=0; i<instructions.size; i++) {	
 			for (j=i+1; j<instructions.size; j++) {
				
 			
 				if(instructions.get(i).get(1).equals(instructions.get(j).get(2))){  //instrction1.operand1.equals(instruction2.operand2)
 					dependencies.add(i+1,j+1,"RAW");									  
 				}
 				else if(instructions.get(i).get(2).equals(instructions.get(j).get(1))){ //instrction1.operand2.equals(instruction2.operand1)
 					dependencies.add(i+1,j+1,"WAR");
 				}
 				else if (instructions.get(i).get(1).equals(instructions.get(j).get(1))){ //instrction1.operand1.equals(instruction2.operand1)
 					dependencies.add(i+1,j+1,"WAW");
 				}
 				else{

 				}
 			}
 		}

		// save all dependencies to ArrayList<Vector> dependencies 
		// ArrayList<Vector> dependencies is already declared as static variable
	}

	public static void main(String[] args) {
		// Parse input code
		LexicalAnalyzer parse = new LexicalAnalyzer();
		instrSetSize = parse.instructions.size();


		// Check for error flag, terminate if true
		if (parse.error.getValue()) {
			System.exit(0);
		}
		
		// initialize registers
		for (int i = 0; i<32; i++) {
			r[i] = new Register(i);
		}

		// Identify dependencies
		setDependencies(parse.instructions);

		// PIPELINING
		int clockcycle = 1;
		while(instrDone != instrSetSize){

			// Pipeline empty
			if(processQueue.isEmpty()){
				// Perform FETCH only
				Instruction instr = new Instruction(parse.instructions.get(counter));
				processQueue.add(instr);
				fetch = 1;
				counter++;			
			// Pipeline not empty
			}else{
				int fcount = 0;
				int dcount = 0;
				int ecount = 0;
				int mcount = 0;
				int wcount = 0;
				int scount = 0;

				// count waiting instr per stages
				for(Instruction instr : processQueue){
					switch(instr.getStage()){
						case -1:
							if(instr.getStalled() == 0) dcount++;
							else if(instr.getStalled() == 1) ecount++;
							else if(instr.getStalled() == 2) mcount++;
							else if(instr.getStalled() == 3) wcount++;
							break;
						case 0:
							dcount++;
							break;
						case 1:
							ecount++;
							break;
						case 2:
							mcount++;
							break;
						case 3:
							wcount++;
							break;
					}
				}

				// FETCH an instruction
				if(counter != instrSetSize){
					Instruction instr = new Instruction(parse.instructions.get(counter));

					// update count
					processQueue.add(instr);
					fetch = 1;
					counter++;
				}

				// DECODE
				if(dcount != 0){
					int index = 0;
					for(Instruction instr : processQueue){
						// look for a stalled instruction waiting for decode
						if(instr.isStalled && instr.getStalled() == 0){

							if(instr.checkDependencies()){
								instr.stall();
								decode = -1;
							}else{
								// Decode instruction
								Decode decoder = new Decode(instr);
								Instruction decoded = decoder.getDecoded();
								decoded.restore();
								processQueue.set(index, decoded);
							}

							break;
						}

						// look for instruction in queue to decode
						if(decode == 0 && instr.getStage() == 0){
							// Decode instruction
							Decode decoder = new Decode(instr);
							Instruction decoded = decoder.getDecoded();
							decoded.nextStage();
							processQueue.set(index, decoded);
							
							decode = 1;

							// if there are other instructions to stall
							for(int i=index; i<processQueue.size(); i++){
								if(processQueue.get(i).isStalled && processQueue.get(i).getStalled() == 0){
									processQueue.get(i).stall();
								}
							}

							break;
						}
						index++;
					}
				}
				
				// EXECUTE
				if(ecount != 0){
					int index = 0;
					for(Instruction instr: processQueue){
						if(instr.isStalled && instr.getStalled() == 1){
							if(instr.checkDependencies()){
								instr.stall();
								execute = -1;
							}else{
								// Execute instruction
								Execute executer = new Execute(instr);
								instr.nextStage();
								execute = 1;
								// processQueue.remove(index); // for test muna 
								// instrDone++; // for test muna
							}
							break;
						}

						// look for instruction to execute
						// must be on decode stage before execute
						if(execute == 0 && instr.getStage() == 1){
							// Execute instruction
							// Execute executer = new Execute(instr);
							instr.nextStage();
							execute = 1;
							// processQueue.remove(index);
							// instrDone++;


							// if there are other instructions to stall
							for(int i=index; i<processQueue.size(); i++){
								if(processQueue.get(i).isStalled && processQueue.get(i).getStalled() == 1){
									processQueue.get(i).stall();
								}
							}
							break;
						}
						
						index++;
					}
				}

				// MEMORY
				if(mcount != 0){
					int index = 0;
					for(Instruction instr: processQueue){
						if(instr.isStalled && instr.getStalled() == 2){
							if(instr.checkDependencies()){
								instr.stall();
								memory = -1;
							}else{
								// Memory access
								// insert code here
								instr.nextStage();
								memory = 1;
							}
							break;
						}

						// look for instruction to do memory access
						if(memory == 0 && instr.getStage() == 2){
							// Memory Access
							// insert code here
							instr.nextStage();
							memory = 1;
							
							// if there are other instructions to stall
							for(int i=index; i<processQueue.size(); i++){
								if(processQueue.get(i).isStalled && processQueue.get(i).getStalled() == 2){
									processQueue.get(i).stall();
								}
							}
							break;
						}
						
						index++;
					}
				}


				// WRITEBACK
				if(wcount != 0){
					int index = 0;
					for(Instruction instr: processQueue){
						if(instr.isStalled && instr.getStalled() == 3){
							if(instr.checkDependencies()){
								instr.stall();
								writeback = -1;
							}else{
								// Write to memory
								// insert code here
								instr.nextStage();
								writeback = 1;

								processQueue.remove(index); // for test muna 
								instrDone++; // for test muna

							}
							break;
						}

						// look for instruction to do memory access
						if(writeback == 0 && instr.getStage() == 3){
							// Write to memory
							// insert code here
							instr.nextStage();
							writeback = 1;

							processQueue.remove(index); // for test muna 
							instrDone++; // for test muna
							
							// if there are other instructions to stall
							for(int i=index; i<processQueue.size(); i++){
								if(processQueue.get(i).isStalled && processQueue.get(i).getStalled() == 3){
									processQueue.get(i).stall();
								}
							}
							break;
						}
						
						index++;
					}
				}
			}

			// store values for printing later

			fetchpipeline.add(fetch);
			decodepipeline.add(decode);
			executepipeline.add(execute);
			memorypipeline.add(memory);
			writebackpipeline.add(writeback);


			printQueues(clockcycle);


			// reset
			fetch = 0;
			decode = 0;
			execute = 0;
			memory = 0;
			writeback = 0;
			
			clockcycle++;

		}
	}

	public static void printQueues(int clockcycle){
		System.out.println("Clock Cycle: " + clockcycle);

		for(int i=0; i<fetchpipeline.size(); i++){
			if(fetchpipeline.get(i) == 1){
				System.out.print("F ");
			}else if(fetchpipeline.get(i) == 0){
				System.out.print("  ");
			}else{
				System.out.print("S ");
			}
		}
		System.out.println();
		for(int i=0; i<decodepipeline.size(); i++){
			if(decodepipeline.get(i) == 1){
				System.out.print("D ");
			}else if(decodepipeline.get(i) == 0){
				System.out.print("  ");
			}else{
				System.out.print("S ");
			}
		}
		System.out.println();

		for(int i=0; i<executepipeline.size(); i++){
			if(executepipeline.get(i) == 1){
				System.out.print("E ");
			}else if(executepipeline.get(i) == 0){
				System.out.print("  ");
			}else{
				System.out.print("S ");
			}
		}
		System.out.println();

		for(int i=0; i<memorypipeline.size(); i++){
			if(memorypipeline.get(i) == 1){
				System.out.print("M ");
			}else if(memorypipeline.get(i) == 0){
				System.out.print("  ");
			}else{
				System.out.print("S ");
			}
		}
		System.out.println();

		for(int i=0; i<writebackpipeline.size(); i++){
			if(writebackpipeline.get(i) == 1){
				System.out.print("W ");
			}else if(writebackpipeline.get(i) == 0){
				System.out.print("  ");
			}else{
				System.out.print("S ");
			}
		}
		System.out.println();
	}
}
