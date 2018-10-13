package id.solvinap.dev.view.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import id.solvinap.dev.R;


/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class CustomTextFormat extends Fragment {
    //    UI COMPONENT
    private View view;
    private HorizontalScrollView container;
    private ImageButton bold, italic, underline, strikeThrough, highlight;

    //    UI HELPER
    private CustomResizeAnimation customResizeAnimation;

    //    LOCAL VARIABLE
    private int containerWidth;
    private boolean boldActived = false, italicActived = false, underlineActived = false,
            strikeThroughActived = false, highlightActived = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_text_format, container, false);
        init();
        setEvent();

        return view;
    }

    public CustomTextFormat() {
    }

    private void init() {
        container = (HorizontalScrollView) view.findViewById(R.id.text_format_container);
        bold = (ImageButton) view.findViewById(R.id.text_format_bold);
        italic = (ImageButton) view.findViewById(R.id.text_format_italic);
        underline = (ImageButton) view.findViewById(R.id.text_format_underline);
        strikeThrough = (ImageButton) view.findViewById(R.id.text_format_strikethrough);
        highlight = (ImageButton) view.findViewById(R.id.text_format_highlight);

        container.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerWidth = container.getMeasuredWidth();
    }

    private void setEvent() {
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!boldActived) {
                    bold.setBackgroundResource(R.drawable.custom_background_text_format_selected);
                    boldActived = true;
                    setBold(true);
                } else {
                    bold.setBackgroundResource(R.drawable.custom_background_text_format);
                    boldActived = false;
                    setBold(false);
                }
            }
        });

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!italicActived) {
                    italic.setBackgroundResource(R.drawable.custom_background_text_format_selected);
                    italicActived = true;
                    setItalic(true);
                } else {
                    italic.setBackgroundResource(R.drawable.custom_background_text_format);
                    italicActived = false;
                    setItalic(false);
                }
            }
        });

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!underlineActived) {
                    underline.setBackgroundResource(R.drawable.custom_background_text_format_selected);
                    underlineActived = true;
                    setUnderline(true);
                } else {
                    underline.setBackgroundResource(R.drawable.custom_background_text_format);
                    underlineActived = false;
                    setUnderline(false);
                }
            }
        });

        strikeThrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!strikeThroughActived) {
                    strikeThrough.setBackgroundResource(R.drawable.custom_background_text_format_selected);
                    strikeThroughActived = true;
                    setStrikeThrough(true);
                } else {
                    strikeThrough.setBackgroundResource(R.drawable.custom_background_text_format);
                    strikeThroughActived = false;
                    setStrikeThrough(false);
                }
            }
        });

        highlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!highlightActived) {
                    highlight.setBackgroundResource(R.drawable.custom_background_text_format_selected);
                    highlightActived = true;
                    setHighlight(true);
                } else {
                    highlight.setBackgroundResource(R.drawable.custom_background_text_format);
                    highlightActived = false;
                    setHighlight(false);
                }
            }
        });
    }

    public void show() {
        customResizeAnimation = new CustomResizeAnimation(container);
        customResizeAnimation.setParams(container.getWidth(), containerWidth);
        customResizeAnimation.setDuration(250);

        container.setVisibility(View.VISIBLE);
        container.startAnimation(customResizeAnimation);
    }

    public void hide() {
        customResizeAnimation = new CustomResizeAnimation(container);
        customResizeAnimation.setDuration(250);
        customResizeAnimation.setParams(container.getWidth(), 0);
        customResizeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                container.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        container.startAnimation(customResizeAnimation);
    }

    //    INTERFACE
    private CustomTextFormat.OnTextFormatStyle mTextFormatStyle;

    public interface OnTextFormatStyle {
        void setBold(boolean enabled);

        void setItalic(boolean enabled);

        void setUnderline(boolean enabled);

        void setStrikeThrough(boolean enabled);

        void setHighlight(boolean enabled);
    }

    public void setOnTextFormatStyle(CustomTextFormat.OnTextFormatStyle listener) {
        mTextFormatStyle = listener;
    }

    private void setBold(boolean enabled) {
        if (mTextFormatStyle != null) {
            mTextFormatStyle.setBold(enabled);
        }
    }

    private void setItalic(boolean enabled) {
        if (mTextFormatStyle != null) {
            mTextFormatStyle.setItalic(enabled);
        }
    }

    private void setUnderline(boolean enabled) {
        if (mTextFormatStyle != null) {
            mTextFormatStyle.setUnderline(enabled);
        }
    }

    private void setStrikeThrough(boolean enabled) {
        if (mTextFormatStyle != null) {
            mTextFormatStyle.setStrikeThrough(enabled);
        }
    }

    private void setHighlight(boolean enabled) {
        if (mTextFormatStyle != null) {
            mTextFormatStyle.setHighlight(enabled);
        }
    }
}