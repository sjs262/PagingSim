import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        Process mainProcess = new Process();

        mainProcess.pageTable.setMapping(5, 2);
        mainProcess.pageTable.setMapping(7, 3);
        Processor.mainMemory[mainProcess.pageTable.getMapping(5)] = 1;
        Processor.mainMemory[mainProcess.pageTable.getMapping(7)] = 2;
        Processor.registers[1] = 5;
        Processor.registers[3] = 6;
        mainProcess.instrMemory = new int[] {
                0b101011_00001_00000_0000000000000001, // SW reg3 reg0 0 --> store the word (0) in reg0 into the address stored by reg3 (6)
                0b100011_00001_00000_0000000000000000, // LW reg1 reg0 0 --> load the word (1) addressed by reg1 (5) into reg0
                0b100011_00001_00000_0000000000000010, // LW reg1 reg0 2 --> load the word (2) addressed by reg1 + 2 (7) into reg0
                0b101011_00001_00000_0000000000000001, // SW reg1 reg0, 0 --> store the word (2) in reg0 into the address stored by reg1 + 1 (6)
                0b100011_00001_00000_0000000000000000, // LW reg1 reg0 0 --> load the word (1) addressed by reg1 (5) into reg0
                0b100011_00001_00000_0000000000000001, // LW reg1 reg0 0 --> load the word (2) addressed by reg1 + 1 (6) into reg0
                0b100011_00001_00000_0000000000000011, // LW reg1 reg0 0 --> load the word (0) addressed by reg1 + 3 (8) into reg0
                0b000010_00000000000000000000000000 // J 0 --> Jump to the first program instruction
        };

        Frame frame = new Frame();
        frame.setBounds(1210, 0, 720, 1000);
        frame.setLayout(null);
        frame.setTitle("Processor Data Monitor");
        frame.setBackground(Color.BLACK);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
        frame.setVisible(true);

        Label[] registerStorage = new Label[16];
        for (int i = 0; i < 16; i++) {
            Label registerName = new Label("reg" + i + ":");
            registerName.setBounds(20, 80 + 40 * i, 40, 30);
            registerName.setForeground(Color.WHITE);
            frame.add(registerName);

            registerStorage[i]  = new Label(Integer.toString(Processor.registers[i]));
            registerStorage[i].setBounds(70, 80 + 40 * i, 100, 30);
            registerStorage[i].setForeground(Color.WHITE);
            frame.add(registerStorage[i]);
        }

        Label programCounter = new Label("PC:");
        programCounter.setBounds(110, 40, 30, 30);
        programCounter.setForeground(Color.WHITE);
        frame.add(programCounter);

        Label PC = new Label("0");
        PC.setBounds(150, 40, 60, 30);
        PC.setForeground(Color.WHITE);
        frame.add(PC);

        Label execInstr = new Label("Executing Instruction:");
        execInstr.setBounds(220, 40, 130, 30);
        execInstr.setForeground(Color.WHITE);
        frame.add(execInstr);

        Label EI = new Label(mainProcess.getInstruction(0).toString());
        EI.setBounds(360, 40, 200, 30);
        EI.setForeground(Color.WHITE);
        frame.add(EI);

        Button cycle = new Button("Cycle");
        cycle.addActionListener(e -> {
            Processor.cycle(mainProcess);
            for (int i = 0; i < 16; i++)
                registerStorage[i].setText(Integer.toString(Processor.registers[i]));
            PC.setText(Integer.toString(Processor.programCounter));
            EI.setText(mainProcess.getInstruction(Processor.programCounter).toString());
        });
        cycle.setBounds(20, 40, 80, 30);
        frame.add(cycle);
    }
}