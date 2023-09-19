import javax.swing.*;

public class FrameController {

    public static void ReturnToMain(){
        JFrame mainFrame = FrameManager.getFrame();
        mainFrame.dispose();
        JFrame newMainFrame = FrameManager.getFrame();
        newMainFrame.revalidate();
    }
    public static void ErrorMessage(String errorMessage){
        JFrame mainFrame = FrameManager.getFrame();
        JOptionPane.showMessageDialog(mainFrame,errorMessage);
    }
}
