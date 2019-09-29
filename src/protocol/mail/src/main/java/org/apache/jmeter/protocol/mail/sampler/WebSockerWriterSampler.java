package org.apache.jmeter.protocol.mail.sampler;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class WebSockerWriterSampler extends AbstractSampler implements Interruptible {
    private static final Logger log = LoggerFactory.getLogger(WebSockerWriterSampler.class);

    private static final long serialVersionUID = 240L;

    private static final Set<String> APPLICABLE_CONFIG_CLASSES = new HashSet<>(
            Arrays.asList("org.apache.jmeter.config.gui.SimpleConfigGui"));

    private static final String PROTOCOL = "protocol";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private volatile boolean busy;

    public WebSockerWriterSampler() {
        setProtocol("ws"); // default protocol
    }

    @Override
    public SampleResult sample(Entry entry) {
        boolean isOK = false; // Did sample succeed?

        SampleResult parent = new SampleResult();
        parent.setSampleLabel(getName());

        final String protocol = getProtocol();

        String samplerString = toString();
        parent.setSamplerData(samplerString);

        /*
         * Perform the sampling
         */
        parent.sampleStart(); // Start timing
        try {
            // Create empty properties
            Properties props = new Properties();

            // Get session
//            Session session = Session.getInstance(props, null);

            // Get the store
//            Store store = session.getStore(protocol);
//            store.connect(getServer(), getPortAsInt(), getUserName(), getPassword());
//            StringBuilder pdata = new StringBuilder();
//            pdata.append(" messages found\n");
//            parent.setResponseData(pdata.toString(),null);
//            parent.setDataType(SampleResult.TEXT);
//            parent.setContentType("text/plain"); // $NON-NLS-1$
            parent.setResponseData("dummy response data",null);
            parent.setDataType(SampleResult.TEXT);
            parent.setContentType("text/plain");

            busy = true;

            // Close connection
//            store.close();

            parent.setResponseCodeOK();
            parent.setResponseMessageOK();
            isOK = true;
        } catch (NoClassDefFoundError ex) {
            log.debug(ex.toString(), ex);
            parent.setResponseCode("500");
            parent.setResponseMessage(ex.toString());
//        } catch (MessagingException ex) {
//            log.debug(ex.toString(), ex);
//            parent.setResponseCode("500");
//            parent.setResponseMessage(ex.toString() + "\n" + samplerString);
        } finally {
            busy = false;
        }

        if (parent.getEndTime() == 0) { // not been set by any child samples
            parent.sampleEnd();
        }

        parent.setSuccessful(isOK);
        return parent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getProtocol());
        sb.append("://");
        String name = getUserName();
        if (name.length() > 0){
            sb.append(name);
            sb.append("@");
        }
        sb.append(getServer());
        int port=getPortAsInt();
        if (port != -1){
            sb.append(":").append(port);
        }
        sb.append("/");
        return sb.toString();
    }

    public String getProtocol() {
        return getPropertyAsString(PROTOCOL);
    }

    public void setProtocol(String protocol) {
        setProperty(PROTOCOL, protocol);
    }

    public String getServer() {
        return getPropertyAsString(HOST);
    }

    public void setHost(String server) {
        setProperty(HOST, server);
    }

    public String getPort() {
        return getPropertyAsString(PORT);
    }

    private int getPortAsInt() {
        return getPropertyAsInt(PORT, -1);
    }

    public void setPort(String port) {
        setProperty(PORT, port, "");
    }

    public String getUserName() {
        return getPropertyAsString(USERNAME);
    }

    public void setUserName(String username) {
        setProperty(USERNAME, username);
    }

    public String getPassword() {
        return getPropertyAsString(PASSWORD);
    }

    public void setPassword(String password) {
        setProperty(PASSWORD, password);
    }

    @Override
    public boolean interrupt() {
        boolean isBusy = busy;
        busy = false;
        return isBusy;
    }

    @Override
    public boolean applies(ConfigTestElement configElement) {
        String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
        return APPLICABLE_CONFIG_CLASSES.contains(guiClass);
    }
}
