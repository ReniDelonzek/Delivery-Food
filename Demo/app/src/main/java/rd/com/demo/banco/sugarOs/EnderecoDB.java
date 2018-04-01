package rd.com.demo.banco.sugarOs;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;


/**
 * Created by Reni on 08/02/2018.
 */

@IgnoreExtraProperties
public class EnderecoDB extends SugarRecord<EnderecoDB> {
    private String endereco, complemento, bairro, numero, nome;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private long time;

    public EnderecoDB(){}
    public EnderecoDB(String endereco, String complemento, String bairro, String numero, String nome){
        this.bairro = bairro;
        this.complemento = complemento;
        this.endereco = endereco;
        this.numero = numero;
        this.nome = nome;
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
