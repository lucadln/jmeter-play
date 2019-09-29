package org.apache.jmeter.protocol.mail.sampler.gui;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.mail.sampler.WebSockerWriterSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

public class WebSockerWriterGui extends AbstractSamplerGui {

    private static final long serialVersionUID = 240L;

    private JTextField payloadField;

    private final String payloadLabel = "Payload: ";

    public WebSockerWriterGui() {
        init();
        initFields();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = getConstraints();

        payloadField = new JPasswordField(20);
        addField(settingsPanel, payloadLabel, payloadField, gbc);

        JPanel settings = new VerticalPanel();
        settings.add(Box.createVerticalStrut(50));
        settings.add(settingsPanel);

        add(makeTitlePanel(), BorderLayout.NORTH);
        add(settings, BorderLayout.CENTER);
    }

    private void initFields() {
        payloadField.setText("");
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
        WebSockerWriterSampler writerSampler = (WebSockerWriterSampler) element;
        payloadField.setText(writerSampler.getPassword());

        super.configure(element);
    }

    @Override
    public void modifyTestElement(TestElement element) {
        element.clear();
        configureTestElement(element);

        WebSockerWriterSampler writerSampler = (WebSockerWriterSampler) element;
        writerSampler.setPassword(payloadField.getText());
    }

    @Override
    public TestElement createTestElement() {
        WebSockerWriterSampler writerSampler = new WebSockerWriterSampler();
        modifyTestElement(writerSampler);
        return writerSampler;
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
