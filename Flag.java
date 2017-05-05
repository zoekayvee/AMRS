import java.util.*;
import java.io.*;

public class Flag {
	private boolean value;

	public Flag() {
		value = false;
	}

	public Flag(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return this.value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public void toggle() {
		this.value = !this.value;
	}

}