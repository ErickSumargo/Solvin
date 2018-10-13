package id.solvin.dev.view.widget;

/**
 * Created by edinofri on 12/01/2017.
 */


import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.onegravity.rteditor.RTEditText;

/**
 * Created by Erick Sumargo on 1/11/2017.
 */

public class ClassRichEditText extends RTEditText {
    private ActionMode actionMode;
    private OnOverridingContextMenu mOnOverridingContextMenu;

    public interface OnOverridingContextMenu {
        void onCut();

        void onCopy();

        void onPaste();
    }

    public void setOnOverridingContextMenu(OnOverridingContextMenu listener) {
        mOnOverridingContextMenu = listener;
    }

    public ClassRichEditText(Context context) {
        super(context);
        init();
    }

    public ClassRichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClassRichEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        actionMode = this.startActionMode(new ActionBarCallBack());
        this.setCustomSelectionActionModeCallback(new ActionBarCallBack());
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id) {
            case android.R.id.paste:
                onPaste();
                return true;
        }
        return super.onTextContextMenuItem(id);
    }

    public void onCut() {
        if (mOnOverridingContextMenu != null)
            mOnOverridingContextMenu.onCut();
    }

    public void onCopy() {
        if (mOnOverridingContextMenu != null)
            mOnOverridingContextMenu.onCopy();
    }

    public void onPaste() {
        if (mOnOverridingContextMenu != null)
            mOnOverridingContextMenu.onPaste();
    }

    class ActionBarCallBack implements android.view.ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.cut:
                    onCut();
                    mode.finish();
                    return true;
                case android.R.id.copy:
                    onCopy();
                    mode.finish();
                    return true;
                case android.R.id.paste:
                    onPaste();
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
        }
    }
}