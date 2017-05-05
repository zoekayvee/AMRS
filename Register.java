import java.util.*;
import java.io.*;

public class Register {
	private int value;
	private int id;

	public Register(int id) {
		this.value = 0;
		this.id = id;
	}

	public int getID() {
		return this.id;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}