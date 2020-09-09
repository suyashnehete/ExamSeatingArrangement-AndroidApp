package com.examseatingarrangement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.examseatingarrangement.Model.Department;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<String> classes;
    String college_name, department_name;
    GridView grid;
    View view;
    AppCompatAutoCompleteTextView edittext;
    Menu menu = null;
    Toolbar toolbar;
    static int position = 0;
    Boolean isDark = false;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        grid = findViewById(R.id.grid);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(HomeActivity.this, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(HomeActivity.this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) HomeActivity.this, PERMISSIONS, 122);
            }
        }

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_white_dot_24dp, null));
        view = View.inflate(HomeActivity.this, R.layout.layout_edittext, null);
        edittext = view.findViewById(R.id.edittext);

        setSupportActionBar(toolbar);


        SharedPreferences preferences = getSharedPreferences("ESA", MODE_PRIVATE);
        college_name = preferences.getString("collegeName", "asdf");
        department_name = preferences.getString("departmentName", "sdf");
        getSupportActionBar().setTitle(department_name);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                alert.setTitle("Add Class");
                alert.setMessage("Enter Class No.");
                alert.setView(view);
                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (edittext.getText().toString().trim().isEmpty()) {
                            Toast.makeText(HomeActivity.this, "Class No. Cannot Be Empty.", Toast.LENGTH_SHORT).show();
                        } else {
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Departments").child(college_name).child(department_name);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Department department = dataSnapshot.getValue(Department.class);
                                    ArrayList<String> array = department.getClasses();
                                    Boolean isNotAvailable = true;
                                    for (String i : array) {
                                        if (edittext.getText().toString().trim().equals(i)) {
                                            isNotAvailable = false;
                                            edittext.setText("");
                                        }
                                    }
                                    if (isNotAvailable) {
                                        array.add(edittext.getText().toString().trim());
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("classes", array);
                                        ref.updateChildren(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        edittext.setText("");
                                                        Toast.makeText(HomeActivity.this, "Class Added Successfully.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                edittext.setText("");
                                                Toast.makeText(HomeActivity.this, "Try Again After Some Time.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(HomeActivity.this, "Class Exists", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
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
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseDatabase.getInstance().getReference("Departments").child(college_name).child(department_name)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() != 0) {
                            Department department = dataSnapshot.getValue(Department.class);
                            grid.setAdapter(new GridAdapter(HomeActivity.this, department.getClasses(), college_name, department_name));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
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
                    new Intent(HomeActivity.this, SettingActivity.class)
            );
            return true;
        } else if (id == R.id.update_password) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle("Update Password");
            alert.setMessage("Enter New Password.");
            alert.setView(view);
            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (edittext.getText().toString().trim().isEmpty()) {
                        Toast.makeText(HomeActivity.this, "Password Cannot Be Empty.", Toast.LENGTH_SHORT).show();
                    } else {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("login_password", edittext.getText().toString().trim());
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Departments").child(college_name).child(department_name);
                        ref.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(HomeActivity.this, "Password Updated.", Toast.LENGTH_SHORT).show();
                                edittext.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, "Password Not Updated.", Toast.LENGTH_SHORT).show();
                                edittext.setText("");
                            }
                        });

                    }
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
            return true;
        } else if (id == R.id.update_id) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle("Update Login ID");
            alert.setMessage("Enter New Login ID.");
            alert.setView(view);
            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (edittext.getText().toString().trim().isEmpty()) {
                        Toast.makeText(HomeActivity.this, "Password Cannot Be Empty.", Toast.LENGTH_SHORT).show();
                    } else {
                        final ProgressDialog dialog1 = new ProgressDialog(HomeActivity.this);
                        dialog1.setMessage("Updating...");
                        dialog1.setCancelable(false);
                        dialog1.show();
                        final HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("login_id", edittext.getText().toString().trim());
                        FirebaseDatabase.getInstance().getReference("Departments").child(college_name)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Boolean isLoginAvailable = true;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Department department1 = snapshot.getValue(Department.class);
                                            if (department1.getLogin_id().equals(edittext.getText().toString().trim()) && !department_name.equals(department1.getName())) {
                                                isLoginAvailable = false;
                                                Toast.makeText(HomeActivity.this, "Login ID Already Exists. Try another Login ID", Toast.LENGTH_SHORT).show();
                                                dialog1.dismiss();
                                            }
                                        }
                                        if (isLoginAvailable) {
                                            FirebaseDatabase.getInstance().getReference("Departments").child(college_name).child(department_name)
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(HomeActivity.this, "Login ID Updated", Toast.LENGTH_SHORT).show();
                                                            dialog1.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(HomeActivity.this, "Login ID Not Updated, Try After Some Time.", Toast.LENGTH_SHORT).show();
                                                    dialog1.dismiss();
                                                }
                                            });
                                        }
                                        edittext.setText("");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        dialog1.dismiss();
                                        edittext.setText("");
                                    }
                                });

                    }
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
            return true;
        } else if (id == R.id.delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle("Delete Account");
            alert.setMessage("Do You Really Want to Delete " + department_name + ".");
            alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference("Departments").child(college_name).child(department_name)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDatabase.getInstance().getReference("Colleges").child(college_name).child(department_name)
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    getSharedPreferences("ESA", MODE_PRIVATE).edit().clear().commit();
                                    Toast.makeText(HomeActivity.this, "Department Deleted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.generate_student) {
            generateEmptyStudentExcelFile();
        } else if (id == R.id.register_students) {
            startActivity(
                    new Intent(HomeActivity.this, RegisterStudentActivity.class)
            );
        } else if (id == R.id.view_student) {
            startActivity(
                    new Intent(HomeActivity.this, ViewStudentsActivity.class)
            );
        } else if (id == R.id.logout) {
            getSharedPreferences("ESA", MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("ESA", MODE_PRIVATE).edit().putString("collegeName", college_name).commit();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download GPThane App");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Download app form https://gpthnae.org.in");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.feedback) {
            startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO)
                    .setData(Uri.parse("mailto:gptexamseatingarrangement@gmail.com?subject=Exam Seating Arrangement")), "Send Feedback"));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void generateEmptyStudentExcelFile() {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();


        HSSFRow row1 = sheet.createRow(0);
        HSSFCell c;

        c = row1.createCell(0);
        c = row1.createCell(1);
        c = row1.createCell(2);
        c = row1.createCell(3);
        c = row1.createCell(4);
        c = row1.createCell(5);

        sheet.setColumnWidth(0, (11 * 500));
        sheet.setColumnWidth(1, (7 * 500));
        sheet.setColumnWidth(2, (5 * 500));
        sheet.setColumnWidth(3, (11 * 500));
        sheet.setColumnWidth(4, (7 * 500));
        sheet.setColumnWidth(5, (5 * 500));

        row1.getCell(0).setCellValue("Name");
        row1.getCell(1).setCellValue("Enrollment");
        row1.getCell(2).setCellValue("Roll No.");
        row1.getCell(3).setCellValue("Email");
        row1.getCell(4).setCellValue("Phone No.");
        row1.getCell(5).setCellValue("Seat No.");
        try {
            String fullPath = Environment.getExternalStorageDirectory().getPath() + "/ESA";
            File dirl = new File(fullPath);
            if (!dirl.exists()) {
                dirl.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, "Students List.xls");
            file.createNewFile();
            fOut = new FileOutputStream(file);
            wb.write(fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(HomeActivity.this, "File Generated to ESA Folder", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class GridAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> classes;
    String college, department;


    public GridAdapter(Context context, ArrayList<String> classes, String college, String department) {
        this.context = context;
        this.classes = classes;
        this.college = college;
        this.department = department;
    }

    @Override
    public int getCount() {
        return classes.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AppCompatButton btn = new AppCompatButton(context);
        btn.setText(classes.get(position));
        btn.setLayoutParams(new GridView.LayoutParams(250, 250));
        btn.setBackgroundResource(R.drawable.classes);
        btn.setTextColor(Color.BLACK);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(
                        new Intent(context, SeatAllocationActivity.class)
                                .putExtra("classno", classes.get(position))
                );
            }
        });

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Do You Want to delete " + btn.getText());
                alert.setTitle("Delete Class");
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("Departments").child(college).child(department)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Department department1 = dataSnapshot.getValue(Department.class);
                                        ArrayList<String> arr = department1.getClasses();
                                        arr.remove(btn.getText().toString());
                                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                        hashMap.put("classes", arr);
                                        FirebaseDatabase.getInstance().getReference("Departments").child(college).child(department)
                                                .updateChildren(hashMap);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });
                alert.setNegativeButton("Cancel", null);
                alert.create();
                if (context != null) {
                    alert.show();
                }
                return true;
            }
        });

        return btn;
    }
}
