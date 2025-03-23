package hu.opau.voltvault;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetDialogWithResult extends BottomSheetDialog {

    Object result;
    boolean hasResult = false;

    public BottomSheetDialogWithResult(@NonNull Context context) {
        super(context);
    }

    public BottomSheetDialogWithResult(@NonNull Context context, int theme) {
        super(context, theme);
    }

    public void closeWithResult(Object obj) {
        result = obj;
        hasResult = true;
        dismiss();
    }
}
