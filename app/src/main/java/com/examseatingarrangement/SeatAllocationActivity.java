package com.examseatingarrangement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.examseatingarrangement.Model.Department;
import com.examseatingarrangement.Model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class SeatAllocationActivity extends AppCompatActivity {

    String college_name, department_name, semester_name, classno, strmonth, strday, stryear;
    String[] semArray;
    AppCompatSpinner select_semester;
    GridView firstRow, secondRow, thirdRow, forthRow, fifthRow;
    AppCompatTextView select_date;
    View v;
    AppCompatAutoCompleteTextView edittext;
    Boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDark = getSharedPreferences("ESADARK", MODE_PRIVATE).getBoolean("darkMode", true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_allocation);

        getSupportActionBar().setTitle("Bench Allocation");

        classno = getIntent().getStringExtra("classno");
        SharedPreferences preferences = getSharedPreferences("ESA", MODE_PRIVATE);
        college_name = preferences.getString("collegeName", null);
        department_name = preferences.getString("departmentName", null);

        v = View.inflate(SeatAllocationActivity.this, R.layout.layout_edittext, null);
        edittext = v.findViewById(R.id.edittext);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);

        select_semester = findViewById(R.id.select_semester);
        firstRow = findViewById(R.id.firstRow);
        firstRow.setVerticalScrollBarEnabled(true);
        secondRow = findViewById(R.id.secondRow);
        thirdRow = findViewById(R.id.thirdRow);
        forthRow = findViewById(R.id.forthRow);
        fifthRow = findViewById(R.id.fifthRow);
        select_date = findViewById(R.id.select_date);


        Calendar cal = Calendar.getInstance();
        if (department_name.toLowerCase().contains("computer")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "CO2I", "CO4I", "CO6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "CO1I", "CO3I", "CO5I"};
            }
        } else if (department_name.toLowerCase().contains("it") || department_name.toLowerCase().contains("information")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "IF2I", "IF4I", "IF6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "IF1I", "IF3I", "IF5I"};
            }
        } else if (department_name.toLowerCase().contains("civil")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "CE2I", "CE4I", "CE6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "CE1I", "CE3I", "CE5I"};
            }
        } else if (department_name.toLowerCase().contains("electrical")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "EE2I", "EE4I", "EE6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "EE1I", "EE3I", "EE5I"};
            }
        } else if (department_name.toLowerCase().contains("mechanical")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "ME2I", "ME4I", "ME6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "ME1I", "ME3I", "ME5I"};
            }
        } else if (department_name.toLowerCase().contains("electronics")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "EC2I", "EC4I", "EC6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "EC1I", "EC3I", "EC5I"};
            }
        } else if (department_name.toLowerCase().contains("chemical")) {
            if (cal.get(Calendar.MONTH) < 5 || cal.get(Calendar.MONTH) == 11) {
                this.semArray = new String[]{"Select Semester", "CH2I", "CH4I", "CH6I"};
            } else {
                this.semArray = new String[]{"Select Semester", "CH1I", "CH3I", "CH5I"};
            }
        } else if (department_name.toLowerCase().contains("tr") || department_name.toLowerCase().contains("travel")) {
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

        select_semester.setAdapter(new ArrayAdapter<String>(SeatAllocationActivity.this, R.layout.layout_spinner, semArray));

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                DatePickerDialog dialog = new DatePickerDialog(SeatAllocationActivity.this);
                dialog.show();
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        select_date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        if (select_semester != null) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(SeatAllocationActivity.this);
                            alert.setTitle("Add Class");
                            alert.setMessage("Enter Total Bench Count.");
                            if (v.getParent() != null) {
                                ((ViewGroup) v.getParent()).removeView(v);
                            }

                            alert.setView(v);
                            alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (edittext.getText().toString().trim().isEmpty()) {
                                        Toast.makeText(SeatAllocationActivity.this, "Bench Count Cannot Be Empty.", Toast.LENGTH_SHORT).show();
                                    } else if (select_date.getText().toString().equals("Select Exam Date")) {
                                        Toast.makeText(SeatAllocationActivity.this, "Select Exam Date", Toast.LENGTH_SHORT).show();
                                    } else if (Integer.parseInt(edittext.getText().toString().trim()) > 50) {
                                        Toast.makeText(SeatAllocationActivity.this, "Class can contain maximum of 50 Benches Only.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        strmonth = select_date.getText().toString().split("-")[1];
                                        strday = select_date.getText().toString().split("-")[0];
                                        stryear = select_date.getText().toString().split("-")[2];
                                        showBenches(classno, college_name, department_name, semester_name, Integer.parseInt(edittext.getText().toString().trim()));
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
                            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                                alert.show();
                            }
                        }
                    }
                });

            }
        });
        select_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Select Semester")) {
                    semester_name = null;
                    firstRow.setAdapter(null);
                    secondRow.setAdapter(null);
                    thirdRow.setAdapter(null);
                    forthRow.setAdapter(null);
                    fifthRow.setAdapter(null);
                } else {
                    semester_name = parent.getItemAtPosition(position).toString();
                    if (!select_date.getText().toString().equals("Select Exam Date")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(SeatAllocationActivity.this);
                        alert.setTitle("Add Class");
                        alert.setMessage("Enter Total Bench Count.");
                        if (v.getParent() != null) {
                            ((ViewGroup) v.getParent()).removeView(v);
                        }

                        alert.setView(v);
                        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (edittext.getText().toString().trim().isEmpty()) {
                                    Toast.makeText(SeatAllocationActivity.this, "Bench Count Cannot Be Empty.", Toast.LENGTH_SHORT).show();
                                } else if (select_date.getText().toString().equals("Select Exam Date")) {
                                    Toast.makeText(SeatAllocationActivity.this, "Select Exam Date", Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(edittext.getText().toString().trim()) > 50) {
                                    Toast.makeText(SeatAllocationActivity.this, "Class can contain maximum of 50 Benches Only.", Toast.LENGTH_SHORT).show();
                                } else {
                                    strmonth = select_date.getText().toString().split("-")[1];
                                    strday = select_date.getText().toString().split("-")[0];
                                    stryear = select_date.getText().toString().split("-")[2];
                                    showBenches(classno, college_name, department_name, semester_name, Integer.parseInt(edittext.getText().toString().trim()));
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
                        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                            alert.show();
                        }
                    }
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

    private void showBenches(String classno, String college, String department, String semester, float benchCount) {
        ArrayList<String> firstData, secondData, thirdData, forthData, fifthData;
        firstData = new ArrayList<>();
        secondData = new ArrayList<>();
        thirdData = new ArrayList<>();
        forthData = new ArrayList<>();
        fifthData = new ArrayList<>();

        int oneRowBenches = (int) Math.ceil(benchCount / 5);
        int benches = 1;
        int i = 0;
        while (i < oneRowBenches && benches <= benchCount) {
            firstData.add(Integer.toString(benches++));
            i++;
        }
        i = 0;
        while (i < oneRowBenches && benches <= benchCount) {
            secondData.add(Integer.toString(benches++));
            i++;
        }
        Collections.reverse(secondData);

        i = 0;
        while (i < oneRowBenches && benches <= benchCount) {
            thirdData.add(Integer.toString(benches++));
            i++;
        }

        i = 0;
        while (i < oneRowBenches && benches <= benchCount) {
            forthData.add(Integer.toString(benches++));
            i++;
        }
        Collections.reverse(forthData);

        i = 0;
        while (i < oneRowBenches && benches <= benchCount) {
            fifthData.add(Integer.toString(benches++));
            i++;
        }

        firstRow.setAdapter(new BenchAdapter(SeatAllocationActivity.this, college, department, semester, firstData, classno, strday, strmonth, stryear, isDark));
        secondRow.setAdapter(new BenchAdapter(SeatAllocationActivity.this, college, department, semester, secondData, classno, strday, strmonth, stryear, isDark));
        thirdRow.setAdapter(new BenchAdapter(SeatAllocationActivity.this, college, department, semester, thirdData, classno, strday, strmonth, stryear, isDark));
        forthRow.setAdapter(new BenchAdapter(SeatAllocationActivity.this, college, department, semester, forthData, classno, strday, strmonth, stryear, isDark));
        fifthRow.setAdapter(new BenchAdapter(SeatAllocationActivity.this, college, department, semester, fifthData, classno, strday, strmonth, stryear, isDark));
    }
}

class BenchAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> data;
    static String college, department, semester, roll, enrollment, classno, day, month, year;
    static ArrayList<String> students;
    Boolean isDark;

    public BenchAdapter(Context context, String college, String department, String semester, ArrayList<String> data, String classno, String day, String month, String year, Boolean isDark) {
        this.context = context;
        this.data = data;
        this.college = college;
        this.department = department;
        this.semester = semester;
        this.classno = classno;
        this.day = day;
        this.month = month;
        this.year = year;
        this.isDark = isDark;
    }

    @Override
    public int getCount() {
        return data.size();
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
        final String[] strname = new String[1];
        final String[] strroll = new String[1];
        final String[] strenroll = new String[1];
        final String[] strseat = new String[1];


        FirebaseDatabase.getInstance().getReference("Colleges").child(college).child(department).child("Students")
                .child(semester)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() != 0) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Student student = snapshot.getValue(Student.class);
                                if (student.getBenchno().equals(data.get(position)) && student.getClassno().equals(classno)) {
                                    btn.setText(student.getRoll());
                                    strenroll[0] = student.getEnrollment();
                                    strname[0] = student.getName();
                                    strroll[0] = student.getRoll();
                                    strseat[0] = student.getSeatno();
                                    break;
                                }
                            }
                        } else {
                            btn.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        btn.setTextColor(Color.WHITE);
        btn.setBackgroundResource(R.drawable.bench);
        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,80));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.getText().toString().isEmpty()) {
                    allotBenches(classno, data.get(position), btn);
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Student Details");
                    alert.setMessage("Name: " + strname[0] +
                            "\nEnrollment No.: " + strenroll[0] +
                            "\nRoll No.: " + strroll[0] +
                            "\nSeat No.: " + strseat[0]
                    );
                    alert.setPositiveButton("Ok", null);
                    alert.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("classno", "Not Assigned");
                            hashMap.put("benchno", "Not Assigned");
                            FirebaseDatabase.getInstance().getReference("Colleges").child(college).child(department).child("Students")
                                    .child(semester).child(strenroll[0]).updateChildren(hashMap);
                            btn.setText("");
                        }
                    });

                    alert.create();
                    if (context != null) {
                        alert.show();
                    }
                }
            }
        });


        return btn;
    }

    private void allotBenches(final String classno, final String benchno, final AppCompatButton btn) {
        students = new ArrayList<>();
        roll = enrollment = null;
        FirebaseDatabase.getInstance().getReference("Colleges").child(college).child(department).child("Students").child(semester)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            Toast.makeText(context, "Students Not Found!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Student student = snapshot.getValue(Student.class);
                                if (!student.getClassno().equals(classno)) {
                                    roll = student.getRoll();
                                    students.add(
                                            "Name: " + student.getName() +
                                                    "\nEnrollment No.: " + student.getEnrollment() +
                                                    "\nRoll No.: " + student.getRoll() +
                                                    "\nSeat No.: " + student.getSeatno()
                                    );
                                }
                            }
                            LinearLayout layout = new LinearLayout(context);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setGravity(Gravity.CENTER);
                            layout.setBackgroundColor(Color.BLACK);
                            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View customview = layoutInflater.inflate(R.layout.layout_list, null);
                            final ListView ls = customview.findViewById(R.id.list);
                            AppCompatButton cancel = customview.findViewById(R.id.cancel);
                            if (isDark) {
                                customview.findViewById(R.id.tl).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));
                            } else {
                                customview.findViewById(R.id.tl).setBackgroundColor(Color.WHITE);
                            }
                            final PopupWindow popupWindow = new PopupWindow(customview, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
                            ls.setAdapter(new ArrayAdapter<>(context, R.layout.layout_fourline_textview, R.id.text, students));
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                }
                            });

                            ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    btn.setText(roll);
                                    popupWindow.dismiss();
                                    String[] details = parent.getItemAtPosition(position).toString().split("\n");
                                    enrollment = details[1].split(":")[1].trim();
                                    roll = details[2].split(":")[1].trim();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("classno", classno);
                                    hashMap.put("benchno", benchno);
                                    hashMap.put("month", Integer.parseInt(month.trim()));
                                    hashMap.put("date", Integer.parseInt(day.trim()));
                                    hashMap.put("year", Integer.parseInt(year.trim()));

                                    FirebaseDatabase.getInstance().getReference("Colleges").child(college).child(department).child("Students").child(semester)
                                            .child(enrollment).updateChildren(hashMap);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
