/*
 * Copyright (C) 2018 Matthias Grimm <matthiasgrimm@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package hws.gui;

import hws.Defaults;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 *
 * @author Matthias Grimm <matthiasgrimm@users.sourceforge.net>
 */
public class AboutController
extends WindowFX
{
    @FXML  private TextFlow textArea;
    @FXML  private Button btnClose;
    
    /**
     * Programmer and License notice for ProjectMaster V2.0
     * 
     * @throws IOException
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public AboutController() throws IOException
    {
        super("About.fxml", "harzwasserspiegel.css");
        stage.setTitle("About");
        stage.setResizable(false);

        Text text;
        text = new Text(String.format("Version: %s\n", Defaults.getVersionString()));
        text.setStyle("-fx-font-weight: bold; -fx-font-size:1.2em");
        textArea.getChildren().add(text);
        
        text = new Text("Authors: Matthias Grimm and Andreas Ochmann, \u00A92023\n");
        textArea.getChildren().add(text);

        text = new Text("\n");
        text.setStyle("-fx-font-size: 0.4em");
        textArea.getChildren().add(text);

        text = new Text("Status of Harz water reserves\n");
        textArea.getChildren().add(text);
        
        text = new Text("\n");
        text.setStyle("-fx-font-size: 0.4em");
        textArea.getChildren().add(text);

        text = new Text("Easy overview about water reservoirs in the Harz with daily tracking"
                      + " of fill grade, input and output\n");
        textArea.getChildren().add(text);

        text = new Text("\n");
        text.setStyle("-fx-font-size: 0.4em");
        textArea.getChildren().add(text);

        text = new Text("This program is free software; you can redistribute " 
                      + "it and/or modify it under the terms of the GNU General Public License " 
                      + "as published by the Free Software Foundation; either version 3 of the " 
                      + "License, or (at your option) any later version.");
        textArea.getChildren().add(text);
    }

    @FXML
    protected void handleAction(ActionEvent ev)
    {
        if (ev.getSource() == btnClose) close();
    }
}




