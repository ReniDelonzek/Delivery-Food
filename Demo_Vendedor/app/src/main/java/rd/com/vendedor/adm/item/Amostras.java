package rd.com.vendedor.adm.item;

import java.io.Serializable;

/**
 * Created by Reni on 04/02/2018.
 */

public class Amostras implements Serializable{
    private String titulo, url, caminho, userid,//o caminho é o id da amostra
    categoria, nome_estabelecimento, id_estabelecimento, tipo_estabelecimento;
    private int quantidade = 0;//quantidade de produtos nessa amostra, caso não seja inserido nenhum, o padrao é 0
    private String cidade, cidadecode;



    public Amostras(){

    }
    public Amostras(String titulo, String url, String categoria){
        this.titulo = titulo;
        this.url = url;
        this.categoria = categoria;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCidadecode() {
        return cidadecode;
    }

    public void setCidadecode(String cidadecode) {
        this.cidadecode = cidadecode;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTipo_estabelecimento() {
        return tipo_estabelecimento;
    }

    public void setTipo_estabelecimento(String tipo_estabelecimento) {
        this.tipo_estabelecimento = tipo_estabelecimento;
    }

    public String getId_estabelecimento() {
        return id_estabelecimento;
    }
    public String getNome_estabelecimento() {
        return nome_estabelecimento;
    }
    public void setId_estabelecimento(String id_estabelecimento) {
        this.id_estabelecimento = id_estabelecimento;
    }
    public void setNome_estabelecimento(String nome_estabelecimento) {
        this.nome_estabelecimento = nome_estabelecimento;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
    public String getCaminho() {
        return caminho;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
