

import jdk.jfr.Event;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.desktop.QuitEvent;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyFrame extends JFrame implements ActionListener {
    String fileLocation = this.getClass().getClassLoader().getResource("").getPath();
    String basicsFileLocation = fileLocation + "//Basics.txt";
    String TimeAssignedFile = fileLocation + "//Time.txt";
    Boolean Memory;
    ArrayList<String> memoryStorage;
    ArrayList<List> compactedMemoryStorage;
    int eventNum;
    Boolean eventCreation;

    //
    JButton addToBasics;
    JButton lookAtSchedule;
    JButton createSchedule;
    JButton returnToMain;
    MyTextfield inputBegin;
    JPanel optionPanel;


    //Add to Basics Buttons
    JLabel notificationNoEvents;
    JPanel addToBasicsPanel;
    JTextField orderNumber;
    JButton enterButton;
    JTextArea eventInfoText;
    JTextField eventName;
    JPanel panelNoEvents;
    JLabel notifText;

    //Look at Schedule Panel
    JPanel eventDisplayPanel;
    List<JButton> infoButtonList=new ArrayList<>();

    //Create Schedule
    Boolean First=true;
    JPanel displayCreate;
    JTextField beginTimeHour;
    JTextField beginTimeMin;
    JTextField endTimeHour;
    JTextField endTimeMin;

    JButton enter;
    JButton beginAM;
    JButton beginPM;
    JButton endAM;
    JButton endPM;
    String beginPorA = "";
    String endPorA = "";
    Integer beginTimeHourInt;
    Integer beginTimeMinInt;
    Integer endTimeHourInt;
    Integer endTimeMinInt;
    JButton enterManualEvent;
    JButton nextPage;
    JPanel manualTimeDisplay;
    JPanel manualEditDisplay;
    Boolean timeAltered = false;
    TreeMap<Integer,JPanel> eventsToDisplay;
    Integer manualTimeDisplayNumberOfEvents=5;
    Boolean mainTimeComponent = false;
    Boolean methodRan = false;
    int[] finalTimeArray;
    List<int[]> totalEventList = new ArrayList<int[]>();
    List<String> totalEventTimesList = new ArrayList<>();
    int totalManualEvents;
    int totalManualTimeHour;
    int totalManualTimeMin;
    List<List> timeCompactMemStor;

//    JTextField beginTimeEventF;
    List<JTextField> beginTimeEventFList;
    List<JTextField> beginTimeEventSList;
    List<JTextField> endTimeEventFList;
    List<JTextField> endTimeEventSList;
    List<JToggleButton> amButtonList;
    List<JToggleButton> pmButtonList;
    List<JPanel> PmAmPanelList;

// JTextField ManualEdit Panel
    List<JToggleButton> deleteEventList;
    List<JTextField> orderNumberList;
    Boolean manualTime=false;
    Boolean manualEdit=false;

    public void ReadMemory(){
        eventCreation = false;
        memoryStorage = new ArrayList();
        int i = 0;
        try {
            BufferedReader bw = new BufferedReader(
                    new FileReader(basicsFileLocation));
            String line;
            while((line = bw.readLine()) != null){
                for (String s:line.split(" ")) {
                    memoryStorage.add((" " + s));
                }
            }
            bw.close();
            Memory = true;
        }catch (Exception ex){
            Memory = false;
        }
        eventNum = 0;
        CompactMem();


    }
    public void CompactMem(){

        //compacts memory to lists within lists
        List<String> temp = new ArrayList<String>();

        compactedMemoryStorage = new ArrayList<List>();


        int blankOccurence = 0;
        memoryStorage.add(" [en] ");
        Boolean firstOccurence = true; 
        for (String word : memoryStorage) {
            if (word.equals(" ") || word.equals("")) {
                blankOccurence++;
                if (blankOccurence > 1) ;
            } else {
                blankOccurence = 0;
            }
            String wordNoSpace = word.replaceAll("\\s", "");
            temp.add(word);

            if (wordNoSpace.equals("[en]")) {
                if (firstOccurence == true) {
                    firstOccurence = false;
                } else {
                    temp.remove(temp.size() - 1);
                    eventNum++;
                    compactedMemoryStorage.add(new ArrayList(temp));
                    temp.clear();
                    temp.add(" [en] ");
                }
            }
            if (wordNoSpace.equals("[time]")) eventCreation = true;
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
    public void FrameLazySusan(Boolean manualTime, Boolean manualEdit) {
        // Manual Time Display code
        JPanel largeBlank = new JPanel();
        largeBlank.setPreferredSize(new Dimension(60, 25));
        largeBlank.setBackground(Color.GRAY);
        JPanel manualTimeTitle = new JPanel();
        manualTimeTitle.setPreferredSize(new Dimension(200, 30));
        manualTimeTitle.setBackground(Color.GRAY);
        manualTimeTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel manualTimeTitleLabel = new JLabel("Manual Event Time Input");
        manualTimeTitleLabel.setForeground(Color.WHITE);
        manualTimeTitle.add(manualTimeTitleLabel);

        manualTimeDisplay = new JPanel();
        manualTimeDisplay.setSize(new Dimension(200, 400));
        manualTimeDisplay.setBounds(300, 0, 300, 400);
        manualTimeDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        manualTimeDisplay.setBackground(Color.GRAY);
        manualTimeDisplay.add(manualTimeTitle);


        //Buttons for display
        //- Uses a list to create a unique readable button for each display event
        beginTimeEventFList = new ArrayList();
        for (int i = 1; i < compactedMemoryStorage.size() + 1; i++) {
            JTextField beginTimeEventF = new JTextField();
            beginTimeEventF.setName("beginTimeEventF" + String.valueOf(i));
            beginTimeEventFList.add(beginTimeEventF);
        }
        beginTimeEventSList = new ArrayList();
        for (int i = 1; i < compactedMemoryStorage.size() + 1; i++) {
            JTextField beginTimeEventS = new JTextField();
            beginTimeEventS.setName("beginTimeEventS" + String.valueOf(i));
            beginTimeEventSList.add(beginTimeEventS);
        }
        endTimeEventFList = new ArrayList();
        for (int i = 1; i < compactedMemoryStorage.size() + 1; i++) {
            JTextField endTimeEventF = new JTextField();
            endTimeEventF.setName("endTimeEventF" + String.valueOf(i));
            endTimeEventFList.add(endTimeEventF);
        }
        endTimeEventSList = new ArrayList();
        for (int i = 1; i < compactedMemoryStorage.size() + 1; i++) {
            JTextField endTimeEventS = new JTextField();
            endTimeEventS.setName("endTimeEventS" + String.valueOf(i));
            endTimeEventSList.add(endTimeEventS);
        }

        //ManualEdit Display Code
        JPanel manualEditTitle = new JPanel();
        manualEditTitle.setPreferredSize(new Dimension(200, 30));
        manualEditTitle.setBackground(Color.GRAY);
        manualEditTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel manualEditTitleLabel = new JLabel("Event Edit");
        manualTimeTitleLabel.setForeground(Color.WHITE);
        manualEditTitle.add(manualEditTitleLabel);

        manualEditDisplay = new JPanel();
        manualEditDisplay.setSize(new Dimension(200, 400));
        manualEditDisplay.setBounds(300, 0, 300, 400);
        manualEditDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        manualEditDisplay.setBackground(Color.GRAY);
        manualEditDisplay.add(manualEditTitle);




        //Buttons for display
        //- Uses a list to create a unique readable button for each display event
        deleteEventList = new ArrayList();
        for (int i = 1; i < compactedMemoryStorage.size() + 1; i++) {
            JToggleButton deleteEventButton = new JToggleButton();
            deleteEventButton.setName("deleteEventButton" + String.valueOf(i));
            deleteEventButton.setBackground(Color.RED);
            JLabel deleteButtonLabel = new JLabel("Delete");
            deleteButtonLabel.setForeground(Color.WHITE);
            deleteButtonLabel.setFont(new Font("Times New Romans",Font.BOLD,10));
            deleteEventButton.add(deleteButtonLabel);
            deleteEventButton.setBounds(130,5,70,20);
            deleteEventList.add(deleteEventButton);
        }
        orderNumberList = new ArrayList();
        for (int i = 1; i < compactedMemoryStorage.size() + 1; i++) {
            JTextField orderNumberButton = new JTextField();
            orderNumberButton.setName("orderNumberButton" + String.valueOf(i));
            orderNumberButton.setBounds(75,30,40,20);
            orderNumberList.add(orderNumberButton);
        }


        //Each Use
        enterManualEvent= new JButton();enterManualEvent.setPreferredSize(new Dimension(120, 30));enterManualEvent.setBackground(Color.RED);enterManualEvent.addActionListener(this);
        JLabel enterManualTimeLabel = new JLabel("Enter Info");
        enterManualTimeLabel.setForeground(Color.WHITE);enterManualEvent.add(enterManualTimeLabel);

        nextPage = new JButton();
        nextPage.setPreferredSize(new Dimension(120, 30));
        nextPage.addActionListener(this);
        nextPage.setBackground(Color.WHITE);
        JLabel nextPageLabel = new JLabel("Next Page");
        nextPage.add(nextPageLabel);

        JPanel controlButtonsPanel = new JPanel();
        controlButtonsPanel.setPreferredSize(new Dimension(250, 40));
        controlButtonsPanel.setBackground(Color.GRAY);
        controlButtonsPanel.add(nextPage);
        controlButtonsPanel.add(enterManualEvent);



        String title = "";
        Boolean titleBool = false;
        ArrayList<String> tempList = new ArrayList<>();
        eventsToDisplay = new TreeMap<>();
        int maxDisplay = 4;
        int i = 0;
        Boolean fourEventsBool = false;
        Boolean First = true;
        int EventIncremental = 0;
        for (List listItem : compactedMemoryStorage) {
            title = "";
            i++;
            for (Object eventWord : listItem) {

                String eventWordString = String.valueOf(eventWord).replaceAll("\\s", "");
                if (eventWordString.equals("[ei]")) titleBool = false;
                if (titleBool) title += (" " + eventWordString);
                if (eventWordString.equals("[en]")) {
                    titleBool = true;
                }
                if (eventCreation) {
                    if (eventWordString.equals("[time]")) {
                        //WIP
                    }
                } else {
                    if (eventWordString.equals("[en]") & !First) {
                        tempList.add("[time]");
                        tempList.add("");
                        for (String el : tempList) {

                        }
                    } else {
                        tempList.add(eventWordString);
                        First = false;
                    }

                }
            }
            if (i >= maxDisplay & !fourEventsBool) {
                eventsToDisplay.put(i++, controlButtonsPanel);
                fourEventsBool = true;
            }
            JPanel event = new JPanel();
            event.setLayout(new FlowLayout(FlowLayout.LEFT));
            event.setBackground(Color.BLUE);

            if (manualEdit) {
                JPanel orderNumberPanel = new JPanel();
                orderNumberPanel.setBounds(60,5,70,20);
                orderNumberPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
                JLabel orderNumberPanelText = new JLabel("Order Number");
                orderNumberPanelText.setFont(new Font("Times New Roman",Font.PLAIN,10));
                orderNumberPanel.add(orderNumberPanelText);

                JPanel eventNamePanel = new JPanel();
                eventNamePanel.setBounds(0,5,60,20);
                eventNamePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
                JLabel eventNamePanelLabel = new JLabel("Event Name");
                eventNamePanelLabel.setFont(new Font("Times New Roman",Font.PLAIN,10));
                eventNamePanel.add(eventNamePanelLabel);
                event.setPreferredSize(new Dimension(200,60));
                JToggleButton deleteButton = deleteEventList.get(EventIncremental);
                event.setLayout(null);
                JTextField orderNumberButton = orderNumberList.get(EventIncremental);
                orderNumberButton.setPreferredSize(new Dimension(50, 20));
                EventIncremental++;

                JPanel eventTitlePanel = new JPanel();
                eventTitlePanel.setPreferredSize(new Dimension(40, 30));
                JLabel eventTitle = new JLabel(title);
                eventTitle.setForeground(Color.WHITE);
                eventTitlePanel.add(eventTitle);
                eventTitlePanel.setBackground(Color.BLUE);
                eventTitlePanel.setBounds(0,20,60,20);


                event.add(eventNamePanel);
                event.add(eventTitlePanel);
                event.add(orderNumberPanel);
                event.add(deleteButton,BorderLayout.EAST);
                event.add(orderNumberButton,BorderLayout.SOUTH);
                eventsToDisplay.put(i, event);
            }
            if (manualTime) {
                JTextField beginTimeEventF = beginTimeEventFList.get(EventIncremental);
                JTextField beginTimeEventS = beginTimeEventSList.get(EventIncremental);
                JTextField endTimeEventF = endTimeEventFList.get(EventIncremental);
                JTextField endTimeEventS = endTimeEventSList.get(EventIncremental);
                EventIncremental++;

                JPanel blankSpace = new JPanel();
                blankSpace.setPreferredSize(new Dimension(40, 20));
                blankSpace.setBackground(Color.BLUE);

                JLabel beginTime = new JLabel("Begin Time");
                JLabel endTime = new JLabel("End Time");
                beginTime.setForeground(Color.WHITE);
                endTime.setForeground(Color.WHITE);
                beginTime.setPreferredSize(new Dimension(70, 20));
                endTime.setPreferredSize(new Dimension(60, 20));

                beginTimeEventF.setPreferredSize(new Dimension(30, 20));
                beginTimeEventS.setPreferredSize(new Dimension(30, 20));
                endTimeEventF.setPreferredSize(new Dimension(30, 20));
                endTimeEventS.setPreferredSize(new Dimension(30, 20));

                JPanel eventTitlePanel = new JPanel();
                eventTitlePanel.setPreferredSize(new Dimension(40, 30));
                JLabel eventTitle = new JLabel(title);
                eventTitlePanel.add(eventTitle);

                event.add(blankSpace);
                event.add(beginTime);
                event.add(endTime);

                event.add(eventTitlePanel);

                event.add(beginTimeEventF);
                event.add(beginTimeEventS);
                event.add(endTimeEventF);
                event.add(endTimeEventS);

                event.setPreferredSize(new Dimension(200, 70));
                eventsToDisplay.put(i, event);

            }
        }
        if (manualTime) {
            if (!fourEventsBool) {
                eventsToDisplay.put(++i, controlButtonsPanel);
            }
            if (eventsToDisplay.size() > 4) {
                for (i = 1; i < 5; i++) {
                    manualTimeDisplay.add(eventsToDisplay.get(i));              
                }
            } else {
                for (i = 1; i < eventsToDisplay.size() + 1; i++) {
                    manualTimeDisplay.add(eventsToDisplay.get(i));             
                }

            }
        }
            if (manualEdit) {
                if (!fourEventsBool) {
                    eventsToDisplay.put(++i, controlButtonsPanel);
                }
                if (eventsToDisplay.size() > 4) {
                    for (i = 1; i < 5; i++) {
                        manualEditDisplay.add(eventsToDisplay.get(i));              
                    }
                } else {
                    for (i = 1; i < eventsToDisplay.size() + 1; i++) {
                        manualEditDisplay.add(eventsToDisplay.get(i));              
                    }

                }

            }
        this.revalidate();
    }
    public void AddToBasics() {
        optionPanel.remove(lookAtSchedule);
        optionPanel.remove(addToBasics);
        optionPanel.remove(createSchedule);
        this.repaint();
        manualEdit = true;
        FrameLazySusan(false,true);

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
        if (compactedMemoryStorage.size()!=0) orderNumberPanel.add(orderNumber);
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
        eventLabel.setHorizontalAlignment((int)CENTER_ALIGNMENT);
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
        eventInfo.setHorizontalAlignment((int)CENTER_ALIGNMENT);
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
        notifText.setHorizontalAlignment((int)CENTER_ALIGNMENT);
        notifText.setFont(new Font("Times New Roman",Font.BOLD,16));

        notifText.setVisible(false);
        basicsNotif.add(notifText);


        //Adding each component to basics panel
        addToBasicsPanel.add(orderNumberPanel);
        addToBasicsPanel.add(eventNamePanel);
        addToBasicsPanel.add(eventInfoPanel);
        addToBasicsPanel.add(basicsNotif);



        addToBasicsPanel.setVisible(true);
        this.add(addToBasicsPanel);
        this.add(manualEditDisplay);
        this.revalidate();
        this.repaint();




    }
    public void ReturnToMain(){
        this.dispose();
        new MyFrame();
        this.revalidate();
    }
    public void LookAtSchedule() {
        optionPanel.remove(lookAtSchedule);
        optionPanel.remove(addToBasics);
        optionPanel.remove(createSchedule);
        //    notification for No events in Look at schedule
        Boolean FileExists = Files.exists(Path.of("Time.txt"));
        if (FileExists){
            Error("Schedule Times Not Yet Created! \n Please Click Create A New Schedule To Set Times");
            ReturnToMain();
            return;
        }
        List<String> tempList = new ArrayList<>();
        for (int i = 1; i< compactedMemoryStorage.size()+1; i++){
            JButton eventButton = new JButton("i");
            eventButton.setName("InfoButton" + String.valueOf(i));
            eventButton.setBackground(Color.white);
            eventButton.addActionListener(this);
            infoButtonList.add(eventButton);
        }


        eventNum = 0;
        CompactMem();





        panelNoEvents = new JPanel();
        panelNoEvents.setPreferredSize(new Dimension(200, 100));
        panelNoEvents.setBounds(200, 100, 200, 100);
        this.add(panelNoEvents);

        notificationNoEvents = new JLabel("No Events To Show");
        notificationNoEvents.setForeground(Color.RED);
        notificationNoEvents.setBackground(Color.GRAY);
        notificationNoEvents.setFont(new Font("Times New Roman", Font.BOLD, 20));
        panelNoEvents.add(notificationNoEvents);


        this.repaint();

        if (eventNum == 0) {
            panelNoEvents.setVisible(true);
            notificationNoEvents.setVisible(true);
            this.revalidate();

            Timer timer = new Timer(1600, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    panelNoEvents.setVisible(false);
                    optionPanel.setVisible(true);


                }
            });
            timer.setRepeats(false); // Execute the action only once
            timer.start();

        }else {
            panelNoEvents.setVisible(false);
//            create display for each
            eventDisplayPanel.setPreferredSize(new Dimension(600, 500));
            eventDisplayPanel.setBounds(0, 0, 600, 500);
            eventDisplayPanel.setVisible(true);

            //Booleans to indicate whether to add or stop adding items to event panels
            Boolean EventNameBool = false;
            Boolean EventInfoBool = false;
            Boolean EventTimeBool = false;
            Boolean firstWord = true;
            int eventOrder = 9999;

            String eventNameString = "";
            String eventTimeString = "";
            int EventNum = 1;
            TreeMap<Integer, JPanel> eventMapName = new TreeMap<>();
            String[] eventSplit;
            List<String> eventInfoList = new ArrayList<>();
            String eventInfoString = "";
            eventNum = 0;
            try {
                BufferedReader bw = new BufferedReader(
                        new FileReader(TimeAssignedFile));
                String line;
                while ((line = bw.readLine()) != null) {
                    JPanel event = new JPanel();
                    event.setPreferredSize(new Dimension(120, 100));
                    event.setBackground(Color.GREEN);
                    event.setLayout(new FlowLayout());

                    for (String s : line.split(" ")) {
                        String eventWord = s;
                        String eventWordString = String.valueOf(eventWord).replaceAll("\\s", "");
                        if (eventWordString.equals("[en]")) {
                            EventNameBool = true;
                            EventInfoBool = false;
                            EventTimeBool = false;
                            firstWord = true;
                        }
                        if (eventWordString.equals("[ei]")) {
                            EventNameBool = false;
                            EventInfoBool = true;
                            EventTimeBool = false;
                            firstWord = true;
                        }
                        if (eventWordString.equals("[Time]")) {
                            EventNameBool = false;
                            EventInfoBool = false;
                            EventTimeBool = true;
                            firstWord = true;
                        }
                        if (EventInfoBool) {
                            if (firstWord) firstWord = false;
                            else {
                                eventInfoString += (" " + (String) eventWordString);
                            }
                        }
                        if (EventNameBool) {
                            if (firstWord) firstWord = false;
                            else {
                                eventNameString += (String) eventWordString;
                            }
                        }
                        if (EventTimeBool) {
                            if (firstWord) firstWord = false;
                            else {
                                eventTimeString = eventWordString;
                            }
                        }
                    }

                    eventInfoList.add(eventInfoString);
                    JLabel eventName = new JLabel(eventNameString);
                    eventName.setFont(new Font("Times New Roman", Font.BOLD, 20));
                    eventName.setPreferredSize(new Dimension(80, 20));

                    eventSplit = eventTimeString.split("-");
                    if (Integer.valueOf(eventSplit[0]) > 12) {
                        eventSplit[0] = String.valueOf(Integer.valueOf(eventSplit[0]) - 12);
                    }
                    if (Integer.valueOf(eventSplit[2]) > 12) {
                        eventSplit[2] = String.valueOf(Integer.valueOf(eventSplit[2]) - 12);
                    }
                    String eventTimeS = eventSplit[0] + ":" + eventSplit[1] + "-->" + eventSplit[2] + ":" + eventSplit[3];
                    JLabel eventTime = new JLabel(eventTimeS);
                    eventTime.setFont(new Font("Times New Roman", Font.BOLD, 14));
                    eventTime.setPreferredSize(new Dimension(100, 25));
                    event.add(eventName);

                    event.add(eventTime);
                    JButton eventButton = infoButtonList.get(eventNum);
                    event.add(eventButton);


                    eventNameString = "";
                    eventTimeString = "";
                    eventInfoString = "";

                    event.setVisible(true);
                    eventMapName.put(eventNum, event);
                    eventNum++;

                }
                bw.close();
            } catch (Exception ex) {
            }


            int eventsDisplayed = -1;
            int increm = 0;
            Map<JButton, ActionListener> buttonActions = new HashMap<>();
            for (JButton eventButton : infoButtonList) {
                eventButton.putClientProperty("index", increm);
                ActionListener actionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton clickedButton = (JButton) e.getSource();
                        int buttonIndex = (int) eventButton.getClientProperty("index");
                        Error(eventInfoList.get(buttonIndex));
                    }
                };
                eventButton.addActionListener(actionListener);
                buttonActions.put(eventButton, actionListener);
                increm++;

            }

            for (int i = 0; i < eventMapName.lastKey() + 1; i++) {

                if (eventMapName.get(i) == null){
                    continue;
                }
                if (eventsDisplayed == eventMapName.lastKey()){
                    break;
                }
                else {
                    eventsDisplayed += 1;
                }
                eventDisplayPanel.add(eventMapName.get(i));
            }

                }

            }




    public static String CheckTime(String[] array,Boolean Increase,Boolean Military){
        // CheckTime(tempArray,true,false);
        if (!Military) {
            if (Increase) {
                if (Integer.valueOf(array[3]) > 59) {
                    while (Integer.valueOf(array[3]) > 59) {
                        array[3] = String.valueOf(Integer.valueOf(array[3]) - 60);
                        array[2] = String.valueOf(Integer.valueOf(array[2]) + 1);
                    }
                }
            } else {
                if (Integer.valueOf(array[1]) < 0 || Integer.valueOf(array[3]) < 0) {
                    while (Integer.valueOf(array[1]) < 0) {
                        array[1] = String.valueOf(Math.abs(Integer.valueOf(array[1])));
                        array[0] = String.valueOf(Integer.valueOf(array[0]) - 1);
                    }
                    while (Integer.valueOf(array[3]) < 0) {
                        array[3] = String.valueOf(Integer.valueOf(array[3]) + 60);
                        array[2] = String.valueOf(Integer.valueOf(array[2]) - 1);
                    }
                }
                if (Integer.valueOf(array[3]) > 60){
                    array[2] = String.valueOf(Integer.valueOf(array[2]) + 1);
                    array[3] = String.valueOf(Integer.valueOf(array[3]) - 60);
                }

            }
        }
        else{
            //non decrease or increase of # prior
        }
        String tempString = String.join("-", array);
        return tempString;
    }
    public void TimeAssignmentAlgorithim(){
        try{
            final int nonManualEvents = totalEventList.size() - totalManualEvents;
            final int nonManualTimeHour = finalTimeArray[0] - totalManualTimeHour;
            final int nonManualTimeMin = finalTimeArray[1] - totalManualTimeMin;
            final float nonManualTimeHourFloat = (float) nonManualTimeHour / (float) nonManualEvents;
            float hourRemainder = (nonManualTimeHourFloat * 60) % 60;
            final int[] eventTimeArray = {nonManualTimeHour / nonManualEvents, (nonManualTimeMin / nonManualEvents) + (int) hourRemainder};    //[avg hour: avg min]

            //edits compact Schedule and adds Time values
            Boolean timeRead = false;
            int currentTimeHour = beginTimeHourInt;
            int currentTimeMin = beginTimeMinInt;
            int lastHour = endTimeHourInt;
            int lastMin = endTimeMinInt;
            int calculatedHour = 0;
            int calculatedMin = 0;
            String calculatedTime = "";
            int timeDiffer = 0;
            String[] tempArray;
            String tempString = "";
            Boolean first = true;
            int eventShiftNum = 0;
            int eventNum = 0;
            List<List> storage = new ArrayList<>();
            int manualEventsRan = 0;
            List<Integer> timeIndexList = new ArrayList<>();
            timeCompactMemStor = compactedMemoryStorage.stream().collect(Collectors.toList());
            int nonManualEventsRan=0;

            //Best fit algorithim
            int bestFitHour = beginTimeHourInt;
            int bestFitMin  = beginTimeMinInt;

            for (int i = 0; i < totalEventTimesList.size(); i++){
                String val = totalEventTimesList.get(i);
                if (val.equals("blank")){
                    bestFitHour += eventTimeArray[0];
                    bestFitMin += eventTimeArray[1];
                    if (bestFitMin > 60){
                        bestFitHour ++;
                        bestFitMin -= 60;
                    }
                } else{
                    String eventTime = totalEventTimesList.get(eventNum);
                    String[] eventSplit = eventTime.split("-");
                    timeDiffer = (bestFitHour - Integer.valueOf(eventSplit[0])) *60 + bestFitMin - Integer.valueOf(eventSplit[1]);
                    //checks to see if time is closer as a PM value
                    if (timeDiffer >= 200) {

                        int possibleTime = (((bestFitHour - (Integer.valueOf(eventSplit[0]) + 12)) * 60) + (bestFitMin - Integer.valueOf(eventSplit[1])));
                        if (possibleTime < timeDiffer & possibleTime >= -200) {
                            eventSplit[0] = String.valueOf(Integer.valueOf(eventSplit[0]) + 12);
                            if (Integer.valueOf(eventSplit[0]) - 12 - Integer.valueOf(eventSplit[2]) < 0)
                                eventSplit[2] = String.valueOf(Integer.valueOf(eventSplit[2]) + 12);
                            totalEventTimesList.set(eventNum,String.join("-",eventSplit));

                        }
                    }
                    else if (timeDiffer <= -200){
                        int possibleTime = (((bestFitHour - (Integer.valueOf(eventSplit[0]) + 12)) *60) + (bestFitMin - Integer.valueOf(eventSplit[1])));
                        if (possibleTime > timeDiffer & timeDiffer <= 200){
                            timeDiffer = possibleTime;
                            eventSplit[0] = String.valueOf(Integer.valueOf(eventSplit[0]) + 12);
                            if (Integer.valueOf(eventSplit[0])- 12 - Integer.valueOf(eventSplit[2]) < 0) eventSplit[2] = String.valueOf(Integer.valueOf(eventSplit[2]) + 12);
                            totalEventTimesList.set(eventNum,String.join("-",eventSplit));

                        }
                    }
                    //recalculates timeDiffer and if it's still too large, it'll replace
                    timeDiffer = (bestFitHour - Integer.valueOf(eventSplit[0])) *60 + bestFitMin - Integer.valueOf(eventSplit[1]);

                    if (timeDiffer >= 200){
                        int remainder = timeDiffer % (eventTimeArray[0] * 60 + eventTimeArray[1]); //Simplifying: remainder = how much its digging into the previous time
                        eventShiftNum = Math.abs(Integer.valueOf(timeDiffer / (eventTimeArray[0] * 60 + eventTimeArray[1])));
                        int getVal = eventNum - (eventShiftNum);
                        List temp = compactedMemoryStorage.get(eventNum);
                        totalEventTimesList.remove(eventNum);
                        totalEventTimesList.add(getVal,val);
                        compactedMemoryStorage.remove(eventNum);
                        compactedMemoryStorage.add(getVal,temp);

                    }
                    else if (timeDiffer <= -200){
                        int remainder = timeDiffer % (eventTimeArray[0] * 60 + eventTimeArray[1]); //Simplifying: remainder = how much its digging into the previous time
                        eventShiftNum = Math.abs(Integer.valueOf(timeDiffer / (eventTimeArray[0] * 60 + eventTimeArray[1])));
                        int getVal = eventNum+(eventShiftNum+1);
                        List temp = compactedMemoryStorage.get(eventNum);
                        totalEventTimesList.remove(eventNum);
                        totalEventTimesList.add(getVal,val);
                        compactedMemoryStorage.remove(eventNum);
                        compactedMemoryStorage.add(getVal,temp);
                    }

                }

                eventNum++;
            }




            eventNum=0;
            timeDiffer = 0;

            for (int j = 0; j<compactedMemoryStorage.size(); j++) {
                List<String> list = compactedMemoryStorage.get(j);
                if (currentTimeMin > 59) {
                    currentTimeMin = 0;
                    currentTimeHour++;
                }
                timeRead = false;

                for (int i = 0; i < list.size(); i++) {
                    String eventWordString = String.valueOf(list.get(i)).replaceAll("\\s", "");             //rewrite to reading just last value of list
                    if (totalEventTimesList.get(eventNum).equals("Pass")) {
                        continue;
                    }
                    if (totalEventTimesList.get(eventNum).equals("blank")) {
                        if (timeRead) {
                            calculatedTime = String.valueOf(currentTimeHour) + "-" + String.valueOf(currentTimeMin) + "-";
                            calculatedHour = currentTimeHour + eventTimeArray[0];
                            calculatedMin = currentTimeMin + eventTimeArray[1];
                            while (calculatedMin > 59) {
                                if (calculatedMin > 59) {
                                    calculatedMin -= 60;
                                    calculatedHour++;
                                }
                            }
                            currentTimeHour = calculatedHour;
                            currentTimeMin = calculatedMin;
                            calculatedTime += String.valueOf(calculatedHour) + "-" + String.valueOf(calculatedMin);
                            timeCompactMemStor.get(eventNum).set(i,calculatedTime);
                            timeIndexList.add(i);
                            nonManualEventsRan++;
                        }

                    }
                    else {

                        if (timeRead){

                            String eventTime = totalEventTimesList.get(eventNum);
                            String[] eventSplit = eventTime.split("-");
                            timeDiffer = (currentTimeHour - Integer.valueOf(eventSplit[0])) *60 + currentTimeMin - Integer.valueOf(eventSplit[1]);

                            manualEventsRan++;

                            int remainder = timeDiffer % (eventTimeArray[0] * 60 + eventTimeArray[1]); //Simplifying: remainder = how much its digging into the previous time
                            eventShiftNum = Math.abs(Integer.valueOf(timeDiffer / (eventTimeArray[0] * 60 + eventTimeArray[1])));
                            //neg time = event in future | pos time = event in past
                            //method to find nearest time so standard time can be used for events

                            if (timeDiffer == 0){
                                calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                timeCompactMemStor.get(eventNum).set(i,calculatedTime);
                            }
                            if (timeDiffer < 0) {
                                //event is in future
                                if (remainder >= -20 || eventNum == timeCompactMemStor.size()-1) {
//                                    int getVal = eventNum + (eventShiftNum+1);
                                    int getVal = eventNum-(eventShiftNum+1);
                                    //event is twenty min in future    +20 current time too
                                    //elongate previous event and add new event after
                                    if (eventNum == timeCompactMemStor.size()-1){
                                        //edge case. Not certain how to fix so temp fix
                                        String val = String.valueOf(timeCompactMemStor.get(eventNum-1).get(timeIndexList.get(eventNum-1)));
                                        tempArray = val.split("-");

                                        tempString = CheckTime(tempArray, true, false);
                                        timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), tempString);
                                        calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                        timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                        currentTimeHour = Integer.valueOf(eventSplit[2]);
                                        currentTimeMin = Integer.valueOf(eventSplit[3]);
                                        timeIndexList.add(i);
                                    }else {


                                        String val = String.valueOf(timeCompactMemStor.get(eventNum - 1).get(timeIndexList.get(eventNum - 1)));
                                        tempArray = val.split("-");
                                        tempArray[3] = String.valueOf(Integer.valueOf(tempArray[3]) - remainder);
                                        tempString = CheckTime(tempArray, true, false);
                                        timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), tempString);
                                        calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                        timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                        currentTimeHour = Integer.valueOf(eventSplit[2]);
                                        currentTimeMin = Integer.valueOf(eventSplit[3]);
                                        timeIndexList.add(i);
                                        //reduces time

                                        timeRead = false;
                                        if (nonManualEvents - nonManualEventsRan != 0) {
                                            if (eventTimeArray[1] - (timeDiffer / (nonManualEvents - nonManualEventsRan)) < 0) {
                                                eventTimeArray[0] -= 1;
                                                eventTimeArray[1] = (eventTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan))) + 60;
                                            } else {
                                                eventTimeArray[1] = (eventTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)));
                                            }
                                        }
                                    }
                                } else {
                                    //event is more than twenty minutes in the future
                                    //remove and add to end. change val in time index
//                                    int getVal = eventNum + (eventShiftNum+1);               //new get val pulls from future events

                                    timeCompactMemStor.remove(eventNum);
                                    timeCompactMemStor.add(list);
                                    compactedMemoryStorage.add(list);
                                    String tempTime = totalEventTimesList.get(eventNum);
                                    totalEventTimesList.remove(eventNum);
                                    totalEventTimesList.add(tempTime);
                                    eventNum--;


                                    timeRead =false;
                                    break;
                                }


                            } else if (timeDiffer > 0) {
                                int getVal = Math.abs(eventNum - (eventShiftNum+1));
                                // event is in past
                                if (remainder <= 20) {
                                    //alters event before insertion event and reduces time of previous event
                                    String val = String.valueOf(timeCompactMemStor.get(getVal).get(timeIndexList.get(getVal)));
                                    tempArray = val.split("-");
                                    tempArray[3] = String.valueOf(Integer.valueOf(tempArray[3]) - remainder);
                                    tempString = CheckTime(tempArray, true, false);
                                    timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), tempString);
                                    calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                    timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                    currentTimeHour = Integer.valueOf(eventSplit[2]);
                                    currentTimeMin = Integer.valueOf(eventSplit[3]);
                                    timeIndexList.add(i);

                                    if (nonManualEvents - nonManualEventsRan!=0) {
                                        if (eventTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)) > 60) {
                                            eventTimeArray[0] += 1;
                                            eventTimeArray[1] = (eventTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan))) - 60;
                                        } else {
                                            eventTimeArray[1] = (eventTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)));
                                        }
                                    }

                                } else {
                                    //remainder is greater than 20
                                    //alters event before insertion event and reduces time
                                    if (eventNum >= timeCompactMemStor.size()-2) {
                                        //edge case. Not certain how to fix so temp fix
                                        String val = String.valueOf(timeCompactMemStor.get(eventNum - 1).get(timeIndexList.get(eventNum - 1)));
                                        tempArray = val.split("-");
                                        tempArray[2] = eventSplit[0];
                                        tempArray[3] = eventSplit[1];
                                        timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), String.join("-",tempArray));
                                        calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                        timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                        currentTimeHour = Integer.valueOf(eventSplit[2]);
                                        currentTimeMin = Integer.valueOf(eventSplit[3]);
                                        timeIndexList.add(i);

                                    } else {
                                        //part 1 of event
                                        String val = String.valueOf(timeCompactMemStor.get(getVal).get(timeIndexList.get(getVal)));
                                        tempArray = val.split("-");

                                        tempArray[2] = eventSplit[0];
                                        tempArray[3] = eventSplit[1];


                                        tempString = CheckTime(tempArray, true, false);
                                        timeCompactMemStor.get(getVal).set(timeIndexList.get(getVal), tempString);

                                        //manual event insertion
                                        calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                        timeCompactMemStor.remove(list);
                                        list.set(i, calculatedTime);

                                        timeCompactMemStor.add(getVal + 1, list);
                                        timeIndexList.add(getVal + 1, i);                 //current event so no need to remove. check
                                        //part 2 of event
                                        tempArray[0] = eventSplit[2];
                                        tempArray[1] = eventSplit[3];
                                        tempArray[2] = String.valueOf(Integer.valueOf(eventSplit[2]));
                                        tempArray[3] = String.valueOf(Integer.valueOf(eventSplit[3]) + remainder);
                                        tempString = CheckTime(tempArray, true, false);
                                        List temp = new ArrayList(timeCompactMemStor.get(getVal));     //grabs get val event (previous event to insertion)
                                        temp.set(timeIndexList.get(getVal), tempString);               //copies time index of get val event and adjusts time to be remainder
                                        timeCompactMemStor.add(getVal + 2, temp);                  // adds new remainder event after insertion event
                                        timeIndexList.add(getVal + 2, timeIndexList.get(getVal));
                                        totalEventTimesList.add(getVal + 2, "Pass");
                                        eventNum++;                                                   // because another event was added, eventNum should increase so it doesn't iterate over

                                        //adjust every future event
                                        int pastPresentTimeHour = Integer.valueOf(tempArray[2]);
                                        int pastPresentTimeMin = Integer.valueOf(tempArray[3]);
                                        String[] calcTimeInner = {tempArray[2], tempArray[3], tempArray[2], tempArray[3]};
                                        for (int startVal = getVal + 3; startVal < eventNum + 1; startVal++) {            //have it end at at the current event
                                            calcTimeInner[2] = String.valueOf(Integer.valueOf(calcTimeInner[2]) + eventTimeArray[0]);
                                            calcTimeInner[3] = String.valueOf(Integer.valueOf(calcTimeInner[3]) + eventTimeArray[1]);
                                            String value = CheckTime(calcTimeInner, true, false);
                                            timeCompactMemStor.get(startVal).set(timeIndexList.get(startVal), value);
                                            String val1 = calcTimeInner[2];
                                            String val2 = calcTimeInner[3];
                                            calcTimeInner[0] = val1;
                                            calcTimeInner[1] = val2;
                                        }
                                        timeRead = false;
                                        currentTimeHour = Integer.valueOf(calcTimeInner[2]);
                                        currentTimeMin = Integer.valueOf(calcTimeInner[3]);


                                    }
                                }


                            }


                        }

                    }

                    if (eventWordString.equals("[Time]")) timeRead = true;
                    else timeRead = false;

                }
                eventNum++;
//            storage.add(list);
            }


    }catch( Exception exception) {

        }
    }

    public void EnterManualTime() {
        if (!mainTimeComponent) {
            Error("Please Complete Schedule Time to the Left First!");
            return;
        }
        int beginTimeF = 0;
        int beginTimeS = 0;
        int endTimeF = 0;
        int endTimeS = 0;
        Boolean beginTime = false;
        Boolean endTime = false;
        totalManualEvents = 0;
        int[] currentTimeArray = {0};
        totalManualTimeHour = 0;
        totalManualTimeMin = 0;
        int[] blankArray = {0};
        String blankString = "blank";
        String stringTime = "";
        Boolean AM = false;
        Boolean PM = false;

        for (int i = 0; i < beginTimeEventFList.size(); i++) {
            beginTimeF = 0;
            beginTimeS = 0;
            endTimeF = 0;
            endTimeS = 0;
            beginTime = false;
            endTime = false;


            if (!(beginTimeEventFList.get(i).getText().equals("")) & EvaluteNum(beginTimeEventFList.get(i).getText(), 99999)) {
                beginTime = true;
                beginTimeF = Integer.valueOf(beginTimeEventFList.get(i).getText());
                timeAltered = true;
            }
            if (!(beginTimeEventSList.get(i).getText().equals("")) & EvaluteNum(beginTimeEventSList.get(i).getText(), 99999)) {
                beginTime = true;
                beginTimeS = Integer.valueOf(beginTimeEventSList.get(i).getText());
                timeAltered = true;
            }
            if (!(endTimeEventFList.get(i).getText().equals("")) & EvaluteNum(endTimeEventFList.get(i).getText(), 99999)) {
                endTime = true;
                endTimeF = Integer.valueOf(endTimeEventFList.get(i).getText());
                timeAltered = true;
            }
            if (!(endTimeEventSList.get(i).getText().equals("")) & EvaluteNum(beginTimeEventSList.get(i).getText(), 99999)) {
                endTime = true;
                endTimeS = Integer.valueOf(endTimeEventSList.get(i).getText());
                timeAltered = true;
            }
//            if (amButtonList.get(i).isSelected()){
//                AM = true;
//            }
//            else if (pmButtonList.get(i).isSelected()){
//                PM = true;
//            }
            int endTimeF1 = endTimeF;
            if (endTimeF < beginTimeF) {
                endTimeF1 = 12 + endTimeF;
            }
            if (endTimeF1 - beginTimeF < 0) {
                Error("Illegal Time at event" + i + 1);
                return;
            }
            if (endTimeF - beginTimeF == 0) {
                if (endTimeS - beginTimeS < 0) {
                    Error("Illegal Time at event" + i + 1);
                    return;
                }
            }
            if (beginTime & endTime) {
                stringTime = beginTimeF + "-" + beginTimeS + "-" + endTimeF1 + "-" + endTimeS;
                totalManualEvents += 1;
                currentTimeArray = TimeCalculation(beginTimeF, beginTimeS, endTimeF, endTimeS, "Pass", "Pass");    //running pass makes the time calculation method assume the smallest possible time and check it's plausibility
                totalManualTimeHour += currentTimeArray[0];
                totalManualTimeMin += currentTimeArray[1];
                totalEventList.add(currentTimeArray);
                totalEventTimesList.add(stringTime);
                stringTime = "";
            } else {
                totalEventList.add(blankArray.clone());
                totalEventTimesList.add(blankString);
            }
            AM = false;
            PM = false;
        }
        //Runs after collecting values from each event and assigns times to each event
        TimeAssignmentAlgorithim();

        //write to a time file

        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(TimeAssignedFile));
            for (List list : timeCompactMemStor) {
                for (Object val : list) {
                    String word = String.valueOf(val);
                    String wordNoSpace = word.replaceAll("\\s", "");
                    word = " " + wordNoSpace + " ";
                    bw.write(word);
                }
                bw.newLine();
            }
            bw.close();
        } catch (Exception ex) {
            return;
        }


    }
    public static boolean EvaluteNum(String Num, int maxValue){
        //Evaluates Num to check if it's a reasonable num
        //-Checks: Blank, not a number, negative

        if (Num.isEmpty() || Num.isBlank()) return false;
        for (char c : Num.toCharArray()){
            if (!Character.isDigit(c)) return false;
        }
        if (Integer.valueOf(Num) <= 0) return false;
        if (Integer.valueOf(Num) > maxValue)return false;
        return true;
    }
    public void EnterManualEdit(){
        int eventsDeleted =0;
        for (int i =0; i<orderNumberList.size(); i++){
            if (deleteEventList.get(i).getModel().isSelected()){
                compactedMemoryStorage.remove(i);
                eventsDeleted+=1;
            }
            else if (EvaluteNum(orderNumberList.get(i).getText(),compactedMemoryStorage.size())) {
                String newNum = orderNumberList.get(i).getText();
                ReorderList(String.valueOf(i + 1 - eventsDeleted), newNum);
            }
            else if (!deleteEventList.get(i).getModel().isSelected() || orderNumberList.get(i).getText().isEmpty()){
                continue;
            }else{
                Error("Order Number Input Contains Illegal Values!");
                return;
            }

            ReorderList("0","0");
            this.revalidate();
            this.repaint();
        }
    }
    public void CreateSchedule(){
        optionPanel.remove(lookAtSchedule);
        optionPanel.remove(addToBasics);
        optionPanel.remove(createSchedule);
        manualTime = true;
        FrameLazySusan(true,false);


        displayCreate = new JPanel();
        beginTimeHour = new JTextField();
        JLabel beginTimeLabel = new JLabel("Beginning Time");
        JLabel beginHour = new JLabel("Hour");
        JLabel beginMin = new JLabel("Min");
        beginTimeMin = new JTextField();
        endTimeHour = new JTextField();
        JLabel endTimeLabel = new JLabel("Ending Time");
        JLabel endMin = new JLabel("Min");
        JLabel endHour = new JLabel("Hour");
        endTimeMin = new JTextField();
        enter = new JButton("Enter");
        beginAM = new JButton("AM");
        beginPM = new JButton("PM");
        endAM = new JButton("AM");
        endPM = new JButton("PM");


        //
        displayCreate.setBounds(0,80,300,150);
        displayCreate.setLayout(new FlowLayout(FlowLayout.LEFT));
        displayCreate.setBackground(Color.gray);
        beginTimeHour.setPreferredSize(new Dimension(50,30));
        beginTimeLabel.setPreferredSize(new Dimension(110,30));
        beginTimeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1,true));
        beginMin.setPreferredSize(new Dimension(50,15));
        beginHour.setPreferredSize(new Dimension(60,15));
        endTimeLabel.setPreferredSize(new Dimension(130,30));
        endTimeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1,true));
        endHour.setPreferredSize(new Dimension(50,15));
        endMin.setPreferredSize(new Dimension(80,15));


        beginTimeMin.setPreferredSize(new Dimension(50,30));
        endTimeHour.setPreferredSize(new Dimension(50,30));
        endTimeMin.setPreferredSize(new Dimension(50,30));
        enter.setPreferredSize(new Dimension(50,30));
        beginTimeHour.setVisible(true);
        beginTimeMin.setVisible(true);
        endTimeHour.setVisible(true);
        endTimeMin.setVisible(true);
        enter.setVisible(true);
        displayCreate.add(beginTimeLabel);
        displayCreate.add(endTimeLabel);
        displayCreate.add(beginHour);
        displayCreate.add(beginMin);
        displayCreate.add(endHour);
        displayCreate.add(endMin);
        displayCreate.add(beginTimeHour);
        displayCreate.add(beginTimeMin);
        displayCreate.add(endTimeHour);
        displayCreate.add(endTimeMin);
        displayCreate.add(enter);
        displayCreate.add(beginAM);
        displayCreate.add(beginPM);
        displayCreate.add(endAM);
        displayCreate.add(endPM);
        displayCreate.setVisible(true);
        enter.addActionListener(this);
        beginAM.addActionListener(this);
        beginPM.addActionListener(this);
        endAM.addActionListener(this);
        endPM.addActionListener(this);
        this.add(displayCreate);
        this.add(manualTimeDisplay);


        this.revalidate();
        this.repaint();

    }
    public static int[] TimeCalculation(int beginTimeHour,int beginTimeMin,int endTimeHour,int endTimeMin,String beginPMorAM,String endPMorAM){
        Boolean minorEvent = false;
        if (beginPMorAM.equals("Pass")){
            minorEvent = true;
            //logic to decide PM or AM
            if (endTimeHour > 12){
                endTimeHour-=12;
            }
            if (beginTimeHour > 12){
                beginTimeHour-=12;
            }

            if (endTimeHour - beginTimeHour < 0){
                beginPMorAM = "AM";
                endPMorAM = "PM";
            }else {
                beginPMorAM = "AM";
                endPMorAM = "AM";
            }
        }
        //Calculates Overall Time Avaliable
        Integer finalTimeHour;
        Integer finalTimeMin;
        //check hours and minutes
        if (beginTimeMin > 60 || beginTimeMin < 0)return null;
        else if (endTimeMin > 60 || endTimeMin < 0) return null;
        else if (beginTimeHour > 12 || beginTimeHour < 0) return null;
        else if (endTimeHour > 12 || endTimeHour < 0) return null;
        //Correct values if different time periods and if minutes need to be carried from hours
        if (!endPMorAM.equals(beginPMorAM)) endTimeHour +=12;
        if (endTimeMin-beginTimeMin<0){
            endTimeHour -=1;
            endTimeMin = Math.abs(60-beginTimeMin);
            beginTimeMin = 0;
        }
        finalTimeHour = endTimeHour-beginTimeHour;
        finalTimeMin = endTimeMin-beginTimeMin;
        if (minorEvent){
            if (finalTimeHour > 10){
                int[] badTime = {8,0};
                return badTime;
            }
        }
        int[] finalTimeArray = {finalTimeHour,finalTimeMin};
        return finalTimeArray;
    }

    public void Error(String message){
        JOptionPane.showMessageDialog(this,message);
    }
    public MyFrame() {
        //          Memory Reader
        ReadMemory();



        //buttons for option panel interface
        addToBasics = new JButton("Add To Basics");
        addToBasics.addActionListener(this);
        lookAtSchedule = new JButton("Look At Schedule");
        lookAtSchedule.addActionListener(this);
        createSchedule = new JButton("Create a New Schedule");
        createSchedule.addActionListener(this);
        returnToMain = new JButton("Main Menu");
        returnToMain.addActionListener(this);


        // optional / selection panel for home screen interface
        optionPanel = new JPanel();
        optionPanel.setBackground(Color.BLACK);
        optionPanel.setLayout(new FlowLayout());
        optionPanel.setBounds(0,320,600,60);


        //frame setup
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(600,400));
        this.setLayout(null);
        this.pack();
        this.setResizable(false);

        inputBegin = new MyTextfield(200,100,200,100);
        this.add(inputBegin);
        inputBegin.setVisible(false);



        //Buttons for selection panel
        optionPanel.add(addToBasics);
        optionPanel.add(lookAtSchedule);
        optionPanel.add(createSchedule);
        optionPanel.add(returnToMain);
        this.add(optionPanel,BorderLayout.SOUTH);

        eventDisplayPanel = new JPanel();
        eventDisplayPanel.setBackground(Color.GRAY);
        eventDisplayPanel.setLayout(new FlowLayout());
        eventDisplayPanel.setPreferredSize(new Dimension(300,100));
        eventDisplayPanel.setBounds(300,0,300,600);
        eventDisplayPanel.setVisible(false);
        this.add(eventDisplayPanel);


        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Add to Basics Actions
        if (e.getSource() == addToBasics) {
            AddToBasics();
        }

        if (e.getSource() == enter) {
            if (manualTime) {
                if (!(beginTimeHour.getText().equals("") || beginTimeMin.getText().equals("") || endTimeHour.getText().equals("") || endTimeMin.getText().equals("")) & !methodRan) {
                    if (!(beginPorA.equals("") || endPorA.equals(""))) {
                        beginTimeHourInt = Integer.valueOf(beginTimeHour.getText());
                        beginTimeMinInt = Integer.valueOf(beginTimeMin.getText());
                        endTimeHourInt = Integer.valueOf(endTimeHour.getText());
                        endTimeMinInt = Integer.valueOf(endTimeMin.getText());
                        finalTimeArray = TimeCalculation(beginTimeHourInt, beginTimeMinInt, endTimeHourInt, endTimeMinInt, beginPorA, endPorA);
                        mainTimeComponent = true;
                        this.revalidate();
                        methodRan = true;
                    }
                }
            }

        }
        if (e.getSource() == enterButton) {
            if ((orderNumber.getText().isEmpty() & compactedMemoryStorage.size() == 1) || eventInfoText.getText().isEmpty() || eventName.getText().isEmpty()) {
                notifText.setVisible(true);
            } else {
                notifText.setVisible(false);
                //Write to Basics.txt

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
                    CompactMem();
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
        //Look at Schedule Actions
        if (e.getSource() == lookAtSchedule) {
            LookAtSchedule();
        }
        //Return to Main Actions
        if (e.getSource() == returnToMain) {
            ReturnToMain();
        }
        //Create Schedule Actions
        if (e.getSource() == createSchedule) {
            CreateSchedule();
        }
        if (e.getSource() == beginAM) {
            beginPorA = "AM";
            beginAM.getModel().setPressed(true);
            beginPM.getModel().setPressed(true);
            beginAM.removeActionListener(this);
            beginPM.removeActionListener(this);
            this.repaint();
        }
        if (e.getSource() == beginPM) {
            beginPorA = "PM";
            beginAM.getModel().setPressed(true);
            beginPM.getModel().setPressed(true);
            beginAM.removeActionListener(this);
            beginPM.removeActionListener(this);
            this.repaint();
        }
        if (e.getSource() == endAM) {
            endPorA = "AM";
            endAM.getModel().setPressed(true);
            endPM.getModel().setPressed(true);
            endAM.removeActionListener(this);
            endPM.removeActionListener(this);
            this.repaint();
        }
        if (e.getSource() == endPM) {
            endPorA = "PM";
            endAM.getModel().setPressed(true);
            endPM.getModel().setPressed(true);
            endAM.removeActionListener(this);
            endPM.removeActionListener(this);
            this.repaint();
        }
        if (e.getSource() == enterManualEvent) {
            if (manualTime) EnterManualTime();
            if (manualEdit) EnterManualEdit();
        }

        if (e.getSource() == nextPage) {
            //Features to Add: Create a loop of events

            if (manualTime) {
                int sizeDifference = eventsToDisplay.size() - manualTimeDisplayNumberOfEvents;
                if (!First) manualTimeDisplayNumberOfEvents += 1;
                if (sizeDifference <= 0) {
                    Error("No more Events!");
                    return;
                }
                if (sizeDifference > 3) sizeDifference = 3;

                for (int i = 0; 3 > i; i++) {
                    manualTimeDisplay.remove(1);
                }
                this.revalidate();
                this.repaint();
                int indexVal = 1;
                for (int i = 4; (i > 1); i--) {
                    if (manualTimeDisplayNumberOfEvents > eventsToDisplay.size()) {
                        manualTimeDisplayNumberOfEvents++;
                        this.revalidate();
                        return;
                    }
                    manualTimeDisplay.add(eventsToDisplay.get(manualTimeDisplayNumberOfEvents), indexVal);
                    indexVal++;
                    manualTimeDisplayNumberOfEvents++;
                }
                manualTimeDisplayNumberOfEvents--;
                First = false;
            }


            if (manualEdit) {
                int sizeDifference = eventsToDisplay.size() - manualTimeDisplayNumberOfEvents;
                if (!First) manualTimeDisplayNumberOfEvents += 1;
                if (sizeDifference <= 0) {
                    Error("No more Events!");
                    return;
                }
                if (sizeDifference > 3) sizeDifference = 3;

                for (int i = 0; 3 > i; i++) {
                    manualEditDisplay.remove(1);
                }
                this.revalidate();
                this.repaint();
                int indexVal = 1;
                for (int i = 4; (i > 1); i--) {
                    if (manualTimeDisplayNumberOfEvents > eventsToDisplay.size()) {
                        manualTimeDisplayNumberOfEvents++;
                        this.revalidate();
                        return;
                    }
                    manualEditDisplay.add(eventsToDisplay.get(manualTimeDisplayNumberOfEvents), indexVal);
                    indexVal++;
                    manualTimeDisplayNumberOfEvents++;
                }
                manualTimeDisplayNumberOfEvents--;
            }
            First = false;

        }
        this.revalidate();

        }



    public class MyTextfield extends JTextField {
        MyTextfield(int x, int y, int width, int height) {
            this.setPreferredSize(new Dimension(width, height));
            this.setFont(new Font("Times New Roman", Font.PLAIN, 20));
            this.setBounds(x, y, width, height);
            this.setVisible(true);
        }
    }

    public static void main(String args[]){new MyFrame();}
}

