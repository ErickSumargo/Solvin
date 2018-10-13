package id.solvinap.dev.view.widget;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class CustomResizeAnimation extends Animation {
    private int startWidth;
    private int deltaWidth; // distance between start and end width
    private View view;

    /**
     * constructor, do not forget to use the setParams(int, int) method before
     * starting the animation
     *
     * @param v
     */
    public CustomResizeAnimation(View v) {
        this.view = v;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        view.getLayoutParams().width = (int) (startWidth + deltaWidth * interpolatedTime);
        view.requestLayout();
    }

    /**
     * set the starting and ending height for the resize animation
     * starting height is usually the views current height, the end height is the height
     * we want to reach after the animation is completed
     *
     * @param start height in pixels
     * @param end   height in pixels
     */
    public void setParams(int start, int end) {

        this.startWidth = start;
        deltaWidth = end - startWidth;
    }

    /**
     * set the duration for the hideshowanimation
     */
    @Override
    public void setDuration(long durationMillis) {
        super.setDuration(durationMillis);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}