import javax.swing.*;
import java.awt.*;

public class Main {
    
    public static void main(String[] args) {
        Process mainProcess = new Process();

        mainProcess.setMapping(5, 2);
        mainProcess.setMapping(7, 3);
        Processor.mainMemory[mainProcess.getMapping(5)] = 1;
        Processor.mainMemory[mainProcess.getMapping(7)] = 2;
        Processor.registers[1] = 5;
        Processor.registers[3] = 6;
        mainProcess.instrMemory = new int[] {
                0b101011_00001_00000_0000000000000001, // SW reg1 reg0 0 --> store the word (0) in reg0 into the address stored by reg1 (6)
                0b100011_00001_00000_0000000000000000, // LW reg1 reg0 0 --> load the word (1) addressed by reg1 (5) into reg0
                0b100011_00001_00000_0000000000000010, // LW reg1 reg0 2 --> load the word (2) addressed by reg1 + 2 (7) into reg0
                0b101011_00001_00000_0000000000000001, // SW reg1 reg0 0 --> store the word (2) in reg0 into the address stored by reg1 + 1 (6)
                0b100011_00001_00000_0000000000000000, // LW reg1 reg0 0 --> load the word (1) addressed by reg1 (5) into reg0
                0b100011_00001_00000_0000000000000001, // LW reg1 reg0 0 --> load the word (2) addressed by reg1 + 1 (6) into reg0
                0b100011_00001_00000_0000000000000011, // LW reg1 reg0 0 --> load the word (0) addressed by reg1 + 3 (8) into reg0
                0b000010_00000000000000000000000000 // J 0 --> Jump to the first program instruction
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
                JPanel datapath   = new JPanel() {
                    private final Image datapathChart;
                    {
                        setLayout(null);
                        datapathChart = new ImageIcon(Toolkit.getDefaultToolkit().getImage("datapath.png")).getImage();
                        setPreferredSize(new Dimension(datapathChart.getWidth(this), datapathChart.getHeight(this)));
                    }
                    @Override
                    public void paintComponent(Graphics g) { // Override the paintComponent method to paint the datapath as background
                        super.paintComponent(g);
                        g.drawImage(datapathChart, 0, 0, this);
                    }
                }; add(datapath, gbc);
                JLabel RegDst     = new JLabel(Boolean.toString(Processor.RegDst    )); datapath.add(RegDst    );
                JLabel Jump       = new JLabel(Boolean.toString(Processor.Jump      )); datapath.add(Jump      );
                JLabel Branch     = new JLabel(Boolean.toString(Processor.Branch    )); datapath.add(Branch    );
                JLabel MemRead    = new JLabel(Boolean.toString(Processor.MemRead   )); datapath.add(MemRead   );
                JLabel MemtoReg   = new JLabel(Boolean.toString(Processor.MemtoReg  )); datapath.add(MemtoReg  );
                JLabel ALUOp      = new JLabel(Integer.toString(Processor.ALUOp     )); datapath.add(ALUOp     );
                JLabel MemWrite   = new JLabel(Boolean.toString(Processor.MemWrite  )); datapath.add(MemWrite  );
                JLabel ALUSrc     = new JLabel(Boolean.toString(Processor.ALUSrc    )); datapath.add(ALUSrc    );
                JLabel RegWrite   = new JLabel(Boolean.toString(Processor.RegWrite  )); datapath.add(RegWrite  );
                JLabel ALUControl = new JLabel(Integer.toString(Processor.ALUControl)); datapath.add(ALUControl);
                RegDst    .setBounds(466, 158, 50, 50);
                Jump      .setBounds(451, 179, 50, 50);
                Branch    .setBounds(464, 199, 50, 50);
                MemRead   .setBounds(486, 218, 50, 50);
                MemtoReg  .setBounds(490, 237, 50, 50);
                ALUOp     .setBounds(465, 257, 50, 50);
                MemWrite  .setBounds(486, 277, 50, 50);
                ALUSrc    .setBounds(468, 297, 50, 50);
                RegWrite  .setBounds(478, 319, 50, 50);
                ALUControl.setBounds(650, 580, 50, 50);
                
                // Registers
                gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
                JPanel registers = new JPanel(); add(registers, gbc);
                JLabel[] regLabs = new JLabel[16]; // Need to store these in an array for updating later
                for (int i = 0; i < 16; i++) {
                    regLabs[i] = new JLabel("reg" + i + ":  " + Processor.registers[i]);
                    registers.add(regLabs[i]);
                }
                registers.setLayout(new BoxLayout(registers, BoxLayout.Y_AXIS));
                
                // Program Counter & Next Instruction to Execute
                gbc.gridx = 1; gbc.gridy = 0;
                JLabel programCounter = new JLabel("Program Counter:  0");
                add(programCounter, gbc);
                
                gbc.gridx = 2; gbc.gridy = 0;
                JLabel executingInstruction = new JLabel("Executing Instruction:  " + mainProcess.getInstruction(0));
                add(executingInstruction, gbc);
                
                // Cycle button
                gbc.gridx = 0; gbc.gridy = 0;
                JButton cycle = new JButton("Cycle"); add(cycle, gbc);
                cycle.addActionListener(e -> {
                    // Execute a single cycle on the processor
                    Processor.cycle(mainProcess);
        
                    // Update register storage labels
                    for (int i = 0; i < 16; i++)
                        regLabs[i].setText("reg" + i + ":  " + Processor.registers[i]);
        
                    // Update program counter and executing instruction labels
                    programCounter      .setText("Program Counter:  "       + Processor.programCounter                            );
                    executingInstruction.setText("Executing Instruction:  " + mainProcess.getInstruction(Processor.programCounter));
        
                    // Update control labels
                    RegDst    .setText(Boolean.toString(Processor.RegDst    ));
                    Jump      .setText(Boolean.toString(Processor.Jump      ));
                    Branch    .setText(Boolean.toString(Processor.Branch    ));
                    MemRead   .setText(Boolean.toString(Processor.MemRead   ));
                    MemtoReg  .setText(Boolean.toString(Processor.MemtoReg  ));
                    ALUOp     .setText(Integer.toString(Processor.ALUOp     ));
                    MemWrite  .setText(Boolean.toString(Processor.MemWrite  ));
                    ALUSrc    .setText(Boolean.toString(Processor.ALUSrc    ));
                    RegWrite  .setText(Boolean.toString(Processor.RegWrite  ));
                    ALUControl.setText(Integer.toString(Processor.ALUControl));
                });
            }
        };

        frame.setBounds(750, 0, 1180, 1000);
        frame.setVisible(true);
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame() {
//            @Override
//            public void paint(Graphics g) {
//                super.paint(g);
//
//                Image img = null;
//                try {
//                    img = ImageIO.read(new File("datapath.png"));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                g.drawImage(img, 200, 200, this);
//            }
//        };
//        frame.setVisible(true);
//        frame.setBounds(750, 0, 1180, 1000);
//        frame.setLayout(null);
//
//
//        JButton cycle = new JButton("Cycle");
//        cycle.setBounds(20, 20, 80, 30);
//        frame.add(cycle);
//
//
//        JLabel label = new JLabel("label");
//        label.setBounds(20, 60, 80, 30);
//        frame.add(label);
//
//    }
}