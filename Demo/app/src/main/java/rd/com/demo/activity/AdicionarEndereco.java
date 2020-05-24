package rd.com.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import rd.com.demo.R;
import rd.com.demo.banco.sugarOs.EnderecoDB;
import rd.com.demo.item.Usuario;
import rd.com.demo.item.firebase.Endereco;

public class AdicionarEndereco extends AppCompatActivity {
    TextInputEditText endereco, complemento, numero, bairro, nome, apelido;
    TextInputLayout ly_endereco, ly_complemento, ly_numero, ly_bairro;
    CheckBox sn;
    Button add, pular;
    Usuario usuario;
    Button mapa;
    String lat, longi;
    TextView textViewCidade, textViewEstado;
    String estadoSigla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_endereco);
        setTitle("Adicionar endereço");

        usuario = new Usuario();
        endereco = findViewById(R.id.endereco);
        complemento = findViewById(R.id.complemento);
        numero = findViewById(R.id.numero);
        sn = findViewById(R.id.checkBox3);
        add = findViewById(R.id.button5);
        pular = findViewById(R.id.pular);
        bairro = findViewById(R.id.bairro);
        nome = findViewById(R.id.nome);
        apelido = findViewById(R.id.apelido);
        mapa = findViewById(R.id.button11);
        ly_endereco = findViewById(R.id.textInputLayout);
        ly_complemento = findViewById(R.id.textInputLayout2);
        ly_numero = findViewById(R.id.textInputLayout3);
        ly_bairro = findViewById(R.id.textInputLayout4);
        textViewCidade = findViewById(R.id.spinner3);
        textViewEstado = findViewById(R.id.spinner2);


        if (getIntent().getStringExtra("userid") != null){
         usuario.setId(getIntent().getStringExtra("userid"));
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            usuario.setId(sharedPreferences.getString("userid", ""));
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checar_campos()){
                    if (sn.isChecked()){
                        numero.setText("s/n");
                    }
                    salvar_endereco();

                }
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), ComecarCadastro.class));
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            String nom = sharedPreferences.getString("nome", "");//obter
            nome.setText(nom);
        }
        pular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    numero.setText("s/n");
                }
            }
        });
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                Endereco ender = (Endereco) data.getSerializableExtra("endereco");
                lat = ender.getLat();
                longi = ender.getLongi();
                ly_endereco.setVisibility(View.VISIBLE);
                ly_complemento.setVisibility(View.VISIBLE);
                ly_numero.setVisibility(View.VISIBLE);
                ly_bairro.setVisibility(View.VISIBLE);
                textViewEstado.setVisibility(View.VISIBLE);
                textViewCidade.setVisibility(View.VISIBLE);
                sn.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);

                endereco.setText(ender.getEndereco());
                bairro.setText(ender.getBairro());
                if (ender.getNumero() != null) {
                    if (!ender.getNumero().contains("-")) {
                        numero.setText(ender.getNumero());
                    }
                }


                mapa.setText("Redefinir Endereço");
                textViewCidade.setText(ender.getCidade());
                textViewEstado.setText(ender.getEstado());
                estadoSigla = ender.getEstadosigla();

            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        setResult(RESULT_CANCELED);
    }
    private void salvar_endereco() {
        Endereco ender = new Endereco(endereco.getText().toString(), complemento.getText().toString(),
                bairro.getText().toString(), numero.getText().toString(), nome.getText().toString(),
                apelido.getText().toString(), textViewCidade.getText().toString(), textViewEstado.getText().toString(),
                estadoSigla, lat, longi);
        Intent intent = new Intent();
        intent.putExtra("endereco", ender);
        setResult(RESULT_OK, intent);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(usuario.getId())
                .child("Endereco")
                .push()
                .setValue(ender).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Snackbar.make(nome, "Endereço adicionado com sucesso", Snackbar.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //startActivity(intent);
                        finish();
                    }
                }, 1500);

            }
        });
        EnderecoDB en = new EnderecoDB(endereco.getText().toString(),
                complemento.getText().toString(), bairro.getText().toString(),
                numero.getText().toString(), nome.getText().toString(), "", "", "",
                lat, longi);
        en.setTime(System.currentTimeMillis());
        en.save();
    }
    private boolean checar_campos() {
        if (nome.getText().toString().length() < 1){
            nome.setError("Preencha o nome da pessoa que irá receber");
            return false;
        }
        if (endereco.getText().toString().length() < 1){
            endereco.setError("Insira um endereço válido");
            return false;
        } else if (bairro.getText().toString().length() < 1){
            bairro.setError("Não deixe em branco");
            return false;
        } else if (apelido.getText().toString().length() < 1){
            apelido.setError("Insira um apelido para identificar o endereço");
            return false;
        }
        else if (numero.getText().length() < 1 && !sn.isChecked()){
            numero.setError("Insira um numero válido");
            Snackbar.make(numero, "Insira o número ou marque a opção ao lado caso não tiver", Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
