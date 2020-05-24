package rd.com.demofuncionario.activity;

/**
 * Created by Reni on 26/03/2018.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.auxiliar.Constants;
import rd.com.demofuncionario.item.firebase.Funcionario;

@SuppressWarnings("ALL")
public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    Button entrar;
    TextInputEditText email, senha;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog dialog;
    ArrayAdapter<String> adapter;
    List<String> id;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        entrar = findViewById(R.id.entrar);
        spinner = findViewById(R.id.spinner);

        setTitle("Faça o Login");

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chegarCampos()){
                    if (id.isEmpty()){
                        Snackbar.make(v, "Por favor, aguarde os estabelecimentos serem carregados", Snackbar.LENGTH_SHORT).show();
                    } else {
                        hideKeyboard();
                        dialog = ProgressDialog.show(Login.this, "",
                                "Verificando dados, aguarde...", true);
                        logar();
                    }
                }
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String pp = spinner.getSelectedItem().toString();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    FirebaseFirestore.getInstance()
                            .collection("Adm")
                            .document(id.get(spinner.getSelectedItemPosition()))
                            .collection("funcionarios")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Funcionario funcionario = documentSnapshot.toObject(Funcionario.class);

                                SharedPreferences sharedPreferences =
                                        getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                String estabelecimentoId = documentSnapshot.get("estabelecimentoid").toString();
                                String estabelecimentoTipo = documentSnapshot.get("estabelecimentotipo").toString();
                                String cidade = documentSnapshot.get("cidade").toString();
                                String cidadecode = documentSnapshot.get("cidadecode").toString();

                                editor.putString(Constants.idEstabelecimento, estabelecimentoId).apply();
                                editor.putString(Constants.nomeEstabelecimento, documentSnapshot.get("estabelecimentonome").toString()).apply();
                                editor.putString(Constants.tipoEstabelecimento, estabelecimentoTipo).apply();

                                editor.putString(Constants.cidadeCode, cidadecode).apply();
                                editor.putString(Constants.cidade, cidade).apply();


                                editor.putString(Constants.nome, funcionario.getNome()).apply();
                                editor.putString(Constants.sobrenome, funcionario.getSobrenome()).apply();
                                editor.putString(Constants.id, firebaseAuth.getCurrentUser().getUid()).apply();
                                editor.putString(Constants.cargo, funcionario.getFuncao()).apply();
                                editor.putString(Constants.email, email.getText().toString()).apply();
                                salvar_token(firebaseAuth.getInstance().getCurrentUser().getUid(), estabelecimentoTipo,
                                        cidadecode, estabelecimentoId);

                                FirebaseMessaging.getInstance().subscribeToTopic("pedidos." + estabelecimentoId);
                                dialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                firebaseAuth.signOut();
                                dialog.dismiss();
                                Snackbar.make(entrar, "Ouve um erro ao recuperar as informações, tem certeza que você esta" +
                                        "fazendo login em conta Adm?", Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //if (e.getMessage().eq)
                            Log.e("Error getting document", e.getMessage());
                            e.printStackTrace();
                            FirebaseAuth.getInstance().signOut();
                            try {
                                dialog.dismiss();
                                Snackbar.make(entrar,
                                        "Acesso negado, tem certeza que está logando com uma conta de administrador?",
                                        Snackbar.LENGTH_LONG).show();
                                email.setText("");
                                senha.setText("");
                                email.requestFocus();
                            } catch (Exception r){
                                r.printStackTrace();
                            }
                        }
                    });
                }
            }
        };
        setupAdapter();
    }
    private void salvar_token(String uid, String tipo, String cidadecode, String estabelecimentoId) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        FirebaseFirestore.getInstance()
                .collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(tipo)
                .collection(estabelecimentoId)//recupera o id do estabelecimento
                .document("Funcionarios")
                .collection("Tokens")
                .add(map)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }
    private void setupAdapter(){
        id = new ArrayList<>();
        final List<String> arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arrayList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            arrayList.add(dataSnapshot.child("nome").getValue(String.class));
                            id.add(dataSnapshot.child("id").getValue(String.class));
                            adapter.notifyDataSetChanged();
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

        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("lanchonetes")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            arrayList.add(dataSnapshot.child("nome").getValue(String.class));
                            id.add(dataSnapshot.child("id").getValue(String.class));
                            adapter.notifyDataSetChanged();
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

        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("lanchonetes")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            arrayList.add(dataSnapshot.child("nome").getValue(String.class));
                            id.add(dataSnapshot.child("id").getValue(String.class));
                            adapter.notifyDataSetChanged();
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
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private void logar() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Snackbar.make(entrar, "Ocorreu uma falha na tentativa de login, tente novamente", Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
    }
    private boolean chegarCampos() {
        if (email.getText().toString().length() < 1){
            email.setError("Preencha com seu email de identificação");
            return false;
        } else if (!validateEmail(email.getText().toString())){
            Snackbar.make(email, "O email precisa seguir o formato 'email@exemplo.com'", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (senha.getText().toString().length() < 6){
            Snackbar.make(senha, "A senha precisa conter no mínimo 6 caracteres", Snackbar.LENGTH_SHORT).show();
            senha.setError("Senha muito curta");
            return false;
        } else {
            return true;
        }
    }
    public boolean validateEmail(String email) {
        final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
