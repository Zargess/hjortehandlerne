package com.zargess.android.Client;

import android.os.Message;
import com.zargess.android.TestApp.UIHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MFH on 29-04-2014.
 */
public class Client implements Serializable {
    private String IPAddress;
    private int Port;
    private Socket clientSocket;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;
    private UIHandler handler;
    private Thread listeningThread;
    private List<ConnectionListener> connectionListeners;
    private boolean isConnected;

    public Client(String ip, int port, UIHandler h) {
        IPAddress = ip;
        Port = port;
        handler = h;
        connectionListeners = new ArrayList<ConnectionListener>();
        isConnected = false;
    }

    private void StartClient() {
        listeningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(IPAddress, Port);
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outToServer.writeBytes("Connected" + '\n');
                    isConnected = true;
                    while (!listeningThread.isInterrupted()) {
                        String modifiedSentence = inFromServer.readLine();
                        Message msg = Message.obtain(handler);
                        if (modifiedSentence != null && msg != null) {
                            msg.obj = modifiedSentence;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listeningThread.start();
    }

    public boolean TerminateClient() {
        try {
            if (!listeningThread.isInterrupted()) {
                listeningThread.interrupt();
            }
            SendRequest("Client disconnecting");
            clientSocket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean SendRequest(String text) {
        try {
            outToServer.writeBytes(text + '\n');
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void AddListener(ConnectionListener cl) {
        connectionListeners.add(cl);
    }

    public void RemoveListener(ConnectionListener cl) {
        connectionListeners.remove(cl);
    }

    public void Connect() {
        if (clientSocket != null && clientSocket.isConnected()) {
            TerminateClient();
        }
        StartClient();
    }

    private void Connected() {
        for (ConnectionListener c : connectionListeners) {
            c.Connected();
        }
    }

    private void Disconnected() {
        for (ConnectionListener c : connectionListeners) {
            c.Disconnected();
        }
    }

    /**
     * Getters and Setters
     *
     * @return
     */
    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }
}