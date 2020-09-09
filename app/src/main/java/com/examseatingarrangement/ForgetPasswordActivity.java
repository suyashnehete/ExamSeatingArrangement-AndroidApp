package com.examseatingarrangement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Lifecycle;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.examseatingarrangement.Model.Department;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ForgetPasswordActivity extends AppCompatActivity {

    AppCompatEditText loginid, password, answer;
    AppCompatTextView question;
    AppCompatButton change;
    static String college, strquestion, stranswer, strdepartment;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().setTitle("Forget Password");
        SharedPreferences preferences = getSharedPreferences("ESA", MODE_PRIVATE);
        college = preferences.getString("collegeName", null);

        loginid = findViewById(R.id.loginid);
        password = findViewById(R.id.password);
        answer = findViewById(R.id.answer);
        question = findViewById(R.id.question);
        change = findViewById(R.id.change);
        ref = FirebaseDatabase.getInstance().getReference("Departments").child(college);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getVisibility() == View.GONE) {
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Boolean isNotFound = true;
                            if (dataSnapshot.getChildrenCount() == 0) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(ForgetPasswordActivity.this);
                                alert.setTitle("Alert");
                                alert.setMessage("No Department Found!!!");
                                alert.setPositiveButton("Register Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(ForgetPasswordActivity.this, RegisterDepartmentActivity.class)
                                                .putExtra("collegeName", college));
                                    }
                                });
                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                alert.create();
                                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                                    alert.show();
                                }
                            } else {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Department department = snapshot.getValue(Department.class);
                                    if (department.getLogin_id().equals(loginid.getText().toString().trim())) {
                                        isNotFound = false;
                                        strdepartment = department.getName();
                                        strquestion = department.getQuestion();
                                        stranswer = department.getAnswer();
                                        question.setVisibility(View.VISIBLE);
                                        question.setText("Question: " + strquestion);
                                        answer.setVisibility(View.VISIBLE);
                                        change.setText("Submit");
                                    }
                                }
                                if (isNotFound) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(ForgetPasswordActivity.this);
                                    alert.setTitle("Alert");
                                    alert.setMessage("Enter valid Login ID");
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

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else if (password.getVisibility() == View.GONE) {
                    if (answer.getText().toString().trim().equals(stranswer)) {
                        password.setVisibility(View.VISIBLE);
                        change.setText("Change Password");
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ForgetPasswordActivity.this);
                        alert.setTitle("Alert");
                        alert.setMessage("Enter Valid Answer");
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
                } else if (!password.getText().toString().isEmpty()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("login_password", password.getText().toString().trim());
                    ref.child(strdepartment).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ForgetPasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                    startActivity(
                                            new Intent(ForgetPasswordActivity.this, AdminLoginActivity.class)
                                                    .putExtra("collegeName", college)
                                    );
                                }
                            });
                } else if (password.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ForgetPasswordActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Password Cannot Be Empty");
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
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
