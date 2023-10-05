import javax.swing.*;
import java.awt.*;

public class FrameController {
    private static Point currentScreenLocation = new Point(0,0);
    private static Boolean mainFrameDisposed = false;

    public static void ReturnToMain(){
        JFrame mainFrame = FrameManager.getFrame();
        currentScreenLocation = mainFrame.getLocationOnScreen();
        mainFrame.dispose();
        mainFrameDisposed = true;
        mainFrame = FrameManager.getFrame();
        InitializeMemory initalizeMemory = new InitializeMemory();
        OptionPanel.RestoreButtons();
    }
    public static Boolean checkIfFrameDisposed(){
        if (mainFrameDisposed){
            mainFrameDisposed = false;
            return true;
        }
        else return false;
    }
    public static Point getCurrentScreenLocation(){
        return currentScreenLocation;
    }
    public static void ErrorMessage(String errorMessage){
        JFrame mainFrame = FrameManager.getFrame();
        JOptionPane.showMessageDialog(mainFrame,errorMessage);
    }
}
