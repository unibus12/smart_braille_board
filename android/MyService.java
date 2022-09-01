package com.example.cattower;

import android.app.Service;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyService extends Service {
    private Socket socket;
    private static final String TAG="mytag";
    public int step;
    public String mode;
    boolean isConnected = true;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServerThread", "startThread");
        ConnectThread thread = new ConnectThread("192.168.227.99");
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        step = intent.getIntExtra("step",0);

        mode = intent.getStringExtra("mode");

        Log.d(TAG, "mode=" + mode);

        if (step==1) {
            StartThread sthread = new StartThread();
            sthread.start();
            Log.d(TAG,"StartThread!");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class ConnectThread extends Thread {
        String hostname;

        public ConnectThread(String addr) {
            hostname = addr;
        }

        public void run() {
            try { //클라이언트 소켓 생성
                int port = 35000;
                Log.d(TAG, "Socket 생성 start");
                socket = new Socket(hostname, port);
                Log.d(TAG, "Socket 생성, 연결.");
                InetAddress addr = socket.getInetAddress();
                String tmp = addr.getHostAddress();

            } catch (UnknownHostException uhe) { // 소켓 생성 시 전달되는 호스트(www.unknown-host.com)의 IP를 식별할 수 없음.
                Log.e(TAG, " 생성 Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)");
            } catch (IOException ioe) { // 소켓 생성 과정에서 I/O 에러 발생.
                Log.e(TAG, " 생성 Error : 네트워크 응답 없음");
            } catch (SecurityException se) { // security manager에서 허용되지 않은 기능 수행.
                Log.e(TAG, " 생성 Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)");
            } catch (IllegalArgumentException le) { // 소켓 생성 시 전달되는 포트 번호(65536)이 허용 범위(0~65535)를 벗어남.
                Log.e(TAG, " 생성 Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)");
            }
        }
    }
    class StartThread extends Thread{

        int bytes;
        String Dtmp;

        public StartThread(){

        }

        public String byteArrayToHex(byte[] a) {
            StringBuilder sb = new StringBuilder();
            for(final byte b: a)
                sb.append(String.format("%02x ", b&0xff));
            return sb.toString();
        }

        public void run(){
            // 데이터 송신
            try{
                Log.d(TAG, mode);
                byte[] m_data = mode.getBytes();
                OutputStream m_output = socket.getOutputStream();
                m_output.write(m_data);

                Log.d(TAG, "mode COMMAND 송신");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"데이터 송신 오류");
            }

            // 데이터 수신
            try {
                Log.d(TAG, "데이터 수신 준비");

                //TODO:수신 데이터(프로토콜) 처리

                while (true) {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    String read = buf.readLine();
                    Log.d(TAG, "buf read = " + read);

                    String[] array = read.split(",");
                    if (array[0].equals("login")){
                        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent.putExtra("step", 3);
                        showIntent.putExtra("data", read);
                        startActivity(showIntent);
                    }
                    else if (array[0].equals("step")){
                        Intent showIntent = new Intent(getApplicationContext(), StepActivity.class);
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent.putExtra("step", 3);
                        showIntent.putExtra("data", read);
                        startActivity(showIntent);
                    }
                    else if (array[0].equals("temp")){
                        Intent showIntent = new Intent(getApplicationContext(), HeatActivity.class);
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent.putExtra("step", 3);
                        showIntent.putExtra("data", read);
                        startActivity(showIntent);
                    }
                    else if (array[0].equals("weight")){
                        Intent showIntent = new Intent(getApplicationContext(), EatActivity.class);
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent.putExtra("step", 3);
                        showIntent.putExtra("data", read);
                        startActivity(showIntent);
                    }
                    else if (array[0].equals("play")){
                        Intent showIntent = new Intent(getApplicationContext(), PlayActivity.class);
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent.putExtra("step", 3);
                        showIntent.putExtra("data", read);
                        startActivity(showIntent);
                    }
                    else if (array[0].equals("health")){
                        Intent showIntent = new Intent(getApplicationContext(), HealthActivity.class);
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent.putExtra("step", 3);
                        showIntent.putExtra("data", read);
                        startActivity(showIntent);
                    }
                    else if (array[0].equals("info")){
                        Intent showIntent = new Intent(getApplicationContext(), InfoActivity.class);
                        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent.putExtra("step", 3);
                        showIntent.putExtra("data", read);
                        startActivity(showIntent);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
                Log.e(TAG,"수신 에러");
            }

        }

    }
}