import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public enum Instruction {
	// -------------------------------------------------
	SLL(0, Format.R, 0),
	
	J(2, Format.J), 		SRL(0, Format.R, 2),
	JAL(3, Format.J), 		SRA(0, Format.R, 3),
	// -------------------------------------------------
	BEQ(4, Format.I), 		SLLV(0, Format.R, 4),
	BNE(5, Format.I),
	BLEZ(6, Format.I), 		SRLV(0, Format.R, 6),
	BGTZ(7, Format.I), 		SRAV(0, Format.R, 7),
	// -------------------------------------------------
	ADDI(8, Format.I), 		JR(0, Format.R, 8),
	ADDIU(9, Format.I), 	JALR(0, Format.R, 9),
	SLTI(10, Format.I), 	MOVZ(0, Format.R, 10),
	SLTIU(11, Format.I), 	MOVN(0, Format.R, 11),
	// -------------------------------------------------
	ANDI(12, Format.I), 	SYSCALL(0, Format.R, 12),
	ORI(13, Format.I), 		BREAK(0, Format.R, 13),
	XORI(14, Format.I),
	LUI(15, Format.I), 		SYNC(0, Format.R, 15),
	// -------------------------------------------------
	MFHI(0, Format.R, 16),
	MTHI(0, Format.R, 17),
	MFLO(0, Format.R, 18),
	MTLO(0, Format.R, 19),
	// -------------------------------------------------
	
	
	
	
	// -------------------------------------------------
	MULT(0, Format.R, 24),
	MULTU(0, Format.R, 25),
	DIV(0, Format.R, 26),
	DIVU(0, Format.R, 27),
	// -------------------------------------------------
	
	
	
	
	// -------------------------------------------------
	LB(32, Format.I), 		ADD(0, Format.R, 32),
	LH(33, Format.I), 		ADDU(0, Format.R, 33),
	LWL(34, Format.I), 		SUB(0, Format.R, 34),
	LW(35, Format.I), 		SUBU(0, Format.R, 35),
	// -------------------------------------------------
	LBU(36, Format.I),		AND(0, Format.R, 36),
	LHU(37, Format.I), 		OR(0, Format.R, 37),
	LWR(38, Format.I), 		XOR(0, Format.R, 38),
							NOR(0, Format.R, 39),
	// -------------------------------------------------
	SB(40, Format.I),
	SH(41, Format.I),
	SWL(42, Format.I), 		SLT(0, Format.R, 42),
	SW(43, Format.I), 		SLTU(0, Format.R, 43),
	// -------------------------------------------------
	
	
	SWR(46, Format.I),
	CACHE(47, Format.I),
	// -------------------------------------------------
	LL(48, Format.I), 		TGE(0, Format.R, 48),
	LWC1(49, Format.I), 	TGEU(0, Format.R, 49),
	LWC2(50, Format.I), 	TLT(0, Format.R, 50),
	PREF(51, Format.I), 	TLTU(0, Format.R, 51),
	// -------------------------------------------------
							TEQ(0, Format.R, 52),
	LDC1(53, Format.I),
	LDC2(54, Format.I), 	TNE(0, Format.R, 54),
	
	// -------------------------------------------------
	SC(56, Format.R),
	SWC1(57, Format.I),
	SWC2(58, Format.I),
	
	// -------------------------------------------------
	
	SDC1(61, Format.I),
	SDC2(62, Format.I);
	
	// -------------------------------------------------
	
	enum Format {
		R(4),
		I(3),
		J(1);
		
		private final int argLength;
		
		Format(int argLength) {
			this.argLength = argLength;
		}
		
		int getArgLength() {
			return argLength;
		}
	}
	
	
	private final int opcode;
	private final Integer funct;
	private final Format format;
	
	private static final int[] R = Processor.registers;
	private static final int[] PC = Processor.PC;
	
	Instruction(int opcode, Format format, Integer... funct) {
		this.opcode = opcode;
		this.funct = funct.length > 0 ? funct[0] : null;
		this.format = format;
	}
	
	public int getOp() {
		return opcode;
	}
	
	public Integer getFunct() {
		return funct;
	}
	
	public Format getFormat() {
		return format;
	}
	
	private static final int rs = 0, rt = 1, rd = 2, shamt = 3, imm = 2, addr = 0;
	public final static EnumMap<Instruction, Consumer<Integer[]>> execs = new EnumMap<>(
		Map.ofEntries(
			Map.entry(ADD, 		args -> R[args[rd]] = R[args[rs]] + R[args[rt]]),
			Map.entry(ADDI, 	args -> R[args[rt]] = R[args[rs]] + args[imm]),
			Map.entry(ADDIU,	args -> R[args[rt]] = R[args[rs]] + args[imm]),
			Map.entry(ADDU,		args -> R[args[rd]] = R[args[rs]] + R[args[rt]]),
			Map.entry(AND, 		args -> R[args[rd]] = R[args[rs]] & R[args[rt]]),
			Map.entry(ANDI, 	args -> R[args[rt]] = R[args[rs]] & args[imm]),
			Map.entry(BEQ,		args -> { if (R[args[rs]] == R[args[rt]]) PC[0] += args[imm]; }),
			Map.entry(BNE, 		args -> { if (R[args[rs]] != R[args[rt]]) PC[0] += args[imm]; }),
			Map.entry(J, 		args -> PC[0] += args[addr]),
			Map.entry(JAL, 		args -> { R[31] = PC[0] + 1; PC[0] = args[addr]; }),
			Map.entry(JR,		args -> PC[0] = R[args[rs]]),
			Map.entry(LBU,		args -> R[args[rt]] = memGet(R[args[rs]] + args[imm]) & 0x000000FF),
			Map.entry(LHU,		args -> R[args[rt]] = memGet(R[args[rs]] + args[imm]) & 0x0000FFFF),
			Map.entry(LL,		args -> R[args[rt]] = memGet(R[args[rs]] + args[imm])),
			Map.entry(LUI,		args -> R[args[rt]] = memGet(R[args[rs]] + args[imm]) & 0xFFFF0000),
			Map.entry(LW,		args -> R[args[rt]] = memGet(R[args[rs]] + args[imm])),
			Map.entry(NOR,		args -> R[args[rd]] = ~(R[args[rs]] | R[args[rt]])),
			Map.entry(OR,		args -> R[args[rd]] = R[args[rs]] | R[args[rt]]),
			Map.entry(ORI,		args -> R[args[rt]] = R[args[rs]] | args[imm]),
			Map.entry(SLT, 		args -> R[args[rd]] = (R[args[rs]] < R[args[rt]]) ? 1 : 0),
			Map.entry(SLTI,		args -> R[args[rt]] = (R[args[rs]] < args[imm]) ? 1 : 0),
			Map.entry(SLTIU,	args -> R[args[rt]] = (R[args[rs]] < args[imm]) ? 1 : 0),
			Map.entry(SLTU,		args -> R[args[rd]] = (R[args[rs]] < R[args[rt] ]) ? 1 : 0),
			Map.entry(SLL,		args -> R[args[rd]] = R[args[rt]] << args[shamt]),
			Map.entry(SRL,		args -> R[args[rd]] = R[args[rt]] >>> args[shamt]),
			Map.entry(SB,		args -> memSet(R[args[rs]] + args[imm], memGet(R[args[rs]] + args[imm]) & 0xFFFFFF00 + R[args[rt]] & 0x000000FF)),
			Map.entry(SC,		args -> memSet(R[args[rs]] + args[imm], Processor.atomic ? 1 : 0)),
			Map.entry(SH,		args -> memSet(R[args[rs]] + args[imm], memGet(R[args[rs]] + args[imm]) & 0xFFFF0000 + R[args[rt]] & 0x0000FFFF)),
			Map.entry(SW,		args -> memSet(R[args[rs]] + args[imm], R[args[rt]])),
			Map.entry(SUB,		args -> R[args[rd]] = R[args[rs]] - R[args[rt]]),
			Map.entry(SUBU,		args -> R[args[rd]] = R[args[rs]] - R[args[rt]])
		)
	);
	
	private static int memGet(int addr) {
		return MemoryManager.getContent(addr);
	}
	
	private static void memSet(int addr, int value) {
		MemoryManager.setContent(addr, value);
	}
}