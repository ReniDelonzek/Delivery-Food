package rd.com.demofuncionario.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.auxiliar.Constants;

public class Wellcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        String estabelecimentoid = sharedPreferences.getString(Constants.idEstabelecimento, "");
        String estabelecimentotipo = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        String cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");
        if (!estabelecimentoid.isEmpty() || !estabelecimentotipo.isEmpty() || !cidadecode.isEmpty()
                && FirebaseAuth.getInstance().getCurrentUser() != null){

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }
}
