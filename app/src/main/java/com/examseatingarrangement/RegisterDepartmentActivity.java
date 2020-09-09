package com.examseatingarrangement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.Lifecycle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.examseatingarrangement.Model.Department;
import com.examseatingarrangement.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterDepartmentActivity extends AppCompatActivity {

    String college_name;
    AppCompatEditText department, classes, loginid, password, question, answer;
    AppCompatButton register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Boolean isDark = getSharedPreferences("ESADARK", MODE_PRIVATE).getBoolean("darkMode", false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_department);

        getSupportActionBar().setTitle("Register Department");
        department = findViewById(R.id.department);
        classes = findViewById(R.id.classes);
        password = findViewById(R.id.password);
        loginid = findViewById(R.id.loginid);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (department.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RegisterDepartmentActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Department");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        alert.show();
                    }
                } else if (loginid.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RegisterDepartmentActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Login ID");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        alert.show();
                    }
                } else if (password.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RegisterDepartmentActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Password");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        alert.show();
                    }
                } else if (classes.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RegisterDepartmentActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Classes");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        alert.show();
                    }
                } else if (question.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RegisterDepartmentActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Question");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        alert.show();
                    }
                } else if (answer.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RegisterDepartmentActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Questions Answer");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        alert.show();
                    }
                } else {
                    onRegister(
                            department.getText().toString().trim(),
                            loginid.getText().toString().trim(),
                            password.getText().toString().trim(),
                            question.getText().toString().trim(),
                            answer.getText().toString().trim(),
                            classes.getText().toString().trim()
                    );
                }
            }
        });
    }

    private void onRegister(final String department, final String loginid, final String password, final String question, final String answer, String classes) {
        final ProgressDialog dialog = new ProgressDialog(RegisterDepartmentActivity.this);
        dialog.setMessage("Registering...");
        dialog.setCancelable(false);
        dialog.show();
        final SharedPreferences preferences = getSharedPreferences("ESA", MODE_PRIVATE);
        college_name = preferences.getString("collegeName", null);
        String[] str = classes.split(",");
        final ArrayList<String> classDivision = new ArrayList<>();
        for (String i : str) {
            classDivision.add(i.trim());
        }
        FirebaseDatabase.getInstance().getReference("Departments").child(college_name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean isLoginAvailable = true;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Department department1 = snapshot.getValue(Department.class);
                            if (department1.getLogin_id().equals(loginid)) {
                                isLoginAvailable = false;
                                Toast.makeText(RegisterDepartmentActivity.this, "Login ID Already Exists. Try another Login ID", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                        if (isLoginAvailable) {
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Departments").child(college_name).child(department);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() == 0) {
                                        reference.setValue(new Department(department, loginid, password, question, answer, classDivision))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        preferences.edit().putString("departmentName", department).commit();
                                                        dialog.dismiss();
                                                        startActivity(
                                                                new Intent(RegisterDepartmentActivity.this, HomeActivity.class)
                                                        );
                                                        Toast.makeText(RegisterDepartmentActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterDepartmentActivity.this);
                                                alert.setTitle("Registration Error");
                                                alert.setMessage("Try After Some Time.");
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
                                        });
                                    } else {
                                        dialog.dismiss();
                                        AlertDialog.Builder alert = new AlertDialog.Builder(RegisterDepartmentActivity.this);
                                        alert.setTitle("Alert");
                                        alert.setMessage("Department Already Registered.");
                                        alert.setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(
                                                        new Intent(RegisterDepartmentActivity.this, AdminLoginActivity.class)
                                                                .putExtra("collegeName", college_name)
                                                );
                                            }
                                        });
                                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(
                                                        new Intent(RegisterDepartmentActivity.this, MainActivity.class)
                                                );
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
                                    dialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                    }
                });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
