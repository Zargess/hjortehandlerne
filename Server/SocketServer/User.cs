using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SocketServer {
    public class User {
        public string Name { get; private set; }
        public Location Position { get; set; }

        public User(string name, double la, double lo) {
            Name = name;
            Position = new Location(la, lo);
        }

        public override string ToString() {
            return "(" + Name + "; " + Position + ")";
        }
    }
}
