package com.lambton.note_elite_android.note;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.database.LocationDataFileDao;
import com.lambton.note_elite_android.model.LocationDataFile;
import com.lambton.note_elite_android.model.Note;

import java.util.ArrayList;
import java.util.List;
import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;
@IntentBuilder
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,  GoogleMap.OnMarkerDragListener {
    private static final int REQUEST_CODE = 1;
    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    private Marker homeMarker;
    private LatLng noteLocation;
    private boolean notesFlag = false;
    String subStringLocation;
    static int noteIdNumber;
    private static final String TAG = "MapsActivity";
    @Extra
    Integer noteId;
    Note note;

    List<LocationDataFile> locationDataFilesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationDataFilesList = new ArrayList<>();

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(MapsActivity.this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                setHomeMarker(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (!hasLocationPermission()) {
            requestLocationPermission();
        } else {
            startUpdateLocations();
        }

        locationDataFilesList = LocationDataFileDao.getLatestLocation(noteIdNumber);

        if (locationDataFilesList.size() != 0){

            MarkerOptions options = new MarkerOptions().position(new LatLng(locationDataFilesList.get(0).getLatitude(),locationDataFilesList.get(0).getLongitude()))
                    .title("This Note")
                    .snippet("Created over here");
            mMap.addMarker(options);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationDataFilesList.get(0).getLatitude(),locationDataFilesList.get(0).getLongitude()),15));
        }
    }
    private void startUpdateLocations() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(!notesFlag){
            setHomeMarker(lastKnownLocation);
        }
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void setHomeMarker(Location location) {
        if(location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions options = new MarkerOptions().position(userLocation)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet("Your Location Data File");
            homeMarker = mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            noteLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }
    @Override
    public void onMarkerDragStart(Marker marker) {
    }
    @Override
    public void onMarkerDrag(Marker marker) {
    }
    @Override
    public void onMarkerDragEnd(Marker marker) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}