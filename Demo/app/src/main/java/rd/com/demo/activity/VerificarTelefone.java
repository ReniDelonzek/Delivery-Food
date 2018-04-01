package rd.com.demo.activity;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rd.com.demo.R;
import rd.com.demo.item.Usuario;

public class VerificarTelefone extends AppCompatActivity {
    TextInputEditText telefone, codigo;
    TextInputLayout l_telefone, l_codtelefone, l_codigo;
    Button verificar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private final String TAG = "VerificarTelefone";
    FirebaseAuth mAuth;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    boolean codigoEnviado;
    Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_telefone);

        telefone = findViewById(R.id.telefone);
        codigo = findViewById(R.id.codigo);
        verificar = findViewById(R.id.verificar);
        l_codtelefone = findViewById(R.id.textInputLayout6);
        l_telefone = findViewById(R.id.textInputLayout_numero);
        l_codigo = findViewById(R.id.inputCodigo);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("pt");
        obterDadosUsuario();

        verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!codigoEnviado) {
                    if (telefone.getText().toString().length() > 10) {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+55" +telefone.getText().toString(), // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                VerificarTelefone.this,               // Activity (for callback binding)
                                mCallbacks);
                        codigoEnviado = true;
                        l_telefone.setVisibility(View.GONE);
                        l_codtelefone.setVisibility(View.GONE);
                        l_codigo.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (codigo.getText().toString().equals(verificationId)){
                        atualizarStatus();
                    } else {
                        Snackbar.make(v, "O codigo informado não corresponde ao enviado", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                atualizarStatus();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request

                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                codigoEnviado = false;
                Snackbar.make(telefone, "Ocorreu uma falha ao verificar seu número", Snackbar.LENGTH_SHORT).show();
                l_codigo.setVisibility(View.GONE);
                l_telefone.setVisibility(View.VISIBLE);
                l_codtelefone.setVisibility(View.VISIBLE);
                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String Id,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + Id);

                // Save verification ID and resending token so we can use them later
                verificationId = Id;
                mResendToken = token;


                // ...
            }
            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId){
                codigoEnviado = false;
                Snackbar.make(telefone, "Tempo limite para SMS esgotado", Snackbar.LENGTH_SHORT).show();
                l_codigo.setVisibility(View.GONE);
                l_telefone.setVisibility(View.VISIBLE);
                l_codtelefone.setVisibility(View.VISIBLE);
            }

        };

    }

    private void atualizarStatus(){
        Map<String, Object> map1 = new HashMap<>();
        map1.put("numverificado", true);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/status/" , map1);

        FirebaseDatabase.getInstance().getReference()
                .updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(verificar, "Número verificado com sucesso", Snackbar.LENGTH_SHORT).show();
                tempo(3000);
            }
        });
    }
    private void obterDadosUsuario() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuario = dataSnapshot.getValue(Usuario.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private void tempo(final int time){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              finish();
                //
            }
        }, time);
    }
}
