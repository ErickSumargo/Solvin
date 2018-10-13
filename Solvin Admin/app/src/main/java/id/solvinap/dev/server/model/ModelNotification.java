package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataNotification;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/15/2017.
 */

public class ModelNotification extends Response {
    private ResultData data;

    public ResultData getData() {
        return data;
    }

    public class ResultData{
        private int count;

        private List<DataNotification> notificationList;

        public int getCount() {
            if (notificationList != null) {
                return notificationList.size();
            }
            return count;
        }

        public List<DataNotification> getNotificationList() {
            return notificationList;
        }
    }
}
