

package hws;

import hws.gui.MainWindowController;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Matthias Grimm <matthiasgrimm@users.sourceforge.net>
 */
public class Main
extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        setUserAgentStylesheet(STYLESHEET_MODENA);
        
        MainWindowController mw = new MainWindowController(stage);
        stage.getIcons().add(mw.getImageResource("harzwasserspiegel_16x16.png"));
        stage.getIcons().add(mw.getImageResource("harzwasserspiegel_32x32.png"));
        stage.getIcons().add(mw.getImageResource("harzwasserspiegel_64x64.png"));
        stage.getIcons().add(mw.getImageResource("harzwasserspiegel_256x256.png"));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);       // start JavaFX Thread
    }
}
