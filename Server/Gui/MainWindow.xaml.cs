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

namespace Gui {
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow {
        public MainWindow() {
            InitializeComponent();
            Loaded += (s, e) => {
                Term.AbortRequested += (ss, ee) => MessageBox.Show("Abort !");
                Term.CommandEntered += (ss, ee) => Term.InsertNewPrompt();

                Term.RegisteredCommands.Add("set-root");
                Term.RegisteredCommands.Add("set-stat-fold");
                Term.RegisteredCommands.Add("load");
                Term.RegisteredCommands.Add("reload");
                Term.RegisteredCommands.Add("server");
                Term.RegisteredCommands.Add("exit");

                Term.Text += "Welcome !\n";
                Term.Text += "Hit tab to complete your current command.\n";
                Term.Text += "Use ctrl+c to raise an AbortRequested event.\n\n";
                Term.Text += "Available commands are:\n";
                Term.RegisteredCommands.ForEach(cmd => Term.Text += "  - " + cmd + "\n");
                Term.InsertNewPrompt();

                Term.Focus();
            };
        }

        public void PrintText(string text) {
            
        }
    }
}
