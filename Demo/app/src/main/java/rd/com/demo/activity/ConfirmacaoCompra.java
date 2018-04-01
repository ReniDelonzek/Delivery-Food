package rd.com.demo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import rd.com.demo.R;
import rd.com.demo.adapter.AdapterConfirmacaoCompra;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.item.firebase.Endereco;
import rd.com.demo.banco.sugarOs.Carinho_itemDB;
import rd.com.demo.item.firebase.Estabelecimento;
import rd.com.demo.item.firebase.Pedidos;

import static rd.com.demo.auxiliares.Constants.clienteBusca;
import static rd.com.demo.auxiliares.Constants.levarCasa;
import static rd.com.demo.auxiliares.Constants.levarMesa;

public class ConfirmacaoCompra extends AppCompatActivity {
    private static String AUTH_KEY =
            "AAAAMncO7Qs:APA91bGyay_ka9e1B2v7LMLcfVgIvOZDLQvOyFgpsi2iOAAWP6XkSYxlSetZ7rlrL6Xz9Ssuc8lY18hVJtdujK4SnUE1QQbn3pMsXtC3wHBLO2A82l_oNs9dwlsAod5bsjxBujepWguI";
    TextView cliente, textviewEndereco, complemento, precoTotal;
    RadioButton avista, cartao;
    Button pedir, alterar_endereco, add_cartao;
    CardView  cardView_Endereco, cardView_Mesa;
    Carinho_itemDB carinhoitem;
    String token, id_pedido;
    ImageView img_local;
    String userId, userName;
    boolean item_unico;
    RadioButton radioButton1, radioButton2, radioButton3;
    String tipo_entrega = "O cliente vem buscar";//padrao
    TextView ender_loja;
    List<Carinho_itemDB> carinho_itemDBList;
    RecyclerView recyclerView;
    NestedScrollView nestedScrollView;
    CircleImageView imagem_estabelecimento;
    TextView nome_estabelecimento, slogan_estabelecimento;
    Estabelecimento estabelecimento;
    List<Endereco> enderecos;
    Endereco endereco;
    Spinner spinner;
    ProgressBar progressBar;
    TextInputEditText mensagem;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_compra);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Confirme sua compra");

        setarIdViews();

        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);
        add_cartao.setVisibility(View.GONE);
        enderecos = new ArrayList<>();

        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals("comprar")) {
                if (getIntent().getStringExtra("type") != null) {
                    if (getIntent().getStringExtra("type").equals("buyAll")) {
                        item_unico = false;

                        carinhoitem = (Carinho_itemDB) getIntent().getSerializableExtra("pedido");
                        String[] titulos = carinhoitem.getTitulo().split("¨");
                        String[] descricoes = carinhoitem.getDescricao().split("¨");
                        String[] urls = carinhoitem.getUrl().split("¨");
                        String[] codigos = carinhoitem.getCodigo().split("¨");
                        String[] quantidade = carinhoitem.getQuantidade().split("¨");
                        String[] preco = carinhoitem.getPreco().split("¨");
                        String[] nomeamotra = carinhoitem.getPreco().split("¨");

                        RecyclerView.Adapter adapter = new AdapterConfirmacaoCompra(carinho_itemDBList, precoTotal, carinhoitem);
                        recyclerView.setAdapter(adapter);

                        for (int i = 0; i < titulos.length; i++) {
                            carinho_itemDBList.add(new Carinho_itemDB(titulos[i], descricoes[i], carinhoitem.getTipoestabelecimento(),
                                    carinhoitem.getEstabelecimento(), carinhoitem.getEstabelecimentoid(),
                                    codigos[i], urls[i], quantidade[i], preco[i], "00/00/00",
                                    true, true, carinhoitem.getCidadecode(), carinhoitem.getCidade(),
                                    nomeamotra[i]
                                    ));

                            adapter.notifyItemInserted(carinho_itemDBList.size() - 1);
                        }

                    } else {
                        item_unico = true;
                        carinhoitem = (Carinho_itemDB) getIntent().getSerializableExtra("pedido");
                        RecyclerView.Adapter adapter = new AdapterConfirmacaoCompra(carinho_itemDBList, precoTotal, carinhoitem);
                        recyclerView.setAdapter(adapter);
                        carinho_itemDBList.add(carinhoitem);
                        adapter.notifyItemInserted(carinho_itemDBList.size() - 1);
                    }
                } else {
                    item_unico = true;
                    carinhoitem = (Carinho_itemDB) getIntent().getSerializableExtra("pedido");
                    RecyclerView.Adapter adapter = new AdapterConfirmacaoCompra(carinho_itemDBList, precoTotal, carinhoitem);
                    recyclerView.setAdapter(adapter);
                    carinho_itemDBList.add(carinhoitem);
                    adapter.notifyItemInserted(carinho_itemDBList.size() - 1);
                }

                String[] p = carinhoitem.getPreco().split("¨");
                String[] q = carinhoitem.getQuantidade().split("¨");
                double precoT = 0;
                for (int i = 0; i < p.length; i++) {
                    precoT = precoT + Double.parseDouble(p[i]) * Double.parseDouble(q[i]);
                }
                precoTotal.setText(String.format("R$%s", String.format(Locale.getDefault(), "%.2f", precoT)));
            }

        }
        obter_dados_estabelecimento();
        ender_loja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estabelecimento.getCoordenadas() != null) {
                    Uri gmmIntentUri = Uri.parse("geo:" + estabelecimento.getCoordenadas());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        Snackbar.make(v, "É necessario ter o Google Maps instalado para abrir o endereço no mapa", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "Endereço da loja indisponivel", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> data = obter_data();
                String antendimento[] = estabelecimento.getAtendimento().split(",");
                int horaAbertura = Integer.parseInt(antendimento[0].replace(":", ""));
                int horaFechamento = Integer.parseInt(antendimento[1].replace(":", ""));
                int horaAtual = Integer.parseInt(data.get("hora").substring(0, 5).replace(":", ""));
                if(horaAbertura <= horaAtual
                        && horaFechamento >= horaAtual){
                    int to = 0;
                    for (int i = 0; i < carinho_itemDBList.size(); i++) {
                        to += Integer.parseInt(carinho_itemDBList.get(i).getQuantidade().replaceAll("[^0-9]", ""));
                    }
                    confirmarCompraDialog("Confirme", "Deseja comfirmar o pedido de " +
                            to + " itens no valor de " + precoTotal.getText().toString() + "?");
                } else {
                    Snackbar.make(recyclerView, "Opss, " + estabelecimento.getNome() + " está fechado agora \uD83D\uDE22", Snackbar.LENGTH_SHORT ).show();
                }
            }
        });

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cardView_Endereco.setVisibility(View.GONE);
                    cardView_Mesa.setVisibility(View.GONE);
                    ender_loja.setVisibility(View.VISIBLE);
                    img_local.setVisibility(View.VISIBLE);
                    tipo_entrega = clienteBusca;
                }
            }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cardView_Endereco.setVisibility(View.VISIBLE);
                    cardView_Mesa.setVisibility(View.GONE);
                    ender_loja.setVisibility(View.GONE);
                    img_local.setVisibility(View.GONE);
                    tipo_entrega = levarCasa;
                    if (endereco == null){
                        listar_enderecos(false);
                    }
                }
            }
        });
        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cardView_Endereco.setVisibility(View.GONE);
                    cardView_Mesa.setVisibility(View.VISIBLE);
                    ender_loja.setVisibility(View.GONE);
                    img_local.setVisibility(View.GONE);
                    tipo_entrega = levarMesa;
                }
            }
        });
        recyclerView.setFocusable(false);
        alterar_endereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listar_enderecos(true);
            }
        });

        cartao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    avista.setChecked(true);
                    cartao.setChecked(false);
                    Snackbar.make(buttonView,
                            "Ainda não estamos processando pagamentos dentro do aplicativo, em breve estará disponível",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "Vamos criar uma conta antes", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(getApplicationContext(), ComecarCadastro.class), 2);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            userName = sharedPreferences.getString("nome", "");
            userId = sharedPreferences.getString("userid", "");
            obter_endereco();
        }

    }

    private void setarIdViews() {
        cliente = findViewById(R.id.cliente);
        textviewEndereco = findViewById(R.id.endereco);
        complemento = findViewById(R.id.complemento);
        precoTotal = findViewById(R.id.textView5);
        pedir = findViewById(R.id.button4);
        avista = findViewById(R.id.radioButton4);
        cartao = findViewById(R.id.radioButton5);
        alterar_endereco = findViewById(R.id.button3);
        add_cartao = findViewById(R.id.add_cartao);
        radioButton1 = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        cardView_Endereco = findViewById(R.id.cardView2);
        cardView_Mesa = findViewById(R.id.cardView3);
        ender_loja = findViewById(R.id.endereco_loja);
        img_local = findViewById(R.id.imageView2);
        recyclerView = findViewById(R.id.recyclerView);
        carinho_itemDBList = new ArrayList<>();
        nestedScrollView = findViewById(R.id.nestedScrollView);
        imagem_estabelecimento = findViewById(R.id.estabelecimento_imagem);
        nome_estabelecimento = findViewById(R.id.estabelecimento);
        slogan_estabelecimento = findViewById(R.id.slogan_estabelecimento);
        spinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar3);
        mensagem = findViewById(R.id.mensagem);
    }

    private void pedir(){
        if (!carinhoitem.getQuantidade().equals("0")) {
            String ende = "";
            String apelidoendereco = "";
            switch (tipo_entrega){
                case clienteBusca:
                    ende = estabelecimento.getEndereco();
                    apelidoendereco = "Você precisa buscar seu pedido na loja";
                    break;
                case levarCasa:
                    ende = endereco.toString();
                    apelidoendereco = endereco.getApelido();
                    break;
                case levarMesa:
                    ende = spinner.getSelectedItem().toString();
                    apelidoendereco = "Seu pedido será entregue em sua mesa";
                    break;
            }
            //obter preco unitario
            StringBuilder precos = new StringBuilder();
            for(int i = 0; i < carinho_itemDBList.size(); i++){
                precos.append(carinho_itemDBList.get(i).getPreco());
                if (i < carinho_itemDBList.size() - 1){//verifica se este nao e o ultimo item da lista, caso n for add ¨
                    precos.append("¨");
                }
            }
            Log.v("precos", precos.toString());
            Map<String, String> map = obter_data();
            final Pedidos pedido = new Pedidos(carinhoitem.getTitulo(), carinhoitem.getDescricao(), carinhoitem.getCodigo(),
                    "Pedido Enviado", carinhoitem.getEstabelecimento(), carinhoitem.getid(),
                    carinhoitem.getTipoestabelecimento(), carinhoitem.getQuantidade().replace ("\n", ""), userId, userName,
                    ende, tipo_entrega, map.get("data"), map.get("hora"), mensagem.getText().toString(), 0,
                    FirebaseInstanceId.getInstance().getToken(), precos.toString(), apelidoendereco,
                    carinhoitem.getCidadecode(), carinhoitem.getCidade());
            pedido.setDatacompleta(map.toString());

            FirebaseFirestore.getInstance()
                    .collection("estabelecimentos")
                    .document(Constants.cidade)
                    .collection(String.valueOf(carinhoitem.getCidadecode()))
                    .document(carinho_itemDBList.get(0).getTipoestabelecimento())
                    .collection(estabelecimento.getId())
                    .document("pedidos")
                    .collection("novos")
                    .add(pedido).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    id_pedido = documentReference.getId();
                    salvarUsuario(pedido);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    Snackbar.make(recyclerView, "OPs, ocorreu uma falha ao finalizar o pedido", Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            dialog.dismiss();
            Snackbar.make(add_cartao, "Insira pelo menos um produto na lista", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void salvarUsuario(final Pedidos pedido) {
        FirebaseFirestore.getInstance()
                .collection("Pedidos")
                .document("users")
                .collection(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .document(id_pedido)
                .set(pedido)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Pedido realizado com sucesso", Toast.LENGTH_SHORT).show();
                        remover_carinho();
                        notificarEstabelecimento("topic", pedido);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                dialog.dismiss();
                Snackbar.make(recyclerView, "Ops, ocorreu uma falha ao finalizar o pedido", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarCompraDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialo, int which) {
                dialog = ProgressDialog.show(ConfirmacaoCompra.this, "",
                        "Fazendo o pedido, aguarde...", true);
                pedir();
            }
        });
        AlertDialog alerta;
        alerta = builder.create();
        alerta.setTitle(title);
        alerta.setMessage(msg);
        alerta.show();
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
    private void configurarAdapter(String id){
        final List<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        carregar_mesas(arrayList, adapter, id);
    }
    private void carregar_mesas(final List<String> arrayList, final ArrayAdapter<String> adapter, String res) {
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child(carinhoitem.getTipoestabelecimento())
                .child(res)
                .child("mesas")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        progressBar.setVisibility(View.GONE);
                        arrayList.add(dataSnapshot.getValue(String.class));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private void listar_enderecos(boolean cancelable){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ConfirmacaoCompra.this);
        builderSingle.setTitle("Endereços Salvos");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ConfirmacaoCompra.this,
                R.layout.item_dialog);
        if (!enderecos.isEmpty()) {
            for (int i = 0; i < enderecos.size(); i++) {
                arrayAdapter.add(enderecos.get(i).getEndereco() + "\n" + enderecos.get(i).getBairro() +
                        " - " + enderecos.get(i).getNumero());
            }
        } else {
            arrayAdapter.add("Adicione um novo endereço");
        }

        builderSingle.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (endereco == null){
                    Snackbar.make(spinner, "Você precisa selecionar um endereço de entrega para que possamos levar até você"
                    , Snackbar.LENGTH_SHORT).show();
                    cardView_Endereco.setVisibility(View.GONE);
                    cardView_Mesa.setVisibility(View.GONE);
                    ender_loja.setVisibility(View.VISIBLE);
                    img_local.setVisibility(View.VISIBLE);
                    tipo_entrega = clienteBusca;
                    radioButton2.setChecked(false);
                    radioButton1.setChecked(true);

                }
                dialog.dismiss();
            }
        });
        builderSingle.setPositiveButton("Adicionar novo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(getApplicationContext(), AdicionarEndereco.class), 1);
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (enderecos.isEmpty()){
                    startActivityForResult(new Intent(getApplicationContext(), AdicionarEndereco.class), 1);
                } else {
                    endereco = enderecos.get(which);
                    setar_endereco();
                }

            }
        });
        builderSingle.setCancelable(cancelable);
        builderSingle.show();
    }
    private void obter_dados_estabelecimento(){
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child(carinho_itemDBList.get(0).getTipoestabelecimento())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child("id").getValue(String.class).equals(carinhoitem.getEstabelecimentoid())) {
                                estabelecimento = dataSnapshot.getValue(Estabelecimento.class);
                                nome_estabelecimento.setText(estabelecimento.getNome());
                                slogan_estabelecimento.setText(estabelecimento.getSlogan());
                                Picasso.with(getApplicationContext()).load(estabelecimento.getUrl())
                                        .into(imagem_estabelecimento);
                                ender_loja.setText(estabelecimento.getEndereco());
                                configurarAdapter(dataSnapshot.getKey());
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }
    private void obter_endereco(){
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("Endereco")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (!enderecos.contains(dataSnapshot.getValue(Endereco.class))){
                            enderecos.add(dataSnapshot.getValue(Endereco.class));
                            if (endereco == null){
                                endereco = dataSnapshot.getValue(Endereco.class);
                                setar_endereco();
                            }

                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private void setar_endereco() {
        textviewEndereco.setText(endereco.getEndereco());
        cliente.setText(endereco.getNome());
        if (!endereco.getComplemento().isEmpty()){
            complemento.setText(String.format("%s - %s", endereco.getComplemento(), endereco.getNumero()));
        } else {
            complemento.setText(endereco.getNumero());
        }
        calcularFrete();
    }

    private void calcularFrete() {
        String latlon1[] = estabelecimento.getCoordenadas().split(",");
    }

    private void notificarEstabelecimento(final String type, final Pedidos pedido) {//notifica o estabelecimento sobre o pedido
            new Thread() {
                @Override
                public void run() {
                    JSONObject jPayload = new JSONObject();
                    JSONObject jNotification = new JSONObject();
                    JSONObject jData = new JSONObject();
                    try {
                        jNotification.put("title", carinhoitem.getNomeamostra());
                        jNotification.put("body", carinhoitem.getQuantidade() + "x " +
                                carinhoitem.getTitulo().replaceAll("¨", ", "));
                        jNotification.put("sound", "default");
                        jNotification.put("badge", "1");
                        jNotification.put("click_action", "OPEN_ACTIVITY_1");
                        jNotification.put("icon", "ic");

                        jData.put("action", "OPEN_ACTIVITY_1");
                        jData.put("data", pedido);


                        switch (type) {
                            case "tokens":
                                JSONArray ja = new JSONArray();
                                ja.put(token);
                                //ja.put(FirebaseInstanceId.getInstance().getToken());
                                jPayload.put("registration_ids", ja);
                                break;
                            case "topic":
                                jPayload.put("to", "/topics/pedidos." + estabelecimento.getId());
                                break;
                            case "condition":
                                jPayload.put("condition", "'sport' in topics || 'news' in topics");
                                break;
                            default:
                                jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
                        }

                        jPayload.put("priority", "high");
                        jPayload.put("notification", jNotification);
                        jPayload.put("data", jData);

                        URL url = new URL("https://fcm.googleapis.com/fcm/send");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Authorization", "key=" + ConfirmacaoCompra.AUTH_KEY);
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
    private void remover_carinho() {
        Carinho_itemDB.deleteAll(Carinho_itemDB.class,
                "codigo = ? and id = ?", carinhoitem.getCodigo(), carinhoitem.getid());
    }
    class ImageSwitcherPicasso implements Target {

        private ImageSwitcher mImageSwitcher;
        private Context mContext;

        public ImageSwitcherPicasso(Context context, ImageSwitcher imageSwitcher){
            mImageSwitcher = imageSwitcher;
            mContext = context;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            mImageSwitcher.setImageDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {

        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (!enderecos.contains((Endereco) data.getSerializableExtra("endereco"))){
                    enderecos.add((Endereco) data.getSerializableExtra("endereco"));
                }
                listar_enderecos(endereco != null);
            } else if (resultCode == RESULT_CANCELED && endereco == null){
                listar_enderecos(false);
            }

        } else if (requestCode == 2){//resultado da tela de login
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(getApplicationContext(), "Você não pode comprar se não criar uma conta no nosso serviço", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                userName = sharedPreferences.getString("nome", "");
                userId = sharedPreferences.getString("userid", "");
                obter_endereco();
            }
        }
    }
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }
    @Override
    public void onResume(){
        super.onResume();

    }

}