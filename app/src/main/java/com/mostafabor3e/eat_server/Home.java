package com.mostafabor3e.eat_server;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mostafabor3e.eat_server.Adapter.AdapterGategroy;
import com.mostafabor3e.eat_server.Model.Gategroy;
import com.mostafabor3e.eat_server.Model.Token;
import com.mostafabor3e.eat_server.Service.FcmMessagingService;
import com.mostafabor3e.eat_server.ui.Order;
import com.mostafabor3e.eat_server.viewModel.HomeViewModel;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    Button upload,select;
    MaterialEditText name;
    Gategroy newGategroy;
    AdapterGategroy adapterGategroy;
    private HomeViewModel homeViewModel;
    List<Gategroy>gategroyList;
    DatabaseReference databaseReference;
     static final int IMAGE_RQUEST=1;
     Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        gategroyList=new ArrayList<>();

updateTaken(FirebaseInstanceId.getInstance().getToken());
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
         databaseReference = FirebaseDatabase.getInstance().getReference("Category");
        //databaseReference.push().setValue(newGategroy);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        Intent ser=new Intent(getBaseContext(), FcmMessagingService.class);
        startService(ser);
        homeViewModel.getGategroy().observe(this, new Observer<List<Gategroy>>() {
            @Override
            public void onChanged(List<Gategroy> gategroys) {
                gategroyList=gategroys;

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pip();

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.home, R.id.cart, R.id.order)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public void pip(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Add new Category");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_add,null);
        name=view.findViewById(R.id.ed_phono);
        select=view.findViewById(R.id.bt_call);
        upload=view.findViewById(R.id.bt_delete);
        alertDialog.setMessage("please fill full information");
        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectimage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimage();
            }
        });

        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }
    private void updateTaken(String token) {
        SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
        String phone=preferences.getString("phone","none");
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Tokens");
        Token data=new Token(token,true);
        reference.child(phone).setValue(data);
    }

    public void selectimage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_RQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_RQUEST&&resultCode==RESULT_OK){
            uri=data.getData();
            select.setText("Image Selected");
        }
    }
    private void uploadimage(){
        if (uri!=null) {
            final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Uploading......");
            dialog.show();
            String ImageName= UUID.randomUUID().toString();
            final StorageReference reference = FirebaseStorage.getInstance().getReference().child("Image/"+ImageName);
            final StorageReference storageReference = reference.child(uri.getLastPathSegment());

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(Home.this, "Uploading success", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dawnload = String.valueOf(uri);
                                    newGategroy=new Gategroy(name.getText().toString(),dawnload);
                                    databaseReference.push().setValue(newGategroy);

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()){
            case "update":
               // Toast.makeText(this, "update  "+ gategroyList.get(item.getOrder()).getKey(), Toast.LENGTH_SHORT).show();
                pipUpdata(gategroyList.get(item.getOrder()).getKey(),gategroyList.get(item.getOrder()));


                return true;
            case "delete":
                DeleteCaregroy(gategroyList.get(item.getOrder()).getKey());
                return true;
            default:}

        return super.onContextItemSelected(item);
    }
    public void pipUpdata(final String key , final Gategroy gategroy){


        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Category");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_add,null);
        name=view.findViewById(R.id.ed_phono);
        select=view.findViewById(R.id.bt_call);
        upload=view.findViewById(R.id.bt_delete);
        name.setText(gategroy.getName());
        alertDialog.setMessage("please fill full information");
        alertDialog.setView(view);

        alertDialog.setIcon(R.drawable.ic_baseline_update_24);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectimage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeImage(gategroy);
                databaseReference.child(key).setValue(gategroy);
            }
        });

        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gategroy.setName(name.getText().toString());
                databaseReference.child(key).setValue(gategroy);


            }
        });
        alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }
    private void ChangeImage(final Gategroy gategroy){
        if (uri!=null) {
            final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Changing Image.....");
            dialog.show();
            String ImageName= UUID.randomUUID().toString();
            final StorageReference reference = FirebaseStorage.getInstance().getReference().child("Image/"+ImageName);
            final StorageReference storageReference = reference.child(uri.getLastPathSegment());

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(Home.this, "Uploading success", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dawnload = String.valueOf(uri);
                                    gategroy.setImage(dawnload);

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }
    }
    public void DeleteCaregroy(String key){
        databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            //private Object ContentView;

            @Override
            public void onSuccess(Void aVoid) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout,"delete ",Snackbar.LENGTH_SHORT).show();
                Toast.makeText(Home.this, "Delete Success ", Toast.LENGTH_SHORT).show();

            }
        });
    }
}