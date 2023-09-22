import javax.swing.*;

public class FrameController {
    private static Boolean mainFrameDisposed = false;

    public static void ReturnToMain(){
        JFrame mainFrame = FrameManager.getFrame();
        mainFrame.dispose();
        mainFrameDisposed = true;
        mainFrame = FrameManager.getFrame();
        InitalizeMemory initalizeMemory = new InitalizeMemory();
        OptionPanel.RestoreButtons();
    }
    public static Boolean checkIfFrameDisposed(){
        if (mainFrameDisposed){
            mainFrameDisposed = false;
            return true;
        }
        else return false;
    }
    public static void ErrorMessage(String errorMessage){
        JFrame mainFrame = FrameManager.getFrame();
        JOptionPane.showMessageDialog(mainFrame,errorMessage);
    }
}
