package rd.com.vendedor.adm.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.utils.Constants;

public class ReAutenticar extends AppCompatActivity {
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_autenticar);

        final TextInputLayout inputLayout = findViewById(R.id.textInputLayout2);
        final TextInputEditText editText = findViewById(R.id.senha);
        Button button = findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() < 1){
                    inputLayout.setError("Insira sua senha");
                } else {
                    hideKeyboard();
                    SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
                    String email = sharedPreferences.getString(Constants.email, "");//obter
                    final String tipo = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
                    dialog = ProgressDialog.show(ReAutenticar.this, "",
                            "Verificando dados, aguarde...", true);

                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email, editText.getText().toString())//todo adicionar email salvo do adm
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseFirestore.getInstance()
                                            .collection("Adm")
                                            .document(tipo)
                                            .collection("info")
                                            .document("Adm")
                                            .collection("ids")
                                            .document(authResult.getUser().getUid())
                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            setResult(RESULT_OK);
                                            finish();
                                        }}).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            e.printStackTrace();
                                            Snackbar.make(editText, "A senha inserida não confere com sua conta de Adm", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                         e.printStackTrace();
                            Snackbar.make(editText, "Ouve uma falha ao fazer o login, tente novamente", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        setResult(0);
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
