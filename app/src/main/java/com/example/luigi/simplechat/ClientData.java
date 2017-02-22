package com.example.luigi.simplechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientData {
    private boolean initialized = false;
    private String name;
    private InetAddress address;
    public PrintWriter out = null;
    public BufferedReader in = null;
    private Socket socket;

    ClientData(String name, InetAddress address, BufferedReader in, PrintWriter out) {
        this.name = name;
        this.address = address;
        this.out = out;
        this.in = in;
    }

    ClientData(String name, String address, BufferedReader in, PrintWriter out) {
        this.name = name;
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.in = in;
        this.out = out;
    }

    ClientData(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            address=socket.getInetAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name + "," + address.getHostAddress();
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized() {
        initialized = true;
    }
}
