/*
 * Copyright 2016-2018 Andres Almiray
 *
 * This file is part of Java Trove Examples
 *
 * Java Trove Examples is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java Trove Examples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Java Trove Examples. If not, see <http://www.gnu.org/licenses/>.
 */
package org.example;

import griffon.core.event.EventHandler;
import griffon.core.threading.UIThreadManager;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Andres Almiray
 */
public class ApplicationEventHandler implements EventHandler {
    @Inject
    private UIThreadManager uiThreadManager;

    public void onThrowableEvent(ThrowableEvent event) {
        uiThreadManager.runInsideUIAsync(() -> {
            TitledPane pane = new TitledPane();
            pane.setCollapsible(false);
            pane.setText("Stacktrace");
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            pane.setContent(textArea);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            event.getThrowable().printStackTrace(new PrintStream(baos));
            textArea.setText(baos.toString());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An unexpected error occurred");
            alert.getDialogPane().setContent(pane);
            alert.showAndWait();
        });
    }
}
