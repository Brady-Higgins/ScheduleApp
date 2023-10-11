
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class TimeAssignmentAlgorithm {
    private List<List<String>> compactMemStorage = InitializeMemory.getCompactMem();
    List<String> totalEventTimesList;

    public List<List<String>> AssignTimeVals(int totalManualEvents, int totalManualTimeHour, int totalManualTimeMin, List<String> eventTimes) {
        totalEventTimesList = eventTimes;
        int nonManualEvents = InitializeMemory.getCompactMem().size() - totalManualEvents;
        int[] finalTimeArray = CreateSchedule.getTimeFrame();
        int nonManualTimeHour = finalTimeArray[0] - totalManualTimeHour;
        int nonManualTimeMin = finalTimeArray[1] - totalManualTimeMin;
        final float nonManualTimeHourFloat = (float) nonManualTimeHour / (float) nonManualEvents;
        float hourRemainder = (nonManualTimeHourFloat * 60) % 60;
        final int[] averageTimeArray = {nonManualTimeHour / nonManualEvents, (nonManualTimeMin / nonManualEvents) + (int) hourRemainder};    //[avg hour: avg min]
        boolean timeRead = false;
        int currentTimeHour = CreateSchedule.getBeginTimeHour();
        int currentTimeMin = CreateSchedule.getBeginTimeMin();
        int calculatedHour;
        int calculatedMin;
        String calculatedTime;
        int timeDiffer;
        String[] tempArray;
        String tempString;
        int eventShiftNum;
        int eventNum = 0;
        List<Integer> timeIndexList = new ArrayList<>();
        int nonManualEventsRan = 0;

        compactMemStorage = BestFitList(averageTimeArray);
        List<List<String>> timeCompactMemStor = BestFitList(averageTimeArray);
        if (!checkConflictingTimes()) {
            FrameController.ErrorMessage("Conflicting Times");
            FrameController.ReturnToMain();
            return null;
        }
        for (int j = 0; j < timeCompactMemStor.size(); j++) {
            List<String> list = timeCompactMemStor.get(eventNum);
            while (currentTimeMin > 59) {
                currentTimeMin = currentTimeMin - 60;
                currentTimeHour++;
            }
            for (int i = 0; i < list.size(); i++) {
                String eventWordString = String.valueOf(list.get(i)).replaceAll("\\s", "");
                if (timeRead) {
                    if (totalEventTimesList.get(eventNum).equals("Ignore")) {
                        break;
                    } else if (totalEventTimesList.get(eventNum).equals("blank")) {

                        calculatedTime = currentTimeHour + "-" + currentTimeMin + "-";
                        calculatedHour = currentTimeHour + averageTimeArray[0];
                        calculatedMin = currentTimeMin + averageTimeArray[1];
                        while (calculatedMin > 59) {
                            calculatedMin -= 60;
                            calculatedHour++;
                        }
                        currentTimeHour = calculatedHour;
                        currentTimeMin = calculatedMin;
                        calculatedTime += calculatedHour + "-" + calculatedMin;
                        list.set(i, calculatedTime);
                        timeIndexList.add(i);
                        nonManualEventsRan++;
                        timeRead=false;
                        break;
                    } else {
                        String eventTime = totalEventTimesList.get(eventNum);
                        String[] eventSplit = eventTime.split("-");
                        int beginHour = Integer.parseInt(eventSplit[0]);
                        int beginMin = Integer.parseInt(eventSplit[1]);
                        int endHour = Integer.parseInt(eventSplit[2]);
                        int endMin = Integer.parseInt(eventSplit[3]);
                        timeDiffer = ((currentTimeHour - beginHour) * 60 + currentTimeMin - beginMin);
                        int remainder = timeDiffer % (averageTimeArray[0] * 60 + averageTimeArray[1]);
                        eventShiftNum = Math.abs((timeDiffer / (averageTimeArray[0] * 60 + averageTimeArray[1])));
                        //neg time = event in future | pos time = event in past
                        if (timeDiffer == 0) {
                            calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                            currentTimeHour = Integer.parseInt(eventSplit[2]);
                            currentTimeMin = Integer.parseInt(eventSplit[3]);
                            timeIndexList.add(i);
                            list.set(i, calculatedTime);
                        }
                        if (timeDiffer < 0) {
                            //event is in the future
                            /*
                             * (case 1) event is 20 min in future or last event= elongate previous event and alter time
                             * (case 2) event is more than 20 min in future = move event forward 1
                             */
                            if (remainder >= -20 || eventNum == timeCompactMemStor.size() - 1) {
                                if (eventNum - 1 < 0) {
                                    calculatedTime = beginHour + "-" + beginMin + "-" + endHour + "-" + endMin;
                                    list.set(i, calculatedTime);
                                } else {
                                    System.out.println("a");
                                    String val = String.valueOf(timeCompactMemStor.get(eventNum - 1).get(timeIndexList.get(eventNum - 1)));
                                    tempArray = val.split("-");
                                    tempArray[3] += Math.abs(remainder);
                                    tempString = CheckTime(tempArray, true);
                                    timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), tempString);
                                    calculatedTime = beginHour + "-" + beginMin + "-" + endHour + "-" + endMin;
                                    timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                }
                                currentTimeHour = endHour;
                                currentTimeMin = endMin;
                                timeIndexList.add(i);
                                if (nonManualEvents-nonManualEventsRan!=0) {
                                    if (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)) < 0) {
                                        while (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)) < 0) {
                                            averageTimeArray[0] -= 1;
                                            averageTimeArray[1] = (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan))) + 60;
                                        }
                                    } else {
                                        averageTimeArray[1] = (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)));
                                    }
                                }
                            } else {
                                System.out.println("b");
                                //event is more than twenty minutes in the future
//                                    int getVal = eventNum + (eventShiftNum+1);               //new get val pulls from future events
                                timeCompactMemStor.remove(eventNum);
                                timeCompactMemStor.add(timeCompactMemStor.size(), list);
                                String tempTime = totalEventTimesList.get(eventNum);
                                totalEventTimesList.remove(eventNum);
                                totalEventTimesList.add(timeCompactMemStor.size() - 1, tempTime);
                                eventNum--;
                                j--;
                            }
                        } else if (timeDiffer > 0) {
                            int getVal = Math.abs(eventNum - (eventShiftNum + 1));
                            // event is in past
                            if (remainder <= 20) {
                                if (eventNum - 1 < 0) {
                                    System.out.println("d");
                                    calculatedTime = beginHour + "-" + beginMin + "-" + endHour + "-" + endMin;
                                    list.set(i, calculatedTime);
                                    currentTimeHour = endHour;
                                    currentTimeMin = endMin;
                                    timeIndexList.add(i);
                                } else {
                                    System.out.println("c");
                                    String val = String.valueOf(timeCompactMemStor.get(eventNum - 1).get(timeIndexList.get(eventNum - 1)));
                                    tempArray = val.split("-");
                                    tempArray[3] = String.valueOf(Integer.parseInt(tempArray[3]) - remainder);
                                    tempString = CheckTime(tempArray, false);
                                    timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), tempString);
                                    calculatedTime = beginHour + "-" + beginMin + "-" + endHour + "-" + endMin;
                                    timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                    currentTimeHour = Integer.parseInt(eventSplit[2]);
                                    currentTimeMin = Integer.parseInt(eventSplit[3]);
                                    timeIndexList.add(i);
                                }
                                if (nonManualEvents-nonManualEventsRan!=0) {
                                    if (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)) > 60) {
                                        averageTimeArray[0] -= 1;
                                        averageTimeArray[1] = (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan))) - 60;
                                    } else {
                                        averageTimeArray[1] = (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)));
                                    }
                                }
                            } else {
                                System.out.println("e");
                                String val = String.valueOf(timeCompactMemStor.get(getVal).get(timeIndexList.get(getVal)));
                                tempArray = val.split("-");

                                tempArray[2] = eventSplit[0];
                                tempArray[3] = eventSplit[1];
                                tempString = CheckTime(tempArray, true);
                                timeCompactMemStor.get(getVal).set(timeIndexList.get(getVal), tempString);

                                //manual event insertion
                                calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                timeCompactMemStor.remove(list);
                                list.set(i, calculatedTime);

                                timeCompactMemStor.add(getVal + 1, list);
                                timeIndexList.add(getVal + 1, i);                 //current event so no need to remove. check
                                String tempTime = totalEventTimesList.get(eventNum);
                                totalEventTimesList.remove(eventNum);
                                totalEventTimesList.add(getVal+1,tempTime);
                                //part 2 of event
                                tempArray[0] = eventSplit[2];
                                tempArray[1] = eventSplit[3];
                                tempArray[2] = String.valueOf(Integer.parseInt(eventSplit[2]));
                                tempArray[3] = String.valueOf(Integer.parseInt(eventSplit[3]) + remainder);
                                tempString = CheckTime(tempArray, true);
                                List<String> temp = new ArrayList<>(timeCompactMemStor.get(getVal));     //grabs get val event (previous event to insertion)
                                temp.set(timeIndexList.get(getVal), tempString);               //copies time index of get val event and adjusts time to be remainder
                                timeCompactMemStor.add(getVal + 2, temp);                  // adds new remainder event after insertion event
                                timeIndexList.add(getVal + 2, timeIndexList.get(getVal));
                                totalEventTimesList.add(getVal + 2, "Ignore");

                                //adjust every future event
                                String[] calcTimeInner = {String.valueOf(tempArray[2]), String.valueOf(tempArray[3]), tempArray[2], tempArray[3]};
                                for (int startVal = getVal + 3; startVal <= eventNum + 1; startVal++) {            //end at the current event
                                    if (totalEventTimesList.get(startVal).equals("blank") || totalEventTimesList.get(startVal).equals("Ignore")) {
                                        calcTimeInner[2] = String.valueOf(Integer.parseInt(calcTimeInner[2]) + averageTimeArray[0]);
                                        calcTimeInner[3] = String.valueOf(Integer.parseInt(calcTimeInner[3]) + averageTimeArray[1]);
                                        String value = CheckTime(calcTimeInner, true);
                                        System.out.println(value);
                                        timeCompactMemStor.get(startVal).set(timeIndexList.get(startVal), value);
                                        String val1 = calcTimeInner[2];
                                        String val2 = calcTimeInner[3];
                                        calcTimeInner[0] = val1;
                                        calcTimeInner[1] = val2;
                                        totalEventTimesList.set(startVal, "Ignore");
                                    }
                                    else{
                                        calcTimeInner[2] = totalEventTimesList.get(startVal).split("-")[2];
                                        calcTimeInner[3] = totalEventTimesList.get(startVal).split("-")[3];
                                    }
                                }
                                currentTimeHour = Integer.parseInt(calcTimeInner[2]);
                                currentTimeMin = Integer.parseInt(calcTimeInner[3]);
                                timeRead=false;
                                j++;
                                eventNum++;
                                break;
                            }
                        }
                    }
                }
                timeRead = eventWordString.equals("[Time]");
            }
            eventNum++;
        }


    return timeCompactMemStor;
    }
    public static String CheckTime(String[] array,boolean Increase){
            if (Increase) {
                if (Integer.parseInt(array[3]) > 59) {
                    while (Integer.parseInt(array[3]) > 59) {
                        array[3] = String.valueOf(Integer.parseInt(array[3]) - 60);
                        array[2] = String.valueOf(Integer.parseInt(array[2]) + 1);
                    }
                }
            } else {
                if (Integer.parseInt(array[1]) < 0 || Integer.parseInt(array[3]) < 0) {
                    while (Integer.parseInt(array[1]) < 0) {
                        array[1] = String.valueOf(Math.abs(Integer.parseInt(array[1])));
                        array[0] = String.valueOf(Integer.parseInt(array[0]) - 1);
                    }
                    while (Integer.parseInt(array[3]) < 0) {
                        array[3] = String.valueOf(Integer.parseInt(array[3]) + 60);
                        array[2] = String.valueOf(Integer.parseInt(array[2]) - 1);
                    }
                }
                if (Integer.parseInt(array[3]) > 60){
                    array[2] = String.valueOf(Integer.parseInt(array[2]) + 1);
                    array[3] = String.valueOf(Integer.parseInt(array[3]) - 60);
                }

            }

        return String.join("-", array);
    }


    public static int[] TimeCalculation(int beginTimeHour,int beginTimeMin,int endTimeHour,int endTimeMin,String beginPMorAM,String endPMorAM){
        int finalTimeHour;
        int finalTimeMin;
        if (beginPMorAM.equals("Pass")){
            //time is already converted to military time
            if (endTimeHour - beginTimeHour < 0){
                finalTimeHour = endTimeHour - beginTimeHour + 24;
            }else finalTimeHour = endTimeHour - beginTimeHour;
        }
        else{
            if (beginTimeMin > 60 || beginTimeMin < 0)return null;
            else if (endTimeMin > 60 || endTimeMin < 0) return null;
            else if (beginTimeHour > 12 || beginTimeHour < 0) return null;
            else if (endTimeHour > 12 || endTimeHour < 0) return null;

            if (!endPMorAM.equals(beginPMorAM)) endTimeHour +=12;
            //Correct values if different time periods and if minutes need to be carried from hours
            if (endTimeMin-beginTimeMin<0){
                endTimeHour -=1;
                endTimeMin = Math.abs(60-beginTimeMin);
                beginTimeMin = 0;
            }
            finalTimeHour = endTimeHour-beginTimeHour;
        }
        //Calculates Overall Time available
        finalTimeMin = endTimeMin -beginTimeMin;
        while (finalTimeMin <0){
            finalTimeMin += 60;
            finalTimeHour--;
        }
        return new int[]{finalTimeHour,finalTimeMin};
    }
    private Boolean checkConflictingTimes(){
        List<Integer> endTimes = new ArrayList<>();

        for (String s : totalEventTimesList) {
            if (s.equals("blank")) continue;
            String[] parts = s.split("-");
            int beginHour = Integer.parseInt(parts[0]);
            int endHour = Integer.parseInt(parts[2]);

            if (!endTimes.isEmpty() && beginHour < Collections.max(endTimes)) {
                return false; // Conflict detected
            }

            endTimes.add(endHour);
        }

        return true; // No conflicts were found
    }
    private List<List<String>> BestFitList(int[] averageTimeArray){
        //Best fit algorithm
        int bestFitHour = CreateSchedule.getBeginTimeHour();
        int bestFitMin  = CreateSchedule.getBeginTimeMin();
        int timeDiffer;
        int eventShiftNum;
        int eventNum =0;
        int eventBeginHour;
        int eventBeginMin;
        int nonManualTimePerEvent;
        int getVal;
        List<String> totalEventTimesListCopy= totalEventTimesList.stream().toList();
        for (int i = 0; i < totalEventTimesList.size(); i++){
            String val = totalEventTimesListCopy.get(i);
            if (val.equals("blank")){
                bestFitHour += averageTimeArray[0];
                bestFitMin += averageTimeArray[1];
                if (bestFitMin > 60){
                    bestFitHour ++;
                    bestFitMin -= 60;
                }
            } else{
                String eventTime = totalEventTimesList.get(eventNum);
                String[] eventSplit = eventTime.split("-");
                eventBeginHour = Integer.parseInt(eventSplit[0]);
                eventBeginMin = Integer.parseInt(eventSplit[1]);
                nonManualTimePerEvent = averageTimeArray[0] * 60 + averageTimeArray[1];
                timeDiffer = ((bestFitHour - eventBeginHour) *60 + (bestFitMin - eventBeginMin));
                eventShiftNum = (int) (double) Math.abs(timeDiffer / nonManualTimePerEvent);
                if (Math.abs(timeDiffer) >= nonManualTimePerEvent){
                    if (timeDiffer > 0) {
                        getVal = eventNum - (eventShiftNum);
                        List<String> temp = compactMemStorage.get(eventNum);
                        totalEventTimesList.remove(eventNum);
                        totalEventTimesList.add(getVal, val);
                        compactMemStorage.remove(eventNum);
                        compactMemStorage.add(getVal, temp);
                    }
                    if (timeDiffer < 0){
                        getVal = eventNum+eventShiftNum;
                        List<String> temp = compactMemStorage.get(eventNum);
                        totalEventTimesList.remove(eventNum);
                        totalEventTimesList.add(getVal,val);
                        compactMemStorage.remove(eventNum);
                        compactMemStorage.add(getVal,temp);
                    }
                }

            }
            eventNum++;
        }
        return compactMemStorage;
    }
}
