using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace SocketServer {
    class Server {
        private TcpListener Listener { get; set; }
        public Thread ListenThread { get; private set; }
        private List<TcpClient> Clients { get; set; }
        private bool Stop { get; set; }

        public Server() {
            Listener = new TcpListener(IPAddress.Any, 8060);
            ListenThread = new Thread(ListenForClients);
            Clients = new List<TcpClient>();
        }

        public void StartServer() {
            Stop = false;
            ListenThread.Start();
        }

        public void Terminate() {
            Stop = true;
            Clients.ForEach(x => x.Close());
        }

        private void ListenForClients() {
            Listener.Start();
            while (!Stop) {
                var client = Listener.AcceptTcpClient();
                var clientThread = new Thread(HandleClientComm);
                clientThread.Start(client);
                if (!Clients.Contains(client)) {
                    Clients.Add(client);
                }
            }
        }

        private void HandleClientComm(object client) {
            var tcpClient = (TcpClient)client;
            var clientStream = tcpClient.GetStream();
            var ns = tcpClient.GetStream();
            var sw = new StreamWriter(ns);

            var message = new byte[4096];
            while (true) {
                var bytesRead = 0;

                try {
                    bytesRead = clientStream.Read(message, 0, 4096);
                } catch {
                    break;
                }

                if (bytesRead == 0) {
                    break;
                }
                var temp = tcpClient.Client.RemoteEndPoint;
                var encoder = new UTF8Encoding();
                var m = encoder.GetString(message, 0, bytesRead).Split('\n')[0];
                Console.WriteLine("(from client: " + temp + ") => " + m);
                var s = encoder.GetString(message, 0, bytesRead);
                if (!ns.CanWrite) continue;
                sw.WriteLine(s);
                sw.Flush();
            }

            sw.Close();
            ns.Close();
            tcpClient.Close();
        }
    }
}