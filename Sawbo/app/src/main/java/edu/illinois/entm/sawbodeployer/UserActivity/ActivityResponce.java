package edu.illinois.entm.sawbodeployer.UserActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahsa on 7/23/2017.
 */

public class ActivityResponce {
    UserActivities[] successful;
    String[] failed;
    String err;

    public String getErr() {
        return err;
    }

    public String[] getFailed() {
        return failed;
    }

    public UserActivities[] getSuccessful() {
        return successful;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public void setFailed(String[] failed) {
        this.failed = failed;
    }

    public void setSuccessful(UserActivities[] successful) {
        this.successful = successful;
    }

    @Override
    public String toString() {
        return successful+" "+failed+" "+err;
    }
}
