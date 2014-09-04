using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using SocketServer;

namespace Gui {
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow {
        private Server WebServer { get; set; }

        public MainWindow() {
            InitializeComponent();
            Loaded += (s, e) => {
                Term.AbortRequested += (ss, ee) => MessageBox.Show("Abort !");
                Term.CommandEntered += (ss, ee) => CheckCommands(ee.Command);

                Term.RegisteredCommands.Add("start");
                Term.RegisteredCommands.Add("status");
                Term.RegisteredCommands.Add("exit");
                Term.RegisteredCommands.Add("help");

                Term.Text += "Welcome !\n";
                Term.Text += "Hit tab to complete your current command.\n";
                Term.Text += "Use ctrl+c to raise an AbortRequested event.\n\n";
                Term.ShowCommands();
                Term.InsertNewPrompt();

                Term.Focus();
                WebServer = new Server(PrintText);
            };
        }

        private void CheckCommands(Command command) {
            switch (command.Name) {
                case "help":
                    Term.ShowCommands();
                    break;
                case "start":
                    WebServer.StartServer();
                    break;
                case "exit":
                    WebServer.Terminate();
                    break;
                case "status":
                    PrintText("Server status is:" + WebServer.ListenThread.IsAlive);
                    break;
            }
            Term.InsertNewPrompt();
        }

        public void PrintText(string text) {
            Term.Print(text);
        }
    }
}
