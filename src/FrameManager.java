
import javax.swing.*;
import java.awt.*;
public class FrameManager {
    private static JFrame ScheduleAppFrame;
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 400;

    private FrameManager() {}

    public static JFrame getFrame() {
        if (ScheduleAppFrame == null) {
            ScheduleAppFrame = new JFrame();
            ScheduleAppFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ScheduleAppFrame.setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));
            ScheduleAppFrame.setLayout(null);
            ScheduleAppFrame.pack();
            ScheduleAppFrame.setResizable(false);           //change
            ScheduleAppFrame.setVisible(true);
            ScheduleAppFrame.add(OptionPanel.getOptionPanel());
            ScheduleAppFrame.revalidate();
        }
        return ScheduleAppFrame;
    }

    public static void addComponent(JComponent component) {
        getFrame().add(component);
        getFrame().repaint();
    }

    public static void removeComponent(JComponent component) {
        getFrame().remove(component);
        getFrame().repaint();
    }
    public static void changeComponentVisibility(JComponent component,Boolean visble){
        //Look at later
        getFrame().add(component);
        component.setVisible(visble);
    }

    public static void showFrame() {
        getFrame().pack();
        getFrame().setVisible(true);
    }

    public static void hideFrame() {
        getFrame().setVisible(false);
    }
    public static void refreshFrame(){

    }
}
