package com.examseatingarrangement.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.examseatingarrangement.Model.Student;
import com.examseatingarrangement.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private Context context;
    private List<Student> uploads;
    private String department, semester, college;

    public StudentAdapter(Context context, List<Student> uploads) {
        this.uploads = uploads;
        this.context = context;
    }

    public StudentAdapter(Context context, List<Student> uploads, String department, String semester, String college) {
        this.context = context;
        this.uploads = uploads;
        this.department = department;
        this.semester = semester;
        this.college = college;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_student, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Student upload = uploads.get(position);

        holder.data.setText(
                "Name: " + upload.getName() +
                        "\nEnrollment No: " + upload.getEnrollment() +
                        "\nRoll No.: " + upload.getRoll() +
                        "\nSeat No.: " + upload.getSeatno() +
                        "\nPhone No.: " + upload.getPhone() +
                        "\nEmail: " + upload.getEmail() +
                        "\nDate: " + upload.getDate() + "-" + upload.getMonth() + "-" + upload.getYear() +
                        "\nClass No.: " + upload.getClassno() +
                        "\nBench No.: " + upload.getBenchno()
        );

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Colleges").child(college).child(department)
                        .child("Students").child(semester).child(upload.getEnrollment()).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Student Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView data;
        public AppCompatButton delete;

        public ViewHolder(View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.data);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
