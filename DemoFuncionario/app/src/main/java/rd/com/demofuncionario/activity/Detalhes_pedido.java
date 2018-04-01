package rd.com.demofuncionario.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.adapter.Pedidos_adapter;
import rd.com.demofuncionario.auxiliar.Constants;
import rd.com.demofuncionario.item.firebase.Pedidos;

public class Detalhes_pedido extends AppCompatActivity {
    Pedidos pedido;
    RecyclerView recyclerView;
    List<Pedidos> pedidos;
    TextView endereco, data, msg;
   // Spinner status;
    String time;
    ImageView status1, status2, status3;
    String idestabelecimento, tipoestabelecimento;
    private String cidadecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_pedido);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Detalhes do Pedido");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);

        recyclerView = findViewById(R.id.recyclerView);
        endereco = findViewById(R.id.endereco);
        data = findViewById(R.id.tempo);
        //status = findViewById(R.id.spinner2);
        msg = findViewById(R.id.msg);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pedido.getStatus_code() == (Constants.andamento_code)) {
                    dialog("Deseja confirmar o pedido?",
                            "O cliente será notificado que o pedido dele está pronto", 1);
                }
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        idestabelecimento = sharedPreferences.getString(Constants.idEstabelecimento, "");
        tipoestabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        time = getIntent().getStringExtra("time");
        pedidos = new ArrayList<>();

        if (pedido.getCodigo().contains("¨")) {//varios produtos
            String[] codigos = pedido.getCodigo().split("¨");
            String[] titulos = pedido.getTitulo().split("¨");
            String[] descricoes = pedido.getDescricao().split("¨");
            String[] quantidades = pedido.getQuantidade().split("¨");


            for (int i = 0; i < codigos.length; i++) {
                pedidos.add(new Pedidos(titulos[i], descricoes[i], codigos[i], pedido.getStatus(), pedido.getNomeEstabelecimento(),
                        pedido.getIdEstabelecimento(), quantidades[i], pedido.getUserid(), pedido.getUsername(),
                        pedido.getEndereco(), pedido.getTipoEntrega(),
                        pedido.getData(), pedido.getHora(), pedido.getMensagem(), pedido.getStatus_code(), pedido.getUserToken()));
            }

        } else {//pedido de item unico
            pedidos.add(pedido);
        }
        RecyclerView.Adapter adapter = new Pedidos_adapter(pedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        endereco.setText(pedido.getEndereco());
        data.setText(time);
        msg.setText(pedido.getMensagem());

        /*/////////////////////
        Definindo botoes de status
         *//////////////

        status1 = findViewById(R.id.id1);
        status2 = findViewById(R.id.id2);
        status3 = findViewById(R.id.id3);

        TextDrawable drawable1 = TextDrawable.builder()
                .buildRound("1", Color.BLUE); // radius in px
        status1.setImageDrawable(drawable1);

        if (pedido.getStatus_code() >= Constants.pronto_code
                && pedido.getStatus_code() < Constants.concluido_code) {
            TextDrawable drawable2 = TextDrawable.builder()
                    .buildRound("2", Color.BLUE); // radius in px
            status2.setImageDrawable(drawable2);
            TextDrawable drawable3 = TextDrawable.builder()
                    .buildRound("3", R.color.mb_blue_dark); // R.color nao funciona
            status3.setImageDrawable(drawable3);

        } else if (pedido.getStatus_code() == Constants.concluido_code){
            fab.hide();
            TextDrawable drawable2 = TextDrawable.builder()
                    .buildRound("2", Color.BLUE); // radius in px
            status2.setImageDrawable(drawable2);
            TextDrawable drawable3 = TextDrawable.builder()
                    .buildRound("3", Color.BLUE); // radius in px
            status3.setImageDrawable(drawable3);
        }
        if (pedido.getStatus_code() == Constants.enviado_code){
            atualizarPedido(Constants.andamento, Constants.andamento_code, "", false);
        }
    }

    private void dialog(String title, String msg, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (i){
                    case 1://confirmar pedido
                        switch (pedido.getTipoEntrega()) {
                            case Constants.clienteBusca:
                                pedido.setStatus_code(Constants.aguardandoRetirada_code);
                                atualizarPedido(Constants.aguardandoRetirada, Constants.aguardandoRetirada_code,
                                        "Seu pedido está aguardando sua retirada \uD83D\uDE01", true);
                                break;
                            case Constants.levarCasa:
                                pedido.setStatus_code(Constants.saiuEntrega_code);
                                atualizarPedido(Constants.saiuEntrega, Constants.saiuEntrega_code,
                                        "Seu pedido saiu para entrega \uD83D\uDE01", true);
                                break;
                            case Constants.levarMesa:
                                pedido.setStatus_code(Constants.pronto_code);
                                atualizarPedido(Constants.pronto, Constants.pronto_code,
                                        "Seu pedido está pronto, logo logo ele chega aí \uD83D\uDE01", true);
                                break;
                        }
                        TextDrawable drawable1 = TextDrawable.builder()
                                .buildRound("2", Color.BLUE); // radius in px
                        status2.setImageDrawable(drawable1);
                    break;
                    case 2:
                        atualizarPedido(Constants.cancelado, Constants.cancelado_code, "Seu pedido foi cancelado \uD83D\uDE15", true);
                        break;
                }
            }
        });
        AlertDialog alerta;
        alerta = builder.create();
        alerta.setTitle(title);
        alerta.setMessage(msg);
        alerta.show();
    }
    private void atualizarPedido(String status, int statuscode, final String msg, final boolean b) {
        FirebaseFirestore.getInstance()
                .collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(tipoestabelecimento)
                .collection(idestabelecimento)
                .document("pedidos")
                .collection("novos")
                .document(pedido.getId())
                .update("status", status, "status_code", statuscode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(recyclerView, "Pedido atualizado com sucesso!", Snackbar.LENGTH_SHORT).show();
                if (b)
                notificar_usuario(msg,
                        pedido.getTitulo().replace("¨", ", "));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Snackbar.make(recyclerView, "Ocorreu uma falha ao atualizar o pedido: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

        Map<String, Object> sta = new HashMap<>();
        sta.put("statuscode", statuscode);
        sta.put("status", status);
        sta.put("idestabelecimento", idestabelecimento);
        FirebaseFirestore.getInstance()
                .collection("Pedidos")
                .document("users")
                .collection(pedido.getUserid())
                .document(pedido.getId())
                //.collection("status")
                //.document("status")
                //.set(sta)
                .update("status", status, "status_code", statuscode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Erro:", e.getMessage());
                Log.e("Erro:", e.getCause().toString());
                Snackbar.make(recyclerView, "Ouve uma falha ao atualizar o pedido: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }
    private void notificar_usuario(final String title, final String ped) {
        new Thread() {
            @Override
            public void run() {

                JSONObject jPayload = new JSONObject();
                JSONObject jNotification = new JSONObject();
                JSONObject jData = new JSONObject();
                try {
                    jNotification.put("title", title);
                    jNotification.put("body", ped);
                    jNotification.put("sound", "default");
                    jNotification.put("badge", "1");
                    jNotification.put("click_action", "OPEN_ACTIVITY_1");
                    jNotification.put("icon", "ic");

                    jData.put("action", "OPEN_ACTIVITY_1");
                    jData.put("data", ped);


                    JSONArray ja = new JSONArray();
                    ja.put(pedido.getUserToken());
                            //ja.put(FirebaseInstanceId.getInstance().getToken());

                    jPayload.put("registration_ids", ja);

                    jPayload.put("priority", "high");
                    jPayload.put("notification", jNotification);
                    jPayload.put("data", jData);

                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "key=" + Constants.AUTH_KEY);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // Send FCM message content.
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(jPayload.toString().getBytes());

                    // Read FCM response.
                    InputStream inputStream = conn.getInputStream();
                    final String resp = convertStreamToString(inputStream);

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("Messaging", resp);
                        }
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detalhes_pedido, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cancelar) {
            dialog("Deseja cancelar o pedido?", "O cliente receberá uma notificação sobre", 2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
