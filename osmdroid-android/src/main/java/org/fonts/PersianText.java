package org.fonts;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import org.osmdroid.library.R;


@SuppressLint("AppCompatCustomView")
public class PersianText extends TextView
{
    private String typeFace="";

    public PersianText(Context context) {
        this(context,null);
    }

    public PersianText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PersianText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttr(context,attrs,defStyle);
        if(typeFace!=null)
            setTypeFace(context);
    }

    private void getAttr(Context context, AttributeSet attrs,int def){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TypeFacePersian,
                def, 0);
        try {
            typeFace = a.getString(R.styleable.TypeFacePersian_persianFontAddress);
            if(typeFace!=null && !typeFace.contains("fonts/")){
                typeFace="fonts/"+typeFace;
            }
        } finally {
            a.recycle();
        }
    }


    private void setTypeFace(Context context){
        if(!typeFace.equals("")&&typeFace!=null)
        {
            Typeface face = Typeface.createFromAsset(context.getAssets(), typeFace);
            super.setTypeface(face);
        }
    }
}
