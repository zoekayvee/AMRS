import java.util.*;
import java.io.*;

public class Main{
	static int instrSetSize;
	static int clockcycle;
	static int counter = 1;
	static int stalls = 0;
	static int instrDone = 0;
	static Computer computer = new Computer();
	static ArrayList<Instruction> processQueue = new ArrayList<Instruction>();
	static ArrayList<Instruction> instrQueue = new ArrayList<Instruction>();

	static ArrayList<Vector> hazards = new ArrayList<Vector>();
	static ArrayList<Vector> hazardsEncountered = new ArrayList<Vector>();
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
	static boolean stall = false;

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

		stall = true;
		return instruction;
	}
	
	public static void setDependencies(ArrayList<Vector> instructions){
		/*
			Vector contains 3 elements
			0 - instruction 1 ID (int)
			1 - instruction 2 ID (int)
			2 - type of hazard (string)

			Example of a vector
			< 1, 4, RAW >
		*/

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

		System.out.println("Input file name:");
		System.out.println("(e.g. input.txt)");
		Scanner sc = new Scanner(System.in);
		System.out.print(">>> ");
		String fileinput = sc.nextLine();

		// Parse input code
		LexicalAnalyzer parse = new LexicalAnalyzer(fileinput);
		if(!parse.parsed) System.out.println("Error parsing file. Please try again.");


		instrSetSize = parse.instructions.size();


		// Check for error flag, terminate if true
		if (parse.error.getValue()) {
			System.exit(0);
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
		clockcycle = 1;
		while(instrDone != instrSetSize){
			// reset
			fetch = false;
			decode = false;
			execute = false;
			memory = false;
			writeback = false;
			stall = false;

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
								computer.Writeback(instruction);

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
								computer.Writeback(instruction);

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
								computer.Memory(instruction);

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
								computer.Memory(instruction);

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
								instruction.executed = computer.Execute(instruction);
								
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
								instruction.executed = computer.Execute(instruction);

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
								instruction.decoded = computer.Decode(instruction);
								
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
								instruction.decoded = computer.Decode(instruction);
								
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

				index++;
			}

			if(stall) stalls++;
			printInfo(clockcycle);
			clockcycle++;
		}
	}

	public static void printInfo(int clockcycle){
		System.out.println();
		System.out.println("CLOCK CYCLE: " + clockcycle);
		System.out.println("Stalls: " + stalls);

		// Print clock cycle label
		System.out.print("      ");
		for(int i=0; i<clockcycle; i++){
			if(i>9) System.out.print(i + " ");
			else System.out.print(i + "  ");
		}
		System.out.println();

		for(Instruction i : instrQueue){
			if(i.id > 9){
				System.out.print("I" + i.id + ":" + "  " );
			}else{
				System.out.print("I" + i.id + ":" + "   ");
			}

			for(String x : i.pipe){
				System.out.print(x + "  ");
			}
			System.out.println();
		}

		// Print hazards

		System.out.println();
		System.out.println("Hazards Encountered:");
		boolean empty = true;
		for(Vector entry : hazardsEncountered) {
	    if((int) entry.get(0) == clockcycle){
	    	System.out.println("Instructions: " + entry.get(1) + " & " + entry.get(2) + " Type: " + entry.get(3));
	    	empty = false;
	    }
		}
		if(empty) System.out.println("-- None.");
		System.out.println();

		computer.printRegisters();
	}
}

