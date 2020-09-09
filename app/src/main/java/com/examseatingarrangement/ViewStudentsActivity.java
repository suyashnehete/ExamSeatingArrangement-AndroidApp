package com.examseatingarrangement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.examseatingarrangement.Adapter.StudentAdapter;
import com.examseatingarrangement.Model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewStudentsActivity extends AppCompatActivity {

    LinearLayoutManager layoutManager;
    RecyclerView student_list;
    String department, college, semester;
    String[] semArray;
    AppCompatSpinner select_semester;
    ArrayList<Student> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Boolean isDark = getSharedPreferences("ESADARK", MODE_PRIVATE).getBoolean("darkMode", false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_students);
        layoutManager = new LinearLayoutManager(this);

        getSupportActionBar().setTitle("Students");
        student_list = (RecyclerView) findViewById(R.id.student_list);

        select_semester = findViewById(R.id.select_semester);
        student_list.setHasFixedSize(true);
        student_list.setLayoutManager(layoutManager);

        uploads = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("ESA", MODE_PRIVATE);
        college = preferences.getString("collegeName", null);
        department = preferences.getString("departmentName", null);

        Calendar cal = Calendar.getInstance();
        if (department.toLowerCase().contains("computer")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "CO2I", "CO4I", "CO6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "CO1I", "CO3I", "CO5I"};
            }
        } else if (department.toLowerCase().contains("it") || department.toLowerCase().contains("information")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "IF2I", "IF4I", "IF6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "IF1I", "IF3I", "IF5I"};
            }
        } else if (department.toLowerCase().contains("civil")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "CE2I", "CE4I", "CE6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "CE1I", "CE3I", "CE5I"};
            }
        } else if (department.toLowerCase().contains("electrical")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "EE2I", "EE4I", "EE6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "EE1I", "EE3I", "EE5I"};
            }
        } else if (department.toLowerCase().contains("mechanical")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "ME2I", "ME4I", "ME6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "ME1I", "ME3I", "ME5I"};
            }
        } else if (department.toLowerCase().contains("electronics")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "EC2I", "EC4I", "EC6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "EC1I", "EC3I", "EC5I"};
            }
        } else if (department.toLowerCase().contains("chemical")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "CH2I", "CH4I", "CH6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "CH1I", "CH3I", "CH5I"};
            }
        } else if (department.toLowerCase().contains("tr") || department.toLowerCase().contains("travel")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "TR2G", "TR4G", "TR6G"};
            } else {
                this.semArray = new String[]{"Select Semester", "TR1G", "TR3G", "TR5G"};
            }
        } else {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "2nd Semester", "4th Semester", "6th Semester"};
            } else {
                this.semArray = new String[]{"Select Semester", "1st Semester", "3rd Semester", "5th Semester"};
            }
        }

        select_semester.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_spinner, semArray));

        select_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Select Semester")) {
                    semester = null;
                } else {
                    semester = parent.getItemAtPosition(position).toString();

                    uploads = new ArrayList<>();

                    FirebaseDatabase.getInstance().getReference("Colleges").child(college).child(department)
                            .child("Students").child(semester)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    uploads.clear();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Student upload = postSnapshot.getValue(Student.class);
                                        uploads.add(upload);
                                    }
                                    if (uploads.size() == 0) {
                                        AlertDialog.Builder alert = new AlertDialog.Builder(ViewStudentsActivity.this);
                                        alert.setTitle("Alert");
                                        alert.setMessage("No Students Found!!!");
                                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onBackPressed();
                                            }
                                        });
                                        alert.create();
                                        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                                            alert.show();
                                        }
                                    } else {
                                        //creating adapter
                                        RecyclerView.Adapter adapter = new StudentAdapter(ViewStudentsActivity.this, uploads, department, semester, college);

                                        student_list.setAdapter(adapter);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
