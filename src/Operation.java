public record Operation(Instruction instr, Integer... args) {
	public void execute() {
		Instruction.execs.get(instr).accept(args);
	}
}
