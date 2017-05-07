package com.gali.apps.eifoyesh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;

/**
 * A RecyclerView with ContextMenu
 */

public class ContextMenuRecyclerView extends RecyclerView {


    public ContextMenuRecyclerView(Context context) {
            super(context);
        }

        public ContextMenuRecyclerView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ContextMenuRecyclerView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        private RecyclerContextMenuInfo mContextMenuInfo;

        @Override
        protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
            return mContextMenuInfo;
        }

        @Override
        public boolean showContextMenuForChild(View originalView) {
            final int longPressPosition = getChildAdapterPosition(originalView);
            if (longPressPosition >= 0) {
                final long longPressId = getAdapter().getItemId(longPressPosition);
                mContextMenuInfo = new RecyclerContextMenuInfo(longPressPosition,longPressId);
                return super.showContextMenuForChild(originalView);
            }
            return false;
        }

        public class RecyclerContextMenuInfo implements ContextMenu.ContextMenuInfo             {

            public RecyclerContextMenuInfo(int position, long id) {
                this.position = position;
                this.id = id;
            }

            final public int position;
            final public long id;
        }


}
