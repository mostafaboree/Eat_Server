package com.mostafabor3e.eat_server.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mostafabor3e.eat_server.Adapter.AdapterOrder;
import com.mostafabor3e.eat_server.Commen;
import com.mostafabor3e.eat_server.Interface.SetonClickLisener;
import com.mostafabor3e.eat_server.Model.Notification;
import com.mostafabor3e.eat_server.Model.Request;
import com.mostafabor3e.eat_server.Model.Response;
import com.mostafabor3e.eat_server.Model.Sender;
import com.mostafabor3e.eat_server.Model.Token;
import com.mostafabor3e.eat_server.R;
import com.mostafabor3e.eat_server.Remot.ApiServec;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class Order extends AppCompatActivity {
   List<Request> orderes;
   RecyclerView recyclerView;
   DatabaseReference order;
   AdapterOrder adapterOrder;
   String phone;
   MaterialSpinner spinner;
   ApiServec  apiServec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        orderes= new ArrayList<>();
        recyclerView=findViewById(R.id.rec_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setHasFixedSize(true);
        getOrder();
        SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
        phone=preferences.getString("phone","none");
        apiServec= Commen.getFcm();

    }
    public void getOrder(){
        order= FirebaseDatabase.getInstance()
                .getReference("Request");
        order.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderes.clear();
                for(DataSnapshot id:snapshot.getChildren()){
                    Request request=id.getValue(Request.class);
                    String order_id=id.getKey();
                    request.setId(order_id);
                   String statues= ConvertCodeToStatu(request.getStute());
                   request.setStute(statues);
                    orderes.add(request);}


                adapterOrder=new AdapterOrder(orderes, getBaseContext(), new SetonClickLisener() {
                    @Override
                    public void onclick(View veiw, int postion, boolean isLongClick) {
                        SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                        editor.putString("order_id",orderes.get(postion).getId());
                        editor.putString("order_phone",orderes.get(postion).getPhone());
                        editor.apply();
                        Intent intent=new Intent(getBaseContext(),TrackerOrder.class);
                        intent.putExtra("order",orderes.get(postion));
                        startActivity(intent);
                    }
                });
                adapterOrder.notifyDataSetChanged();
                recyclerView.setAdapter(adapterOrder);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String ConvertCodeToStatu(String status){
        if (status.equals("0"))
            return "placed";

        else if (status.equals("1"))
            return "On My Way";

        else return "Shipped";

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()){
            case "Update":
                Diloge(orderes.get(item.getOrder()).getId(),orderes.get(item.getOrder()));

                return true;
            case "Delete":
                deleteOrder((orderes.get(item.getOrder()).getId()));
                adapterOrder.notifyDataSetChanged();
                return true;
            case "Details":
                Intent details=new Intent(getBaseContext(),OrderDetails.class);
                details.putExtra("order_id",orderes.get(item.getOrder()).getId());
                startActivity(details);
            default:}
        return super.onContextItemSelected(item);
    }

public void Diloge(final String key, final Request item){
        final DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Request");

    AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
    alertDialog.setTitle("Update Status");
    LayoutInflater inflater=this.getLayoutInflater();
    View view=inflater.inflate(R.layout.udata_order,null);
    spinner=view.findViewById(R.id.sp_order);
    spinner.setItems("Placed","On My Wat","Shipped");
    alertDialog.setMessage("please Select your Status");

    alertDialog.setView(view);
    alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            item.setStute(String.valueOf(spinner.getSelectedIndex()));
            String it=Commen.ConvertCodeToStatu(String.valueOf(spinner.getSelectedIndex()));

            reference.child(key).setValue(item);
            senNotifiactionOrder(key,it);
            dialogInterface.dismiss();
        }
    });
    alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    });
    alertDialog.show();
}
public void deleteOrder(String key){
    final DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Request");
    reference.child(key).removeValue();
    View parentLayout = findViewById(android.R.id.content);
    Snackbar.make(parentLayout,"delete ",Snackbar.LENGTH_SHORT).show();


}
    private void senNotifiactionOrder(final String order_id,String item) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=databaseReference.orderByChild("istoken").equalTo(false);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);

                    Notification notification=new Notification("You order stute"+order_id+"update"+item,"Eat App");
                    Sender sender=new Sender(token.getTekon(),notification);
                    apiServec.sendNotifacation(sender).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if (response.isSuccessful())
                                Toast.makeText(Order.this, "order stuate update ", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}