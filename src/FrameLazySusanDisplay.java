import com.sun.source.tree.Tree;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class FrameLazySusanDisplay implements ActionListener {
    private JButton enterManualEvent;
    private JButton nextPage;
    private Boolean classManualEdit;
    private Boolean classManualTime;
    private ArrayList<JToggleButton> deleteEventList;
    private ArrayList<JTextField> orderNumberList;
    private JPanel controlButtonsPanel;
    ArrayList<List> compactedMemoryStorage = InitalizeMemory.getCompactMem();
    final int compactMemSize = InitalizeMemory.getCompactMem().size() + 1;
    private static List<JTextField> endTimeEventFList;
    static List<JTextField> beginTimeEventFList;
    static List<JTextField> beginTimeEventSList;
    static List<JTextField> endTimeEventSList;

    TreeMap<Integer,JPanel> eventsToDisplay;
    JPanel manualEditDisplay;
    JPanel manualTimeDisplay;


    private JPanel CreateManualTimeDisplay() {

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

        JPanel manualTimeDisplay = new JPanel();
        manualTimeDisplay.setSize(new Dimension(200, 400));
        manualTimeDisplay.setBounds(300, 0, 300, 400);
        manualTimeDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        manualTimeDisplay.setBackground(Color.GRAY);
        manualTimeDisplay.add(manualTimeTitle);

        beginTimeEventFList = new ArrayList();
        for (int i = 1; i < compactMemSize; i++) {
            JTextField beginTimeEventF = new JTextField();
            beginTimeEventF.setName("beginTimeEventF" + String.valueOf(i));
            beginTimeEventFList.add(beginTimeEventF);
        }
        beginTimeEventSList = new ArrayList();
        for (int i = 1; i < compactMemSize; i++) {
            JTextField beginTimeEventS = new JTextField();
            beginTimeEventS.setName("beginTimeEventS" + String.valueOf(i));
            beginTimeEventSList.add(beginTimeEventS);
        }
        endTimeEventFList = new ArrayList();
        for (int i = 1; i < compactMemSize; i++) {
            JTextField endTimeEventF = new JTextField();
            endTimeEventF.setName("endTimeEventF" + String.valueOf(i));
            endTimeEventFList.add(endTimeEventF);
        }
        endTimeEventSList = new ArrayList();
        for (int i = 1; i < compactMemSize; i++) {
            JTextField endTimeEventS = new JTextField();
            endTimeEventS.setName("endTimeEventS" + String.valueOf(i));
            endTimeEventSList.add(endTimeEventS);
        }
        return manualTimeDisplay;
    }
    public JPanel CreateManualEditDisplay(){
        JPanel manualEditTitle = new JPanel();
        manualEditTitle.setPreferredSize(new Dimension(200, 30));
        manualEditTitle.setBackground(Color.GRAY);
        manualEditTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel manualEditTitleLabel = new JLabel("Event Edit");
        manualEditTitleLabel.setForeground(Color.WHITE);
        manualEditTitle.add(manualEditTitleLabel);

        JPanel manualEditDisplay = new JPanel();
        manualEditDisplay.setSize(new Dimension(200, 400));
        manualEditDisplay.setBounds(300, 0, 300, 400);
        manualEditDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        manualEditDisplay.setBackground(Color.GRAY);
        manualEditDisplay.add(manualEditTitle);

        return manualEditDisplay;
    }

    public JPanel CreateFrameLazySusanDisplay(Boolean manualTime, Boolean manualEdit) {
        // Manual Time Display code

        classManualEdit = manualEdit;
        classManualTime = manualTime;


        deleteEventList = new ArrayList();
        for (int i = 1; i < compactMemSize; i++) {
            JToggleButton deleteEventButton = new JToggleButton();
            deleteEventButton.setName("deleteEventButton" + String.valueOf(i));
            deleteEventButton.setBackground(Color.RED);
            JLabel deleteButtonLabel = new JLabel("Delete");
            deleteButtonLabel.setForeground(Color.WHITE);
            deleteButtonLabel.setFont(new Font("Times New Romans", Font.BOLD, 10));
            deleteEventButton.add(deleteButtonLabel);
            deleteEventButton.setBounds(150, 5, 70, 20);
            deleteEventList.add(deleteEventButton);
        }
        orderNumberList = new ArrayList();
        for (int i = 1; i < compactMemSize; i++) {
            JTextField orderNumberButton = new JTextField();
            orderNumberButton.setName("orderNumberButton" + String.valueOf(i));
            orderNumberButton.setBounds(90, 30, 40, 20);
            orderNumberList.add(orderNumberButton);
        }

        enterManualEvent = new JButton();
        enterManualEvent.setPreferredSize(new Dimension(120, 30));
        enterManualEvent.setBackground(Color.RED);
        enterManualEvent.addActionListener(this);
        JLabel enterManualTimeLabel = new JLabel("Enter Info");
        enterManualTimeLabel.setForeground(Color.WHITE);
        enterManualEvent.add(enterManualTimeLabel);

        nextPage = new JButton();
        nextPage.setPreferredSize(new Dimension(120, 30));
        nextPage.addActionListener(this);
        nextPage.setBackground(Color.WHITE);
        JLabel nextPageLabel = new JLabel("Next Page");
        nextPage.add(nextPageLabel);

        controlButtonsPanel = new JPanel();
        controlButtonsPanel.setPreferredSize(new Dimension(250, 40));
        controlButtonsPanel.setBackground(Color.GRAY);
        controlButtonsPanel.add(nextPage);
        controlButtonsPanel.add(enterManualEvent);


        if (classManualEdit) {
            manualEditDisplay = CreateManualEditDisplay();
        }
        if (classManualTime) {
            manualTimeDisplay = CreateManualTimeDisplay();
        }

        String title = "";
        Double fontSize = 0.0;
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
            }
            if (i >= maxDisplay & !fourEventsBool) {
                eventsToDisplay.put(i++, controlButtonsPanel);
                fourEventsBool = true;
            }
            //creates an event and adds elements
            JPanel event = new JPanel();
            event.setLayout(new FlowLayout(FlowLayout.LEFT));
            event.setBackground(Color.BLUE);

            //reads title to determine font size
            if (title.length() > 15) {
                title = title.substring(0, 15);
                title += "...";
            }

            if (manualEdit) {
                JPanel orderNumberPanel = new JPanel();
                orderNumberPanel.setBounds(80, 5, 70, 20);
                orderNumberPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                JLabel orderNumberPanelText = new JLabel("Order Number");
                orderNumberPanelText.setFont(new Font("Times New Roman", Font.PLAIN, 10));
                orderNumberPanel.add(orderNumberPanelText);

                JPanel eventNamePanel = new JPanel();
                eventNamePanel.setBounds(0, 5, 80, 20);
                eventNamePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                JLabel eventNamePanelLabel = new JLabel("Event Name");
                eventNamePanelLabel.setFont(new Font("Times New Roman", Font.PLAIN, 10));
                eventNamePanel.add(eventNamePanelLabel);
                JToggleButton deleteButton = deleteEventList.get(EventIncremental);
                event.setLayout(null);
                JTextField orderNumberButton = orderNumberList.get(EventIncremental);
                orderNumberButton.setPreferredSize(new Dimension(60, 20));
                EventIncremental++;

                JPanel eventTitlePanel = new JPanel();
                eventTitlePanel.setPreferredSize(new Dimension(50, 30));
                JLabel eventTitle = new JLabel(title);

                eventTitle.setForeground(Color.WHITE);
                eventTitle.setFont(new Font("Times New Roman", Font.PLAIN, 10));
                eventTitlePanel.add(eventTitle);
                eventTitlePanel.setBackground(Color.BLUE);
                eventTitlePanel.setBounds(0, 25, 85, 20);


                event.add(eventNamePanel);
                event.add(eventTitlePanel);
                event.add(orderNumberPanel);
                event.add(deleteButton, BorderLayout.EAST);
                event.add(orderNumberButton, BorderLayout.SOUTH);
                event.setPreferredSize(new Dimension(220, 60));
                eventsToDisplay.put(i, event);
            }
            if (manualTime) {
                JTextField beginTimeEventF = beginTimeEventFList.get(EventIncremental);
                JTextField beginTimeEventS = beginTimeEventSList.get(EventIncremental);
                JTextField endTimeEventF = endTimeEventFList.get(EventIncremental);
                JTextField endTimeEventS = endTimeEventSList.get(EventIncremental);
                EventIncremental++;

                JPanel blankSpace = new JPanel();
                blankSpace.setPreferredSize(new Dimension(70, 20));
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
                eventTitlePanel.setPreferredSize(new Dimension(70, 30));
                JLabel eventTitle = new JLabel(title);
                eventTitle.setFont(new Font("Times New Roman", Font.PLAIN, 10));
                eventTitlePanel.add(eventTitle);

                event.add(blankSpace);
                event.add(beginTime);
                event.add(endTime);

                event.add(eventTitlePanel);

                event.add(beginTimeEventF);
                event.add(beginTimeEventS);
                event.add(endTimeEventF);
                event.add(endTimeEventS);

                event.setPreferredSize(new Dimension(220, 70));
                eventsToDisplay.put(i, event);

            }
        }
        if (classManualTime) {
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
        if (classManualEdit) {
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
        if (classManualTime) {
            return manualTimeDisplay;
        }
        else{return manualEditDisplay;}

    }
    public static List<JTextField> getListOfBeginningTimesHour(){
        return beginTimeEventFList;
    }
    public static List<JTextField> getListOfBeginningTimesMin(){
        return beginTimeEventSList;
    }
    public static List<JTextField> getListOfEndingTimesHour(){
        return endTimeEventFList;
    }
    public static List<JTextField> getListOfEndingTimesMin(){
        return endTimeEventSList;
    }
    public List<JTextField> getOrderNumberChangeList(){
        return orderNumberList;
    }
    public List<JToggleButton> getDeleteEventList(){
        return deleteEventList;
    }
    @Override
    public void actionPerformed(ActionEvent e ){
        String buttonCommand = e.getActionCommand();
        if (buttonCommand.equals("nextPage")){
            int manualDisplayNumberOfEvents=5;
            Boolean First = true;
            if (classManualTime) {
                int sizeDifference = eventsToDisplay.size() - manualDisplayNumberOfEvents;
                if (!First) manualDisplayNumberOfEvents += 1;
                if (sizeDifference <= 0) {
                    FrameController.ErrorMessage("No more Events!");
                    return;
                }
                if (sizeDifference > 3) sizeDifference = 3;

                for (int i = 0; 3 > i; i++) {
                    manualTimeDisplay.remove(1);
                }
                int indexVal = 1;
                for (int i = 4; (i > 1); i--) {
                    if (manualDisplayNumberOfEvents > eventsToDisplay.size()) {
                        manualDisplayNumberOfEvents++;
                        return;
                    }
                    manualTimeDisplay.add(eventsToDisplay.get(manualDisplayNumberOfEvents), indexVal);
                    indexVal++;
                    manualDisplayNumberOfEvents++;
                }
                manualDisplayNumberOfEvents--;
                First = false;
            }
            if (classManualEdit) {
                int sizeDifference = eventsToDisplay.size() - manualDisplayNumberOfEvents;
                if (!First) manualDisplayNumberOfEvents += 1;
                if (sizeDifference <= 0) {
                    FrameController.ErrorMessage("No more Events!");
                    return;
                }
                if (sizeDifference > 3) sizeDifference = 3;

                for (int i = 0; 3 > i; i++) {
                    manualEditDisplay.remove(1);
                }
                int indexVal = 1;
                for (int i = 4; (i > 1); i--) {
                    if (manualDisplayNumberOfEvents > eventsToDisplay.size()) {
                        manualDisplayNumberOfEvents++;
                        return;
                    }
                    manualEditDisplay.add(eventsToDisplay.get(manualDisplayNumberOfEvents), indexVal);
                    indexVal++;
                    manualDisplayNumberOfEvents++;
                }
                manualDisplayNumberOfEvents--;
            }
            First = false;

        }

        if (buttonCommand.equals("enterManualTime")){
            if (classManualTime){
                CreateSchedule createScheduleInstance = OptionPanel.getCreateSchedule();
                createScheduleInstance.AssignScheduleValues();
            }
            if (classManualEdit) {
                AddToBasics addToBasicsInstance = OptionPanel.getAddToBasics();
                addToBasicsInstance.EnterManualEdit();
            }
        }

    }

}
