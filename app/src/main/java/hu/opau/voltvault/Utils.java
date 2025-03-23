package hu.opau.voltvault;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.TypedValue;

import androidx.annotation.ColorInt;

public class Utils {

    public static float dpToPx(Context ctx, float dip) {
        return dpCalc(ctx, dip);
    }

    public static int spToPx(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPxInt(Context ctx, float dip) {
        return (int)dpCalc(ctx, dip);
    }

    private static float dpCalc(Context ctx, float dip) {
        Resources r = ctx.getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
    }

    public static int getColor(Context ctx, int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = ctx.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    public static Bitmap convertBase64(String base64) {
        byte[] decodedString = Base64.decode(base64.replace("data:image/png;base64,", ""), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

}
