package com.examseatingarrangement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.CompoundButton;

import com.examseatingarrangement.Model.Department;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    AppCompatEditText login_id, password;
    AppCompatTextView register, forget;
    AppCompatButton login;
    AppCompatCheckBox show_password;
    String college_name, department_name;
    Boolean isNotAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        getSupportActionBar().setTitle("Admin Login");



        college_name = getSharedPreferences("ESA", MODE_PRIVATE).getString("collegeName", null);

        login_id = findViewById(R.id.login_id);
        password = findViewById(R.id.password);
        register = findViewById(R.id.not_registered);
        forget = findViewById(R.id.forget_password);
        show_password = findViewById(R.id.show_password);
        login = findViewById(R.id.login);

        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, RegisterDepartmentActivity.class));
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, ForgetPasswordActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_id.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AdminLoginActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Login ID.");
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(AdminLoginActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Enter Password.");
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
                    onLogin(login_id.getText().toString(), password.getText().toString(), college_name);
                }
            }
        });
    }

    private void onLogin(final String id, final String password, final String college_name) {
        final ProgressDialog dialog = new ProgressDialog(AdminLoginActivity.this);
        dialog.setMessage("Authenticating...");
        dialog.setCancelable(false);
        dialog.show();
        FirebaseDatabase.getInstance().getReference("Departments").child(college_name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            dialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(AdminLoginActivity.this);
                            alert.setTitle("Alert");
                            alert.setMessage("Department Not Registered.");
                            alert.setPositiveButton("Register Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(AdminLoginActivity.this, RegisterDepartmentActivity.class)
                                            .putExtra("collegeName", college_name));
                                }
                            });
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                                Department department = snapshot.getValue(Department.class);
                                if (department.getLogin_id().equals(id)) {
                                    isNotAvailable = false;
                                    if (department.getLogin_password().equals(password)) {
                                        department_name = department.getName();
                                        getSharedPreferences("ESA", MODE_PRIVATE).edit().putString("departmentName", department_name)
                                                .commit();
                                        dialog.dismiss();
                                        startActivity(
                                                new Intent(AdminLoginActivity.this, HomeActivity.class)
                                        );
                                    } else {
                                        dialog.dismiss();
                                        AlertDialog.Builder alert = new AlertDialog.Builder(AdminLoginActivity.this);
                                        alert.setTitle("Alert");
                                        alert.setMessage("Enter Valid Password.");
                                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        alert.create();
                                        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                                            alert.show();
                                        }
                                    }
                                }
                            }
                            if (isNotAvailable) {
                                dialog.dismiss();
                                AlertDialog.Builder alert = new AlertDialog.Builder(AdminLoginActivity.this);
                                alert.setTitle("Alert");
                                alert.setMessage("Department Not Registered.");
                                alert.setPositiveButton("Register Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(AdminLoginActivity.this, RegisterDepartmentActivity.class)
                                                .putExtra("collegeName", college_name));
                                    }
                                });
                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
