using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SocketServer {
    public class User {
        public string Name { get; private set; }
        public Coordinates Position { get; set; }
        public User(string name) {
            Name = name;
        }

        public override string ToString() {
            return "(" + Name + ", " + Position + ")";
        }
    }

    public class Coordinates {
        
    }
}
