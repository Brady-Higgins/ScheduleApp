
import javax.swing.*;
import java.awt.*;
public class FrameManager {
    private static JFrame ScheduleAppFrame;
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 400;

    private FrameManager() {}

    public static JFrame getFrame() {
        if (ScheduleAppFrame == null || FrameController.checkIfFrameDisposed()) {
            ScheduleAppFrame = new JFrame();
            ScheduleAppFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ScheduleAppFrame.setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));
            ScheduleAppFrame.setLayout(null);
            ScheduleAppFrame.pack();
            ScheduleAppFrame.setResizable(false);
            ScheduleAppFrame.setLocation(FrameController.getCurrentScreenLocation());
            ScheduleAppFrame.setVisible(true);
            ScheduleAppFrame.add(OptionPanel.getOptionPanel());
            ScheduleAppFrame.revalidate();
        }
        return ScheduleAppFrame;
    }
}
