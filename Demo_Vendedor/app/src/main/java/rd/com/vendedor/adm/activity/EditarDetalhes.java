package rd.com.vendedor.adm.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.utils.Constants;

public class EditarDetalhes extends AppCompatActivity {
    TextInputEditText preco, distancia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_detalhes);

        preco = findViewById(R.id.precoKm);
        distancia = findViewById(R.id.ditancia);

        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preco.getText().toString().isEmpty() && !distancia.getText().toString().isEmpty()){
                    SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
                    String estabelecimento_id = sharedPreferences.getString(Constants.caminho, "");
                    String tipoEstabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/precoKm", Double.parseDouble(preco.getText().toString()));
                    childUpdates.put("/distanciamax", Double.parseDouble(distancia.getText().toString()));
                    FirebaseDatabase.getInstance().getReference()
                            .child("Estabelecimentos").child(tipoEstabelecimento)
                            .child(estabelecimento_id)
                            .updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Falha ao atualizar os preços", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Snackbar.make(distancia, "Por favor preencha os dois valores", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
