package rd.com.demofuncionario.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.auxiliar.Constants;
import rd.com.demofuncionario.item.firebase.Pedidos;

public class ConfirmarPedido extends AppCompatActivity {
    Button confirmar, cancelar;
    String cidadecode, tipoestabelecimento, idestabelecimento;
    Pedidos pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_pedido);

        confirmar = findViewById(R.id.entregue);
        cancelar = findViewById(R.id.cancelado);

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        idestabelecimento = sharedPreferences.getString(Constants.idEstabelecimento, "");
        tipoestabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog("Deseja finalizar o pedido?", "Só confirme depois que o pedido ter sido entregue", 0);
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog("Deseja cancelar o pedido?", "O cliente receberá uma notificação sobre", 1);
            }
        });
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
                if (i == 0) {//confirmar pedido
                    atualizarPedido(Constants.concluido, Constants.concluido_code, "Seu pedido foi concluído \uD83D\uDE09");
                } else {//cancelar pedio
                    atualizarPedido(Constants.cancelado, Constants.cancelado_code, "Seu pedido foi cancelado \uD83D\uDE15");
                }
            }
        });
        AlertDialog alerta;
        alerta = builder.create();
        alerta.setTitle(title);
        alerta.setMessage(msg);
        alerta.show();
    }
    private void atualizarPedido(String status, int statuscode, final String msg) {
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
                        Snackbar.make(cancelar, "Pedido atualizado com sucesso!", Snackbar.LENGTH_SHORT).show();
                        notificar_usuario(msg,
                                pedido.getTitulo().replace("¨", ", "));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Snackbar.make(cancelar, "Ocorreu uma falha ao atualizar o pedido: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(cancelar, "Ouve uma falha ao atualizar o pedido: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
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
}
