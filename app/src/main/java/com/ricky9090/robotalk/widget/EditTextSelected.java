package com.ricky9090.robotalk.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.ricky9090.robotalk.R;

public class EditTextSelected extends AppCompatEditText {

    private SelectionListener mListener;

    public EditTextSelected(@NonNull Context context) {
        this(context, null);
    }

    public EditTextSelected(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public EditTextSelected(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mListener != null) {

            if (getText() != null && getText().toString() != null) {
                String tmp = getText().toString().substring(selStart, selEnd);
                mListener.onSelectedChange(tmp);
            }
        }
    }

    public void setListener(SelectionListener mListener) {
        this.mListener = mListener;
    }

    public interface SelectionListener {
        void onSelectedChange(String selected);
    }
}
