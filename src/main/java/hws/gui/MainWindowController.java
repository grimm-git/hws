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
import hws.gui.charts.ChartRangePane;
import hws.gui.charts.LocalDateAxis;
import java.io.IOException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * FXML Controller class.<p>
 *
 * @author Matthias Grimm <matthiasgrimm@users.sourceforge.net>
 */
public class MainWindowController
extends WindowFX
{

    @FXML  private HBox  hboxContents;
    @FXML  private Button  btnClose;
    @FXML  private Label  errorMsg;
    @FXML  private MenuItem miExit;
    @FXML  private MenuItem miAbout;
    
    private final MainWindowData dataModel;

    public MainWindowController(Stage stage) throws IOException
    {
        super(stage, "MainWindow.fxml", "harzwasserspiegel.css");
        stage.setTitle(String.format("Harzer Wasserspiegel V%d.%d%s",
                Defaults.APP_VERSION, Defaults.APP_REVISION, Defaults.APP_SUFFIX));
        stage.setResizable(true);
        setMsgLabel(errorMsg);
        
        dataModel = new MainWindowData(); // create dialogue data model
        
        ChartRangePane rangePane = createTestLineChart();
        hboxContents.getChildren().add(rangePane);
        HBox.setHgrow(rangePane, Priority.ALWAYS); 
    }
    
    // ---------------------------------------------------------------------------------------- 
    //                                      FXML GUI handler
    // ---------------------------------------------------------------------------------------- 
    @FXML
    protected void handleAction(ActionEvent ev)
    {
        if (ev.getSource() == btnClose) close();
    }

    @FXML
    protected void handleKeys(KeyEvent ev)
    {
        if (ev.getEventType() == KeyEvent.KEY_PRESSED) {
            if (ev.getCode() == KeyCode.ENTER) {
                if (ev.getSource() == btnClose) close();
            }
        } else if (ev.getEventType() == KeyEvent.KEY_TYPED) {
            String str = ev.getCharacter();
            for (int n = 0; n < str.length(); n++) {
                char c = str.charAt(n);
                if (Character.isLetterOrDigit(c) || " -_()".indexOf(c) >= 0) {
                    errorMsg.setText("");
                }
            }
        }
    }

    @FXML
    protected void handleMenus(ActionEvent event) throws IOException
    {
        // File Menu
        if (event.getSource() == miExit) {
            close();

        // Help Menu
        } else if (event.getSource() == miAbout) {
            AboutController ctrl = new AboutController();
            ctrl.show();
        }
    }

    @SuppressWarnings("unchecked")
    private ChartRangePane createTestLineChart()
    {
        ObservableList<XYChart.Series<LocalDate,Number>> series = FXCollections.observableArrayList();

        LocalDateAxis xAxis = new LocalDateAxis(LocalDate.of(1900, 1, 1), LocalDate.of(2200,12,31));
        NumberAxis yAxis = new NumberAxis(0, 1700, 100);
        
        final LineChart<LocalDate, Number> lineChart = new LineChart<>(xAxis, yAxis);
        
        lineChart.setData(series);
        lineChart.setVerticalGridLinesVisible(true);
        
        XYChart.Series<LocalDate,Number> seriesChina = new XYChart.Series<>();
        seriesChina.setName("China");
        seriesChina.getData().addAll(new XYChart.Data<>(LocalDate.of(1950, 7, 2), 555),
                                     new XYChart.Data<>(LocalDate.of(2000, 7, 2), 1275),
                                     new XYChart.Data<>(LocalDate.of(2050, 7, 2), 1395),
                                     new XYChart.Data<>(LocalDate.of(2100, 7, 2), 1182),
                                     new XYChart.Data<>(LocalDate.of(2150, 7, 2), 1149));
        
        XYChart.Series<LocalDate,Number> seriesIndia = new XYChart.Series<>();
        seriesIndia.setName("India");
        seriesIndia.getData().addAll(new XYChart.Data<>(LocalDate.of(1950, 7, 2), 358),
                                     new XYChart.Data<>(LocalDate.of(2000, 7, 2), 1017),
                                     new XYChart.Data<>(LocalDate.of(2050, 7, 2), 1531),
                                     new XYChart.Data<>(LocalDate.of(2100, 7, 2), 1458),
                                     new XYChart.Data<>(LocalDate.of(2150, 7, 2), 1308));

        XYChart.Series<LocalDate,Number> seriesUSA = new XYChart.Series<>();
        seriesUSA.setName("USA");
        seriesUSA.getData().addAll(new XYChart.Data<>(LocalDate.of(1950, 7, 2), 158),
                                   new XYChart.Data<>(LocalDate.of(2000, 7, 2), 285),
                                   new XYChart.Data<>(LocalDate.of(2050, 7, 2), 409),
                                   new XYChart.Data<>(LocalDate.of(2100, 7, 2), 437),
                                   new XYChart.Data<>(LocalDate.of(2150, 7, 2), 453));
        
        series.addAll(seriesChina, seriesIndia, seriesUSA);
        return new ChartRangePane(lineChart);
    }
}
