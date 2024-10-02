package com.example.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MainActivity extends AppCompatActivity implements LocationListener {
    GridView gridView;
    ArrayList<Images> galleryList = new ArrayList<>();
    ArrayList<String> storageIDlist = new ArrayList<String>();
    ArrayList<dataClass> dataList = new ArrayList<dataClass>();
    ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

    LocationManager locationManager;
    String address, city, country;
    int posit;
    LatLng loc;
    private DrawerLayout drawer;
    TextView photosText;
    ImageView upload;
    int time;
    int global = 0;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, MainActivity.this);

    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activitymain);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        downloadFile();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},100);
        }

        upload = findViewById(R.id.imageView4);
        gridView = findViewById(R.id.gridView);
        photosText = findViewById(R.id.textView3);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        ArrayList<Images> tempList = getIntent().getParcelableArrayListExtra("galleryList");
        if(tempList != null)
        galleryList = tempList;
        CustomAdapter adapter = new CustomAdapter(this, R.layout.adapter_layout, galleryList);
        gridView.setAdapter(adapter);
        if(galleryList.size()>0)
            photosText.setVisibility(View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_gallery)
                {
                    Toast.makeText(MainActivity.this, "Photo Gallery", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.nav_camera)
                {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 100);
                            photosText.setVisibility(View.INVISIBLE);
                }

                else if (item.getItemId() == R.id.nav_map) {
                    Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MapsActivity2.class);
                    intent.putParcelableArrayListExtra("galleryList", galleryList);
                    startActivity(intent);
                }

                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap pos = galleryList.get(position).getBitmap();
                posit = position;
                ShowDialogueBox(pos, posit);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            Date today = Calendar.getInstance().getTime();//getting date
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
            String date = formatter.format(today);
            galleryList.add(new Images(city, country,address, R.drawable.ic_launcher_foreground, captureImage, loc, date));
            CustomAdapter adapter = new CustomAdapter(this, R.layout.adapter_layout, galleryList);
            gridView.setAdapter(adapter);

        }
    }

    public void ShowDialogueBox(Bitmap pos, int posit)
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
                CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.adapter_layout, galleryList);
                gridView.setAdapter(adapter);
                dialog.dismiss();
                if(galleryList.size() == 0)
                {
                    photosText.setVisibility(View.VISIBLE);
                }
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try { addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); } catch (IOException e) { }
         address = addresses.get(0).getAddressLine(0);
         city = addresses.get(0).getLocality();
         country = addresses.get(0).getCountryName();
         loc = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void uploadFile()
    {
        for(int x = 0; x<galleryList.size(); x++)
        {

            Bitmap bitmap = galleryList.get(x).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            time = (int) System.currentTimeMillis();
            StorageReference fileReference = storageReference.child(String.valueOf(time));
            fileReference.putBytes(data);

            String id2 = databaseReference.push().getKey();
            databaseReference.child(id2).setValue(new dataClass(galleryList.get(x).getAddress(), galleryList.get(x).getCity(), galleryList.get(x).getCountry(), galleryList.get(x).getDate(), String.valueOf(time), String.valueOf(galleryList.get(x).getLocation())));


        }
    }

    public void downloadFile()
    {
        Log.e("d", "downloaded");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren())
                {
                    dataClass s = postSnapshot.getValue(dataClass.class);
                    dataList.add(s);
                    storageIDlist.add(s.getIds());
                }

                if(dataList.size() >0)
                {
                    photosText.setVisibility(View.INVISIBLE);
                }

                    try {
                        for(int x = 0; x<dataList.size(); x++)
                        {
                            global = x;
                            storageReference = FirebaseStorage.getInstance().getReference().child("uploads/" + storageIDlist.get(global));
                             File localFile = File.createTempFile(storageIDlist.get(x), "");

                            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    bitmapList.add(bitmap);

                                    String actstring = dataList.get(global).getLocations();
                                    actstring = actstring.replace("(", "");
                                    actstring = actstring.replace(")", "");
                                    actstring = actstring.replace("lat/lng:", "");
                                    actstring = actstring.replace(" ", "");
                                    String[] latlong = actstring.split(",");

                                    double latitude = Double.parseDouble(latlong[0]);
                                    double longitude = Double.parseDouble(latlong[1]);
                                    galleryList.add(new Images(dataList.get(global).getCitys(), dataList.get(global).getCountrys(), dataList.get(global).getAddresss(), 5, bitmapList.get(global),new LatLng(latitude, longitude), dataList.get(global).getDates()));
                                    CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.adapter_layout, galleryList);
                                    gridView.setAdapter(adapter);
                                    databaseReference.removeValue();
                                    storageReference.delete();
                                }
                            });

                        }

                    } catch (IOException e) { e.printStackTrace(); }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}