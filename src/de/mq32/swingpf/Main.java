package de.mq32.swingpf;

import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) {

        try {
            final XmlLoader loader = new XmlLoader();
            assert loader != null;
            Component comp = null;

            Object loadee = loader.load("window.xml");
            System.out.println();
            loader.getResources().printDebugInfo();
            if (loadee instanceof Component) {
                comp = (Component) loadee;
            } else {
                System.out.println("Loaded object of type " + loadee.getClass().getName() + " is no instance of javax.swing.Component, thus cannot be shown.");
                return;
            }

            JButton button = (JButton) loader.getResources().findResource("toolTipButton");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton exitButton = (JButton) loader.getResources().findResource("exitButton");
                    exitButton.setText("Cheese!");
                }
            });

            comp.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
