public class Processor {
    public static final int[] PC = { 0 };
    public static boolean atomic;
    /**
     * $zero: 0b00000 - constant zero
     * $at: 0b00001 - assembler temporary
     * $v0-$v1: 0b00010-0b00011 - values for function results and expression evaluation
     * $a0-$a3: 0b00100-0b00111 - arguments
     * $t0-$t7: 0b01000-0b01111 - temporaries
     * $s0-$s7: 0b10000-0b10111 - saved temporaries
     * $t8-$t9: 0b11000-0b11001 - temporaries
     * $k0-$k1: 0b11010-0b11011 - reserved for OS kernel
     * $gp: 0b11100 - global pointer
     * $sp: 0b11101 - stack pointer
     * $fp: 0b11110 - frame pointer
     * $ra: 0b11111 - return address
     */
    public static final int[] registers = new int[32];

    public static void cycle(Process process) {
        process.getOperation(PC[0]).execute();
        PC[0]++;
    }
}
