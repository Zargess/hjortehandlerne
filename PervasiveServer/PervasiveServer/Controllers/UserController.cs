using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using PervasiveServer.Models;

namespace PervasiveServer.Controllers {
    public class UserController : ApiController {
        // GET: api/User
        public IEnumerable<User> Get() {
            throw new NotImplementedException();
        }

        // GET: api/User/5
        public User Get(int id) {
            throw new NotImplementedException();
        }

        // POST: api/User
        public HttpResponseMessage Post(User value) {
            throw new NotImplementedException();
        }

        // PUT: api/User/5
        public User Put(User value) {
            throw new NotImplementedException();
        }

        // DELETE: api/User/5
        public HttpResponseMessage Delete(int id) {
            throw new NotImplementedException();
        }
    }
}
