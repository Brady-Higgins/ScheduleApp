

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddToBasics implements ActionListener {
    JLabel notificationNoEvents;
    JPanel addToBasicsPanel;
    JTextField orderNumber;
    JButton enterButton;
    JTextArea eventInfoText;
    JTextField eventName;
    JPanel panelNoEvents;
    JLabel notifText;
    ArrayList<List> compactedMemoryStorage = InitalizeMemory.getCompactMem();
    final int compactMemSize = InitalizeMemory.getCompactMem().size() + 1;
    JFrame mainFrame = FrameManager.getFrame();
    private String fileLocation = this.getClass().getClassLoader().getResource("").getPath();
    private String basicsFileLocation = fileLocation + "//Basics.txt";
    AddToBasics(){
       CreateGUI();
    }
    public void CreateGUI() {
        OptionPanel.ReturnToMainOnly();

        final Boolean manualEdit = true;
        JPanel lazySusanDisplay = new FrameLazySusanDisplay().CreateFrameLazySusanDisplay(!manualEdit,manualEdit);

        //Add to basics Panel
        addToBasicsPanel = new JPanel();
        addToBasicsPanel.setPreferredSize(new Dimension(250,300));
        addToBasicsPanel.setBounds(0,0,250,300);
        addToBasicsPanel.setBackground(Color.GRAY);


        // Panel for order number
        JPanel orderNumberPanel = new JPanel();
        orderNumberPanel.setPreferredSize(new Dimension(200, 50));
        orderNumberPanel.setLayout(new FlowLayout());

        // Label for order number
        JLabel orderNumberLabel = new JLabel("Order Number: ");
        orderNumberPanel.add(orderNumberLabel);

        //orderNumber button
        enterButton = new JButton("Enter All");
        enterButton.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        enterButton.setPreferredSize(new Dimension(70,20));
        enterButton.addActionListener(this);
        orderNumberPanel.add(enterButton);

        // Text field for order number
        orderNumber = new JTextField(10);
        final int lastElm = 1;
        if (compactMemSize!=lastElm) orderNumberPanel.add(orderNumber);
        else{
            JPanel orderNumber1 = new JPanel();
            orderNumber1.setPreferredSize(new Dimension(100,30));
            JLabel orderNumber1Label = new JLabel("1");
            orderNumber1.add(orderNumber1Label);
            orderNumberPanel.add(orderNumber1);
        }
        //           Event Name

        // Panel for Event Name
        JPanel eventNamePanel = new JPanel();
        eventNamePanel.setPreferredSize(new Dimension(200, 50));
        eventNamePanel.setLayout(new BorderLayout());

        // Label for Event Name
        JLabel eventLabel = new JLabel("Event Name");
        eventLabel.setHorizontalAlignment(SwingConstants.CENTER);
        eventNamePanel.add(eventLabel);

        // Text field for event name
        eventName = new JTextField(10);
        eventNamePanel.add(eventName,BorderLayout.SOUTH);

        //            Event info

        // Panel for Event Info
        JPanel eventInfoPanel = new JPanel();
        eventInfoPanel.setPreferredSize(new Dimension(200, 100));
        eventInfoPanel.setLayout(new BorderLayout());

        // Label for Event Info
        JLabel eventInfo = new JLabel("Event Info");
        eventInfo.setHorizontalAlignment(SwingConstants.CENTER);
        eventInfoPanel.add(eventInfo);

        // Text area for event info
        eventInfoText = new JTextArea();
        eventInfoText.setPreferredSize(new Dimension(20,60));
        eventInfoText.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        eventInfoText.setLineWrap(true);
        eventInfoText.setWrapStyleWord(true);
        eventInfoPanel.add(eventInfoText,BorderLayout.SOUTH);

        //              Notification

        JPanel basicsNotif = new JPanel();
        basicsNotif.setPreferredSize(new Dimension(200, 60));
        basicsNotif.setLayout(new BorderLayout());

        notifText = new JLabel("Please Answer All");
        notifText.setForeground(Color.RED);
        notifText.setHorizontalAlignment(SwingConstants.CENTER);
        notifText.setFont(new Font("Times New Roman",Font.BOLD,16));

        notifText.setVisible(false);
        basicsNotif.add(notifText);


        //Adding each component to basics panel
        addToBasicsPanel.add(orderNumberPanel);
        addToBasicsPanel.add(eventNamePanel);
        addToBasicsPanel.add(eventInfoPanel);
        addToBasicsPanel.add(basicsNotif);



        addToBasicsPanel.setVisible(true);
        mainFrame.add(addToBasicsPanel);
        mainFrame.add(lazySusanDisplay);
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    public void EnterManualEdit(){
        List<JTextField> orderNumberList = FrameLazySusanInstance.getOrderNumberChangeList();
        List<JToggleButton> deleteEventList = FrameLazySusanInstance.getDeleteEventList();
        int eventsDeleted =0;
        for (int i =0; i<orderNumberList.size(); i++){
            if (deleteEventList.get(i).getModel().isSelected()){
                compactedMemoryStorage.remove(i);
                eventsDeleted+=1;
            }
            else if (CreateSchedule.EvaluateNum(orderNumberList.get(i).getText(),compactedMemoryStorage.size())) {
                String newNum = orderNumberList.get(i).getText();
                ReorderList(String.valueOf(i + 1 - eventsDeleted), newNum);
            }
            else if (!deleteEventList.get(i).getModel().isSelected() || orderNumberList.get(i).getText().isEmpty()){
                continue;
            }else{
                FrameController.ErrorMessage("Order Number Input Contains Illegal Values!");
                return;
            }
            ReorderList("0","0");

        }
    }
    public void ReorderList(String originalNum, String newNum){
        //reorders the values on Basics.txt based on Compact Mem. If originalNum = newNum it's used to write to memory
        //Rearranges Compacted memory
        if (!originalNum.equals(newNum)) {
            List<String> tempCopy = compactedMemoryStorage.get(Integer.valueOf(originalNum) - 1);
            compactedMemoryStorage.remove(Integer.valueOf(originalNum) - 1);
            compactedMemoryStorage.add(Integer.valueOf(newNum) - 1, tempCopy);
        }

        //Writes compacted memory to Basics
        Boolean first = true;
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(basicsFileLocation));
            for (List<String> list : compactedMemoryStorage)
                for (String word : list) {
                    String wordNoSpace = word.replaceAll("\\s", "");
                    if (wordNoSpace.replaceAll("\\s", "").equals("[en]")) {
                        if (!first) {
                            bw.newLine();
                        } else first = false;
                    }
                    bw.write(wordNoSpace+" ");
                }
            bw.close();
        } catch (Exception ex) {
            return;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e ){
        if (e.getSource()==enterButton){
            if ((orderNumber.getText().isEmpty() & compactedMemoryStorage.size() == 1) || eventInfoText.getText().isEmpty() || eventName.getText().isEmpty()) {
                notifText.setVisible(true);
            } else {
                notifText.setVisible(false);
                //Write to Basics.txt
                List<String> memoryStorage = InitalizeMemory.getMemStorage();
                if (compactedMemoryStorage.size() >= 1) {
                    memoryStorage.remove(memoryStorage.size() - 1);              //brute force error handeling. Actual Logic should be used to fix it later. (Read Mem)
                }
                memoryStorage.add(" [en] ");
                memoryStorage.add(eventName.getText()); //event name
                memoryStorage.add(" [ei] ");
                memoryStorage.add(eventInfoText.getText());   //event info
                memoryStorage.add(" [Time] ");
                memoryStorage.add("Pass");
                String orderNumberVal = "1";
                if (compactedMemoryStorage.size() != 0) {
                    orderNumberVal = orderNumber.getText();
                }
                if (Integer.valueOf(orderNumberVal) <= compactedMemoryStorage.size()) {
                    InitalizeMemory.updateMemory(memoryStorage);
                    compactedMemoryStorage = InitalizeMemory.CompactMem();
                    ReorderList(String.valueOf(compactedMemoryStorage.size()), orderNumber.getText());
                } else {
                    if (memoryStorage.get(0).isBlank()){
                        memoryStorage.remove(0);                                                //more brute force
                    }
                    if (memoryStorage.get(0).equals(" [en] ") & memoryStorage.get(1).equals(" [en] "))
                        memoryStorage.remove(0);
                    Boolean first = true;
                    try {
                        BufferedWriter bw = new BufferedWriter(
                                new FileWriter(basicsFileLocation));
                        for (String word : memoryStorage) {
                            String wordNoSpace = word.replaceAll("\\s", "");
                            if (wordNoSpace.replaceAll("\\s", "").equals("[en]")) {
                                if (!first) {
                                    bw.newLine();
                                } else first = false;
                            }
                            bw.write(word);
                        }
                        bw.close();
                    } catch (Exception ex) {
                        return;
                    }

                }
            }
        }

    }
    private static FrameLazySusanDisplay FrameLazySusanInstance = new FrameLazySusanDisplay();
}

