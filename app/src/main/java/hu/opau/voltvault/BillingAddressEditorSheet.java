package hu.opau.voltvault;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hu.opau.voltvault.models.BillingAddress;

public class BillingAddressEditorSheet extends BottomSheetDialogWithResult {

    private Activity parent;

    public BillingAddressEditorSheet(Activity parent, BillingAddress address) {
        super(parent, Utils.getPreferredBottomSheetTheme(parent));
        this.parent = parent;
        setContentView(R.layout.billing_address_editor);

        findViewById(R.id.autoFillBtn).setOnClickListener(l->{

            ArrayList<String> missingPermissions = new ArrayList<>();

            if (ActivityCompat.checkSelfPermission(parent, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                getLocation();
            }
            if (ActivityCompat.checkSelfPermission(parent, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.READ_CONTACTS);
            } else {
                getOwner();
            }
            if (ActivityCompat.checkSelfPermission(parent, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(parent, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.READ_PHONE_STATE);
                missingPermissions.add(Manifest.permission.READ_PHONE_NUMBERS);
            } else {
                getPhoneNumber();
            }

            if (missingPermissions.size() != 0) {
                String[] arr = new String[missingPermissions.size()];
                arr = missingPermissions.toArray(arr);
                parent.requestPermissions(arr, 444);
            }

        });

        ((EditText)findViewById(R.id.fullName)).setText(address.getName());
        ((EditText)findViewById(R.id.phone)).setText(address.getPhone());
        ((EditText)findViewById(R.id.country)).setText(address.getCountry());
        ((EditText)findViewById(R.id.postalCode)).setText(Integer.toString(address.getPostalCode()));
        ((EditText)findViewById(R.id.city)).setText(address.getCity());
        ((EditText)findViewById(R.id.address)).setText(address.getAddress());
        ((CheckBox)findViewById(R.id.setAsPrimary)).setChecked(address.isPrimary());

        findViewById(R.id.saveBtn).setOnClickListener(l ->{
            closeWithResult(true);
        });
    }

    @SuppressLint("Range")
    public void getOwner() {
        Cursor c = getContext().getApplicationContext().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        ((EditText)findViewById(R.id.fullName)).setText(c.getString(c.getColumnIndex("display_name")));
        c.close();
    }

    public void getPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager)getContext().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String mPhoneNumber = tMgr.getLine1Number();
        ((EditText)findViewById(R.id.phone)).setText(mPhoneNumber);
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void getLocation() {
        LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        Location l = lm.getLastKnownLocation(LocationManager.FUSED_PROVIDER);

        Geocoder g = new Geocoder(getContext(), Locale.forLanguageTag("hu-HU"));
        List<Address> addresses;
        try {
            addresses = g.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
        } catch (Exception e) {
            Toast.makeText(parent, "Nem sikerült a helymeghatározás", Toast.LENGTH_SHORT).show();
            return;
        }
        Address a = addresses.get(0);

        ((EditText)findViewById(R.id.country)).setText(a.getCountryName());
        ((EditText)findViewById(R.id.postalCode)).setText(a.getPostalCode());
        ((EditText)findViewById(R.id.city)).setText(a.getLocality());
        ((EditText)findViewById(R.id.address)).setText(a.getThoroughfare() + " " + a.getFeatureName() + ".");
    }
}
