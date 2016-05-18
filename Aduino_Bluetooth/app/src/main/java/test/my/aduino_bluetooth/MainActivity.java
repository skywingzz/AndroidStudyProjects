package test.my.aduino_bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;



public class MainActivity extends ActionBarActivity {
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    BluetoothSocket mSocket;
    OutputStream mOutputStream;
    InputStream mInputStream;
    String mDelimiter="\\n";
    int readBufferPosition;
    byte []readBuffer;
    Thread mWorkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            //장치가 블루투스를 지원하지 않는 경우.
            System.out.println("장치가 블루투스를 지원하지 않는 경우");
            finish();   // 어플리케이션 종료
        }else {
            // 장치가 블루투스를 지원하는 경우.
            if(!mBluetoothAdapter.isEnabled()) {//블루투스가 비활성 상태라면
                System.out.println("장치가 블루투스를 지원하나 비활성화 상태여서 활성화 요청을 보내는 경우");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
                //1 는 사용자 정의 상수로 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때
               // 식별자로 사용되며 0 보다 큰 값으로 정의 해야 한다.

            }else {
                // 블루투스를 지원하며 활성 상태인 경우
                // 페어링된 기기 목록을 보여주고 연결할 장치를 선택.
                System.out.println("블루투스를 지원하며 활성 상태인 경우 페어링된 기기 목록을 보여주고 연결할 장치를 선택");
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if(pairedDevices.size() > 0) {
                    // 페어링 된 장치가 있는 경우.
                    System.out.println("페어링 된 장치가 있는 경우.");
                    selectDevice(pairedDevices);
                }

                else {
                    // 페어링 된 장치가 없는 경우.
                    System.out.println("페어링 된 장치가 없는 경우.");
                    finish();// 어플리케이션 종료
                }
            }




        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    // 블루투스가 활성 상태로 변경됨
                    System.out.println("블루투스가 활성 상태로 변경됨.");
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if(pairedDevices.size() > 0) {
                        // 페어링 된 장치가 있는 경우.
                        System.out.println("페어링 된 장치가 있는 경우.");
                        selectDevice(pairedDevices);
                    }

                    else {
                        // 페어링 된 장치가 없는 경우.
                        System.out.println("페어링 된 장치가 없는 경우.");
                        finish();// 어플리케이션 종료
                    }
                }else if(resultCode == RESULT_CANCELED) {
                    // 블루투스가 비활성 상태임
                    System.out.println("블루투스 활성화 요청에 실패한 경우.");
                    finish();  //  어플리케이션 종료

                }
                break;
        }//

    }

    void selectDevice(Set<BluetoothDevice> pairedDevices) {

        this.pairedDevices=pairedDevices;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");


        // 페어링 된 블루투스 장치의 이름 목록 작성
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : pairedDevices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");    // 취소 항목 추가

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == MainActivity.this.pairedDevices.size()) {
                    // 연결할 장치를 선택하지 않고 '취소'를 누른 경우
                    System.out.println("연결할 장치를 선택하지 않고 '취소'를 누른 경우");
                    finish();
                } else {
                    // 연결할 장치를 선택한 경우
                    // 선택한 장치와 연결을 시도함
                    System.out.println("연결할 장치를 선택한 경우 선택한 장치와 연결을 시도함");
                    connectToSelectedDevice(items[item].toString());
                }
            }
        });


        builder.setCancelable(false);    // 뒤로 가기 버튼 사용 금지
        AlertDialog alert = builder.create();
        alert.show();

    }

    void connectToSelectedDevice(String selectedDeviceName) {
        BluetoothDevice mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성
             mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            System.out.println("소켓 생성:"+mSocket);
            // RFCOMM 채널을 통한 연결
            mSocket.connect();

            // 데이터 송수신을 위한 스트림 열기
             mOutputStream = mSocket.getOutputStream();
             mInputStream = mSocket.getInputStream();

            // 데이터 수신 준비
            beginListenForData();
        }catch(Exception e) {
            // 블루투스 연결 중 오류 발생
            Log.d("jes","블루투스 연결 중 오류 발생");
            e.printStackTrace();
            finish();   // 어플 종료
        }
    }

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for(BluetoothDevice device : pairedDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    public void on(View view) {
        sendData("y");
    }

    public void off(View view) {
        sendData("n");
    }

    void sendData(String msg) {
       // msg += mDelimiter;    // 문자열 종료 표시
       // System.out.println("전송할 문자열:"+msg);
        try {
            mOutputStream.write(msg.getBytes());    // 문자열 전송
        } catch(Exception e) {
            // 문자열 전송 도중 오류가 발생한 경우.
            e.printStackTrace();
            finish();    //  APP 종료
        }
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();

        readBuffer = new byte[1024] ;  //  수신 버퍼
        readBufferPosition = 0;        //   버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            public void run() {
                    while(!Thread.currentThread().isInterrupted()){

                        try {
                            int bytesAvailable = mInputStream.available();    // 수신 데이터 확인
                            if(bytesAvailable > 0) {                     // 데이터가 수신된 경우
                                byte[] packetBytes = new byte[bytesAvailable];
                                mInputStream.read(packetBytes);
                                for(int i=0 ; i<bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if(b == mDelimiter.getBytes()[0]) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                 // 수신된 문자열 데이터에 대한 처리 작업
                                            }
                                        });
                                    }
                                     else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        }
                        catch (IOException ex) {
                            // 데이터 수신 중 오류 발생.
                            ex.printStackTrace();
                            finish();
                        }
                    }
            }
        });

        mWorkerThread.start();
    }




@Override
protected void onDestroy() {
        try {
            mWorkerThread.interrupt();   // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch(Exception e) { }


    }








@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
