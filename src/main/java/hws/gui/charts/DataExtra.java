/*
 * Copyright (C) 2023 grimm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hws.gui.charts;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Interface for the extra data needed to draw a Block into a BlockChart
 * 
 * @param <X>
 * @param <Y>
 */
public interface DataExtra<X,Y>
{
    public void addToList_X(List<X> list);
    public void addToList_Y(List<Y> list);
    public void decorate(StackPane pane);
    public void relocateAndResize(Node node, X xCurrent, Y yCurrent);
}
