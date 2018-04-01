package rd.com.vendedor.adm.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.item.Amostras;
import rd.com.vendedor.adm.utils.Constants;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Adicionar_amostras extends AppCompatActivity {
    TextInputEditText titulo, url;
    TextInputLayout layout_titulo, layout_url;
    Amostras amostras;
    Button apagar;
    ImageButton adc_img;
    ImageView img;
    Spinner spinner;
    boolean edit = false;
    ProgressDialog progressDialog;
    String estabelecimento, estabelecimento_id, tipo_estabelecimento, cidadecode, cidade;//essas  serão as identificacoes de cada estabelecimento,
    //que serao obtidas no login e salvas na memoria do dispositivo

    Uri caminho_imagem;
    String patch;
    boolean add_imagem = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_amostras);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Adicionar Amostra");

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }

        img = findViewById(R.id.imageView6);
        adc_img = findViewById(R.id.abrir_img);
        apagar = findViewById(R.id.apagar);
        titulo = findViewById(R.id.titulo);
        url = findViewById(R.id.url);
        layout_url = findViewById(R.id.input_layout_url);
        layout_titulo = findViewById(R.id.input_titulo);
        spinner = findViewById(R.id.spinner2);

        amostras = new Amostras();
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals("edit")) {
                edit = true;
                amostras.setTitulo(getIntent().getStringExtra("titulo"));
                amostras.setUrl(getIntent().getStringExtra("url"));
                amostras.setQuantidade(getIntent().getIntExtra("quantidade", 0));
                amostras.setCaminho(getIntent().getStringExtra("caminho"));
                titulo.setText(amostras.getTitulo());
                url.setText(amostras.getUrl());
                Picasso.with(getApplicationContext()).load(amostras.getUrl()).into(img);
            }
        } else {
          apagar.setVisibility(View.GONE);
        }
        if (getIntent().getStringExtra(Constants.nomeEstabelecimento) != null){
            estabelecimento = getIntent().getStringExtra(Constants.nomeEstabelecimento);
            estabelecimento_id = getIntent().getStringExtra(Constants.idEstabelecimento);
            SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
            tipo_estabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
            cidadecode = sharedPreferences.getString(Constants.cidadeCode, "84623000");
            cidade = sharedPreferences.getString(Constants.cidade, "84623000");

        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (checar_campos()){
                   adc();
                   progressDialog = ProgressDialog.show(Adicionar_amostras.this, "",
                           "Adicionando amostra, aguarde...", true);
               } else {
                   Snackbar.make(view, "Todos os campos são obrigatórios", Snackbar.LENGTH_SHORT).show();
               }
            }
        });

        apagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog(v);
            }
        });

        final List<String> arrayList = new ArrayList<String>();//todo obter categorias do servidor
        if (tipo_estabelecimento.equals("lanchonetes")) {
            arrayList.add("Aperitivos");
            arrayList.add("Sanduiches");
            arrayList.add("Salgados");
            arrayList.add("Doces");
            arrayList.add("Bebidas");
            arrayList.add("Bebidas Alcoolicas");
            arrayList.add("Frios");
            arrayList.add("Fritos");
            arrayList.add("Pratos Prontos");
            arrayList.add("Outros");
        } else if (tipo_estabelecimento.equals("restaurantes")){
            arrayList.add("Pratos");
            arrayList.add("Porções e Entradas");
            arrayList.add("Petiscos");
            arrayList.add("Carnes");
            arrayList.add("Massas");
            arrayList.add("Saladas");
            arrayList.add("Sobremessas");
            arrayList.add("Lanches");
            arrayList.add("Bebidas");
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        adc_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obter_imagem();
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            permissoes(Adicionar_amostras.this);//checa se o app possui a devida permissão
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            caminho_imagem = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(caminho_imagem, filePathColumn,
                    null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            patch = cursor.getString(columnIndex);
            cursor.close();
            img.setImageBitmap(BitmapFactory.decodeFile(patch));
            add_imagem = true;
            url.setText("gs://pediupagou-start.appspot.com//{id_produto}.jpg");
            url.setEnabled(false);//desabilita
        }
    }
    private void up(final String key){
        Uri file = Uri.fromFile(new File(patch));
        UploadTask uploadTask;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference im = storageRef.child(tipo_estabelecimento + "/amostras/" + key + ".jpg");

        uploadTask = im.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                exception.printStackTrace();
                Snackbar.make(img, "Ocorreu um erro ao salvar a imagem :/", Snackbar.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                FirebaseFirestore.getInstance()
                        .collection("estabelecimentos")
                        .document(Constants.cidade)
                        .collection(cidadecode)
                        .document(tipo_estabelecimento)
                        .collection("amostras")
                        .document(key)
                        .update("caminho", key,  "url", downloadUrl.toString())

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Snackbar.make(titulo, "Amostra adicionada com sucesso", Snackbar.LENGTH_SHORT).show();
                        titulo.setText("");
                        url.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Snackbar.make(titulo, "OPs, algo saiu errado", Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
    private void obter_imagem(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }
    private void permissoes(final Activity activity) {
        String[] PERMISSIONS_STORAGE = new String[0];
            PERMISSIONS_STORAGE = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE
            };
        // Se não possui permissão
        if (ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_EXTERNAL_STORAGE)) {
                // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
            } else {
                // Solicita a permissão
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 0);

            }
        }
    }
    private void adc() {
        amostras = new Amostras(titulo.getText().toString(), url.getText().toString(), spinner.getSelectedItem().toString());
        amostras.setNome_estabelecimento(estabelecimento);
        amostras.setId_estabelecimento(estabelecimento_id);
        amostras.setTipo_estabelecimento(tipo_estabelecimento);
        amostras.setCidadecode(cidadecode);
        amostras.setCidade(cidade);
        try {
            amostras.setUserid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } catch (Exception e){
            e.printStackTrace();
        }

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(tipo_estabelecimento)
                .collection("amostras")
                .add(amostras).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                up(documentReference.getId());//upa a imagem no firebaseStorage
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Snackbar.make(titulo, "Ocorreu uma falha ao adicionar a amostra", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    private void dialog(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog);
        AlertDialog alerta;
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance()//Todo nao esta apagando
                        .collection("estabelecimentos")
                        .document(Constants.cidade)
                        .collection(cidadecode)
                        .document(tipo_estabelecimento)
                        .collection("amostras")
                        .document(amostras.getCaminho())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(view, "Amostra removida com sucesso!", Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        if (e.getMessage().equals("PERMISSION_DENIED: Missing or insufficient permissions.")){
                            Snackbar.make(view, "Parece que você não tem permissão de remover essa amostracode", Snackbar.LENGTH_SHORT).show();
                        } else
                        Snackbar.make(view, "Ocorreu uma falha ao remover a amostracode", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alerta = builder.create();
        alerta.setTitle("Aviso");
        alerta.setMessage("Tem certeza que deseja remover a amostracode?");
        alerta.show();
    }
    private boolean checar_campos() {
        if (titulo.getText().toString().length() < 1){
            layout_titulo.setErrorEnabled(true);
            return false;
        } else if (url.getText().toString().length() < 1){
            return false;
        } else {
            return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (edit) {
            getMenuInflater().inflate(R.menu.add_amostras, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (edit) {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.apagar) {
                dialog(titulo);
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
