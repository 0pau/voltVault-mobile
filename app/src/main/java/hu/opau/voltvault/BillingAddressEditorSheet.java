package hu.opau.voltvault;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hu.opau.voltvault.models.BillingAddress;

public class BillingAddressEditorSheet extends BottomSheetDialogWithResult {

    private Activity parent;

    public BillingAddressEditorSheet(Activity parent, BillingAddress address) {
        super(parent, R.style.Base_Theme_VoltVault_BottomSheet);
        this.parent = parent;
        setContentView(R.layout.billing_address_editor);

        findViewById(R.id.find_location_btn).setOnClickListener(l->{
            if (ActivityCompat.checkSelfPermission(parent, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                parent.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 444);
            } else {
                getLocation();
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
