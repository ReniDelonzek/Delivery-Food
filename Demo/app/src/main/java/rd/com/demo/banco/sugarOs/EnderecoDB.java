package rd.com.demo.banco.sugarOs;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;


/**
 * Created by Reni on 08/02/2018.
 */

@IgnoreExtraProperties
public class EnderecoDB extends SugarRecord<EnderecoDB> {
    private String endereco, complemento, bairro, numero, nome,
            cidade, estado, estadosigla, lat, longi;

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    private long time;


    public EnderecoDB(String endereco, String complemento, String bairro, String numero, String nome,
                      String cidade, String estado, String estadosigla, String lat, String longi){
        this.bairro = bairro;
        this.complemento = complemento;
        this.endereco = endereco;
        this.numero = numero;
        this.nome = nome;
        this.cidade = cidade;
        this.estado = estado;
        this.estadosigla = estadosigla;
        this.lat = lat;
        this.longi = longi;
    }

    public String toString(){
        return "Nome: " + nome + "\n"
                + "Endereço: "
                + endereco + "\n"
                + bairro + "\n"
                + complemento + "\n"
                + numero + "\n"
                + cidade + "\n"
                + estado + "\n"
                + estadosigla;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadosigla() {
        return estadosigla;
    }

    public void setEstadosigla(String estadosigla) {
        this.estadosigla = estadosigla;
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
