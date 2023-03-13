import javax.swing.*;
import java.awt.*;

public class Main {
    
    public static void main(String[] args) {
        Process mainProcess = new Process();
        
        MemoryManager.allocate(mainProcess, 5);
        MemoryManager.setCurrentProcess(mainProcess);
        
        MemoryManager.setContent(20, 6);
        MemoryManager.setContent(21, 2);
        MemoryManager.setContent(22, 0);
        MemoryManager.setContent(23, 3);
        
        /*
         $zero: 0 - constant zero
         $at: 1 - assembler temporary
         $v0-$v1: 2-3 - values for function results and expression evaluation
         $a0-$a3: 4-7 - arguments
         $t0-$t7: 8-15 - temporaries
         $s0-$s7: 16-23 - saved temporaries
         $t8-$t9: 24-25 - temporaries
         $k0-$k1: 26-27 - reserved for OS kernel
         $gp: 28 - global pointer
         $sp: 29 - stack pointer
         $fp: 30 - frame pointer
         $ra: 31 - return address
         */
        
        String[] regNames = {
            "$zero",
            "$at",
            "$v0", "$v1",
            "$a0", "$a1", "$a2", "$a3",
            "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
            "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7",
            "$t8", "$t9",
            "$k0", "$k1",
            "$gp",
            "$sp",
            "$fp", 
            "$ra"
        };
        
        final int 
            zero = 0,
            at = 1,
            v0 = 2, v1 = 3,
            a0 = 4, a1 = 5, a2 = 6, a3 = 7,
            t0 = 8, t1 = 9, t2 = 10, t3 = 11, t4 = 12, t5 = 13, t6 = 14, t7 = 15,
            s0 = 16, s1 = 17, s2 = 18, s3 = 19, s4 = 20, s5 = 21, s6 = 22, s7 = 23,
            t8 = 24, t9 = 25,
            k0 = 26, k1 = 27,
            gp = 28, sp = 29, fp = 30, ra = 31;

        Processor.registers[0b00010] = 214;
        Processor.registers[0b00101] = 0b0101;
        mainProcess.instrMemory = new Operation[] {
            new Operation(Instruction.ANDI, zero, a1, 10),  //$a1 = $zero | 10
            new Operation(Instruction.LW, zero, v1, 20),    //$v1 = M[0 + 20]
            new Operation(Instruction.SW, v1, v0, 15),      //M[6 + 15] = $v0
            new Operation(Instruction.LW, zero, v1, 21),    //$v1 = M[0 + 21]
            new Operation(Instruction.BEQ, v0, v1, -4)      //if 214 == 214, PC = PC + 1 - 4
        };
  
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("Panel.background", Color.BLACK);
        JFrame frame = new JFrame("Processor Monitor") {
            {
                getContentPane().setBackground(Color.BLACK);
                setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(20, 20, 20, 20);
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.weightx = 1.0;
                
                // Datapath Chart
                gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
                
                // Registers
                gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
                JPanel registers = new JPanel(); add(registers, gbc);
                JLabel[] regLabs = new JLabel[16]; // Need to store these in an array for updating later
                for (int i = 0; i < 16; i++) {
                    regLabs[i] = new JLabel(regNames[i] + ":  " + Processor.registers[i]);
                    registers.add(regLabs[i]);
                }
                registers.setLayout(new BoxLayout(registers, BoxLayout.Y_AXIS));
                
                // Program Counter & Next Instruction to Execute
                gbc.gridx = 1; gbc.gridy = 0;
                JLabel programCounter = new JLabel("Program Counter:  0");
                add(programCounter, gbc);
                
                gbc.gridx = 2; gbc.gridy = 0;
                JLabel executingInstruction = new JLabel("Executing Instruction:  " + mainProcess.getOperation(0));
                add(executingInstruction, gbc);
                
                // Cycle button
                gbc.gridx = 0; gbc.gridy = 0;
                JButton cycle = new JButton("Cycle"); add(cycle, gbc);
                cycle.addActionListener(e -> {
                    // Execute a single cycle on the processor
                    Processor.cycle(mainProcess);
        
                    // Update register storage labels
                    for (int i = 0; i < 16; i++)
                        regLabs[i].setText(regNames[i] + ":  " + Processor.registers[i]);
        
                    // Update program counter and executing instruction labels
                    programCounter      .setText("Program Counter:  "       + Processor.PC[0]);
                    if (Processor.PC[0] < mainProcess.instrMemory.length)
                        executingInstruction.setText("Executing Instruction:  " + mainProcess.getOperation(Processor.PC[0]));
                    else executingInstruction.setText("Finished execution.");
                });
            }
        };
        frame.setBounds(750, 0, 1180, 1000);
        frame.setVisible(true);
    }
}