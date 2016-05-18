package test.my.gcmtest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gcm.GCMConstants;
import com.google.android.gcm.GCMRegistrar;


public class MainActivity extends Activity {

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        registerGcm();

    }


    public void registerGcm() {

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        String regId = GCMRegistrar.getRegistrationId(this);

        if (regId.equals("") || regId==null) {
            GCMRegistrar.register(this, "68702137140" );
            regId = GCMRegistrar.getRegistrationId(this);
            TextView text=(TextView)findViewById(R.id.text);
            text.setText("==>"+regId+"<==");
            Log.i("id", regId);
        } else {
            regId = GCMRegistrar.getRegistrationId(this);
            TextView text=(TextView)findViewById(R.id.text);

            text.setText("====메세지 확인 완료====");
            Log.e("id", regId);
        }

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
