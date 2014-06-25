package de.mq32.swingpf;

import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                XmlLoader loader = null;
                try {
                    loader = new XmlLoader();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
                assert loader != null;
                Component comp = null;
                try {
                    Object loadee = loader.load("window.xml");
                    loader.getResources().printDebugInfo();
                    if(loadee instanceof Component) {
                        comp = (Component)loadee;
                    } else {
                        System.out.println("Loaded object of type " + loadee.getClass().getName() + " is no instance of javax.swing.Component, thus cannot be shown.");
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                comp.setVisible(true);
            }
        });

    }
}
