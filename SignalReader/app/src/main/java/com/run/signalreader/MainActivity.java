package com.run.signalreader;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.support.v7.appcompat.R.id.info;

public class MainActivity extends AppCompatActivity {

    static final int CHOOSE_GSM = 1;
    static final int CHOOSE_CDMA = 2;
    static final int CHOOSE_EVDO = 3;
    static final int CHOOSE_LTE = 4;

    int choose_which;

    private Button startRecord;
    private Button stopRecord;
    private TextView recordView;

    private RadioGroup radioButtons;
    private RadioButton buttonGsm;
    private RadioButton buttonCdma;
    private RadioButton buttonEvdo;
    private RadioButton buttonLTE;

    private boolean isRunning;
    FileOutputStream fos;

    int CDMAdbm;
    int EVDOdbm;
    int GSMdbm;

//    int WCDMAdbm;
//    TODO WCDMA的信号暂时无法获取

    int LTEdbm;

    int signal;
    String signal_type;


    GetParams listener;
    TelephonyManager telephonyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRecord = (Button) findViewById(R.id.startRecord);
        stopRecord = (Button) findViewById(R.id.stopRecord);
        recordView = (TextView) findViewById(R.id.recordView);

        radioButtons = (RadioGroup) findViewById(R.id.radioBttons);
        buttonGsm = (RadioButton) findViewById(R.id.radioButton_GSM);
        buttonCdma = (RadioButton) findViewById(R.id.radioButton_CDMA);
        buttonEvdo = (RadioButton) findViewById(R.id.radioButton_EVDO);
        buttonLTE = (RadioButton) findViewById(R.id.radioButton_LTE);

        radioButtons.setOnCheckedChangeListener(new RadioGroupListener());

        listener = new GetParams();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isRunning = true;

                for (int i = 0; i < radioButtons.getChildCount(); i++) {
                    radioButtons.getChildAt(i).setEnabled(false);
                }

                recordView.setText("Recording Now");

                startRecord.setClickable(false);
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

                startRecord.setClickable(true);

                recordView.setText("Free Now");

                for (int i = 0; i < radioButtons.getChildCount(); i++) {
                    radioButtons.getChildAt(i).setEnabled(true);
                }
            }
        });
    }

    private class RadioGroupListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == buttonGsm.getId()){
                choose_which = CHOOSE_GSM;
            }else if (checkedId == buttonCdma.getId()){
                choose_which = CHOOSE_CDMA;
            }else if (checkedId == buttonEvdo.getId()){
                choose_which = CHOOSE_EVDO;
            }else if (checkedId == buttonLTE.getId()){
                choose_which = CHOOSE_LTE;
            }
        }
    }

    public class GetParams extends PhoneStateListener{

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength){
            super.onSignalStrengthsChanged(signalStrength);

            CDMAdbm = signalStrength.getCdmaDbm();
            EVDOdbm = signalStrength.getEvdoDbm();
            GSMdbm = signalStrength.getGsmSignalStrength();



//            try{
//                LTEdbm = (Integer) signalStrength.getClass().getMethod("getLteSignalStrength").invoke(signalStrength);
//            } catch (Exception e){
//                e.printStackTrace();
//            }


            //获取电信LTE的信号
            String[] signals = signalStrength.toString().split(" ");
            LTEdbm = Integer.parseInt(signals[11]);

            System.out.println(signalStrength.toString());

            /*
            *通过getCellInfo的方法获取信号，但是无法实例化，应该使用Creator来实例化CellINFO,暂时不知道怎么写
            **/

//            List<CellInfo> cellInfoList;
//            cellInfoList = telephonyManager.getAllCellInfo();
//            if (null != cellInfoList)
//            {
//                for (CellInfo cellInfo : cellInfoList) {
//                    if (cellInfo instanceof CellInfoGsm) {
//                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm)cellInfo).getCellSignalStrength();
//                        GSMdbm = cellSignalStrengthGsm.getDbm();
//                    } else if (cellInfo instanceof CellInfoCdma) {
//                        CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma)cellInfo).getCellSignalStrength();
//                        CDMAdbm = cellSignalStrengthCdma.getDbm();
//                    } else if (cellInfo instanceof CellInfoWcdma) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                            CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma)cellInfo).getCellSignalStrength();
//                            WCDMAdbm = cellSignalStrengthWcdma.getDbm();
//                        }
//                    } else if (cellInfo instanceof CellInfoLte) {
//                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte)cellInfo).getCellSignalStrength();
//                        LTEdbm = cellSignalStrengthLte.getDbm();
//                    }
//                }
//            }

        }
    }

    private void writeFileToSDCard() throws IOException {
        File parent_path = Environment.getExternalStorageDirectory();

        // 可以建立一个子目录专门存放自己专属文件
        File dir = new File(parent_path.getAbsoluteFile(), "RunNishino");
        dir.mkdir();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date()) + " Signal Record" + ".txt";

        File file = new File(dir.getAbsoluteFile(), date);

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

                telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

                if (choose_which == CHOOSE_GSM){
                    signal = GSMdbm;
                    signal_type = "GSM: ";
                }else if (choose_which == CHOOSE_CDMA){
                    signal = CDMAdbm;
                    signal_type = "CDMA: ";
                }else if (choose_which == CHOOSE_EVDO){
                    signal = EVDOdbm;
                    signal_type = "EVDO: ";
                }else if (choose_which == CHOOSE_LTE){
                    signal = LTEdbm;
                    signal_type = "LTE: ";
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String data = sdf.format(new Date()) +" " + signal_type + signal + "\n";
                byte[] buffer = data.getBytes();

                telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);

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
                handler.postDelayed(this,20000);
            }
        });
    }
}
