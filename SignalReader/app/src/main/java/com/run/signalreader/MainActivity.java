package com.run.signalreader;

import android.os.Environment;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button startRecord;
    private Button stopRecord;
    private TextView recordView;

    private boolean isRunning;
    FileOutputStream fos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRecord = (Button) findViewById(R.id.startRecord);
        stopRecord = (Button) findViewById(R.id.stopRecord);
        recordView = (TextView) findViewById(R.id.recordView);

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = true;

                try {
                    writeFileToSDCard();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;

            }
        });



    }

    private void writeFileToSDCard() throws IOException {
        File parent_path = Environment.getExternalStorageDirectory();

        // 可以建立一个子目录专门存放自己专属文件
        File dir = new File(parent_path.getAbsoluteFile(), "RunNishino");
        dir.mkdir();

        //TODO:将时间写进文件名里
        File file = new File(dir.getAbsoluteFile(), "myfile8.txt");

        Log.d("文件路径", file.getAbsolutePath());

        // 创建这个文件，如果不存在
        file.createNewFile();

        fos = new FileOutputStream(file);

        RecordSignal();
    }

    public void RecordSignal()  {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                //TODO:将信号读取出来，并且在前面加上时间
                String data = "hello, world! RunNishino \n";
                byte[] buffer = data.getBytes();

                try {

                    if (isRunning) {
                        fos.write(buffer, 0, buffer.length);
                        fos.flush();
                    }else{
                        fos.close();
                    }

                } catch (IOException e){
                    e.printStackTrace();
                }
                handler.postDelayed(this,2000);
            }
        });
    }
}
