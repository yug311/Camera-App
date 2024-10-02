package com.example.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.photogallery.databinding.ActivityMaps2Binding;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    LocationManager locationManager;
    ArrayList<Images> galleryList = new ArrayList<Images>();
    private DrawerLayout drawer;
    String city, country, address;
    LatLng loc;
    ArrayList<String> mapIds = new ArrayList<String>();

    @Override
    protected void onResume() {
        super.onResume();
        if(ContextCompat.checkSelfPermission(MapsActivity2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.CAMERA},1000);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, MapsActivity2.this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ContextCompat.checkSelfPermission(MapsActivity2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.CAMERA},1000);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);


        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbara);
        // setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        galleryList = getIntent().getParcelableArrayListExtra("galleryList");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_gallery)
                {
                    Toast.makeText(MapsActivity2.this, "Photo Gallery", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MapsActivity2.this, MainActivity.class);
                    intent.putParcelableArrayListExtra("galleryList", galleryList);
                    intent.putExtra("yo", 5.7);
                    startActivity(intent);
                }
                else if (item.getItemId() == R.id.nav_camera)
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1000);
                }

                else if (item.getItemId() == R.id.nav_map) {
                    Toast.makeText(MapsActivity2.this, "Map", Toast.LENGTH_SHORT).show();
                }

                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mMap!=null)
        {

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Nullable
                @Override
                public View getInfoContents(@NonNull Marker marker) {
                    return null;
                }

                @Nullable
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    int pos = 0;
                    for(int x = 0; x<=mapIds.size() - 1; x++)
                    {
                        if(String.valueOf(marker.getId()).equals(String.valueOf(mapIds.get(x))))
                        {
                            pos = x;
                        }
                    }
                    View row = getLayoutInflater().inflate(R.layout.custom_marker, null);
                    ImageView imageView = row.findViewById(R.id.imageView2);
                    TextView tv1 = row.findViewById(R.id.textView2);
                    TextView tv2 = row.findViewById(R.id.textView4);
                        tv2.setText(galleryList.get(pos).getDate());
                        tv1.setText(galleryList.get(pos).getCity() + ", " + galleryList.get(pos).getCountry());
                        imageView.setImageBitmap(galleryList.get(pos).getBitmap());
                    return row;
                }
            });
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                int pos = 0;
                for(int x = 0; x<=mapIds.size() - 1; x++)
                {
                    if(String.valueOf(marker.getId()).equals(String.valueOf(mapIds.get(x))))
                    {
                        pos = x;
                    }
                }
                Bitmap bitmap = galleryList.get(pos).getBitmap();
            ShowDialogueBox(bitmap, pos, marker);
            }
        });

        for(int x = 0; x<=galleryList.size() - 1; x++)
        {
            Marker m = mMap.addMarker(new MarkerOptions().position(galleryList.get(x).getLocation()));
            mapIds.add(String.valueOf(m.getId()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            Date today = Calendar.getInstance().getTime();//getting date
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
            String date = formatter.format(today);
            galleryList.add(new Images(city, country,address, R.drawable.ic_launcher_foreground, captureImage,loc, date));
            Marker c = mMap.addMarker(new MarkerOptions().position(galleryList.get(galleryList.size() - 1).getLocation()));
            mapIds.add(String.valueOf(c.getId()));
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(MapsActivity2.this, Locale.getDefault());
        List<Address> addresses = null;
        try { addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); } catch (IOException e) { }
        address = addresses.get(0).getAddressLine(0);
        city = addresses.get(0).getLocality();
        country = addresses.get(0).getCountryName();
        loc = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void ShowDialogueBox(Bitmap pos, int posit, Marker marker)
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_box);
        ImageView image = dialog.findViewById(R.id.imageView3);
        Button close = dialog.findViewById(R.id.button4);
        Button delete = dialog.findViewById(R.id.button);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryList.remove(galleryList.size() - 1);
                marker.remove();
                dialog.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView textView = dialog.findViewById(R.id.textView);
        textView.setText(galleryList.get(posit).getDate() + "\n" +galleryList.get(posit).getCity() + ", " + galleryList.get(posit).getCountry());
        image.setImageBitmap(pos);

        dialog.show();
    }
}