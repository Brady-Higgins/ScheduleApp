import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.List;

public class CreateSchedule implements ActionListener {
    private JButton enter;
    static JTextField beginTimeMin;
    static JTextField beginTimeHour;
    static JTextField endTimeHour;
    static JTextField endTimeMin;
    JButton beginAM;
    JButton beginPM;
    JButton endAM;
    JButton endPM;
    static String beginTimePMorAM = "";
    static String endTimePMorAM="";
    private int totalManualEvents;
    static Boolean mainTimeComponent = false;
    List<String> totalEventTimesList = new ArrayList<>();
    private String fileLocation = this.getClass().getClassLoader().getResource("").getPath();
    private String TimeAssignedFile = fileLocation + "//Time.txt";
    private void CreateGUI(){
        Boolean manualTime = true;
        JPanel lazySusanDisplay = new FrameLazySusanDisplay().CreateFrameLazySusanDisplay(manualTime, !manualTime);

        JPanel displayCreate = new JPanel();
        JLabel beginTimeLabel = new JLabel("Beginning Time");
        JLabel beginHour = new JLabel("Hour");
        JLabel beginMin = new JLabel("Min");
        beginTimeHour = new JTextField();
        beginTimeMin = new JTextField();
        endTimeHour = new JTextField();
        endTimeMin = new JTextField();
        JLabel endTimeLabel = new JLabel("Ending Time");
        JLabel endMin = new JLabel("Min");
        JLabel endHour = new JLabel("Hour");
        enter = new JButton("Enter");
        beginAM = new JButton("AM");
        beginPM = new JButton("PM");
        endAM = new JButton("AM");
        endPM = new JButton("PM");

        displayCreate.setBounds(0, 80, 300, 150);
        displayCreate.setLayout(new FlowLayout(FlowLayout.LEFT));
        displayCreate.setBackground(Color.gray);
        beginTimeHour.setPreferredSize(new Dimension(50, 30));
        beginTimeLabel.setPreferredSize(new Dimension(110, 30));
        beginTimeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        beginMin.setPreferredSize(new Dimension(50, 15));
        beginHour.setPreferredSize(new Dimension(60, 15));
        endTimeLabel.setPreferredSize(new Dimension(130, 30));
        endTimeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        endHour.setPreferredSize(new Dimension(50, 15));
        endMin.setPreferredSize(new Dimension(80, 15));


        beginTimeMin.setPreferredSize(new Dimension(50, 30));
        endTimeHour.setPreferredSize(new Dimension(50, 30));
        endTimeMin.setPreferredSize(new Dimension(50, 30));
        enter.setPreferredSize(new Dimension(50, 30));
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
        FrameManager.getFrame().add(displayCreate);
        FrameManager.getFrame().add(lazySusanDisplay);
    }
    CreateSchedule() {
        OptionPanel.ReturnToMainOnly();
        CreateGUI();

    }
    public void AssignScheduleValues(){
        if (!mainTimeComponent) {
            FrameController.ErrorMessage("Please Complete Schedule Time to the Left First!");
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

        int[] blankArray = {0};
        String blankString = "blank";
        String stringTime = "";
        Boolean AM = false;
        Boolean PM = false;
        int totalManualTimeHour=0;
        int totalManualTimeMin=0;


        List<JTextField> beginTimeEventFList = FrameLazySusanDisplay.getListOfEndingTimesHour();
        List<JTextField> beginTimeEventSList = FrameLazySusanDisplay.getListOfBeginningTimesMin();
        List<JTextField> endTimeEventFList = FrameLazySusanDisplay.getListOfEndingTimesHour();
        List<JTextField> endTimeEventSList = FrameLazySusanDisplay.getListOfEndingTimesMin();

        for (int i = 0; i < beginTimeEventFList.size(); i++) {
            beginTimeF = 0;
            beginTimeS = 0;
            endTimeF = 0;
            endTimeS = 0;
            beginTime = false;
            endTime = false;


            if (!(beginTimeEventFList.get(i).getText().equals("")) & EvaluateNum(beginTimeEventFList.get(i).getText(), 99999)) {
                beginTime = true;
                beginTimeF = Integer.valueOf(beginTimeEventFList.get(i).getText());
            }
            if (!(beginTimeEventSList.get(i).getText().equals("")) & EvaluateNum(beginTimeEventSList.get(i).getText(), 99999)) {
                beginTime = true;
                beginTimeS = Integer.valueOf(beginTimeEventSList.get(i).getText());
            }
            if (!(endTimeEventFList.get(i).getText().equals("")) & EvaluateNum(endTimeEventFList.get(i).getText(), 99999)) {
                endTime = true;
                endTimeF = Integer.valueOf(endTimeEventFList.get(i).getText());
            }
            if (!(endTimeEventSList.get(i).getText().equals("")) & EvaluateNum(beginTimeEventSList.get(i).getText(), 99999)) {
                endTime = true;
                endTimeS = Integer.valueOf(endTimeEventSList.get(i).getText());
            }

            int endTimeF1 = endTimeF;
            if (endTimeF < beginTimeF) {
                endTimeF1 = 12 + endTimeF;
            }
            if (endTimeF1 - beginTimeF < 0) {
                FrameController.ErrorMessage("Illegal Time at event" + i + 1);
                return;
            }
            if (endTimeF - beginTimeF == 0) {
                if (endTimeS - beginTimeS < 0) {
                    FrameController.ErrorMessage("Illegal Time at event" + i + 1);
                    return;
                }
            }
            if (beginTime & endTime) {
                stringTime = beginTimeF + "-" + beginTimeS + "-" + endTimeF1 + "-" + endTimeS;
                totalManualEvents += 1;
                currentTimeArray = TimeAssignmentAlgorithim.TimeCalculation(beginTimeF, beginTimeS, endTimeF, endTimeS, "Pass", "Pass");    //running pass makes the time calculation method assume the smallest possible time and check it's plausibility
                totalManualTimeHour += currentTimeArray[0];
                totalManualTimeMin += currentTimeArray[1];
                totalEventTimesList.add(stringTime);
                stringTime = "";
            } else {
                totalEventTimesList.add(blankString);
            }
            AM = false;
            PM = false;
        }
        //Runs after collecting values from each event and assigns times to each event


        List<List> timeCompactMemStor = new TimeAssignmentAlgorithim().TimeAssignmentAlgorithim(totalManualEvents,totalManualTimeHour,totalManualTimeMin);

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == beginAM) {
            beginTimePMorAM = "AM";
            beginAM.getModel().setPressed(true);
            beginPM.getModel().setPressed(true);
            beginAM.removeActionListener(this);
            beginPM.removeActionListener(this);
        }
        if (e.getSource() == beginPM) {
            beginTimePMorAM = "PM";
            beginAM.getModel().setPressed(true);
            beginPM.getModel().setPressed(true);
            beginAM.removeActionListener(this);
            beginPM.removeActionListener(this);
        }
        if (e.getSource() == endAM) {
            endTimePMorAM = "AM";
            endAM.getModel().setPressed(true);
            endPM.getModel().setPressed(true);
            endAM.removeActionListener(this);
            endPM.removeActionListener(this);
        }
        if (e.getSource() == endPM) {
            endTimePMorAM = "PM";
            endAM.getModel().setPressed(true);
            endPM.getModel().setPressed(true);
            endAM.removeActionListener(this);
            endPM.removeActionListener(this);
        }


    }



    public static int[] getTimeFrame() {
        if (!(beginTimeHour.getText().equals("") || beginTimeMin.getText().equals("") || endTimeHour.getText().equals("") || endTimeMin.getText().equals(""))) {
            if (!(beginTimePMorAM.equals("") || endTimePMorAM.equals(""))) {
                int beginTimeHourInt = Integer.valueOf(beginTimeHour.getText());
                int beginTimeMinInt = Integer.valueOf(beginTimeMin.getText());
                int endTimeHourInt = Integer.valueOf(endTimeHour.getText());
                int endTimeMinInt = Integer.valueOf(endTimeMin.getText());
                int[] TimeElapsed = TimeAssignmentAlgorithim.TimeCalculation(beginTimeHourInt, beginTimeMinInt, endTimeHourInt, endTimeMinInt, beginTimePMorAM, endTimePMorAM);
                //TimeElapsed = difference of end time and begin time = [Hour, Min]
                mainTimeComponent = true;
                return TimeElapsed;
            }
        }
        return null;
    }
    public static boolean EvaluateNum(String Num, int maxValue){
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
    public static Boolean getMainTimeComponent(){
        return mainTimeComponent;
    }
    public static int getBeginTimeHour(){
        return Integer.valueOf(beginTimeHour.getText());
    }
    public static int getBeginTimeMin(){
        return Integer.valueOf(beginTimeMin.getText());
    }
    public static int getEndTimeHour(){
        return Integer.valueOf(endTimeHour.getText());
    }
    public static int getEndTimeMin(){
        return Integer.valueOf(endTimeMin.getText());
    }
    public String getBeginTimePMorAM() {
        return beginTimePMorAM;
    }

    public String getEndTimePMorAM() {
        return beginTimePMorAM;
    }
}
