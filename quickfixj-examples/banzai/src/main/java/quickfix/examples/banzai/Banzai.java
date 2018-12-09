/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved.
 *
 * This file is part of the QuickFIX FIX Engine
 *
 * This file may be distributed under the terms of the quickfixengine.org
 * license as defined by quickfixengine.org and appearing in the file
 * LICENSE included in the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 *
 * Contact ask@quickfixengine.org if any conditions of this licensing
 * are not clear to you.
 ******************************************************************************/

package quickfix.examples.banzai;

//package org.quickfixj.jmx.mbean.session;

import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.examples.banzai.restapi.App;
import quickfix.examples.banzai.ui.BanzaiFrame;

import javax.swing.*;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Entry point for the Banzai application.
 */
public class Banzai {
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private static final Logger log = LoggerFactory.getLogger(Banzai.class);
    private static Banzai banzai;
    private boolean initiatorStarted = false;
    private Initiator initiator = null;
    private JFrame frame = null;
    private BlockingQueue<quickfix.examples.banzai.restapi.message.Message> restMsgQueue;
    private BlockingQueue<quickfix.Message> fixMsgQueue;

    public Banzai(String[] args) throws Exception {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = Banzai.class.getResourceAsStream("banzai.cfg");
        } else if (args.length == 1) {
            inputStream = new FileInputStream(args[0]);
        }
        if (inputStream == null) {
            System.out.println("usage: " + Banzai.class.getName() + " [configFile].");
            return;
        }
        SessionSettings settings = new SessionSettings(inputStream);
        inputStream.close();

        boolean logHeartbeats = Boolean.valueOf(System.getProperty("logHeartbeats", "true"));

        OrderTableModel orderTableModel = new OrderTableModel();
        ExecutionTableModel executionTableModel = new ExecutionTableModel();

        File logFile = new File("/home/george/Priya/RestFix.log");
        logFile.createNewFile();
        FileWriter fileWriter = new FileWriter(logFile);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("Starting new demo\n................. ");

        restMsgQueue = new LinkedBlockingQueue<quickfix.examples.banzai.restapi.message.Message>(10);
        fixMsgQueue = new LinkedBlockingQueue<quickfix.Message>(10);
        BanzaiApplication application = new BanzaiApplication(orderTableModel, executionTableModel,
                restMsgQueue,fixMsgQueue,printWriter);

        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true, logHeartbeats);
        MessageFactory messageFactory = new DefaultMessageFactory();

        initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory,
                messageFactory);

        JmxExporter exporter = new JmxExporter();
        exporter.register(initiator);

        frame = new BanzaiFrame(orderTableModel, executionTableModel, application);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        App restServer = new App(restMsgQueue,fixMsgQueue,printWriter);
        Thread restServerThread = new Thread(restServer);
        restServerThread.start();

        Thread restMsgQueueReader = new Thread(new RestMsgQueueReader(application));
        restMsgQueueReader.start();



    }

    public synchronized void logon() {
        if (!initiatorStarted) {
            try {
                initiator.start();
                initiatorStarted = true;

            } catch (Exception e) {
                log.error("Logon failed", e);
            }
        } else {
            for (SessionID sessionId : initiator.getSessions()) {
                Session.lookupSession(sessionId).logon();
            }
        }
    }

    public void logout() {
        for (SessionID sessionId : initiator.getSessions()) {
            Session.lookupSession(sessionId).logout("user requested");
        }
    }

    public void stop() {
        shutdownLatch.countDown();
    }

    public JFrame getFrame() {
        return frame;
    }

    public static Banzai get() {
        return banzai;
    }

    public static void main(String[] args) throws Exception {
        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        banzai = new Banzai(args);
        if (!System.getProperties().containsKey("openfix")) {
            banzai.logon();
        }
        shutdownLatch.await();
    }

}
