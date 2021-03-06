package me.shouheng.notepal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;

/**
 * Created by wang shouheng on 2017/12/23.
 */
public class ColorPreference extends Preference {

    private int value;

    public ColorPreference(Context context) {
        super(context);
        initAttrs(null, 0);
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs, defStyle);
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ColorPreference, defStyle, defStyle);
//        isPrimary = a.getBoolean(R.styleable.ColorPreference_primary, true);
        a.recycle();
        setWidgetLayoutResource(R.layout.widget_pref_color_layout);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        CircleImageView previewView = (CircleImageView) holder.findViewById(R.id.color_view);
        previewView.setFillingCircleColor(getValue());
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            this.value = value;
            if (isPersistent()) {
                persistInt(value);
            }
            notifyChanged();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return ColorUtils.parseColor(a.getString(index), Color.parseColor("#FF049372"));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public int getValue() {
        return value;
    }
}
