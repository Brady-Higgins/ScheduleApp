import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class LookAtSchedule {
    private final String fileLocation = this.getClass().getClassLoader().getResource("").getPath();
    private final String TimeAssignedFile = fileLocation + "//Time.txt";
    private final List<List<String>> compactMem = InitializeMemory.getCompactMem();

    private List<String> eventInfoList = new ArrayList<>();


    public LookAtSchedule(){
        OptionPanel.ReturnToMainOnly();
        Boolean fileExists = CheckTimeFileExists();
        if (fileExists){
            ArrayList<JButton> infoButtonList = new ArrayList<>();
            final int compactMemSize = compactMem.size() + 1;
            int startZeroIncrem =0;
            for (int i = 1; i< compactMemSize; i++){
                JButton eventButton = new JButton("i");
                eventButton.setName("InfoButton" + startZeroIncrem);
                eventButton.setBackground(Color.white);
                eventButton.putClientProperty("index", startZeroIncrem);
                eventButton.addActionListener(e -> {
                    int buttonIndex = (int) eventButton.getClientProperty("index");
                    FrameController.ErrorMessage(eventInfoList.get(buttonIndex));
                });
                startZeroIncrem++;
                infoButtonList.add(eventButton);
            }
        int eventNum;
        if (compactMemSize == 1) {
            FrameController.ErrorMessage("No Events To Display!");
            FrameController.ReturnToMain();
        }else {
            JPanel eventDisplayPanel = new JPanel();
            eventDisplayPanel.setPreferredSize(new Dimension(600, 500));
            eventDisplayPanel.setBounds(0, 0, 600, 500);
            eventDisplayPanel.setVisible(true);

            //Booleans to indicate whether to add or stop adding items to event panels
            boolean EventNameBool = false;
            boolean EventInfoBool = false;
            boolean EventTimeBool = false;
            boolean firstWord = true;

            StringBuilder eventNameStringBuilder = new StringBuilder();
            String eventTimeString = "";
            TreeMap<Integer, JPanel> eventMapName = new TreeMap<>();
            String[] eventSplit;
            StringBuilder eventInfoStringBuilder = new StringBuilder();
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
                        String eventWordString = String.valueOf(s).replaceAll("\\s", "");
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
                                eventWordString =" " + eventWordString;
                                eventInfoStringBuilder.append(eventWordString);
                            }
                        }
                        if (EventNameBool) {
                            if (firstWord) firstWord = false;
                            else {
                                eventWordString =" " + eventWordString;
                                eventInfoStringBuilder.append(eventWordString);
                            }
                        }
                        if (EventTimeBool) {
                            if (firstWord) firstWord = false;
                            else {
                                eventTimeString = eventWordString;
                            }
                        }
                    }
                    String eventNameString = eventNameStringBuilder.toString();
                    if (eventNameString.length() > 25) {
                        eventNameString = eventNameString.substring(0, 25) + "...";
                    }

                    eventInfoList.add(eventInfoStringBuilder.toString());
                    JLabel eventName = new JLabel("<html>" + eventNameString + "</html>");
                    eventName.setFont(new Font("Times New Roman", Font.BOLD, 14));
                    eventName.setVerticalAlignment(JLabel.TOP);
                    eventName.setPreferredSize(new Dimension(90, 40));

                    eventSplit = eventTimeString.split("-");
                    if (Integer.parseInt(eventSplit[0]) > 12) {
                        eventSplit[0] = String.valueOf(Integer.parseInt(eventSplit[0]) - 12);
                    }
                    if (Integer.parseInt(eventSplit[2]) > 12) {
                        eventSplit[2] = String.valueOf(Integer.parseInt(eventSplit[2]) - 12);
                    }
                    String eventTimeS = eventSplit[0] + ":" + eventSplit[1] + "-->" + eventSplit[2] + ":" + eventSplit[3];
                    JLabel eventTime = new JLabel(eventTimeS);
                    eventTime.setFont(new Font("Times New Roman", Font.BOLD, 14));
                    eventTime.setPreferredSize(new Dimension(100, 25));
                    event.add(eventName);
                    event.add(eventTime);
                    JButton eventButton = infoButtonList.get(eventNum);
                    event.add(eventButton);
                    eventTimeString = "";
                    eventNameStringBuilder.setLength(0);
                    eventInfoStringBuilder.setLength(0);
                    event.setVisible(true);
                    eventMapName.put(eventNum, event);
                    eventNum++;

                }
                bw.close();
            } catch (Exception ex) {
                System.out.println(ex);
                FrameController.ErrorMessage("Error in application, resetting.");
                FrameController.ReturnToMain();
            }
            int eventsDisplayed = -1;
            for (int i = 0; i < eventMapName.lastKey() + 1; i++) {

                if (eventMapName.get(i) == null) {
                    continue;
                }
                if (eventsDisplayed == eventMapName.lastKey()) {
                    break;
                } else {
                    eventsDisplayed += 1;
                }
                eventDisplayPanel.add(eventMapName.get(i));
            }
            FrameManager.getFrame().add(eventDisplayPanel);
            FrameManager.getFrame().revalidate();
            FrameManager.getFrame().repaint();
        }
        }
    }
    private Boolean CheckTimeFileExists(){
        boolean FileExists = false;
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
