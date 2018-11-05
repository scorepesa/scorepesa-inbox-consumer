/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biko.mq.listeners;

/**
 *
 * @author thevin
 */
public class QueueConfigsModel {

    String qname;
    String endpoint;
    int threads = 1;
    String QHost;
    String Qusername;
    String QPassword;

    public QueueConfigsModel(String qname, String endpoint, int thread,
            String QHost, String Qusername, String QPassword) {
        this.qname = qname;
        this.endpoint = endpoint;
        this.QHost = QHost;
        this.Qusername = Qusername;
        this.QPassword = QPassword;
        this.threads = thread;
    }

    public String getQHost() {
        return QHost;
    }

    public String getQusername() {
        return Qusername;
    }

    public String getQPassword() {
        return QPassword;
    }

    public int getThreads() {
        return threads;
    }

    public String getQname() {
        return qname;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
