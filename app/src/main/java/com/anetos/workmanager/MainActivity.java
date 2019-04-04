package com.anetos.workmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating constraints
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false) // you can add as many constraints as you want
                .build();

        //creating a data object
        //to pass the data with workRequest
        //we can put as many variables needed
        Data data = new Data.Builder()
                .putString(WorkerClass.TASK_DESC, "The task data passed from MainActivity")
                .build();

        //This is the subclass of our WorkRequest
        final OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(WorkerClass.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        final PeriodicWorkRequest periodicWorkRequest
                = new PeriodicWorkRequest.Builder(WorkerClass.class, 15, TimeUnit.MINUTES)
                .build();
        //A click listener for the button
        //inside the onClick method we will perform the work
        findViewById(R.id.buttonEnqueue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Enqueuing the work request
                WorkManager.getInstance().enqueue(periodicWorkRequest);
            }
        });

        //Getting the TextView
        final TextView textView = findViewById(R.id.textViewStatus);

        //Listening to the work status
        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        //receiving back the data
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            textView.append(workInfo.getOutputData().getString(WorkerClass.TASK_DESC) + "\n");
                            //WorkManager.getInstance().cancelWorkById(workRequest.getId());

                        }
                        //Displaying the status into TextView
                        textView.append(workInfo.getState().name() + "\n");

                    }
                });


        //WorkManager.getInstance().cancelWorkById(workRequest.getId());
    }


}
