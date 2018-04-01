package rd.com.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import rd.com.demo.R;
import rd.com.demo.adapter.AdapterMeusPedidos;
import rd.com.demo.item.firebase.Pedidos;

public class MeusPedidos extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterMeusPedidos adapterMeusPedidos;
    List<Object> list;
    TextView textView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), ComecarCadastro.class));
            finish();
        } else {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            textView = findViewById(R.id.textView);
            list = new ArrayList<>();

            progressBar = findViewById(R.id.progressBar);
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapterMeusPedidos = new AdapterMeusPedidos(list);
            recyclerView.setAdapter(adapterMeusPedidos);
            carregarPedidos();


        }
    }

    private void carregarPedidos() {
        FirebaseFirestore.getInstance()
                .collection("Pedidos")
                .document("users")
                .collection(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                progressBar.setVisibility(View.GONE);
                if (!documentSnapshots.isEmpty()) {
                    for(DocumentSnapshot dc : documentSnapshots) {
                        Pedidos pedidos = dc.toObject(Pedidos.class);
                        list.add(pedidos);
                        adapterMeusPedidos.notifyItemInserted(list.size() - 1);
                    }
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setText("Você ainda não fez nenhum pedido");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                textView.setText("Opps, alguma coisa saiu errado...");
                textView.setTextSize(18f);
                e.printStackTrace();
                Snackbar.make(recyclerView, "Ocorreu um erro ao recuperar os pedidos \uD83D\uDE41", Snackbar.LENGTH_SHORT).show();

            }
        });
    }

}
