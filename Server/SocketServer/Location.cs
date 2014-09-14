using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SocketServer {
    public class Location {
        public double Langtitude { get; set; }
        public double Longtitude { get; set; }

        public Location(double la, double lo) {
            Langtitude = la;
            Longtitude = lo;
        }

        public override string ToString() {
            return Langtitude + "; " + Longtitude;
        }
    }
}
