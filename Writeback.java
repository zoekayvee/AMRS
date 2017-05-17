import java.util.*;
import java.io.*;

public class Writeback {

	public static void Writeback(Instruction instruction) {
		// Places data in designated register
		Main.r[instruction.destination].setValue(instruction.executed);
	}
	
}
