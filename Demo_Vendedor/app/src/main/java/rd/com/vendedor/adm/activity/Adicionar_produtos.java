

package rd.com.vendedor.adm.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.item.Produto;
import rd.com.vendedor.adm.utils.Constants;


public class Adicionar_produtos extends AppCompatActivity {
    private static final String TAG = "Adicionar_produtos";
    EditText nome, descricao, preco;
    Button button;
    Produto produtos;
    String amostracode, categoria, amostranome;
    int quant;
    String url;
    String estabelecimento, estabelecimento_id, tipoEstabelecimento, cidadecode, cidade;//essas  serão as identificacoes de cada estabelecimento,
    //que serao obtidas no login e salvas na memoria do dispositivo
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Adicionar Produtos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nome = findViewById(R.id.produto);
        descricao = findViewById(R.id.descricao);
        preco = findViewById(R.id.preco);
        button  = findViewById(R.id.search_categoria);
        produtos = new Produto();

        SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
        tipoEstabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        cidade = sharedPreferences.getString(Constants.cidade, "");
        cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");


        FloatingActionButton fab = findViewById(R.id.fab);
        if (getIntent().getAction().equals("add_from_amostra")){
            amostracode = getIntent().getStringExtra("amostracode");
            amostranome = getIntent().getStringExtra("amostranome");

            estabelecimento = getIntent().getStringExtra(Constants.nomeEstabelecimento);
            estabelecimento_id = getIntent().getStringExtra(Constants.idEstabelecimento);
            cidadecode = getIntent().getStringExtra(Constants.cidadeCode);

            categoria = getIntent().getStringExtra("categoria");
            quant = getIntent().getIntExtra("quantidade", 0);
            url = getIntent().getStringExtra("url");
            button.setVisibility(View.GONE);
            button.setClickable(false);

        } else if (getIntent().getAction().equals("edit")){
            Produto produto = (Produto) getIntent().getSerializableExtra("produto");
            amostracode = produto.getAmostra();
            estabelecimento = produto.getEstabelecimento();
            estabelecimento_id = produto.getEstabelecimento_id();
            cidadecode = produto.getCidadecode();
            categoria = produto.getCategoria();
            quant = getIntent().getIntExtra("quantidade", 0);
            url = produto.getUrl();

            button.setVisibility(View.GONE);
            button.setClickable(false);

            nome.setText(produto.getNome());
            descricao.setText(produto.getDescricao());
            preco.setText(String.format("R$%s", String.format(Locale.getDefault(), "%.2f", produto.getPreco())));
        }



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checar_dados()){
                    Snackbar.make(view, "Verifique os dados, todos os campos são obrigatórios", Snackbar.LENGTH_LONG).show();
                } else {
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();
                    produtos = new Produto("", nome.getText().toString(), descricao.getText().toString(),
                            Double.parseDouble(preco.getText().toString()), categoria);
                    produtos.setTime(ts);
                    produtos.setAmostra(amostracode);
                    produtos.setEstabelecimento(estabelecimento);
                    produtos.setEstabelecimento_id(estabelecimento_id);
                    produtos.setTipo_estabelecimento(tipoEstabelecimento);
                    produtos.setUrl(url);
                    produtos.setCidade(cidade);
                    produtos.setCidadecode(cidadecode);
                    produtos.setNomeamostra(amostranome);

                    if (!getIntent().getAction().equals("edit")){
                        dialog = ProgressDialog.show(Adicionar_produtos.this, "",
                                "Adicionando produto, aguarde...", true);
                        adicionar_produto(produtos);
                    } else {
                        dialog = ProgressDialog.show(Adicionar_produtos.this, "",
                                "Atualizando produto, aguarde...", true);
                        atualizarProduto();
                    }

                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Escolher_amostra.class);
                intent.putExtra("categoria", "Sorvetes");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void atualizarProduto() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(tipoEstabelecimento)
                .collection(estabelecimento_id)
                .document("produtos")
                .collection("produtos")
                .document(produtos.getCodigo())
                .update(
                        "nome", nome.getText().toString(), "descricao",
                        descricao.getText().toString(),
                        "preco", preco.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(nome, "Produto atualizado com sucesso", Snackbar.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Snackbar.make(nome, "Falha ao atualizar o produto", Snackbar.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private boolean checar_dados() {
        return nome.getText().toString().length() >= 1 &&
                descricao.getText().toString().length() >= 2 &&
                preco.getText().toString().length() >= 1;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            produtos.setAmostra(data.getStringExtra("resposta"));
        }
    }
    private void adicionar_produto(final Produto produtos){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(tipoEstabelecimento)
                .collection(estabelecimento_id)
                .document("produtos")
                .collection("produtos")
                .add(produtos)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.v(TAG, documentReference.getId());//obtem o id do produto gerado automaticamente pelo servidor
                        db.collection("estabelecimentos")
                                .document(Constants.cidade)
                                .collection(cidadecode)
                                .document(tipoEstabelecimento)
                                .collection(estabelecimento_id)
                                .document("produtos")
                                .collection("produtos")
                                .document(documentReference.getId())
                                .update("codigo", documentReference.getId())//adiciona o id as informacoes do produto
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Snackbar.make(nome, "Produto adicionado com sucesso :)", Snackbar.LENGTH_SHORT).show();
                                atualizar_amostra();
                                nome.setText("");
                                descricao.setText("");
                                preco.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                e.printStackTrace();
                                Snackbar.make(nome, "Opss, alguma coisa saiu errado, por favor tente de novo", Snackbar.LENGTH_SHORT).show();
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                e.printStackTrace();
                Snackbar.make(nome, "Opss, alguma coisa saiu errado, por favor tente de novo",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizar_amostra() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(tipoEstabelecimento)
                .collection("amostras")
                .document(amostracode)
                .update("quantidade", ++quant)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    Log.v(TAG, "Amostra atualizada com sucesso");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

}
