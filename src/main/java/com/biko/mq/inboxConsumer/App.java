package com.biko.mq.inboxConsumer;

import com.biko.mq.listeners.InboxMessageListener;
import com.biko.mq.listeners.QueueConfigsModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    static final Logger log = LoggerFactory.getLogger(App.class);
    //static Properties props = null;
    static String SMSQUEUESPROPS = "configs.ini";
    static String MQUSERNAME = "";
    static String MQPASSWORD = "";
    static String MQHOST = "";

    public static void main(String[] args) {

//        try {
//            props = fetchConfigurations("inboxConsumer.properties");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        final InboxMessageListener inbox = new InboxMessageListener();
        try {
            ////////////////////////////
            // (SMSQUEUESPROPS,inbox);

            try {
                StringBuilder config = new StringBuilder(getExecutingPath());
                config.append(File.separator).append(SMSQUEUESPROPS);
                final HierarchicalINIConfiguration iniConfObj = new HierarchicalINIConfiguration(config.toString());

                // Get Section names in ini file
                MQHOST = iniConfObj.getSection("mq").getString("HOST");
                MQUSERNAME = iniConfObj.getSection("mq").getString("USERNAME");
                MQPASSWORD = iniConfObj.getSection("mq").getString("PASSWORD");

                Set setOfSections = iniConfObj.getSections();
                Iterator sectionNames = setOfSections.iterator();

                while (sectionNames.hasNext()) {
                    final String sectionName = sectionNames.next().toString();

                    if (!sectionName.equals("mq")) {
                        final QueueConfigsModel configsModel;
                        String queue, url;
                        int threads;
                        threads = iniConfObj.getSection(sectionName).getInt("threads");
                        queue = iniConfObj.getSection(sectionName).getString("queue");
                        url = iniConfObj.getSection(sectionName).getString("url");
                        configsModel = new QueueConfigsModel(queue, url, threads,
                                MQHOST, MQUSERNAME, MQPASSWORD);
                        Runnable startThis = new Runnable() {
                            public void run() {
                                try {

                                    System.out.println("Starting Consumer For: ["
                                            + configsModel.getQname() + "] ENDPOINT: ["
                                            + configsModel.getEndpoint() + "] threads:  "
                                            + "[" + configsModel.getThreads() + "]");

                                    inbox.invoke(configsModel);
                                } catch (Exception eer) {
                                    System.out.println("Unable to start Consumer::" + eer.getMessage());
                                }
                            }
                        };
                        new Thread(startThis).run();
                    }
                }

            } catch (ConfigurationException E) {
                System.out.println(E.getMessage());
            }

            ////////////////////////////
            //   inbox.invoke(10, props);
        } catch (Exception e) {

            System.out.println("ERROR: " + e.getMessage());
        }

    }

    public static Properties fetchConfigurations(String simpleFileName)
            throws IOException {
        StringBuilder config = new StringBuilder(getExecutingPath());
        config.append(File.separator).append(simpleFileName);

        Properties props = new Properties();

        props.load(new FileInputStream(new File(config.toString())));
        props.list(System.out);

        return props;
    }

    public static String getExecutingPath() {
        String resultPath = "";
        Class<?> referenceClass = App.class;
        URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
        try {
            File jarPath = new File(url.toURI()).getParentFile();
            resultPath = jarPath.getPath();
        } catch (URISyntaxException e) {
        }
        return resultPath;
    }
}
