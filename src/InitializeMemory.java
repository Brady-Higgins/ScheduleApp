import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class InitializeMemory {
    private final String fileLocation = this.getClass().getClassLoader().getResource("").getPath();
    private final String basicsFileLocation = fileLocation + "//Basics.txt";

    private static List<String> memoryStorage;
    private static List<List<String>> compactedMemoryStorage;
    private Boolean eventCreation;
    public InitializeMemory(){
        ReadMemory();
        compactedMemoryStorage = CompactMem();
    }
    private void ReadMemory(){
        eventCreation = false;
        memoryStorage = new ArrayList<>();
        try {
            BufferedReader bw = new BufferedReader(
                    new FileReader(basicsFileLocation));
            String line;
            while((line = bw.readLine()) != null){
                for (String s:line.split(" ")) {
                    String stringSNoSpace = s.replaceAll("\\s", "");
                    memoryStorage.add((" " +stringSNoSpace));
                }
            }
            bw.close();

        }catch (Exception ex){
            System.out.println(ex);
            FrameController.ErrorMessage("An Error occurred, please try again");
            FrameController.ReturnToMain();
        }
    }
    public static List<List<String>> CompactMem(){
        List<String> memoryStorageClone = new ArrayList<>(memoryStorage);
        System.out.println("-------------------------");
        System.out.println(memoryStorageClone);
        if (memoryStorageClone.size()==0) return new ArrayList<>();
        //compacts memory to lists within lists
        List<String> temp = new ArrayList<>();
        compactedMemoryStorage = new ArrayList<>();
        memoryStorageClone.add(" [en] ");
        boolean firstOccurrence = true;
        for (String word : memoryStorageClone) {
            String wordNoSpace = word.replaceAll("\\s", "");
            temp.add(word);
            if (wordNoSpace.equals("[en]")) {
                if (firstOccurrence) {
                    firstOccurrence = false;
                } else {
                    temp.remove(temp.size() - 1);
                    compactedMemoryStorage.add(new ArrayList<>(temp));
                    temp.clear();
                    temp.add(" [en] ");
                }
            }

        }
    return compactedMemoryStorage;
    }

    public static List<List<String>> getCompactMem(){
        return compactedMemoryStorage;
    }
    public static void updateMemory(List<String>inputMemory){
        memoryStorage = inputMemory;
    }
    public static List<String> getMemStorage(){
        return memoryStorage;
    }
    public Boolean isMemoryEmpty(){
        return !eventCreation;
    }

}
