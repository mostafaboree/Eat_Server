package com.mostafabor3e.eat_server.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mostafabor3e.eat_server.Commen;
import com.mostafabor3e.eat_server.Model.Request;
import com.mostafabor3e.eat_server.R;
import com.mostafabor3e.eat_server.Remot.GeoCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackerOrder<latLng> extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final int FINE_LOCATION_PERMISSION_REQUEST = 1001;
    public static final int REQUESTCODE = 1;
    private static final String TAG ="TrackerOrder" ;
    EditText phone;
    Button call,delete;
    FloatingActionButton actionButton;



    private Location currentlocation;
    FusedLocationProviderClient locationProviderClient;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequset;

    private static int UPDTATE_INTERVAL = 1100;
    private static int FATEST_INTERVAL = 2000;
    private static int DISPLACMENT = 10;

    private GoogleMap mMap,map;
    SupportMapFragment mapFragment;
    private GeoCode mservice;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_order);
        actionButton=findViewById(R.id.fbt_map);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoLocate();
            }
        });
        Intent intent=getIntent();
       request= (Request) intent.getSerializableExtra("order");
       // Toast.makeText(this, "id"+request.getId(), Toast.LENGTH_SHORT).show();
        mservice= Commen.getGeoCodeService();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            requestRuntimePermission();
        } else {
            if (checkPlayServer()) {
                buildGoogleApiClient();
                creatLocationRequest();
            }
        }
        locationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    private void creatLocationRequest() {
        locationRequset=new LocationRequest();
        locationRequset.setInterval(UPDTATE_INTERVAL);
        locationRequset.setFastestInterval(FATEST_INTERVAL);
        locationRequset.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequset.setSmallestDisplacement(DISPLACMENT);
    }





    private boolean checkPlayServer() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode!=ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "this device is not support", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
        }



    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_PERMISSION_REQUEST);


    }
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }





    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestRuntimePermission();
        }
        else {
            //currentlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            Task<Location> locationTask = locationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentlocation = location;
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double langitude = location.getLongitude();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //String cityName = addresses.get(0).getAddressLine(0);
                        //String admin = addresses.get(0).getAdminArea();

                        String stateName = addresses.get(0).getAddressLine(1);
                        String countryName = addresses.get(0).getAddressLine(2);
                        MarkerOptions options = new MarkerOptions().position(latLng).title("Im here");
                        mMap.clear();
                        mMap.addMarker(options);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
                     //   geoLocate();
                        displayTrack(latLng.toString(),request.getAddress());

                        //drawRoute();


                    } else {
                        Toast.makeText(TrackerOrder.this, "False cant't get current location", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TrackerOrder.this, "massage" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }}
    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");
        LatLng latLng = null;
        String searchString =request.getAddress();

        Geocoder geocoder = new Geocoder(TrackerOrder.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

             latLng=new LatLng(address.getLatitude(),address.getLongitude());

            MarkerOptions options = new MarkerOptions().position(latLng).title("order here"+request.getPhone());
          /*  mMap.addMarker(options);
                  .icon(BitmapDescriptorFactory.fromBitmap()).title("order here");*/
            map.addMarker(options);

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(12.0f));



        }
        else {
            String phone = request.getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);

        }
    }
        private void drawRoute(){
            String address= " Madinet Ahnasia, Ehnasia, Beni Suef Governorate";
            mservice.getCoeCode(address).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject=new JSONObject(response.body());

                        Double lat =((JSONArray) jsonObject.get("results"))
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location")
                                .getDouble("lat");
                        Toast.makeText(TrackerOrder.this, "lat"+lat, Toast.LENGTH_SHORT).show();
                        Double lng =((JSONArray) jsonObject.get("results"))
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location")
                                .getDouble("lng");
                        LatLng orderLocation=new LatLng(lat,lng);

                        MarkerOptions options = new MarkerOptions().position(orderLocation).title("order here");
                        mMap.addMarker(options);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(orderLocation));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(TrackerOrder.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(TrackerOrder.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
      getCurrentLocation();
      startLocationUpdate();


    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;


        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequset,this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        location=currentlocation;
        getCurrentLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient!=null){
            googleApiClient.connect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        map=googleMap;


    }





    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_PERMISSION_REQUEST);
            }
            return false;
        } else {
            return true;
        }
    }
public void displayTrack(String source,String Dis){
        try {
            Uri uri=Uri.parse("https://www.google.co.in/maps/dir/" + source + "/"+Dis);
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(intent);
        }catch (ActivityNotFoundException e){
            Uri uri=Uri.parse("https://play.google.com/store/apps/details?id=com.android.apps.maps");
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(intent);
        }

}






    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    {
                        getCurrentLocation();
                        buildGoogleApiClient();
                        creatLocationRequest();
                    }

                }
                break;
            }
        }
    }

public void SuggestOrder(){
    AlertDialog.Builder dialog=new AlertDialog.Builder(getBaseContext());
    dialog.setTitle("Can't Find Order Location");
    dialog.setMessage("I suggest you call Client or delete Order");
    LayoutInflater inflater=this.getLayoutInflater();
    View view=inflater.inflate(R.layout.item_add,null);
    phone=view.findViewById(R.id.ed_phono);
    call=view.findViewById(R.id.bt_call);
    delete=view.findViewById(R.id.bt_delete);
    phone.setText(request.getPhone());
    dialog.setView(view);

    dialog.setIcon(R.drawable.ic_baseline_update_24);
    call.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Toast.makeText(this, "can't find order location i suggest call order"+request.getPhone(), Toast.LENGTH_SHORT).show();
            String phone = request.getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);

        }
    });
    delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(TrackerOrder.this, "delete", Toast.LENGTH_SHORT).show();
        }
    });

    dialog.setPositiveButton("Back", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent=new Intent(getBaseContext(),Order.class);

        }
    });
    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    });
dialog.show();
}
}