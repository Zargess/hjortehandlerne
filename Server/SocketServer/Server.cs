﻿using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace SocketServer {
    public class Server {
        private TcpListener Listener { get; set; }
        public Thread ListenThread { get; private set; }
        private List<TcpClient> Clients { get; set; }
        private bool Stop { get; set; }
        private Action<string> Print { get; set; }

        // TODO: Make a garbage collector over Clients, so that every none active client is removed from the server emidiatly
        public Server(Action<string> print) {
            Listener = new TcpListener(IPAddress.Any, 8060);
            ListenThread = new Thread(ListenForClients);
            Clients = new List<TcpClient>();
            Print = print;
        }

        public void StartServer() {
            Stop = false;
            Print("Server is starting");
            ListenThread.Start();
        }

        public void Terminate() {
            Listener.Stop();
            Stop = true;
            Clients.ForEach(x => x.Close());
            Print("Done");
        }

        private void ListenForClients() {
            Listener.Start();
            while (!Stop) {
                try {
                    var client = Listener.AcceptTcpClient();
                    var clientThread = new Thread(HandleClientComm);
                    clientThread.Start(client);
                    if (!Clients.Contains(client)) {
                        Clients.Add(client);
                    }
                } catch (SocketException) { }
            }
        }

        private void HandleClientComm(object client) {
            using(var tcpClient = (TcpClient)client)
            using(var ns = tcpClient.GetStream())
            using (var sw = new StreamWriter(ns)) {
                var message = new byte[4096];
                while (true) {
                    var bytesRead = 0;

                    try {
                        bytesRead = ns.Read(message, 0, 4096);
                    } catch {
                        break;
                    }

                    if (bytesRead == 0) {
                        break;
                    }
                    var address = tcpClient.Client.RemoteEndPoint;
                    var encoder = new UTF8Encoding();
                    var m = encoder.GetString(message, 0, bytesRead).Split('\n')[0];
                    Print("(from client: " + address + ") => " + m);
                    var s = encoder.GetString(message, 0, bytesRead);
                    if (!ns.CanWrite) continue;
                    sw.WriteLine(s);
                    sw.Flush();
                }
            }
        }
    }
}