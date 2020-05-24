package rd.com.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import rd.com.demo.banco.sugarOs.Carinho_itemDB;

public class Wellcome_activity extends AppCompatActivity {

    Button avancar;
    ImageView imageView1, imageView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("INIT", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("primeira_vez", false)) {
            //verifica se o usuario ja passou nessa tela
            //se ja, chama a MainActivity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            /*
            setContentView(R.layout.activity_wellcome_activity);
            imageView1 = findViewById(R.id.imageView4);
            imageView2 = findViewById(R.id.imageView5);
            avancar = findViewById(R.id.button6);
            avancar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wellcome);
            imageView1.setAnimation(animation);
            imageView1.startAnimation(animation);
            imageView2.setAnimation(animation);
            imageView2.startAnimation(animation);
*/
         iniciar_bd();
         tempo(0);
        }
    }
    private void tempo(int time){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("INIT", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("primeira_vez", true).apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, time);
    }
    private void iniciar_bd(){
        new Thread(new Runnable() {
            public void run(){
                Carinho_itemDB.find(Carinho_itemDB.class, "");

            }
        }).start();
    }
}
