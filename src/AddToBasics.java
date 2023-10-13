import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AddToBasics implements ActionListener {
    JPanel addToBasicsPanel;
    JTextField orderNumber;
    JButton enterButton;
    JTextArea eventInfoText;
    JTextField eventName;

    List<List<String>> compactedMemoryStorage = InitializeMemory.getCompactMem();
    final int compactMemSize = InitializeMemory.getCompactMem().size();
    JFrame mainFrame = FrameManager.getFrame();
    private final String fileLocation = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath();
    private final String basicsFileLocation = fileLocation + "//Basics.txt";
    AddToBasics(){
       CreateGUI();
    }
    public void CreateGUI() {
        OptionPanel.ReturnToMainOnly();

        final boolean manualEdit = true;
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
        orderNumber = new JTextField(String.valueOf(compactMemSize+1),10);
        final int lastElm = 1;
        if (compactMemSize+1!=lastElm) orderNumberPanel.add(orderNumber);
        else{
            JPanel orderNumber1 = new JPanel();
            orderNumber1.setPreferredSize(new Dimension(100,30));
            JLabel orderNumber1Label = new JLabel("1");
            orderNumber1.add(orderNumber1Label);
            orderNumberPanel.add(orderNumber1);
        }
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
        for (int i =0; i<deleteEventList.size(); i++){
            if (deleteEventList.get(i).getModel().isSelected()){
                compactedMemoryStorage.remove(i);
                deleteEventList.remove(i);
                eventsDeleted+=1;
                i=0;
            }
            else if (CreateSchedule.EvaluateNum(orderNumberList.get(i).getText(),compactedMemoryStorage.size())) {
                String newNum = String.valueOf(Integer.parseInt(orderNumberList.get(i).getText())-1);
                ReorderList(String.valueOf(i- eventsDeleted), newNum);
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

        compactedMemoryStorage = InitializeMemory.getCompactMem();
        originalNum = String.valueOf(Integer.parseInt(originalNum));
        if (Integer.parseInt(originalNum) <0){
            originalNum = "0";
        }
        if (Integer.parseInt(newNum)>compactedMemoryStorage.size()){
            newNum = String.valueOf(compactedMemoryStorage.size()-1);
        }
        if (!originalNum.equals(newNum)) {
            List<String> tempCopy = compactedMemoryStorage.get(Integer.parseInt(originalNum));
            compactedMemoryStorage.remove(Integer.parseInt(originalNum));
            if (compactedMemoryStorage.size()==1){
                List<String> tempCopy2 = compactedMemoryStorage.get(0);
                compactedMemoryStorage.remove(0);
                compactedMemoryStorage.add(tempCopy);
                compactedMemoryStorage.add(tempCopy2);
            }
            else compactedMemoryStorage.add(Integer.parseInt(newNum), tempCopy);
        }

        //Writes compacted memory to Basics
        boolean first = true;
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(basicsFileLocation));
            for (List<String> list : compactedMemoryStorage)
                for (String word : list) {
                    String wordNoSpace = word.replaceAll("\\s", "");
                    if (wordNoSpace.equals("[en]")) {
                        if (!first) {
                            bw.newLine();
                        } else first = false;
                    }
                    bw.write(" " +wordNoSpace);
                }
            bw.close();
        } catch (Exception ex) {
            FrameController.ErrorMessage("An Error occurred, please try again");
            FrameController.ReturnToMain();
        }
    }
    private boolean checkNumString(String s){
        try {
            int numS = Integer.parseInt(s);
            return (numS <= compactMemSize + 1 & numS > 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e ){
        if (e.getSource()==enterButton){
            if (eventInfoText.getText().isEmpty() || eventName.getText().isEmpty()) {
                FrameController.ErrorMessage("Please Complete All Fields!");
            } else {
                String orderNumberValString = orderNumber.getText();
                int orderNumberVal;
                if (!checkNumString(orderNumberValString)) {
                    System.out.println("ypu");
                    orderNumberVal= compactMemSize+1;
                } else orderNumberVal = Integer.parseInt(orderNumberValString);
                List<String> memoryStorage = InitializeMemory.getMemStorage();
                memoryStorage.add(" [en] ");
                Collections.addAll(memoryStorage, eventName.getText().split(" "));
                memoryStorage.add(" [ei] ");
                Collections.addAll(memoryStorage, eventInfoText.getText().split(" "));
                memoryStorage.add(" [Time] ");
                memoryStorage.add("Pass");
                    if (orderNumberVal < compactMemSize) {
                        InitializeMemory.updateMemory(memoryStorage);
                        compactedMemoryStorage = InitializeMemory.CompactMem();
                        ReorderList(String.valueOf(compactedMemoryStorage.size()-1), String.valueOf(Integer.parseInt(orderNumber.getText())-1));
                    } else {
                        if (memoryStorage.get(0).isBlank()) {
                            memoryStorage.remove(0);                                                //more brute force
                        }
                        if (memoryStorage.get(0).equals(" [en] ") & memoryStorage.get(1).equals(" [en] "))
                            memoryStorage.remove(0);
                        boolean first = true;
                        try {
                            BufferedWriter bw = new BufferedWriter(
                                    new FileWriter(basicsFileLocation));
                            for (String word : memoryStorage) {
                                String wordNoSpace = word.replaceAll("\\s", "");
                                if (wordNoSpace.equals("[en]")) {
                                    if (!first) {
                                        bw.newLine();
                                    } else first = false;
                                }
                                bw.write(" " +word);
                            }
                            bw.close();

                        } catch (Exception ex) {
                            FrameController.ErrorMessage("An Error occurred, please try again");
                            FrameController.ReturnToMain();
                        }
                    }

                    }
            FrameController.ReturnToMain();
            new AddToBasics();
            return;
            }
        FrameController.ErrorMessage("Input Invalid");
    }
    private final static FrameLazySusanDisplay FrameLazySusanInstance = new FrameLazySusanDisplay();
}

