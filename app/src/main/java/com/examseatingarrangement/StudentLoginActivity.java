package com.examseatingarrangement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.examseatingarrangement.Model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class StudentLoginActivity extends AppCompatActivity {

    AppCompatSpinner department;
    AppCompatEditText enrollment;
    AppCompatImageButton search;
    Intent intent;
    String college_name, department_name;
    ArrayList<String> departments;
    Boolean isNotAvailable = true;
    CardView cardView;
    AppCompatTextView name, enrollmentno, rollno, seatno, phone, email, classno, benchno, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        getSupportActionBar().setTitle("Student Login");
        department = findViewById(R.id.department);
        enrollment = findViewById(R.id.enrollment);
        search = findViewById(R.id.search);
        cardView = findViewById(R.id.card_view);
        name = findViewById(R.id.name);
        enrollmentno = findViewById(R.id.enrollmentno);
        rollno = findViewById(R.id.rollno);
        seatno = findViewById(R.id.seatno);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        classno = findViewById(R.id.classno);
        benchno = findViewById(R.id.benchno);
        date = findViewById(R.id.date);

        departments = new ArrayList<>();
        departments.add("Select Department");

        intent = getIntent();
        college_name = intent.getStringExtra("collegeName");

        FirebaseDatabase.getInstance().getReference("Colleges").child(college_name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        departments.clear();
                        if (dataSnapshot.getChildrenCount() != 0) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                departments.add(snapshot.getKey());
                            }
                            department.setAdapter(new ArrayAdapter<String>(StudentLoginActivity.this, R.layout.layout_spinner, departments));
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                            alert.setTitle("Alert");
                            alert.setMessage("No Students Registered!!!.\nContact Your Admin.");
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cardView.setVisibility(View.GONE);
                if (parent.getItemAtPosition(position).toString().equals("Select Department")) {
                    department_name = null;
                } else {
                    department_name = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        enrollment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.GONE);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (department_name == null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Select Department");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            department.requestFocus();
                        }
                    });
                    alert.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        alert.show();
                    }
                } else if (enrollment.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Enrollment No.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enrollment.requestFocus();
                        }
                    });
                    alert.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        alert.show();
                    }
                } else {
                    viewDetails(college_name, enrollment.getText().toString().trim(), department_name);
                }
            }
        });


    }

    private void viewDetails(String college, final String enrollment, String department) {
        cardView.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference("Colleges").child(college).child(department).child("Students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                            alert.setTitle("Alert");
                            alert.setMessage("Student Not Registered.\nContact Admin");
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
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Student student = snapshot1.getValue(Student.class);
                                    if (student.getEnrollment().equals(enrollment)) {
                                        isNotAvailable = false;
                                        Calendar cal = Calendar.getInstance();
                                        if (student.getYear() < cal.get(Calendar.YEAR)) {
                                            cardView.setVisibility(View.VISIBLE);
                                            showData(student.getName(), student.getEnrollment(), student.getRoll(), student.getSeatno(), student.getPhone(), student.getEmail(), student.getClassno(), student.getBenchno(), student.getDate() + "-" + student.getMonth() + "-" + student.getYear());
                                        } else if (student.getYear() == cal.get(Calendar.YEAR)) {
                                            if (student.getMonth() < (cal.get(Calendar.MONTH) + 1)) {
                                                cardView.setVisibility(View.VISIBLE);
                                                showData(student.getName(), student.getEnrollment(), student.getRoll(), student.getSeatno(), student.getPhone(), student.getEmail(), student.getClassno(), student.getBenchno(), student.getDate() + "-" + student.getMonth() + "-" + student.getYear());
                                            } else if (student.getMonth() == (cal.get(Calendar.MONTH) + 1)) {
                                                if (student.getDate() < cal.get(Calendar.DAY_OF_MONTH)) {
                                                    cardView.setVisibility(View.VISIBLE);
                                                    showData(student.getName(), student.getEnrollment(), student.getRoll(), student.getSeatno(), student.getPhone(), student.getEmail(), student.getClassno(), student.getBenchno(), student.getDate() + "-" + student.getMonth() + "-" + student.getYear());
                                                } else if (student.getDate() == cal.get(Calendar.DAY_OF_MONTH)) {
                                                    if (cal.get(Calendar.HOUR_OF_DAY) >= 8) {
                                                        cardView.setVisibility(View.VISIBLE);
                                                        showData(student.getName(), student.getEnrollment(), student.getRoll(), student.getSeatno(), student.getPhone(), student.getEmail(), student.getClassno(), student.getBenchno(), student.getDate() + "-" + student.getMonth() + "-" + student.getYear());
                                                    } else {
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                                                        alert.setTitle("Alert");
                                                        alert.setMessage("Data Not Available!!!\nConatct Admin.");
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
                                                    }
                                                } else {
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                                                    alert.setTitle("Alert");
                                                    alert.setMessage("Data Not Available!!!\nConatct Admin.");
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
                                                }
                                            } else {
                                                AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                                                alert.setTitle("Alert");
                                                alert.setMessage("Data Not Available!!!\nConatct Admin.");
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
                                            }
                                        } else {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                                            alert.setTitle("Alert");
                                            alert.setMessage("Data Not Available!!!\nConatct Admin.");
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
                                        }
                                        break;
                                    }
                                }
                            }

                            if (isNotAvailable) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(StudentLoginActivity.this);
                                alert.setTitle("Alert");
                                alert.setMessage("Enrollment No. Is Not Registered.\nContact Admin.");
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
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void showData(String name, String enrollmentno, String rollno, String seatno, String phone, String email, String classno, String benchno, String date) {
        this.name.setText("Name: " + name);
        this.enrollmentno.setText("Enrollment No.: " + enrollmentno);
        this.rollno.setText("Roll No.:" + rollno);
        this.seatno.setText("Seat No.:" + seatno);
        this.phone.setText("Phone No.: " + phone);
        this.email.setText("Email: " + email);
        this.classno.setText("Class No.: " + classno);
        this.benchno.setText("Bench No.: " + benchno);
        this.date.setText("Date: " + date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
