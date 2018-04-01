package rd.com.demo.item;

import android.content.Context;

import rd.com.demo.auxiliares.Constants;
import rd.com.demo.auxiliares.LibraryClass;

/**
 * Created by renid on 03/06/2016.
 */
public class Usuario {

    private String name;
    private String email;
    private String id;
    private String token;
    private String CPF;
    private boolean numverificado;

    public Usuario(String name, String email, String id, String token, String CPF, boolean numverificado) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.token = token;
        this.CPF = CPF;
        this.numverificado = numverificado;
    }

    public Usuario(){}

    public boolean isNumverificado() {
        return numverificado;
    }

    public void setNumverificado(boolean numverificado) {
        this.numverificado = numverificado;
    }

    public String getCPF() {
        return CPF;
    }
    public void setCPF(String CPF) {
        this.CPF = CPF;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){this.id = id;}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public static void salvarDadosLocal(Context context,
                                        String nome, String Email, String id, String token) {
        LibraryClass.saveSP(context, Constants.nomeUsuarioLocal, nome);
        LibraryClass.saveSP(context, Constants.emailUsuarioLocal, Email);
        LibraryClass.saveSP(context, Constants.idUsuarioLocal, id);
        LibraryClass.saveSP(context, Constants.token, token);

    }

}
