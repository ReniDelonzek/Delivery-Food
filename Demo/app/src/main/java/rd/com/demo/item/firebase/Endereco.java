package rd.com.demo.item.firebase;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

import java.io.Serializable;


/**
 * Created by Reni on 08/02/2018.
 */

@IgnoreExtraProperties
public class Endereco implements Serializable {
    private String endereco, complemento, bairro, numero, nome, apelido;

    public Endereco(){}
    public Endereco(String endereco, String complemento, String bairro, String numero, String nome, String apelido){
        this.bairro = bairro;
        this.complemento = complemento;
        this.endereco = endereco;
        this.numero = numero;
        this.nome = nome;
        this.apelido = apelido;
    }

    public String toString(){
        return "Nome: " + nome + "\n"
                + "Endereço: "
                + endereco + "\n"
                + bairro + "\n"
                + complemento + "\n"
                + numero;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNumero() {
        return numero;
    }
    public String getBairro() {
        return bairro;
    }
    public String getComplemento() {
        return complemento;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
}
