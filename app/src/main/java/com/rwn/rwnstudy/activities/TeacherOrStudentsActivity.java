package com.rwn.rwnstudy.activities;

import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.R;

import java.util.Objects;

public class TeacherOrStudentsActivity extends AppCompatActivity {

    Button buttonTeacher,buttonStudent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_or_students);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.welcom_rwn);
        }

        viewIntilization();
        buttonClickAction();

    }







    private void buttonClickAction() {


        buttonTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(TeacherOrStudentsActivity.this,LoginActivity.class);
                intent.putExtra(ConstantForApp.USER,ConstantForApp.TEACHER);
                startActivity(intent);
                finish();
            }
        });

        buttonStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TeacherOrStudentsActivity.this,LoginActivity.class);
                intent.putExtra(ConstantForApp.USER,ConstantForApp.STUDENT);
                startActivity(intent);
                finish();
            }
        });

    }

    private void viewIntilization() {
        buttonStudent = findViewById(R.id.button_student);
        buttonTeacher = findViewById(R.id.button_teacher);

    }
}
