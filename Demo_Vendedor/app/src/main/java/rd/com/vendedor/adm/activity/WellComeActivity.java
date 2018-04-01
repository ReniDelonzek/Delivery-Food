package rd.com.vendedor.adm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import rd.com.vendedor.R;

public class WellComeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_come);

        //SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        //String id = sharedPreferences.getString("id", "");//obter
        if (FirebaseAuth.getInstance().getCurrentUser() == null){//usuario n conectado
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), Main3Activity.class));
            finish();
        }
    }
}
