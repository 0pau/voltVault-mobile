package hu.opau.voltvault;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.res.Configuration;
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

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String formatPrice(long value, String currency) {
        return String.format("%,d", value).replace(",", " ")+" "+currency;
    }

    public static String checkTheme(Context c) {
        String theme = c.getSharedPreferences("user-prefs", MODE_PRIVATE).getString("theme", "system");
        switch (theme) {
            case "light":
                c.getApplicationContext().setTheme(R.style.Base_Theme_VoltVault_Light);
                c.setTheme(R.style.Base_Theme_VoltVault_Light);
                break;
            case "dark":
                c.getApplicationContext().setTheme(R.style.Base_Theme_VoltVault_Dark);
                c.setTheme(R.style.Base_Theme_VoltVault_Dark);
                break;
        }
        return theme;
    }

    public static int getPreferredBottomSheetTheme(Context c) {
        String theme = c.getSharedPreferences("user-prefs", MODE_PRIVATE).getString("theme", "system");
        switch (theme) {
            case "light":
                System.out.println("Light");
                return R.style.Base_Theme_VoltVault_BottomSheet_Light;
            case "dark":
                System.out.println("Dark");
                return R.style.Base_Theme_VoltVault_BottomSheet_Dark;
        }
        System.out.println("System");
        return R.style.Base_Theme_VoltVault_BottomSheet;
    }

}
