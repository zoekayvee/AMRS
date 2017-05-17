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
	static int counter = 1;
	static int stalls = 0;
	static int instrDone = 0;
	static ArrayList<Instruction> processQueue = new ArrayList<Instruction>();
	static ArrayList<Instruction> instrQueue = new ArrayList<Instruction>();

	static ArrayList<Vector> hazards = new ArrayList<Vector>();
	static ArrayList<Vector> dependencies = new ArrayList<Vector>();

	/*
		STATUS for pipeline
		-1 - Stall
		0 - Inactive
		1 - Active

	*/

	static boolean fetch = false;
	static boolean decode = false;
	static boolean execute = false;
	static boolean memory = false;
	static boolean writeback = false;

	static ArrayList<Integer> fetchpipeline = new ArrayList<Integer>();
	static ArrayList<Integer> decodepipeline = new ArrayList<Integer>();
	static ArrayList<Integer> executepipeline = new ArrayList<Integer>();
	static ArrayList<Integer> memorypipeline = new ArrayList<Integer>();
	static ArrayList<Integer> writebackpipeline = new ArrayList<Integer>();

	static ArrayList<Vector> pipes = new ArrayList<Vector>();
	static ArrayList<Vector> done = new ArrayList<Vector>();
	
	public static Instruction performStall(Instruction instruction){
		instruction.stall();
		instruction.pipe.add("S");

		// find representation in queue
		int y = 0;
		for(Instruction x : processQueue){
			if(x.id == instruction.id){
				processQueue.set(y, instruction);
				break;
			}
			y++;
		}

		stalls++;
		return instruction;
	}
	
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

		// save all dependencies to ArrayList<Vector> dependencies 
		// ArrayList<Vector> dependencies is already declared as static variable

		for(int i=0; i<instructions.size()-1; i++){
			for(int j=i+1; j<instructions.size(); j++){

				if(instructions.get(i).get(1).equals(instructions.get(j).get(2))){

					Vector hazard = new Vector();
					hazard.add(i+1);
					hazard.add(j+1);
					hazard.add("RAW");
					hazards.add(hazard);
				}
					
				if(instructions.get(i).get(2).equals(instructions.get(j).get(1))){

					Vector hazard = new Vector();
					hazard.add(i+1);
					hazard.add(j+1);
					hazard.add("WAR");
					hazards.add(hazard);
				}
					
				if(instructions.get(i).get(1).equals(instructions.get(j).get(1))){

					Vector hazard = new Vector();
					hazard.add(i+1);
					hazard.add(j+1);
					hazard.add("WAW");
					hazards.add(hazard);
				}
			}
		}
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

		// Initialize all instructions
		for(Vector instr : parse.instructions){
			Instruction instruction = new Instruction(instr);
			instrQueue.add(instruction);
			counter++;
		}

		// Print instructions
		for(Instruction i: instrQueue){
			System.out.println(i.instr);
		}

		// Print Hazards
		for(Vector haz : hazards){
			System.out.println(haz);
		}

		// PIPELINING
		int clockcycle = 1;
		while(instrDone != instrSetSize){
			// reset
			fetch = false;
			decode = false;
			execute = false;
			memory = false;
			writeback = false;

			int index = 0;
			for(Instruction instruction : instrQueue){
				// Perform stages	

				// Pipeline empty
				if(processQueue.isEmpty()){
					// FETCH only
					instruction.pipe.add("F");
					processQueue.add(instruction);

					fetch = true;
					

					for(int i=0; i<instrQueue.size(); i++){
						if(!instrQueue.get(i).pipe.contains("F")) instrQueue.get(i).pipe.add("-");
					}


					break;
				// Pipeline not empty
				}else{

					// Instruction not yet in process queue
					if(!instruction.pipe.contains("F")){ // not yet fetched
						if(!fetch){
							instruction.pipe.add("F");
							processQueue.add(instruction);

							fetch = true;
							
						}else{
							instruction.pipe.add("-");
						}

					// Instructions in process queue
					}else{
						// REMOVE FINISHED INSTRUCTION
						if(instruction.getStage() == 4){
							int y = 0;
							for(Instruction x : processQueue){
								if(x.id == instruction.id){
									processQueue.remove(y);
									instrDone++;
									break;
								}
								y++;
							}
						}

						// WRITEBACK
						if(instruction.isStalled && instruction.getStalled() == 3){
							// check dependencies
							if(!writeback && instruction.checkDependencies()){ // ready
								// Writeback
								/*
		


								*/

								instruction.restore();
								instruction.pipe.add("W");

								// find representation in queue
								int y = 0;
								for(Instruction x : processQueue){
									if(x.id == instruction.id){
										processQueue.set(y, instruction);
										break;
									}
									y++;
								}

								writeback = true;

							}else{ // stall
								Instruction stalled = performStall(instruction);
							}
						}else if(instruction.getStage() == 3){
							if(!writeback && instruction.checkDependencies()){
								// Writeback
								/*



								*/
								instruction.nextStage();
								instruction.pipe.add("W");

								// find representation in queue
								int y = 0;
								for(Instruction x : processQueue){
									if(x.id == instruction.id){
										processQueue.set(y, instruction);
										break;
									}
									y++;
								}

								writeback = true;

							}else{
								Instruction stalled = performStall(instruction);
							}
						}

						// MEMORY
						if(instruction.isStalled && instruction.getStalled() == 2){
							// check dependencies
							if(!memory && instruction.checkDependencies()){ // ready
								// Memory Access
								/*



								*/
								instruction.restore();
								instruction.pipe.add("M");

								// find representation in queue
								int y = 0;
								for(Instruction x : processQueue){
									if(x.id == instruction.id){
										processQueue.set(y, instruction);
										break;
									}
									y++;
								}

								memory = true;

							}else{ // stall
								Instruction stalled = performStall(instruction);
							}
						}else if(instruction.getStage() == 2){
							if(!memory && instruction.checkDependencies()){
								// Memory Access
								/*



								*/

								instruction.nextStage();
								instruction.pipe.add("M");

								// find representation in queue
								int y = 0;
								for(Instruction x : processQueue){
									if(x.id == instruction.id){
										processQueue.set(y, instruction);
										break;
									}
									y++;
								}

								memory = true;

							}else{
								Instruction stalled = performStall(instruction);
							}
						}

						// EXECUTE
						if(instruction.isStalled && instruction.getStalled() == 1){
							// check dependencies
							if(!execute && instruction.checkDependencies()){ // ready
								// Execute
								/*



								*/
								instruction.restore();
								instruction.pipe.add("E");

								// find representation in queue
								int y = 0;
								for(Instruction x : processQueue){
									if(x.id == instruction.id){
										processQueue.set(y, instruction);
										break;
									}
									y++;
								}

								execute = true;

							}else{ // stall
								Instruction stalled = performStall(instruction);
							}
						}else if(instruction.getStage() == 1){
							if(!execute && instruction.checkDependencies()){
								// Execute
								/*



								*/
								instruction.nextStage();
								instruction.pipe.add("E");

								// find representation in queue
								int y = 0;
								for(Instruction x : processQueue){
									if(x.id == instruction.id){
										processQueue.set(y, instruction);
										break;
									}
									y++;
								}

								execute = true;

							}else{
								Instruction stalled = performStall(instruction);
							}
						}

						// DECODE
						boolean ready = instruction.checkDependencies();
						if(instruction.isStalled && instruction.getStalled() == 0){
							// check dependencies
							if(!decode && ready){ // ready
								// Decode
								// Decode decoder = new Decode(instruction);
								// instruction.decoded = decoder.getDecoded();
								
								instruction.restore();
								instruction.pipe.add("D");

								// find representation in queue
								int y = 0;
								for(Instruction x : processQueue){
									if(x.id == instruction.id){
										processQueue.set(y, instruction);
										break;
									}
									y++;
								}

								decode = true;

							}else{ // stall
								Instruction stalled = performStall(instruction);
							}

						}else if(instruction.getStage() == 0){
							if(!decode && ready){
								// Decode
								// Decode decoder = new Decode(instruction);
								// instruction.decoded = decoder.getDecoded();
								
								instruction.nextStage();
								instruction.pipe.add("D");

								// find representation in queue
								int y = 0;
								for(Instruction x : processQueue){
									if(x.id == instruction.id){
										processQueue.set(y, instruction);
										break;
									}
									y++;
								}

								decode = true;

							}else{
								Instruction stalled = performStall(instruction);
							}
						}						
						
					}


				}

				// printQueues(clockcycle);

				index++;
			}

			printQueues(clockcycle);

			clockcycle++;
			// if(clockcycle == 9) break;
		}
	}

	public static void printQueues(int clockcycle){
		System.out.println();
		System.out.println("Clock Cycle: " + clockcycle);
		System.out.println("Stalls: " + stalls);
		System.out.println();

		// Print clock cycle label
		for(int i=0; i<clockcycle; i++){
			if(i>9) System.out.print(i + " ");
			else System.out.print(i + "  ");
		}

		for(Instruction i : instrQueue){
			for(String x : i.pipe){
				// if(x.equals("-") ) System.out.print("  ");
				// else System.out.print(x + " ");
				System.out.print(x + "  ");
			}
			System.out.println();
		}
	}
}

