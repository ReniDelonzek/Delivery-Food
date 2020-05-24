package rd.com.demo.item.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;


/**
 * Created by Reni on 08/02/2018.
 */

@IgnoreExtraProperties
public class Endereco implements Serializable {
    private String endereco, complemento, bairro, numero, nome,
            apelido, cidade, estado, estadosigla, lat, longi;

    public Endereco(){}
    public Endereco(String endereco, String complemento, String bairro, String numero, String nome, String apelido,
                    String cidade, String estado, String estadosigla, String lat, String longi){
        this.bairro = bairro;
        this.complemento = complemento;
        this.endereco = endereco;
        this.numero = numero;
        this.nome = nome;
        this.apelido = apelido;
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
