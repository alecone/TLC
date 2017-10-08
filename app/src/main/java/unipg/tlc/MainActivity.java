package unipg.tlc;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("kiss_fft");
    }

    /* Public Variables*/
    public String nameTest;
    public int ageTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        final TextView tv1 = (TextView) findViewById(R.id.textView1);
        tv1.setText(peppeName());

        final TextView tv2 = (TextView) findViewById(R.id.textView2);
        tv2.setText(peppeAge());

        final TextView tv3 = (TextView) findViewById(R.id.textView3);

        final Button btnGetName = (Button) this.findViewById(R.id.button1);
        btnGetName.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                EditText addName = (EditText) findViewById(R.id.editText1);
                nameTest = addName.getText().toString();
                btnGetName.setText(stringFromJNI());
                tv1.setText("Hey " + nameTest + "! What's up");
            }
        });

        final Button btnGetAge = (Button) this.findViewById(R.id.button2);
        btnGetAge.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                EditText addAge = (EditText) findViewById(R.id.editText2);
                ageTest = Integer.parseInt(addAge.getText().toString());
                btnGetAge.setText(stringFromJNI());
                tv2.setText("You were born in "+dateOfBirth(ageTest));
                tv3.setText(printStruct(nameTest,ageTest));
            }
        });
    }

    /**
     * A native method that is implemented by a native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native String peppeName();
    public native int dateOfBirth(int age);
    public native String peppeAge();
    public native String printStruct(String name, int age);
}
