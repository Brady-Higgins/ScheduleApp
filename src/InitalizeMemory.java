import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InitalizeMemory {
    private String fileLocation = this.getClass().getClassLoader().getResource("").getPath();
    private String basicsFileLocation = fileLocation + "//Basics.txt";
    private Boolean locationError;


    private static List<String> memoryStorage;
    private static ArrayList<List> compactedMemoryStorage;
    private int eventNum;
    private Boolean eventCreation;
    public InitalizeMemory(){
        ReadMemory();
        compactedMemoryStorage = CompactMem();
    }
    private String getFileLocation(){
        try{
            String fileLocation = this.getClass().getClassLoader().getResource("").getPath();
            locationError = false;
        } catch (Exception e){
            locationError = true;
            String fileLocation = "ErrorFileNotFound";
        }
        if (locationError){
            System.out.println("Error finding location of file");
            System.out.println("Please Try Again Later");
            System.exit(0);
        }
        return fileLocation;
    }

    private void ReadMemory(){
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

        }catch (Exception ex){

        }
        eventNum = 0;
        CompactMem();


    }
    public static ArrayList<List> CompactMem(){
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
                    compactedMemoryStorage.add(new ArrayList(temp));
                    temp.clear();
                    temp.add(" [en] ");
                }
            }

        }
    return compactedMemoryStorage;
    }

    public static ArrayList<List> getCompactMem(){
        return compactedMemoryStorage;
    }
    public static void updateMemory(List<String>inputMemory){
        memoryStorage = inputMemory;
    }
    public static List<String> getMemStorage(){
        return memoryStorage;
    }
    public Boolean isMemoryEmpty(){
        if (eventCreation) return false;
        else return true;
    }

}
