package rd.com.vendedor.adm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}
