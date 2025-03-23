package hu.opau.voltvault;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingDialog extends Dialog {
    private ProgressBar pb;
    protected LoadingDialog(Context context) {
        super(context, R.style.SpinnerDialog);

        pb = new ProgressBar(context);
        pb.getIndeterminateDrawable().setColorFilter(new PorterDuffColorFilter(Utils.getColor(getContext(), R.color.white), PorterDuff.Mode.ADD));

        setCancelable(false);
        setContentView(pb);
    }

    @Override
    public void show() {
        super.show();
    }
}
