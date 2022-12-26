public class Processor {
    public static int programCounter = 0;
    public static final int[] mainMemory = new int[256];

    private static boolean RegDst;
    private static boolean Jump;
    private static boolean Branch;
    private static boolean MemRead;
    private static boolean MemtoReg;
    private static int ALUOp;
    private static boolean MemWrite;
    private static boolean ALUSrc;
    private static boolean RegWrite;

    private static int ALUControl;

    public static final int[] registers = new int[16];

    public static void cycle(Process process) {
        // Retrieve the next instruction to execute from the process' instruction memory
        Process.Instruction instruction = process.getInstruction(programCounter);

        // Establish control signals based on the opcode of the instruction
        control(instruction.getRange(31, 26));

//        System.out.println("Retrieved control signals:" +
//                "\n\tRegDst: " + RegDst +
//                "\n\tJump: " + Jump +
//                "\n\tBranch: " + Branch +
//                "\n\tMemRead: " + MemRead +
//                "\n\tMemtoReg: " + MemtoReg +
//                "\n\tALUOp: " + ALUOp +
//                "\n\tMemWrite: " + MemWrite +
//                "\n\tALUSrc: " + ALUSrc +
//                "\n\tRegWrite: " + RegWrite);

        // Registers to read from and write to
        // Read Register 1 <- Instruction [25:21]
        int readReg1 = instruction.getRange(25, 21);
        // Read Register 2 <- Instruction [20:16]
        int readReg2 = instruction.getRange(20, 16);
        // Write Register <- MUX{ Instruction [20:16], Instruction [15:11], RegDst }
        int writeReg = RegDst ? instruction.getRange(15, 11) : readReg2;

        // Sign extend the address field to find:
        // the branch distance if the instruction is a branch, or
        // the load/store address if it is a load/store instruction
        int address = signExtend(instruction.getRange(15, 0));

        // Read data from the registers
        int readData1 = registers[readReg1];
        int readData2 = registers[readReg2];

        // ALU inputs
        int ALU2 = ALUSrc ? address : readData2;

        // Establish the ALU control signal based on the funct of the instruction
        ALUControl = ALUControl(instruction.getRange(5, 0));

        // Work the ALU
        int ALUResult = ALU(readData1, ALU2);
        boolean Zero = ALUResult == 0;

        // Feed address and write data into the data memory, then work the data memory
        int DMReadData = 0;
        int physicalAddress = process.pageTable.getMapping(ALUResult);
        if (MemRead) DMReadData = mainMemory[physicalAddress];
        if (MemWrite) mainMemory[physicalAddress] = readData2;

        // Feed result of the data memory back into the write data of the register file
        // If RegWrite is true, we are writing to a register
        int writeData = MemtoReg ? DMReadData : ALUResult;
        if (RegWrite) registers[writeReg] = writeData;

        // Increment the program counter, and handle jumps and branches
        programCounter++;
        if (Branch && Zero) programCounter += address;
        else if (Jump) programCounter = (programCounter >>> 26 << 26) + instruction.getRange(25, 0);
    }

    public static void control(int opcode) {
        boolean rFormat = opcode == 0;
        boolean LW = opcode == 35;
        boolean SW = opcode == 43;
        boolean BEQ = opcode == 4;
        boolean jump = opcode == 2;

        RegDst = rFormat;
        Jump = jump;
        ALUSrc = LW || SW;
        MemtoReg = LW;
        RegWrite = rFormat || LW;
        MemRead = LW;
        MemWrite = SW;
        Branch = BEQ;
        ALUOp = 0;
        if (rFormat) ALUOp += 2;
        if (BEQ) ALUOp += 1;
    }

    // Sets the function of the ALU, given ALUOp and funct values
    public static int ALUControl(int funct) {
        if (ALUOp == 0) return 2; // ADD (LW)
        if (ALUOp == 1) return 6; // ADD (SW)
        // Otherwise, the instruction is R-Formatted
        switch (funct % (1 << 4)) {
            case 0: return 2; // ADD
            case 2: return 6; // SUB
            case 4: return 0; // AND
            case 5: return 1; // OR
            case 10:
            default: return 7; // SLT
        }
    }

    public static int signExtend(int input) {
        int highOrderBit = (input & 0x0000FFFF) >>> 15;
        if (highOrderBit == 1)
            return input + 0xFFFF0000;
        return input;
    }

    public static int ALU(int ALU1, int ALU2) {
        switch (ALUControl) {
            case 2: return ALU1 + ALU2; // ADD
            case 6: return ALU1 - ALU2; // SUB
            case 0: return ALU1 & ALU2; // AND
            case 1: return ALU1 | ALU2; // OR
            case 7:
            default: return ALU1 < ALU2 ? 1 : 0; // SLT
        }
    }
}
