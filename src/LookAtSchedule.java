import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class LookAtSchedule {
    private String fileLocation = this.getClass().getClassLoader().getResource("").getPath();
    private String TimeAssignedFile = fileLocation + "//Time.txt";
    private ArrayList<List> compactMem = InitalizeMemory.getCompactMem();
    private int compactMemSize = compactMem.size() + 1;
    private ArrayList<JButton> infoButtonList;
    JPanel eventDisplayPanel;
    List<String> eventInfoList = new ArrayList<>();


    public LookAtSchedule(){
        OptionPanel.ReturnToMainOnly();
        Boolean fileExists = CheckTimeFileExists();

        int startZeroIncrem =0;
        for (int i = 1; i< compactMemSize; i++){
            JButton eventButton = new JButton("i");
            eventButton.setName("InfoButton" + String.valueOf(startZeroIncrem));
            eventButton.setBackground(Color.white);
            eventButton.putClientProperty("index", i);
            eventButton.addActionListener(e -> {
                int buttonIndex = (int) eventButton.getClientProperty("index");
                FrameController.ErrorMessage(eventInfoList.get(buttonIndex));
            });
            startZeroIncrem++;
            infoButtonList.add(eventButton);
        }
        int eventNum = 0;
        if (compactMemSize == 1) {
            FrameController.ErrorMessage("No Events To Display!");
            FrameController.ReturnToMain();
        }else {
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
            String eventInfoString = "";
            eventNum = 0;
            try {
                BufferedReader bw = new BufferedReader(
                        new FileReader(TimeAssignedFile));
                String line;
                while ((line = bw.readLine()) != null) {
                    JPanel event = new JPanel();
                    event.setPreferredSize(new Dimension(120, 120));
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
                                eventNameString += " " + (String) eventWordString;
                            }
                        }
                        if (EventTimeBool) {
                            if (firstWord) firstWord = false;
                            else {
                                eventTimeString = eventWordString;
                            }
                        }
                    }
                    int blankIndex = 15;

                    if (eventNameString.length() > 12){
                        for (int i =12; i< eventNameString.length(); i++){
                            if (String.valueOf(eventNameString.charAt(i)).isBlank()) blankIndex = i;
                        }
                        if (eventNameString.length()>25){
                            eventNameString = eventNameString.substring(0,25) + "...";
                        }
                    }

                    eventInfoList.add(eventInfoString);
                    JLabel eventName = new JLabel("<html>" + eventNameString + "</html>");
                    eventName.setFont(new Font("Times New Roman", Font.BOLD, 14));
                    eventName.setVerticalAlignment(JLabel.TOP);
                    eventName.setPreferredSize(new Dimension(90, 40));

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
    private Boolean CheckTimeFileExists(){
        Boolean FileExists = false;
        File timeFile = new File(TimeAssignedFile);
        if (timeFile.exists() && !timeFile.isDirectory()) FileExists = true;
        if (!FileExists){
            final String errorMessage = "Schedule Times Not Yet Created! \n Please Click Create A New Schedule To Set Times";
            FrameController.ErrorMessage(errorMessage);
            FrameController.ReturnToMain();
        }
        return FileExists;
    }
}
