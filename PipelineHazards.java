// import java.util.*;
// import java.io.*;

// public class PipelineHazards {

// 	public Vector instruction;
// 	public int[][] hazardArray;
// 	public int size;

// 	public PipelineHazards(Vector instruction) {
// 		this.instruction = instruction;
// 		this.size = instruction.size();
// 		this.hazard2DArray = new int[size][size];
// 		int i, j;

// 		for (i=0; i<this.size; i++) {	
// 			for (j=i+1; j<this.size; j++) {
				
// 				/*if(this.instruction.get(i+1).get(1) == this.instruction.get(j+1).get(2) ){  //instruction.1.operand1 == instrction.2.operand2
// 					this.hazardArray[i][j] = 1;
// 				}
// 				else if(this.instruction.get(i+1).get(2) == this.instruction.get(j+1).get(1) ){  //instruction.1.operand1 == instrction.2.operand1
// 					this.hazardArray[i][j] = 2;
// 				}
// 				esle if(this.instruction.get(i+1).get(1) == this.instruction.get(j+1).get(1) ){  //instruction.1.operand1 == instrction.2.operand2
// 					this.hazardArray[i][j] = 3;
// 				}
// 				else{
// 					this.hazardArray[i][j] = 0;									
// 				}
// 				}*/
			
// 				if(this.instruction.get(i+1).get(1).equals(instruction.get(j+1).get(2))){  //firstvec.operand1.equals(secondvec.operand2)
// 					this.hazardArray[i][j] = 1;									  
// 				}
// 				else if(this.instruction.get(i+1).get(2).equals(instruction.get(j+1).get(1))){
// 					this.hazardArray[i][j] = 2;
// 				}
// 				else if (this.instruction.get(i+1).get(1).equals(instruction.get(j+1).get(1))){
// 					this.hazardArray[i][j] = 3;
// 				}
// 				else{
// 					this.hazardArray[i][j] = 0;									
// 				}
// 			}
// 		}
		
// 		System.out.print("-----Hazards-----");
// 		for (i = 0; i < this.size; i++) {
// 			for (j = i+1; j < this.size; j++) {
// 				if(hazardArray[i][j] == 1){
// 					System.out.print("instructions " + i+1 + " and " + j+1 + "- Read After Write");
// 				}
// 				else if(hazardArray[i][j] == 2){
// 					System.out.print("instructions " + i+1 + " and " + j+1 + "- Write After Read");
// 				}
// 				else if(hazardArray[i][j] == 3){
// 					System.out.print("instructions " + i+1 + " and " + j+1 + "- Write After Write");
// 				}
// 				else{}
// 			}
			
// 		}
// 		System.out.print("-----------------");
	
// 	}


// }