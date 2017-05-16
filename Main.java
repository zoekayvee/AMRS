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

	static boolean fetch = false;
	static boolean decode = false;
	static boolean execute = false;
	static boolean memory = false;
	static boolean writeback = false;

	static ArrayList<Boolean> fetchpipeline = new ArrayList<Boolean>();
	static ArrayList<Boolean> decodepipeline = new ArrayList<Boolean>();
	static ArrayList<Boolean> executepipeline = new ArrayList<Boolean>();
	static ArrayList<Boolean> memorypipeline = new ArrayList<Boolean>();
	static ArrayList<Boolean> writebackpipeline = new ArrayList<Boolean>();
	
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


		// PIPELINING
		int clockcycle = 1;
		while(instrDone != instrSetSize){

			// Pipeline empty
			if(processQueue.isEmpty()){
				// Perform FETCH only
				Instruction instr = new Instruction(parse.instructions.get(counter));
				processQueue.add(instr);
				fetch = true;
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
					// System.out.print("F ");
					Instruction instr = new Instruction(parse.instructions.get(counter));

					// update count
					processQueue.add(instr);
					fetch = true;
					counter++;
				}

				// DECODE
				if(dcount != 0){
					int index = 0;
					for(Instruction instr : processQueue){
						// look for 1ST instruction in queue to decode
						// instr must be on FETCH stage first
						if(instr.getStage() == 0){
							// System.out.print("D ");
							// Decode instruction
							// Decode decoder = new Decode(instr);
							// Instruction decoded = processQueue.decoder.getDecoded();
							// decoded.nextStage();
							instr.nextStage();
							// processQueue.set(index, decoded);
							
							decode = true;

							// if there are other instructions
							// to stall
							if(dcount > 1){
								for(int i=index; i<processQueue.size(); i++){
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
						// look for instruction to execute
						// must be on decode stage before execute
						if(instr.getStage() == 1){
							// System.out.print("E ");
							// Execute executer = new Execute(instr);
							instr.nextStage();
							processQueue.remove(index);
							instrDone++;
							execute = true;

							// if there are other instructions
							// to stall
							if(ecount > 1){
								for(int i=index; i<processQueue.size(); i++){
									processQueue.get(i).stall();
								}
							}

							break;
						}
						index++;
					}
				}

				// MEMORY
				// WRITEBACK

				System.out.println();
			}

			// store values for printing later

			fetchpipeline.add(fetch);
			decodepipeline.add(decode);
			executepipeline.add(execute);
			memorypipeline.add(memory);
			writebackpipeline.add(writeback);


			printQueues(clockcycle);


			// reset
			fetch = false;
			decode = false;
			execute = false;
			memory = false;
			writeback = false;
			
			clockcycle++;

		}
	}

	public static void printQueues(int clockcycle){
		System.out.println("Clock Cycle: " + clockcycle);

		for(int i=0; i<fetchpipeline.size(); i++){
			if(fetchpipeline.get(i)){
				System.out.print("F ");
			}else{
				System.out.print("  ");
			}
		}
		System.out.println();
		for(int i=0; i<decodepipeline.size(); i++){
			if(decodepipeline.get(i)){
				System.out.print("D ");
			}else{
				System.out.print("  ");
			}
		}
		System.out.println();

		for(int i=0; i<executepipeline.size(); i++){
			if(executepipeline.get(i)){
				System.out.print("E ");
			}else{
				System.out.print("  ");
			}
		}
		System.out.println();
	}
}