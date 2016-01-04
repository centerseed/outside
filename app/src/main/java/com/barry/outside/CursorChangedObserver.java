package com.barry.outside;

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;

public class CursorChangedObserver {

    public interface OnCursorChangedListener {
        void onCursorChanged(Cursor c);
    }

    private ContentObserver m_contentObserver;
    private DataSetObserver m_datasetObserver;
    private Cursor m_cursor;
    private OnCursorChangedListener m_listener;

    public CursorChangedObserver(OnCursorChangedListener l) {
        m_listener = l;
    }

    public void swapCursor(Cursor c) {
        unregisterObserver(m_cursor);
        m_cursor = c;
        registerObserver(m_cursor);
    }

    protected void registerObserver(Cursor cursor) {
        if (m_contentObserver == null) {
            m_contentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                }
            };
        }
        if (m_datasetObserver == null) {
            m_datasetObserver = new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    if (m_listener != null)
                        m_listener.onCursorChanged(m_cursor);
                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                    if (m_listener != null)
                        m_listener.onCursorChanged(m_cursor);
                }
            };
        }
        if (cursor != null) {
            cursor.registerContentObserver(m_contentObserver);
            cursor.registerDataSetObserver(m_datasetObserver);
        }
    }

    protected void unregisterObserver(Cursor cursor) {
        if (cursor != null) {
            cursor.unregisterContentObserver(m_contentObserver);
            cursor.unregisterDataSetObserver(m_datasetObserver);
        }
    }
}
