public class Process {
    final public PageTable pageTable = new PageTable();
    public int[] instrMemory;

    public Instruction getInstruction(int programCounter) {
        return new Instruction(instrMemory[programCounter]);
    }

    enum InstrFormat {
        R,
        J,
        I
    }

    class Instruction {
        private int value;
        private int[] fields;
        private InstrFormat format;

        public Instruction(int value) {
            this.value = value;
            int opcode = getRange(31, 26);
            switch(opcode) {
                case 0x0: {
                    // R format instruction
                    format = InstrFormat.R;
                    int rs = getRange(25, 21);
                    int rt = getRange(20, 16);
                    int rd = getRange(15, 11);
                    int shamt = getRange(10, 6);
                    int funct = getRange(5, 0);
                    fields = new int[]{ opcode, rs, rt, rd, shamt, funct };
                    return;
                }
                case 0x2:
                case 0x3:
                    // J format instruction
                    format = InstrFormat.J;
                    int address = getRange(25, 0);
                    fields = new int[]{ opcode, address };
                    return;
                default:
                    // I format instruction
                    format = InstrFormat.I;
                    int rs = getRange(25, 21);
                    int rt = getRange(20, 16);
                    int immediate = getRange(15, 0);
                    fields = new int[]{ opcode, rs, rt, immediate };
            }
        }

        public int getValue() {
            return value;
        }
        public int[] getFields() {
            return fields;
        }
        public InstrFormat getFormat() {
            return format;
        }

        public int getRange(int end, int start) {
            return value << (31 - end) >>> (start + 31 - end);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            switch (format) {
                case R:
                    switch (fields[5]) {
                        case 0x20: builder.append("ADD"); break;
                        case 0x21: builder.append("ADDIU"); break;
                        case 0x24: builder.append("AND"); break;
                        case 0x08: builder.append("JR"); break;
                        case 0x27: builder.append("NOR"); break;
                        case 0x25: builder.append("OR"); break;
                        case 0x2A: builder.append("SLT"); break;
                        case 0x2B: builder.append("SLTU"); break;
                        case 0x00: builder.append("SLL"); break;
                        case 0x02: builder.append("SRL"); break;
                        case 0x22: builder.append("SUB"); break;
                        default: builder.append("SUBU");
                    }
                    builder.append("  reg").append(fields[1])
                            .append("  reg").append(fields[2])
                            .append("  reg").append(fields[3])
                            .append("  ").append(fields[4])
                            .append("  ").append(fields[5]);
                    break;
                case I:
                    switch (fields[0]) {
                        case 0x8: builder.append("ADDI"); break;
                        case 0x9: builder.append("ADDIU"); break;
                        case 0xC: builder.append("ANDI"); break;
                        case 0x4: builder.append("BEQ"); break;
                        case 0x5: builder.append("BNE"); break;
                        case 0x24: builder.append("LBU"); break;
                        case 0x25: builder.append("LHU"); break;
                        case 0x30: builder.append("LL"); break;
                        case 0xF: builder.append("LUI"); break;
                        case 0x23: builder.append("LW"); break;
                        case 0xD: builder.append("ORI"); break;
                        case 0xA: builder.append("SLTI"); break;
                        case 0xB: builder.append("SLTIU"); break;
                        case 0x28: builder.append("SB"); break;
                        case 0x38: builder.append("SC"); break;
                        case 0x29: builder.append("SH"); break;
                        default: builder.append("SW");
                    }
                    builder.append("  reg").append(fields[1])
                            .append("  reg").append(fields[2])
                            .append("  ").append(fields[3]);
                    break;
                default:
                    if (fields[0] == 0x2) builder.append("J");
                    else builder.append("JAL");
                    builder.append("  ").append(fields[1]);

            }
            return builder.toString();
        }
    }
}
