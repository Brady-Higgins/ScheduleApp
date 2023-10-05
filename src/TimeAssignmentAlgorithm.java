import java.util.ArrayList;
import java.util.List;


public class TimeAssignmentAlgorithm {
    private List<List<String>> compactMemStorage;
    List<String> totalEventTimesList;
    private List<List<String>> timeCompactMemStor = new ArrayList<>(InitializeMemory.getCompactMem());
    public List<List<String>> AssignTimeVals(int totalManualEvents, int totalManualTimeHour, int totalManualTimeMin,List<String>eventTimes){
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
        int lastHour = CreateSchedule.getEndTimeHour();
        int lastMin = CreateSchedule.getEndTimeMin();
        int calculatedHour;
        int calculatedMin;
        String calculatedTime;
        int timeDiffer;
        String[] tempArray;
        String tempString;
        int eventShiftNum;
        int eventNum = 0;
        List<Integer> timeIndexList = new ArrayList<>();
        int nonManualEventsRan=0;
        int manualEventsRan = 0;

        compactMemStorage = BestFitList(averageTimeArray);
        for (int j = 0; j<compactMemStorage.size(); j++) {
            List<String> list = compactMemStorage.get(j);
            if (currentTimeMin > 59) {
                currentTimeMin = 0;
                currentTimeHour++;
            }
            for (int i = 0; i < list.size(); i++) {
                String eventWordString = String.valueOf(list.get(i)).replaceAll("\\s", "");             //rewrite to reading just last value of list
                if (totalEventTimesList.get(eventNum).equals("Pass")) {
                    continue;
                }
                if (totalEventTimesList.get(eventNum).equals("blank")) {
                    if (timeRead) {
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
                        timeCompactMemStor.get(eventNum).set(i,calculatedTime);
                        timeIndexList.add(i);
                        nonManualEventsRan++;
                    }

                }
                else {

                    if (timeRead){

                        String eventTime = totalEventTimesList.get(eventNum);
                        String[] eventSplit = eventTime.split("-");
                        timeDiffer = (currentTimeHour - Integer.parseInt(eventSplit[0])) *60 + currentTimeMin - Integer.parseInt(eventSplit[1]);

                        manualEventsRan++;

                        int remainder = timeDiffer % (averageTimeArray[0] * 60 + averageTimeArray[1]); //Simplifying: remainder = how much its digging into the previous time
                        eventShiftNum = Math.abs((timeDiffer / (averageTimeArray[0] * 60 + averageTimeArray[1])));
                        //neg time = event in future | pos time = event in past
                        //method to find nearest time so standard time can be used for events

                        if (timeDiffer == 0){
                            calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                            timeCompactMemStor.get(eventNum).set(i,calculatedTime);
                        }
                        if (timeDiffer < 0) {
                            //event is in future
                            if (remainder >= -20 || eventNum == timeCompactMemStor.size()-1) {
                                //event is twenty min in future    +20 current time to
                                //elongate previous event and add new event after
                                if (eventNum == timeCompactMemStor.size()-1){
                                    String val = String.valueOf(timeCompactMemStor.get(eventNum-1).get(timeIndexList.get(eventNum-1)));
                                    tempArray = val.split("-");
                                    tempString = CheckTime(tempArray, true, false);
                                    timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), tempString);
                                    calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                    timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                    currentTimeHour = Integer.parseInt(eventSplit[2]);
                                    currentTimeMin = Integer.parseInt(eventSplit[3]);
                                    timeIndexList.add(i);
                                }else {
                                    String val = String.valueOf(timeCompactMemStor.get(eventNum - 1).get(timeIndexList.get(eventNum - 1)));
                                    tempArray = val.split("-");
                                    tempArray[3] = String.valueOf(Integer.parseInt(tempArray[3]) - remainder);
                                    tempString = CheckTime(tempArray, true, false);
                                    timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), tempString);
                                    calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                    timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                    currentTimeHour = Integer.parseInt(eventSplit[2]);
                                    currentTimeMin = Integer.parseInt(eventSplit[3]);
                                    timeIndexList.add(i);
                                    //reduces time
                                    if (nonManualEvents - nonManualEventsRan != 0) {
                                        if (averageTimeArray[1] - (timeDiffer / (nonManualEvents - nonManualEventsRan)) < 0) {
                                            averageTimeArray[0] -= 1;
                                            averageTimeArray[1] = (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan))) + 60;
                                        } else {
                                            averageTimeArray[1] = (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)));
                                        }
                                    }
                                }
                            } else {
                                //event is more than twenty minutes in the future
                                //remove and add to end. change val in time index
//                                    int getVal = eventNum + (eventShiftNum+1);               //new get val pulls from future events

                                timeCompactMemStor.remove(eventNum);
                                timeCompactMemStor.add(list);
                                compactMemStorage.add(list);
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
                                tempArray[3] = String.valueOf(Integer.parseInt(tempArray[3]) - remainder);
                                tempString = CheckTime(tempArray, true, false);
                                timeCompactMemStor.get(eventNum - 1).set(timeIndexList.get(eventNum - 1), tempString);
                                calculatedTime = eventSplit[0] + "-" + eventSplit[1] + "-" + eventSplit[2] + "-" + eventSplit[3];
                                timeCompactMemStor.get(eventNum).set(i, calculatedTime);
                                currentTimeHour = Integer.parseInt(eventSplit[2]);
                                currentTimeMin = Integer.parseInt(eventSplit[3]);
                                timeIndexList.add(i);

                                if (nonManualEvents - nonManualEventsRan!=0) {
                                    if (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)) > 60) {
                                        averageTimeArray[0] += 1;
                                        averageTimeArray[1] = (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan))) - 60;
                                    } else {
                                        averageTimeArray[1] = (averageTimeArray[1] + (timeDiffer / (nonManualEvents - nonManualEventsRan)));
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
                                    currentTimeHour = Integer.parseInt(eventSplit[2]);
                                    currentTimeMin = Integer.parseInt(eventSplit[3]);
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
                                    tempArray[2] = String.valueOf(Integer.parseInt(eventSplit[2]));
                                    tempArray[3] = String.valueOf(Integer.parseInt(eventSplit[3]) + remainder);
                                    tempString = CheckTime(tempArray, true, false);
                                    List<String> temp = new ArrayList<>(timeCompactMemStor.get(getVal));     //grabs get val event (previous event to insertion)
                                    temp.set(timeIndexList.get(getVal), tempString);               //copies time index of get val event and adjusts time to be remainder
                                    timeCompactMemStor.add(getVal + 2, temp);                  // adds new remainder event after insertion event
                                    timeIndexList.add(getVal + 2, timeIndexList.get(getVal));
                                    totalEventTimesList.add(getVal + 2, "Pass");
                                    eventNum++;                                                   // because another event was added, eventNum should increase so it doesn't iterate over

                                    //adjust every future event
                                    int pastPresentTimeHour = Integer.parseInt(tempArray[2]);
                                    int pastPresentTimeMin = Integer.parseInt(tempArray[3]);
                                    String[] calcTimeInner = {tempArray[2], tempArray[3], tempArray[2], tempArray[3]};
                                    for (int startVal = getVal + 3; startVal < eventNum + 1; startVal++) {            //end at the current event
                                        calcTimeInner[2] = String.valueOf(Integer.parseInt(calcTimeInner[2]) + averageTimeArray[0]);
                                        calcTimeInner[3] = String.valueOf(Integer.parseInt(calcTimeInner[3]) + averageTimeArray[1]);
                                        String value = CheckTime(calcTimeInner, true, false);
                                        timeCompactMemStor.get(startVal).set(timeIndexList.get(startVal), value);
                                        String val1 = calcTimeInner[2];
                                        String val2 = calcTimeInner[3];
                                        calcTimeInner[0] = val1;
                                        calcTimeInner[1] = val2;
                                    }
                                    currentTimeHour = Integer.parseInt(calcTimeInner[2]);
                                    currentTimeMin = Integer.parseInt(calcTimeInner[3]);


                                }
                            }


                        }


                    }

                }

                timeRead = eventWordString.equals("[Time]");

            }
            eventNum++;
//            storage.add(list);
        }

    return timeCompactMemStor;
    }
    public static String CheckTime(String[] array,boolean Increase,boolean Military){
        // CheckTime(tempArray,true,false);
        if (!Military) {
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
        }
        else{
            //non decrease or increase of # prior
        }
        return String.join("-", array);
    }


    public static int[] TimeCalculation(int beginTimeHour,int beginTimeMin,int endTimeHour,int endTimeMin,String beginPMorAM,String endPMorAM){
        boolean minorEvent = false;
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
        //Calculates Overall Time available
        int finalTimeHour;
        int finalTimeMin;
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
                return new int[]{8,0};
            }
        }
        return new int[]{finalTimeHour,finalTimeMin};
    }
    private List<List<String>> BestFitList(int[] averageTimeArray){
        //Best fit algorithm
        int bestFitHour = CreateSchedule.getBeginTimeHour();
        int bestFitMin  = CreateSchedule.getBeginTimeMin();
        int timeDiffer;
        int eventShiftNum;
        int eventNum =0;

        for (int i = 0; i < totalEventTimesList.size(); i++){
            String val = totalEventTimesList.get(i);
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
                timeDiffer = (bestFitHour - Integer.parseInt(eventSplit[0])) *60 + bestFitMin - Integer.parseInt(eventSplit[1]);
                //checks to see if time is closer as a PM value
                if (timeDiffer >= 200) {

                    int possibleTime = (((bestFitHour - (Integer.parseInt(eventSplit[0]) + 12)) * 60) + (bestFitMin - Integer.parseInt(eventSplit[1])));
                    if (possibleTime < timeDiffer & possibleTime >= -200) {
                        eventSplit[0] = String.valueOf(Integer.parseInt(eventSplit[0]) + 12);
                        if (Integer.parseInt(eventSplit[0]) - 12 - Integer.parseInt(eventSplit[2]) < 0)
                            eventSplit[2] = String.valueOf(Integer.parseInt(eventSplit[2]) + 12);
                        totalEventTimesList.set(eventNum,String.join("-",eventSplit));

                    }
                }
                else if (timeDiffer <= -200){
                    int possibleTime = (((bestFitHour - (Integer.parseInt(eventSplit[0]) + 12)) *60) + (bestFitMin - Integer.parseInt(eventSplit[1])));
                    if (possibleTime > timeDiffer){
                        eventSplit[0] = String.valueOf(Integer.parseInt(eventSplit[0]) + 12);
                        if (Integer.parseInt(eventSplit[0])- 12 - Integer.parseInt(eventSplit[2]) < 0) eventSplit[2] = String.valueOf(Integer.parseInt(eventSplit[2]) + 12);
                        totalEventTimesList.set(eventNum,String.join("-",eventSplit));

                    }
                }
                //recalculates timeDiffer and if it's still too large, it'll replace
                timeDiffer = (bestFitHour - Integer.parseInt(eventSplit[0])) *60 + bestFitMin - Integer.parseInt(eventSplit[1]);

                if (timeDiffer >= 200){
                    eventShiftNum = Math.abs((timeDiffer / (averageTimeArray[0] * 60 + averageTimeArray[1])));
                    int getVal = eventNum - (eventShiftNum);
                    List<String> temp = compactMemStorage.get(eventNum);
                    totalEventTimesList.remove(eventNum);
                    totalEventTimesList.add(getVal,val);
                    compactMemStorage.remove(eventNum);
                    compactMemStorage.add(getVal,temp);

                }
                else if (timeDiffer <= -200){
                    eventShiftNum = Math.abs((timeDiffer / (averageTimeArray[0] * 60 + averageTimeArray[1])));
                    int getVal = eventNum+(eventShiftNum+1);
                    List<String> temp = compactMemStorage.get(eventNum);
                    totalEventTimesList.remove(eventNum);
                    totalEventTimesList.add(getVal,val);
                    compactMemStorage.remove(eventNum);
                    compactMemStorage.add(getVal,temp);
                }

            }

            eventNum++;
        }
        return compactMemStorage;
    }
}
