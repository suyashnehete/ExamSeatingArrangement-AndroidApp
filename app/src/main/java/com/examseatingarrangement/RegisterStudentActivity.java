package com.examseatingarrangement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.Lifecycle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.examseatingarrangement.Model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class RegisterStudentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;
    AppCompatSpinner select_semester;
    AppCompatButton upload_document;
    String department,semester,college;
    String[] semArray;
    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        select_semester=findViewById(R.id.select_semester);
        upload_document=findViewById(R.id.upload_document);

        getSupportActionBar().setTitle("Register Students");

        SharedPreferences preferences=getSharedPreferences("ESA",MODE_PRIVATE);
        college=preferences.getString("collegeName",null);
        department=preferences.getString("departmentName",null);

        Calendar cal=Calendar.getInstance();
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
        } else if (department.toLowerCase().contains("tr")||department.toLowerCase().contains("travel")) {
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

        select_semester.setAdapter(new ArrayAdapter<String>(this,R.layout.layout_spinner,semArray));

        select_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Select Semester")){
                    semester=null;
                }else{
                    semester=parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        upload_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(semester==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterStudentActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Select Semester");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            select_semester.requestFocus();
                        }
                    });
                    builder.create();
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        builder.show();
                    }
                }else{
                    UploadFile();

                }
            }
        });

    }

    private void UploadFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        startActivityForResult(Intent.createChooser(intent,"Select Document"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            if (filePath!=null){
                uploadFile(filePath);
            }
        }
    }
    private void uploadFile(final Uri filePath) {
        if (filePath != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Students Data...");
            dialog.show();

            final DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference("Colleges/"+college+"/"+department+"/Students/"+semester);

            final StorageReference storageReference= FirebaseStorage.getInstance().getReference(department+"/Students List/"+semester);

            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    try {
                                        String fullPath = Environment.getExternalStorageDirectory().getPath() + "/ESA";
                                        File dirl = new File(fullPath);
                                        if (!dirl.exists()) {
                                            dirl.mkdirs();
                                        }
                                        final File file = new File(fullPath, "Students List.xls");
                                        file.createNewFile();

                                        FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(downloadUrl))
                                                .getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                try {
                                                    HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
                                                    HSSFSheet sheet = workbook.getSheetAt(0);
                                                    for (int i=1;i<=sheet.getLastRowNum();i++){
                                                        HSSFRow row=sheet.getRow(i);

                                                        if (row!=null) {
                                                            final HSSFCell name = row.getCell(0);
                                                            final HSSFCell enroll = row.getCell(1);
                                                            final HSSFCell roll = row.getCell(2);
                                                            final HSSFCell email = row.getCell(3);
                                                            final HSSFCell phone = row.getCell(4);
                                                            final HSSFCell seat = row.getCell(5);
                                                            if (name!=null&&enroll!=null&&roll!=null&&email!=null&&phone!=null&&seat!=null){
                                                                mDatabase.child(String.format("%.0f",enroll.getNumericCellValue())).setValue(new Student(
                                                                        name.getStringCellValue(),
                                                                        String.format("%.0f",enroll.getNumericCellValue()),
                                                                        String.format("%.0f",roll.getNumericCellValue()),
                                                                        String.format("%.0f",seat.getNumericCellValue()),
                                                                        String.format("%.0f",phone.getNumericCellValue()),
                                                                        email.getStringCellValue(),
                                                                        "Not Assigned",
                                                                        "Not Assigned",
                                                                        0,
                                                                        0,
                                                                        0
                                                                        ));
                                                            }
                                                        }
                                                    }
                                                } catch (FileNotFoundException e) {
                                                    Log.d("Suyash",filePath.toString());
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                storageReference.delete();
                                                Toast.makeText(RegisterStudentActivity.this, "Students Registered Successfully.", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                            }
                                        });

                                    } catch (IOException e) {
                                        dialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception exception) {
                            dialog.dismiss();
                            Toast.makeText(RegisterStudentActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No File Selected.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
