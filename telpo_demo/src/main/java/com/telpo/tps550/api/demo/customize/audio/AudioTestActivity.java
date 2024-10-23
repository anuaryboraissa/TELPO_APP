package com.telpo.tps550.api.demo.customize.audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.customize.Adapters.RecordingAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioTestActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "AudioRecorder";

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String audioFilePath;
    private Button btnRecord;
    private RecyclerView recyclerView;
    private List<File> recordings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);

        btnRecord = findViewById(R.id.btn_record);
        recyclerView = findViewById(R.id.recyclerView);
        recordings = new ArrayList<>();

        if (checkPermissions()) {
            setupRecyclerView();
        } else {
            requestPermissions();
        }

        btnRecord.setOnClickListener(view -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

    }

    private void startRecording() {
        File audioDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Recordings");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }

        audioFilePath = audioDir.getAbsolutePath() + "/recording_" + System.currentTimeMillis() + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            btnRecord.setText("Stop Recording");
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Recording failed: ", e);
        }
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        isRecording = false;
        btnRecord.setText("Start Recording");
        Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show();

        loadRecordings();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadRecordings();
    }

    private void loadRecordings() {
        File audioDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Recordings");
        File[] files = audioDir.listFiles();
        if (files != null) {
            recordings.clear();
            for (File file : files) {
                recordings.add(file);
            }
            recyclerView.setAdapter(new RecordingAdapter(recordings, this));
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkPermissions()) {
                setupRecyclerView();
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}