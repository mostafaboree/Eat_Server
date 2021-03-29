package com.mostafabor3e.eat_server.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mostafabor3e.eat_server.Adapter.AdapterFood;
import com.mostafabor3e.eat_server.Model.Food;
import com.mostafabor3e.eat_server.R;
import com.mostafabor3e.eat_server.viewModel.FoodViewModel;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FoodD extends AppCompatActivity {
    private static final int IMAGE_RQUEST =2;
    Uri UriImage;
    FoodViewModel homeViewModel;
    RecyclerView recyclerView;
    MaterialEditText name,description,discount,price;
    Button upload,select;
    AdapterFood adapterFood;
FirebaseDatabase firebaseDatabase;
DatabaseReference  databaseReference,dRe;
FloatingActionButton add;
List<Food>FoodList;
Food newfood;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_d);
        add=findViewById(R.id.fab_add_food);
        recyclerView=findViewById(R.id.rec_food);
        FoodList=new ArrayList<>();
        SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
        key =preferences.getString("key","none");
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        dRe=FirebaseDatabase.getInstance().getReference("Foods");

        recyclerView.setHasFixedSize(true);

        getFood(key);
        adapterFood=new AdapterFood(FoodList, getBaseContext());
            /*    new SetonClickLisener() {
            @Override
            public void onclick(View veiw, int postion, boolean isLongClickaw) {
                Toast.makeText(FoodD.this, "name = "+FoodList.get(postion).getName(), Toast.LENGTH_SHORT).show();


            }
        });*/
        recyclerView.setAdapter(adapterFood);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pip();

            }
        });

       // getFood();
     /* homeViewModel=ViewModelProviders.of(this).get(FoodViewModel.class);
      homeViewModel.getFood().observe(this, new Observer<List<Food>>() {
          @Override
          public void onChanged(List<Food> foods) {
            *//*  adapterFood=new AdapterFood(foods, getBaseContext(), new SetonClickLisener() {
                  @Override
                  public void onclick(View veiw, int postion, boolean isLongClick) {

                  }
              });*//*
             // recyclerView.setAdapter(adapterFood);
          }
      });*/


    }
    public void getFood(final String key){

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Foods");
        FoodList=new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                FoodList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Food food=dataSnapshot.getValue(Food.class);
                    if (food.getMenuId().toString().equals(key)){

                        food.setKey(dataSnapshot.getKey());
                        FoodList.add(food);
                    }
                    adapterFood.notifyDataSetChanged();




                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FoodList.clear();


            }
        });
    }

    //show alert dialog to add new food

    public void pip(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Add new Category");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_add_food,null);
        name=view.findViewById(R.id.ed_name_addfood);
        description=view.findViewById(R.id.ed_des_addfood);
        discount=view.findViewById(R.id.ed_discount_addfood);
        price=view.findViewById(R.id.ed_price_addfood);
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
    public void selectimage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_RQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_RQUEST&&resultCode==RESULT_OK){
            UriImage=data.getData();
            select.setText("Image Select");

        }
    }
    private void uploadimage() {
        if (UriImage != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading......");
            dialog.show();
            String ImageName = UUID.randomUUID().toString();
            final StorageReference reference = FirebaseStorage.getInstance().getReference().child("Image/" + ImageName);
            final StorageReference storageReference = reference.child(UriImage.getLastPathSegment());

            storageReference.putFile(UriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(getBaseContext(), "Uploading success", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dawnload = String.valueOf(uri);
                                    newfood = new Food(description.getText().toString(), dawnload, discount.getText().toString(),
                                            name.getText().toString(), key, price.getText().toString());
                                    databaseReference.push().setValue(newfood);

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }
    }


    //click lisner menu


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()){
            case "Update":
                Toast.makeText(this, "Update  "+ FoodList.get(item.getOrder()).getKey(), Toast.LENGTH_SHORT).show();
                pipUpdata(FoodList.get(item.getOrder()).getKey(),FoodList.get(item.getOrder()));
                return true;
            case "Delete":
                DeleteCaregroy(FoodList.get(item.getOrder()).getKey());
                return true;
            default:}
        return super.onContextItemSelected(item);
    }

    public void pipUpdata(final String key , final Food gategroy){


        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Category");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.item_add_food,null);
        name=view.findViewById(R.id.ed_name_addfood);
        description=view.findViewById(R.id.ed_des_addfood);
        discount=view.findViewById(R.id.ed_discount_addfood);
        price=view.findViewById(R.id.ed_price_addfood);
        select=view.findViewById(R.id.bt_call);
        upload=view.findViewById(R.id.bt_delete);
        name.setText(gategroy.getName());
        description.setText(gategroy.getDescription());
        price.setText(gategroy.getPrice());
        discount.setText(gategroy.getDiscount());
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
                dRe.child(key).setValue(gategroy);
            }
        });

        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gategroy.setName(name.getText().toString());
                gategroy.setPrice(price.getText().toString());
                gategroy.setDiscount(discount.getText().toString());
                description.setText(description.getText());
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
    private void ChangeImage(final Food gategroy){
        if (UriImage!=null) {
            final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Changing Image.....");
            dialog.show();
            String ImageName= UUID.randomUUID().toString();
            final StorageReference reference = FirebaseStorage.getInstance().getReference().child("Image/"+ImageName);
            final StorageReference storageReference = reference.child(UriImage.getLastPathSegment());

            storageReference.putFile(UriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(getBaseContext(), "Uploading success", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getBaseContext(), "Delete Success ", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
