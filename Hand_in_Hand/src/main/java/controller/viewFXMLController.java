/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import model.Key;
import model.PortListener;

/**
 *
 * @author Andrej Sakal, Gabriel Ionescu
 */
public class viewFXMLController implements Initializable, Observer {

    @FXML
    private Label label;
    @FXML
    private Button spaceButton;
    @FXML
    private Button leftButton;
    @FXML
    private Button rightButton;
    @FXML
    private Hyperlink helpHyper;
    @FXML
    private BorderPane bordPane;
    @FXML
    private ProgressIndicator progressbar;

    //Eine Istanz des ProtListeners wird erstellt
    private static PortListener model;

    //Eine Liste der Keys
    private static List<Key> keyList;
    private int toAssignKey;
    Robot robot;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new PortListener();
        keyList = new LinkedList<>();
        toAssignKey = 0;

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(viewFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Add as Observer
        getModel().addObserver(this);
        //Set loadanimation to visible
        this.progressbar.setVisible(false);

        //If there is no connection...
        if (getModel().getSerialPort() == null) {
            printToLabel("The Device is not connected. "
                    + "Please check the connection!");
        } else if (getModel().getSerialPort() != null) {
            printToLabel("The Device is successfuly connected!");
        }
    }

    /**
     * Writes given String on Label
     * @param text 
     */
    private void printToLabel(final String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }

    /**
     * HyperLink handler for Help
     * @param event
     * @throws IOException 
     */
    @FXML
    private void handleHyperLink(ActionEvent event) throws IOException {
        //Open Help-File with the use of System.getPropertys 
        String filename = System.getProperty("user.dir") + System.getProperty("file.separator") + "Hand_in_Hand"
                + System.getProperty("file.separator") + "Ressources"
                + System.getProperty("file.separator") + "Help_de_De.pdf";

        Desktop.getDesktop().open(new File(filename));//Open file
    }

    /**
     * Handler for the very left button
     * @param event 
     */
    @FXML
    private void handleLeftButton(ActionEvent event) {
        printToLabel("Waiting for input from the microcontroller...");

        System.out.println("Left button pressed!");
        toAssignKey = KeyEvent.VK_LEFT;//Save last pressed button

        //Check if device is connected
        if (getModel().getSerialPort() == null) {
            printToLabel("Please check the connection!\nYou can't assign a "
                    + "button!");
        } else {
            progressbar.setVisible(true);//Show loadingbar
        }
    }

    /**
     * Handler for middle button
     * @param event 
     */
    @FXML
    private void handleSpaceButton(ActionEvent event) {
        printToLabel("Waiting for input from the microcontroller...");

        System.out.println("Space button pressed!");
        toAssignKey = KeyEvent.VK_SPACE;//Save last pressed button

        //Check if device is connected
        if (getModel().getSerialPort() == null) {
            printToLabel("Please check the connection!\nYou can't assign a "
                    + "button!");
        } else {
            progressbar.setVisible(true);//Show loadingbar
        }
    }

    /**
     * Handler for very right button
     * @param event 
     */
    @FXML
    private void handleRightButton(ActionEvent event) {
        printToLabel("Waiting for input from the microcontroller...");

        System.out.println("Right button pressed!");
        toAssignKey = KeyEvent.VK_RIGHT;//Save last pressed button

        //Check if device is connected
        if (getModel().getSerialPort() == null) {
            printToLabel("Please check the connection!\nYou can't assign a "
                    + "button!");
        } else {
            progressbar.setVisible(true);//Show loadingbar
        }
    }

    /**
     * Handles incomming key-value
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Update - method called!");
        String input = getModel().getInputString();

        boolean isInList = false;
        
                //Looking if key is in List
        for (int i = 0; i < keyList.size(); ++i) {
            //If inputvalue maches with key
            if (keyList.get(i).getInputString().equals(input)) {
                System.out.println("Key in list!");
                isInList = true;
                progressbar.setVisible(false);//Hide loadingbar
                robot.keyPress(keyList.get(i).getOutputKey());//"PRESS" button
                if(toAssignKey != 0)
                printToLabel("The button is already assigned!");
            }
        }

        //When key is not in list
        if (!isInList && toAssignKey != 0) {
            printToLabel("Key successfully asigned!");
            keyList.add(new Key(toAssignKey, input));//Adding new key to List
            toAssignKey = 0;
            progressbar.setVisible(false);//Hide loadingbar
            System.out.println("New key added! Size: " + keyList.size());
        }
    }

    /**
     * @return the model
     */
    public static PortListener getModel() {
        return model;
    }

    @FXML
    private void handleResetButton(ActionEvent event) {
        keyList.clear();
    }
}
