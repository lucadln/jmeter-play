package org.apache.jmeter.protocol.mail.sampler.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.mail.sampler.WebSockerConnectorSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

public class WebSockerConnectorGui extends AbstractSamplerGui {

    private static final long serialVersionUID = 240L;

    private JTextField protocolField;

    private JTextField hostField;

    private JTextField portField;

    private JTextField usernameField;

    private JTextField passwordField;

    private final String protocolLabel = "Protocol: ";

    private final String hostLabel = "Host: ";

    private final String portLabel = "Port: ";

    private final String usernameLabel = "Username (optional): ";

    private final String passwordLabel = "Password (optional): ";

    public WebSockerConnectorGui() {
        init();
        initFields();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = getConstraints();

        protocolField = new JTextField(20);
        addField(settingsPanel, protocolLabel, protocolField, gbc);

        hostField = new JTextField(20);
        addField(settingsPanel, hostLabel, hostField, gbc);

        portField = new JTextField(20);
        addField(settingsPanel, portLabel, portField, gbc);

        usernameField = new JTextField(20);
        addField(settingsPanel, usernameLabel, usernameField, gbc);

        passwordField = new JPasswordField(20);
        addField(settingsPanel, passwordLabel, passwordField, gbc);

        JPanel settings = new VerticalPanel();
        settings.add(Box.createVerticalStrut(5));
        settings.add(settingsPanel);

        add(makeTitlePanel(), BorderLayout.NORTH);
        add(settings, BorderLayout.CENTER);
    }

    private void initFields() {
        protocolField.setText("ws");
        hostField.setText("");
        portField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }

    @Override
    public void clearGui() {
        super.clearGui();
        initFields();
    }

    @Override
    public String getLabelResource() {
        // return null and get the static label
        return null;
    }

    @Override
    public String getStaticLabel() {
        return "Open WebSocket Connection";
    }

    @Override
    public void configure(TestElement element) {
        WebSockerConnectorSampler connectorSampler = (WebSockerConnectorSampler) element;
        protocolField.setText(connectorSampler.getProtocol());
        hostField.setText(connectorSampler.getServer());
        portField.setText(connectorSampler.getPort());
        usernameField.setText(connectorSampler.getUserName());
        passwordField.setText(connectorSampler.getPassword());

        super.configure(element);
    }

    @Override
    public void modifyTestElement(TestElement element) {
        element.clear();
        configureTestElement(element);

        WebSockerConnectorSampler connectorSampler = (WebSockerConnectorSampler) element;

        connectorSampler.setProtocol(protocolField.getText());
        connectorSampler.setHost(hostField.getText());
        connectorSampler.setPort(portField.getText());
        connectorSampler.setUserName(usernameField.getText());
        connectorSampler.setPassword(passwordField.getText());
    }

    @Override
    public TestElement createTestElement() {
        WebSockerConnectorSampler connectorSampler = new WebSockerConnectorSampler();
        modifyTestElement(connectorSampler);
        return connectorSampler;
    }

    private void addField(JPanel panel, JLabel label, JComponent field, GridBagConstraints gbc) {
        gbc.fill=GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(label, gbc);
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(field, gbc);
        nextLine(gbc);
    }

    private void addField(JPanel panel, String text, JComponent field, GridBagConstraints gbc) {
        addField(panel, new JLabel(text), field, gbc);
    }

    private void nextLine(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
    }

    private GridBagConstraints getConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        return gbc;
    }
}
