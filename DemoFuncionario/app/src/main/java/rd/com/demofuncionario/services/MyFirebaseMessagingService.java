package rd.com.demofuncionario.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.activity.Detalhes_pedido;
import rd.com.demofuncionario.activity.MainActivity;
import rd.com.demofuncionario.auxiliar.Constants;
import rd.com.demofuncionario.item.firebase.Pedidos;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String FCM_PARAM = "picture";
    private static final String CHANNEL_NAME = "FCM";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
    private int numMessages = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        Log.d("FROM", remoteMessage.getFrom());
        sendNotification(notification, data);
    }

    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        if (data.get("action").equals("OPEN_ACTIVITY_3")) {
            String idpedido = data.get("data");
            recuperarPedido(idpedido);
        } else {
            int id = 0;//id da notificacao
            Intent intent = new Intent(this, MainActivity.class);//Cria um intent que vai ser aberto ao clicar na notificao

            if (data.get("action").equals("OPEN_ACTIVITY_1")) {//pega o parametro 'action' que foi passado pela notificacao e verifica seu valor
                intent = new Intent(this, MainActivity.class);
                ++id;
            } else if (data.get("action").equals("OPEN_ACTIVITY_2")) {
                //intent = new Intent(this, Tela_chat.class);
                //intent.putExtra()
                id = 0;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default")
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .setContentInfo("Pedidos")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setColor(getColor(R.color.colorAccent))
                    .setLights(Color.RED, 1000, 300)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setNumber(4)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "default", CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(CHANNEL_DESC);
                channel.setShowBadge(true);
                channel.canShowBadge();
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }

            Log.v("Id Notificacao", String.valueOf(id));
            assert notificationManager != null;
            notificationManager.notify(id, notificationBuilder.build());
        }
    }

    private void recuperarPedido(String idpedido) {
        String estabelecimentoid, estabelecimentotipo, cidadecode;
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        estabelecimentoid = sharedPreferences.getString(Constants.idEstabelecimento, "");
        estabelecimentotipo = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");

        FirebaseFirestore.getInstance()
                .collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(estabelecimentotipo)
                .collection(estabelecimentoid)
                .document("pedidos")
                .collection("novos")
                .document(idpedido).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()){
                    Pedidos pedidos = documentSnapshot.toObject(Pedidos.class);
                    Intent intent = new Intent(getApplicationContext(), Detalhes_pedido.class);
                    intent.putExtra("pedido", pedidos);
                    intent.putExtra("time", pedidos.getDatacompleta());
                    startActivity(intent);
                }
            }
        });
    }
}
