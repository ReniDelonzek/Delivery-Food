package rd.com.demo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import rd.com.demo.R;
import rd.com.demo.item.firebase.Mensagens;

public class Mensagem extends AppCompatActivity {
    TextInputEditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty()){
                    Map<String, String> dataHora = obter_data();
                    Mensagens mensagens = new Mensagens(editText.getText().toString(), dataHora.get("data"), dataHora.get("hora"));
                    FirebaseDatabase.getInstance().getReference()
                            .child("Mensagens")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .push().setValue(mensagens);
                    Snackbar.make(editText, "Sua mensagem foi enviada, responderemos no seu E-mail. Obrigado^^", Snackbar.LENGTH_SHORT).show();
                    tempo(3000);
                } else {
                    Snackbar.make(editText, "Insira alguma mensagem!", Snackbar.LENGTH_SHORT).show();
                    editText.setError("");
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editText = findViewById(R.id.enviemsg);

    }
    private void tempo(int time){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, time);
    }
    private Map<String, String> obter_data(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
        SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        Map<String, String> map = new HashMap<>();
        map.put("data", dateFormat.format(data_atual));
        map.put("hora", hora.format(data_atual));
        return map;
    }
}
