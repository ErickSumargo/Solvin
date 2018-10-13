package id.solvin.dev.model.response;

import id.solvin.dev.model.basic.Notification;
import id.solvin.dev.model.basic.Response;

import java.util.List;

/**
 * Created by edinofri on 26/02/2017.
 */

public class ResponseNotification extends Response {

    private ResultData data;

    public ResultData getData() {
        return data;
    }

    public class ResultData{
        private List<Notification> notifications;

        public List<Notification> getNotifications() {
            return notifications;
        }

        private int count;

        public int getCount(){
            if(notifications != null){
                return notifications.size();
            }
            return count;
        }

    }
}
