package com.examseatingarrangement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Lifecycle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    static String college_name, department_name;
    View view;
    AppCompatAutoCompleteTextView edittext;
    ArrayList<String> colleges;
    ArrayAdapter<String> adapter;
    AppCompatButton admin, student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        admin = findViewById(R.id.admin);
        student = findViewById(R.id.student);
        view = View.inflate(MainActivity.this, R.layout.layout_edittext, null);
        edittext = view.findViewById(R.id.edittext);
        colleges = new ArrayList<>();

        preferences = getSharedPreferences("ESA", MODE_PRIVATE);
        college_name = preferences.getString("collegeName", null);
        department_name = preferences.getString("departmentName", null);

        if (college_name == null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Add College");
            alert.setMessage("Enter College Name.\nExample: Government Polytechnic, Thane\n\n*Note: College Name Cannot Be Changed.");
            alert.setView(view);
            alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (edittext.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "College Name Cannot be Empty.", Toast.LENGTH_SHORT).show();
                        recreate();
                    } else {
                        preferences.edit()
                                .putString("collegeName", edittext.getText().toString())
                                .commit();
                        college_name = edittext.getText().toString();
                    }
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                }
            });
            alert.create();
            alert.show();
        } else {
            if (department_name != null) {
                startActivity(
                        new Intent(MainActivity.this, HomeActivity.class)
                );
            }
        }
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(MainActivity.this, AdminLoginActivity.class)
                                .putExtra("collegeName", college_name)
                );
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(MainActivity.this, StudentLoginActivity.class)
                                .putExtra("collegeName", college_name)
                );
            }
        });
    }

    private void textChanged(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Departments").orderByKey()
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                colleges.clear();
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        colleges.add(snapshot.getKey());
                    }
                    adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.layout_spinner, colleges);
                    edittext.setThreshold(1);
                    edittext.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(
                    new Intent(MainActivity.this, SettingActivity.class)
            );
            return true;
        } else if (id == R.id.share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download GPThane App");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Download app form https://gpthnae.org.in");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        } else if (id == R.id.feedback) {
            startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO)
                    .setData(Uri.parse("mailto:gptexamseatingarrangement@gmail.com?subject=Exam Seating Arrangement")), "Send Feedback"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean isDark = getSharedPreferences("ESADARK", MODE_PRIVATE).getBoolean("darkMode", true);

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
