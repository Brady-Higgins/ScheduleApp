

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionPanel{
    private static JPanel optionPanel;
    private static JButton addToBasics;
    private static JButton lookAtSchedule;
    private static JButton createSchedule;
    private static JButton returnToMain;
    private static AddToBasics addToBasicsInstance;
    private static LookAtSchedule lookAtScheduleInstance;
    private static CreateSchedule createScheduleInstance;

    private OptionPanel(){}
    private static JPanel CreateOptionPanel(){
        optionPanel = new JPanel();
        optionPanel.setBackground(Color.BLACK);
        optionPanel.setLayout(new FlowLayout());
        optionPanel.setBounds(0,320,600,60);

        addToBasics = new JButton("Add To Basics");
        addToBasics.addActionListener(e -> { addToBasicsInstance = new AddToBasics(); });
        lookAtSchedule = new JButton("Look At Schedule");
        lookAtSchedule.addActionListener(e -> { lookAtScheduleInstance = new LookAtSchedule();});
        createSchedule = new JButton("Create a New Schedule");
        createSchedule.addActionListener(e -> { createScheduleInstance = new CreateSchedule();});
        returnToMain = new JButton("Main Menu");
        returnToMain.addActionListener(e -> {FrameController.ReturnToMain();});

        optionPanel.add(addToBasics);
        optionPanel.add(lookAtSchedule);
        optionPanel.add(createSchedule);
        optionPanel.add(returnToMain);
        return optionPanel;
    }
    public static AddToBasics getAddToBasics(){
        return addToBasicsInstance;
    }
    public static CreateSchedule getCreateSchedule(){
        return createScheduleInstance;
    }
    public static LookAtSchedule getLookAtSchedule(){
        return lookAtScheduleInstance;
    }
    public static JPanel getOptionPanel(){
        if (optionPanel == null){
            optionPanel = CreateOptionPanel();
        }
        return optionPanel;
    }
    public static void ReturnToMainOnly(){
        //potential problem. Access
        optionPanel.remove(lookAtSchedule);
        optionPanel.remove(addToBasics);
        optionPanel.remove(createSchedule);
    }

}

