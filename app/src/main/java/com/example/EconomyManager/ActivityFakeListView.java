package com.example.EconomyManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityFakeListView extends AppCompatActivity // totul e perfect
{
    private ArrayList<MoneyManager> fakeArrayList;
    private CustomAdaptor fakeAdaptor;
    private ListView fakeListView;
    private Button fakeButton;
    private FirebaseAuth fbAuth;
    private DatabaseReference myRef;
    private Calendar currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_list_view);

        fakeArrayList = new ArrayList<>();
        fakeListView = findViewById(R.id.fakeListView);
        fakeButton = findViewById(R.id.fakeAddButton);
        fbAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        currentTime = Calendar.getInstance();
        //tryMethod();
        //setFakeListView();
        fakeAdaptor = new CustomAdaptor();
        fakeListView.setAdapter(fakeAdaptor);
        //fakeAdaptor.notifyDataSetChanged();

        fakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyManager money;
                if (fbAuth.getUid() != null) {
                    money = new MoneyManager("car gas", 150f, "August 03, 2020 15:20:21", "Expense");
                    String uniqueKey = myRef.child(fbAuth.getUid()).child("Fake").push().getKey();
                    if (uniqueKey != null)
                        myRef.child(fbAuth.getUid()).child("Fake").child(uniqueKey).setValue(money);
                }
            }
        });

        if(fbAuth.getUid()!=null)
        {
            myRef.child(fbAuth.getUid()).child("Fake").addChildEventListener(new ChildEventListener()
            {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {
                    String scope = String.valueOf(snapshot.child("scope").getValue()), date = String.valueOf(snapshot.child("date").getValue()), type = String.valueOf(snapshot.child("type").getValue());
                    Float value = Float.parseFloat(String.valueOf(snapshot.child("value").getValue()));

                    MoneyManager money = new MoneyManager(scope, value, date, type);
                    fakeArrayList.add(money);
                    fakeAdaptor.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot)
                {
                    fakeArrayList.remove(0);
                    fakeAdaptor.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

            myRef.child(fbAuth.getUid()).child("Fake").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    fakeArrayList.clear();
                    for (DataSnapshot childIterator : snapshot.getChildren())
                    {
                        MoneyManager money = new MoneyManager(String.valueOf(childIterator.child("scope").getValue()), Float.parseFloat(String.valueOf(childIterator.child("value").getValue())), String.valueOf(childIterator.child("date").getValue()), String.valueOf(childIterator.child("type").getValue()));
                        fakeArrayList.add(money);
                    }
                    fakeAdaptor.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
        }
    }

    class CustomAdaptor extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return fakeArrayList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) // cream fiecare element al listei sub forma unui cardview ce are o imagine, un titlu si o descriere
        {
            MoneyManager money=fakeArrayList.get(position);

            @SuppressLint({"ViewHolder", "InflateParams"}) View view=getLayoutInflater().inflate(R.layout.cardview_transaction_layout,null);
            TextView transactionType=view.findViewById(R.id.transactionType);
            TextView transactionPrice=view.findViewById(R.id.transactionPrice);
            TextView transactionNote=view.findViewById(R.id.transactionNote);
            ImageView transactionDelete=view.findViewById(R.id.transactionDelete);

            transactionType.setText(money.getType());
            transactionPrice.setText(String.valueOf(money.getValue()));
            transactionNote.setText(String.valueOf(money.getNote()));

            transactionDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //fakeArrayList.remove(position);
                    //notifyDataSetChanged();
                    if(fbAuth.getUid()!=null)
                    {
                        myRef.child(fbAuth.getUid()).child("Fake").child("-MEYwICepYVETQ2NWr-X").removeValue();
                    }
                }
            });

            return view;
        }
    }
}