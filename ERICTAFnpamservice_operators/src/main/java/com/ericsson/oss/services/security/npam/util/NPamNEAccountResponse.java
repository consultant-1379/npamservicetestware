package com.ericsson.oss.services.security.npam.util;

public class NPamNEAccountResponse {
    String neName = "";
    String neNpamStatus = "";
    String currentUser = "";
    int id = 0;
    String status = "";

    //Example of response
   /* [{"neName":"${nodes.radio.node3}",
   "neNpamStatus":"NOT_MANAGED",
   "neAccounts":
   [{"neName":"${nodes.radio.node3}",
   "currentUser":"${nodes.radio.node3}",
   "id":"1",
   "errorDetails":"null",
   "status":"DETACHED",
   "lastUpdate":"23-04-16 09:51:59+0000"}]}]
*/

    public String getNeName() {
        return neName;
    }

    public void setNeName(String neName) {
        this.neName = neName;
    }

    public String getNeNpamStatus() {
        return neNpamStatus;
    }

    public void setNeNpamStatus(String neNpamStatus) {
        this.neNpamStatus = neNpamStatus;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNeAccountStatus() {
        return status;
    }

    public void setNeAccountStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NPamNEAccountResponse{" +
                "neName='" + neName + '\'' +
                ", neNpamStatus='" + neNpamStatus + '\'' +
                ", currentUser='" + currentUser + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
