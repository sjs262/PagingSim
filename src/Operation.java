import java.util.Arrays;

public record Operation(Instruction instr, Integer... args) {
	public void execute() {
		Instruction.execs.get(instr).accept(args);
	}
	
	public String toString() {
		return instr + " " + Arrays.toString(args);
	}
}
