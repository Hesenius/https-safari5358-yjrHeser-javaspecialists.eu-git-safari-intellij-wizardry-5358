package com.someone.ppt.server.gui;

import com.someone.ppt.server.*;

import javax.swing.*;
import java.awt.*;

public class LineDisplay extends JPanel {
    private Line line;
    private JPanel statusPanel;
    private Light led1;
    private Light led2;
    private Light led3;
    private Light led4;

    public LineDisplay(final Line line) {
        this.line = line;

        setToolTipText(line.getDescription());
        setPreferredSize(new Dimension(560, 70));
        setBackground(Color.white);
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new GridLayout(1, 2));

        final JLabel nameLabel = new JLabel(line.getDescription() + "(" + line.getIp() + ")");
        nameLabel.setBorder(BorderFactory.createEtchedBorder());
        nameLabel.setBackground(Color.white);
        nameLabel.setVerticalAlignment(JLabel.CENTER);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(nameLabel);

        statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(1, 4));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setBackground(Color.white);
        statusPanel.setPreferredSize(new Dimension(500, 70));

        if (line.getLineID() == 12) {
            error(new Exception("This is a test error!"));
        }

        JLabel label = new JLabel("Receiving");
        led1 = new Light();
        led1.add(label);
        statusPanel.add(led1);
        led1.turnOff();

        label = new JLabel("Sending");
        led2 = new Light();
        led2.add(label);
        statusPanel.add(led2);
        led2.turnOff();

        label = new JLabel("Generating");
        led3 = new Light();
        led3.add(label);
        statusPanel.add(led3);
        led3.turnOff();

        label = new JLabel("Loading");
        led4 = new Light();
        led4.add(label);
        statusPanel.add(led4);
        led4.turnOff();

        this.add(statusPanel);
    }

    private void private_error(final Exception e) {
        statusPanel.setBackground(Color.red);
        setToolTipText(e.getMessage());
        led1.setBackground(Color.red);
        led2.setBackground(Color.red);
        led3.setBackground(Color.red);
        led4.setBackground(Color.red);
    }

    // error recovered
    private void private_recover() {
        setToolTipText(line.getDescription());
        statusPanel.setBackground(Color.white);
        led1.setBackground(Color.white);
        led2.setBackground(Color.white);
        led3.setBackground(Color.white);
        led4.setBackground(Color.white);

    }

    private void private_setSocketSend(final boolean state) {
        if (state) {
            led1.turnOn();
        } else {
            led1.turnOff();
        }
    }

    private void private_setSocketReceive(final boolean state) {
        if (state) {
            led2.turnOn();
        } else {
            led2.turnOff();
        }
    }

    private void private_setPssSend(final boolean state) {
        if (state) {
            led3.turnOn();
        } else {
            led3.turnOff();
        }
    }

    private void private_setPssReceive(final boolean state) {
        if (state) {
            led4.turnOn();
        } else {
            led4.turnOff();
        }
    }

    public void error(final Exception e) {
        SwingUtilities.invokeLater(() -> private_error(e));
    }

    // error recovered
    public void recover() {
        SwingUtilities.invokeLater(this::private_recover);
    }

    public void setSocketSend(final boolean state) {
        SwingUtilities.invokeLater(() -> private_setSocketSend(state));
    }

    public void setSocketReceive(final boolean state) {
        SwingUtilities.invokeLater(() -> private_setSocketReceive(state));
    }

    public void setPssSend(final boolean state) {
        SwingUtilities.invokeLater(() -> private_setPssSend(state));
    }

    public void setPssReceive(final boolean state) {
        SwingUtilities.invokeLater(() -> private_setPssReceive(state));
    }

//        private void delay() {
//			try {
//				this.wait(100);
//			} catch (InterruptedException e) {
//			}
//        }

    private class Light extends JPanel {
        private JButton led;

        private Light() {
            setLayout(new FlowLayout());
            led = new JButton();
            led.setBorder(BorderFactory.createRaisedBevelBorder());
            led.setPreferredSize(new Dimension(30, 30));
            this.add(led);
        }

        private void turnOff() {
            Light.this.setBackground(Light.this.getParent().getBackground());
            led.setBackground(Color.blue);
            led.setBorder(BorderFactory.createLoweredBevelBorder());
        }

        private void turnOn() {
            Light.this.setBackground(Light.this.getParent().getBackground());
            led.setBackground(Color.green);
            led.setBorder(BorderFactory.createRaisedBevelBorder());
        }
    }
}
