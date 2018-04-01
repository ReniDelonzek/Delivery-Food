package rd.com.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rd.com.demo.R;
import rd.com.demo.auxiliares.CPFEditText;
import rd.com.demo.item.Usuario;


public class ConcluirCadastro extends AppCompatActivity {
    String nome, email;
    FirebaseAuth.AuthStateListener registro;
    FirebaseAuth autFb;
    TextInputEditText nom, senha1, senha2, Edemail;
    IndeterminateProgressButton btnMorph1;
    TextInputLayout textInputLayout_email, textInputLayout_senha1,
            textInputLayout_senha2, textInputLayout_nome, cpf_input;
    CPFEditText cpfEditText;
    int modoLogin = 0;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);
        Intent intent = getIntent();
        registro = adidionarLisneterMudancaDados();
        autFb = FirebaseAuth.getInstance();
        registarViews();
        setTitle("Cadastrar");
        getSupportActionBar().setElevation(0f);

        if (intent.getAction().equals("Social")) {
            nome = intent.getStringExtra("nome");
            email = intent.getStringExtra("email");
            if (nome.length() > 0){
                nom.setText(nome);
            }
            if (email.length() > 0){
                if (validateEmail(email) && email.contains("@") && email.contains(".")){
                    textInputLayout_email.setVisibility(View.GONE);
                    Edemail.setText(email);
                }
            }
        }
        btnMorph1 = (IndeterminateProgressButton) findViewById(R.id.btnMorph1);
        btnMorph1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textInputLayout_nome.setErrorEnabled(false);
                textInputLayout_senha1.setErrorEnabled(false);
                textInputLayout_senha2.setErrorEnabled(false);
                email = Edemail.getText().toString();
                if (modoLogin == 0) {
                    criarConta();
                } else {
                    if (email.length() > 0){
                        if (validateEmail(email) && email.contains("@") && email.contains(".")) {
                            if (senha1.getText().toString().length() > 5) {
                                    simulateProgress1(btnMorph1);
                                    hideKeyboard();
                                    Logar();
                            } else {
                                Toast.makeText(getApplicationContext(), "A senha precisa ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                                textInputLayout_senha1.setErrorEnabled(true);
                                textInputLayout_senha1.setError("A senha é muito curta");
                            }
                        } else {
                            textInputLayout_email.setErrorEnabled(true);
                            textInputLayout_email.setError("Formato email inválido");
                        }
                    } else{
                        textInputLayout_email.setErrorEnabled(true);
                        textInputLayout_email.setError("Email não pode ficar vazio");
                    }
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoLogin == 0) {
                    modoDeLogin();
                    modoLogin = 1;
                    login.setText("CRIAR CONTA");
                    btnMorph1.setText("LOGAR");
                } else if (modoLogin == 1){
                    btnMorph1.setText("CRIAR");
                    login.setText(getString(R.string.j_tem_conta_fa_a_o_login));
                    modoLogin = 0;
                    textInputLayout_nome.setVisibility(View.VISIBLE);
                    textInputLayout_senha2.setVisibility(View.VISIBLE);
                    textInputLayout_senha1.setHint("Crie uma senha");
                    cpfEditText.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    private void Logar(){
        autFb.signInWithEmailAndPassword(Edemail.getText().toString(), senha1.getText().toString())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        if (e.getMessage().equals("The password is invalid or the user does not have a password.")){//senha incoreta
                            senha1.setText("");
                            textInputLayout_senha1.setErrorEnabled(true);
                            textInputLayout_senha1.setError("Senha incorreta");
                            Snackbar.make(textInputLayout_email, "A senha inserida está incorreta", Snackbar.LENGTH_SHORT).show();
                        }

                        if (modoLogin == 0) {
                            modoLogin = 1;
                            modoDeLogin();
                            Toast.makeText(getApplicationContext(), "Você já possui uma conta com este email," +
                                    " por favor insira sua senha", Toast.LENGTH_LONG)
                                    .show();
                        }
                        modoDeLogin();
                        modoLogin = 1;
                        login.setText("CRIAR CONTA");
                        btnMorph1.setText("LOGAR");
                        senha1.setText("");
                        btnMorph1.unblockTouch();
                        morphToSquare(btnMorph1, 1000, "Logar");
                        //tempo(1000);
                    }
                });
    }
    private void modoDeLogin(){
        textInputLayout_senha2.setVisibility(View.GONE);
        textInputLayout_senha1.setHint("Sua Senha");
        cpfEditText.setVisibility(View.GONE);
        cpf_input.setVisibility(View.GONE);
    }
    private void criarConta(){
        if (nom.getText().length() > 1){
            if (senha1.length() > 5){
                if (senha1.getText().toString().equals(senha2.getText().toString())){
                    if (cpfEditText.getText().toString().length()> 11 && isCPF(cpfEditText.getText().toString())) {
                        simulateProgress1(btnMorph1);
                        hideKeyboard();
                        autFb.createUserWithEmailAndPassword(Edemail.getText().toString(), senha1.getText().toString())
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        if (e.getMessage().contains("The email address is already in use by another account.")) {
                                            Logar();
                                            Log.v("Senha", "Email ja cadastrado, fazendo Login");
                                        } else {
                                            morphToFailure(btnMorph1,
                                                    integer(R.integer.mb_animation));
                                            Toast.makeText(getApplicationContext(), "Falha ao criar conta", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                });
                    } else {
                      Snackbar.make(cpfEditText, "O CPF digitado é inválido", Snackbar.LENGTH_SHORT).show();
                      cpf_input.setErrorEnabled(true);
                      cpf_input.setError("Insira um CPF válido");
                    }
                } else {
                    textInputLayout_senha2.setErrorEnabled(true);
                    textInputLayout_senha2.setError("As senhas precisam ser iguais");
                    Toast.makeText(getApplicationContext(), "As senhas não coincidem ",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                textInputLayout_senha1.setErrorEnabled(true);
                textInputLayout_senha1.setError("A senha é muito curta");
                Toast.makeText(getApplicationContext(), "Senha muito curta, mínimo 6 caracteres",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            textInputLayout_nome.setErrorEnabled(true);
            textInputLayout_nome.setError("Você precisa colocar uma identificação");
            Toast.makeText(getApplicationContext(), "O nome não pode ficar em branco",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public boolean validateEmail(String email) {
        final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void morphToSquare(final IndeterminateProgressButton btnMorph, int duration
    , String text) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(dimen(R.dimen.mb_width_100))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_blue))
                .colorPressed(color(R.color.mb_blue_dark))
                .text(text);
        btnMorph.morph(square);
    }
    private void simulateProgress1(@NonNull final IndeterminateProgressButton button) {
        int progressColor1 = color(R.color.holo_blue_bright);
        int progressColor2 = color(R.color.holo_green_light);
        int progressColor3 = color(R.color.holo_orange_light);
        int progressColor4 = color(R.color.holo_red_light);
        int color = color(R.color.mb_gray);
        int progressCornerRadius = dimen(R.dimen.mb_corner_radius_4);
        int width = dimen(R.dimen.mb_width_200);
        int height = dimen(R.dimen.mb_height_8);
        int duration = integer(R.integer.mb_animation);
        button.blockTouch(); // prevent user from clicking while button is in progress
        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);
    }
    private void morphToSuccess(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer(R.integer.mb_animation))
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done_white_36dp);
        btnMorph.morph(circle);
    }
    private void morphToFailure(final IndeterminateProgressButton btnMorph, int duration) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_red))
                .colorPressed(color(R.color.mb_red_dark))
                .icon(R.drawable.ic_close_white_24dp);
        btnMorph.morph(circle);
    }
    private void registarViews(){
        nom = (TextInputEditText) findViewById(R.id.editText4);
        senha1 = (TextInputEditText) findViewById(R.id.editText2);
        senha2 = (TextInputEditText) findViewById(R.id.editText3);
        Edemail = (TextInputEditText) findViewById(R.id.text_input_edit_email);
        textInputLayout_email = (TextInputLayout) findViewById(R.id.text_input_layout_email);
        textInputLayout_nome = (TextInputLayout) findViewById(R.id.text_input_layout_nome);
        textInputLayout_senha1 = (TextInputLayout) findViewById(R.id.text_input_layout_senha1);
        textInputLayout_senha2 = (TextInputLayout) findViewById(R.id.text_input_layout_senha2);
        login = (Button) findViewById(R.id.bt_login);
        cpfEditText = findViewById(R.id.cpf);
        cpf_input = findViewById(R.id.textInputLayout5);

    }
    private void tempo(int time){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                morphToSquare(btnMorph1, 1000, "Logar");
            }
        }, time);
    }
    private FirebaseAuth.AuthStateListener adidionarLisneterMudancaDados() {
        return (new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                morphToSuccess(btnMorph1);
                final FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
                if (userFirebase != null ) {
                    SharedPreferences sharedP = getSharedPreferences("Id", Context.MODE_PRIVATE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Usuario usuario = new Usuario();
                    usuario.setEmail(userFirebase.getEmail());
                    usuario.setId(userFirebase.getUid());
                    usuario.setName(nome);
                    usuario.setToken(sharedP.getString("FirebaseToken", ""));
                    usuario.setCPF(cpfEditText.getText().toString());

                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("/users/" + userFirebase.getUid(), usuario);


                    reference.updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                    ///salva usuario no banco de dados no fire


                    SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nome", nom.getText().toString());
                    editor.apply();//editar
                    editor.putString("email", Edemail.getText().toString());
                    editor.apply();//editar
                    editor.putString("userid", userFirebase.getUid());
                    editor.apply();
                    editor.putString("CPF", cpfEditText.getText().toString());
                    editor.apply();

                    //salva dados de usuario localmente
                    String msg;
                    if (modoLogin == 0){
                        msg = "Conta criada com sucesso!";
                    } else {
                        msg = "Login realizado com sucesso";
                    }
                    Snackbar.make(btnMorph1, msg, Snackbar.LENGTH_SHORT).show();
                    ComecarCadastro.finish = true;

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnMorph1.unblockTouch();
                            if (modoLogin == 0){//conta criada
                                Intent intent = new Intent(getApplicationContext(), AdicionarEndereco.class);
                                Intent intent2 = new Intent(getApplicationContext(), VerificarTelefone.class);
                                startActivities(new Intent[]{intent, intent2});
                            } else {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userFirebase.getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child("status")
                                                        .child("numverificado").exists() &&
                                                        (Boolean) dataSnapshot.child("numverificado").getValue()){//verifica se o telefone ja foi verificado
                                                    //caso ja tenha, verifica se ja tem algum endereco adicionado
                                                    if (dataSnapshot.child("Endereco").exists()){//chama a main
                                                        finish();
                                                    } else {
                                                        Intent intent = new Intent(getApplicationContext(), AdicionarEndereco.class);
                                                        startActivity(intent);
                                                    }

                                                } else {//chama as duas atividadades
                                                    Intent intent = new Intent(getApplicationContext(), AdicionarEndereco.class);
                                                    Intent intent2 = new Intent(getApplicationContext(), VerificarTelefone.class);
                                                    startActivities(new Intent[]{intent, intent2});
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }


                            finish();
                        }
                    }, 1500);

                }
            };

        });
    }
    @Override
    public void onResume(){
        super.onResume();
        morphToSquare(btnMorph1, getResources().getInteger(R.integer.mb_animation), "CRIAR");
    }
    @Override
    protected void onStart() {
        super.onStart();
        autFb.addAuthStateListener(registro);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (registro != null) {
            autFb.removeAuthStateListener(registro);
        }
    }
    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }
    public int color(@ColorRes int resId) {
        return ContextCompat.getColor(
                getApplicationContext(), resId);
    }
    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }
    public static boolean isCPF(String CPF) {
        CPF = CPF.replaceAll("\\D", "");//Remove todos os caracteres que nao forem numeros
        Log.v("Cpf", CPF);

    // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") || CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int sm, i, r, num, peso;

    // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
    // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
    // converte o i-esimo caractere do CPF em um numero:
    // por exemplo, transforma o caractere '0' no inteiro 0
    // (48 eh a posicao de '0' na tabela ASCII)
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

    // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char)(r + 48);

    // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }
}

