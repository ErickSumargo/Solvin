package id.solvin.dev.model.basic;

/**
 * Created by Erick Sumargo on 4/1/2017.
 */

public class FeedNotification {
    private int totalFeed;
    private boolean isFirst;

    public FeedNotification(int totalFeed, boolean isFirst) {
        this.totalFeed = totalFeed;
        this.isFirst = isFirst;
    }

    public int getTotalFeed() {
        return totalFeed;
    }

    public boolean isFirst() {
        return isFirst;
    }
}