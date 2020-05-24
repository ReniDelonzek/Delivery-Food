package rd.com.vendedor.adm.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.item.Funcionario;
import rd.com.vendedor.adm.utils.Constants;

public class Adc_funcionario extends AppCompatActivity {

    TextInputEditText nome, emai, sobrenome, numero, senha, senha2;
    FirebaseUser firebaseUser;
    String estabelecimento_id, estabelecimento_name, estabelecimento_tipo;
    ProgressDialog progressDialog;
    String funcId;
    String cidade, cidadecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adc_funcionario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Adicinar Funcionario");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificar_dados()){
                    criar_conta();
                    hideKeyboard();
                    progressDialog = ProgressDialog.show(Adc_funcionario.this, "",
                            "Criando usuario, aguarde...", true);
                }
            }
        });

        nome = findViewById(R.id.nome);
        emai = findViewById(R.id.email);
        sobrenome = findViewById(R.id.sobrenome);
        numero = findViewById(R.id.telefone);
        senha = findViewById(R.id.senha);
        senha2 = findViewById(R.id.senha2);
        numero.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
        estabelecimento_name = sharedPreferences.getString(Constants.nomeEstabelecimento, "");
        estabelecimento_id = sharedPreferences.getString(Constants.idEstabelecimento, "");
        estabelecimento_tipo = sharedPreferences.getString(Constants.tipoEstabelecimento, "");

        cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");
        cidade = sharedPreferences.getString(Constants.cidade, "");
    }

    private void criar_conta(){
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().
                createUserWithEmailAndPassword(emai.getText().toString(), senha.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firebaseUser = authResult.getUser();
                        funcId = authResult.getUser().getUid();
                        Log.v("Adc_funcionario", firebaseUser.getEmail());
                        startActivityForResult(new Intent(getApplicationContext(), ReAutenticar.class), 1);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                if (e.getMessage().equals("The email address is badly formatted.")){
                    emai.setError("Formato inválido");
                } else if (e.getMessage().equals("The email address is already in use by another account.")){
                    emai.setError("Use outro email");
                    Snackbar.make(nome, "Este email já está sendo usado, por favor use outro", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean verificar_dados(){
        if (nome.getText().toString().length() < 2){
            nome.setError("O nome precisa conter pelo menos duas letras");
            return false;
        } else if (emai.getText().toString().length() < 3 || !validateEmail(emai.getText().toString())) {
            emai.setError("Insira um email valido");
            Snackbar.make(emai, "O email precisa ser no formato 'exemplo@email.com'", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (sobrenome.getText().toString().length() < 1){
            sobrenome.setError("Campo obrigatório");
            return false;
        } else if (numero.getText().toString().length() < 10){
            numero.setError("O telefone precisa ser no formato (XX) X XXXX-XXXX");
            return false;
        } else if (senha.getText().toString().length() < 8){
            senha.setError("A senha precisa ter no minimo 8 caracteres");
            return false;
        } else if (!senha2.getText().toString().equals(senha.getText().toString())){
            senha2.setError("As senhas não coincidem");
            return false;
        }
        else {
            return true;
        }
    }
    private void adc_funcionario(){
        Map<String, String> data = obter_data();
        Funcionario funcionario = new Funcionario(emai.getText().toString(), nome.getText().toString(), sobrenome.getText()
        .toString(), estabelecimento_name, estabelecimento_id, estabelecimento_tipo, numero.getText().toString(), cidade, cidadecode,
                data.get("data") + "-" + data.get("hora"), data.get("data"),"1");

        FirebaseFirestore.getInstance()
                .collection("Adm")
                .document(estabelecimento_id)//id estabelecimento//
                .collection("funcionarios")
                .document(funcId)
                .set(funcionario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Funcionario " + nome.getText().toString() + " criado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Snackbar.make(nome, "Falha ao criar o funcionário", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    public boolean validateEmail(String email) {
        final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                adc_funcionario();
            } else if (resultCode == 0) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        }
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private Map<String, String> obter_data(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd", Locale.getDefault());
        SimpleDateFormat hora = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        Map<String, String> map = new HashMap<>();
        map.put("data", dateFormat.format(data_atual));
        map.put("hora", hora.format(data_atual));
        return map;
    }
}
