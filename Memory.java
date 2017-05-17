import java.util.*;
import java.io.*;

public class Memory {

	public static int destination;

	// Parses original instruction to get data's destination
	public static void Memory(Vector instruction) {
		String regNo = (String) instruction.get(1);
		regNo = regNo.substring(1);
		int destReg = Integer.parseInt(regNo);		
		destination = destReg;
	}

	public int getDestination() {
		return this.destination;
	}

}
