package rd.com.demo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import rd.com.demo.R;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.item.firebase.Pedidos;

import static rd.com.demo.auxiliares.Constants.aguardandoRetirada_code;
import static rd.com.demo.auxiliares.Constants.andamento_code;
import static rd.com.demo.auxiliares.Constants.cancelado_code;
import static rd.com.demo.auxiliares.Constants.concluido_code;
import static rd.com.demo.auxiliares.Constants.enviado_code;
import static rd.com.demo.auxiliares.Constants.pronto_code;
import static rd.com.demo.auxiliares.Constants.recebido_code;
import static rd.com.demo.auxiliares.Constants.saiuEntrega_code;


public class AdapterMeusPedidos extends RecyclerView.Adapter {
    private List<Object> list;

    public AdapterMeusPedidos(List<Object> list){
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
           final Holder holder = (Holder) viewHolder;
           final Pedidos pedido = (Pedidos) list.get(position);
           definir_horario(pedido, obter_data(), holder);
           holder.titulo.setText(pedido.getTitulo().replace("|", ", "));
        holder.endereco.setText(pedido.getApelidoendereco());
        holder.status.setText(pedido.getStatus());

           switch (pedido.getStatus_code()){
               case enviado_code://pedido enviado
                   holder.statusBar.setBackgroundColor(color(R.color.holo_blue_bright, holder.status.getContext()));
               break;
               case recebido_code://pedido recebido
                   holder.statusBar.setBackgroundColor(color(R.color.mb_blue, holder.status.getContext()));
               break;
               case andamento_code:
                   holder.statusBar.setBackgroundColor(color(R.color.mb_blue, holder.status.getContext()));
                   break;
               case pronto_code://Pronto!
                   holder.statusBar.setBackgroundColor(color(R.color.green, holder.status.getContext()));
               break;
               case saiuEntrega_code://Saiu pra entrega!
                   holder.statusBar.setBackgroundColor(color(R.color.holo_orange_light, holder.status.getContext()));
               break;
               case aguardandoRetirada_code://Aguardando retidada!
                   holder.statusBar.setBackgroundColor(color(R.color.holo_orange_light, holder.status.getContext()));
               break;
               case concluido_code://finalizado
                   holder.statusBar.setBackgroundColor(color(R.color.green, holder.status.getContext()));
               break;
               case cancelado_code://cancelado
                   holder.statusBar.setBackgroundColor(color(R.color.mb_red, holder.status.getContext()));
                   break;
           }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pedidos p = (Pedidos) list.get(holder.getAdapterPosition());
                //listarOpcoes(v.getContext(), p);
            }
        });
           holder.confirmar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   dialog("Confirme", "Deseja confirmar o recebimento do pedido?",
                           holder.cancelar.getContext(), pedido, 0);
               }
           });
           if (pedido.getStatus_code() >= Constants.pronto_code){
               holder.cancelar.setVisibility(View.GONE);
           }
           else {
               holder.cancelar.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       dialog("", "Tem certeza que deseja cancelar o pedido?", v.getContext(), pedido, 1);
                   }
               });
           }
    }
    private void atualizarPedido(final Pedidos pedido, String status, int statuscode, final View view, final boolean notificar) {
        FirebaseFirestore.getInstance()
                .collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(pedido.getCidadecode())
                .document(pedido.getTipoestabelecimento())
                .collection(pedido.getIdEstabelecimento())
                .document("pedidos")
                .collection("novos")
                .document(pedido.getId())
                .update("status", status, "status_code", statuscode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (notificar){
                            notificarEstabelecimento("O cliente cancelou um pedido", pedido.getTitulo(), pedido);
                        } else {
                            Snackbar.make(view, "Pedido confirmado com sucesso!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Snackbar.make(view, "Ocorreu uma falha ao atualizar o pedido: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

        Map<String, Object> sta = new HashMap<>();
        sta.put("statuscode", statuscode);
        sta.put("status", status);
        sta.put("idestabelecimento", pedido.getIdEstabelecimento());
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
                Snackbar.make(view, "Ouve uma falha ao atualizar o pedido: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }
    private void notificarEstabelecimento(final String title, final String ped, final Pedidos pedidos) {
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

                    jData.put("action", "OPEN_ACTIVITY_3");
                    jData.put("data", pedidos.getId());


                    JSONArray ja = new JSONArray();
                    //ja.put(pedido.getUserToken());
                    //ja.put(FirebaseInstanceId.getInstance().getToken());
                    jPayload.put("to", "/topics/pedidos." + pedidos.getIdEstabelecimento());

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

    private void dialog(String title, String msg, final Context context, final Pedidos pedidos, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)context);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialo, int which) {
                switch (i){
                    case 0:
                        atualizarPedido(pedidos, Constants.concluido, Constants.concluido_code, ((Activity) context).getCurrentFocus(), false);
                        break;
                    case 1:
                        atualizarPedido(pedidos, Constants.cancelado, Constants.cancelado_code, ((Activity) context).getCurrentFocus(), true);
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
    public int color(@ColorRes int resId, Context context) {
        return ContextCompat.getColor(
                context, resId);
    }
    private void definir_horario(Pedidos pedidos, Map<String, String> data, Holder holder_pedidos){
        int minpedido = Integer.parseInt(pedidos.getHora().substring(3, 5));
        int minatual = Integer.parseInt(data.get("hora").substring(3, 5));
        int horapedido = Integer.parseInt(pedidos.getHora().substring(0, 2));
        int horaatual = Integer.parseInt(data.get("hora").substring(0, 2));

        int diaatual = Integer.parseInt(data.get("data").substring(0, 2));
        int diapedido = Integer.parseInt(pedidos.getData().substring(0, 2));

        if (pedidos.getData().equals(data.get("data"))){
            if (horaatual == horapedido){//o pedido foi feito ainda nessa hora
                holder_pedidos.hora.setText(String.format("A %s minutos atrás", String.valueOf(minatual - minpedido)));
            } else if (1 + horapedido == horaatual &&  //verifica se o pedido foi feito a menos de uma hora
                    minatual < minpedido){//e se ja nao se passou mais de 60 minutos
                holder_pedidos.hora.setText(String.format(Locale.getDefault(),
                        "Há %s%d minutos atrás", String.valueOf(60 - minpedido), minatual));
            } else {//pedido foi feito a mais de 1 hr
                int ho = horaatual - horapedido;
                if (ho == 1) {
                    holder_pedidos.hora.setText(String.format("A %s Hora atrás", String.valueOf(ho)));
                } else {
                    holder_pedidos.hora.setText(String.format("A %s Horas atrás", String.valueOf(ho)));
                }
            }
        } else if (diaatual - 1 == diapedido){//pedido foi feito outro dia
            holder_pedidos.hora.setText(String.format("Ontem às %s", pedidos.getHora()));
        } else {
            holder_pedidos.hora.setText(String.format("%s %s", pedidos.getData().substring(0, 5),
                    pedidos.getHora().substring(0, pedidos.getHora().length() - 3)));
        }
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
    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder extends RecyclerView.ViewHolder {//
        private final TextView titulo, status, statusBar, hora, endereco;
        private final RelativeLayout layout;
        private final Button confirmar, cancelar;

        Holder(View itemView) {
            super(itemView);
            endereco = itemView.findViewById(R.id.endereco);
            titulo = itemView.findViewById(R.id.titulo);
            layout = itemView.findViewById(R.id.relativeLayout);
            status = itemView.findViewById(R.id.status);
            statusBar = itemView.findViewById(R.id.status_bar);
            hora = itemView.findViewById(R.id.data);
            confirmar = itemView.findViewById(R.id.confirmar);
            cancelar = itemView.findViewById(R.id.cancelar);



        }
    }
}
