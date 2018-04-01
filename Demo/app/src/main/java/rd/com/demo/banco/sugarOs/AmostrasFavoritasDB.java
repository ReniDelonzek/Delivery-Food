package rd.com.demo.banco.sugarOs;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Reni on 04/02/2018.
 */

public class AmostrasFavoritasDB extends SugarRecord<AmostrasFavoritasDB> implements Serializable{
    private String titulo, url, caminho,//o caminho e o id da amostra
    categoria, nome_estabelecimento, idestabelecimento, tipoestabelecimento, cidade;
    private int quantidade = 0;//quantidade de produtos nessa amostra, caso não seja inserido nenhum, o padrao é 0
    private String cidadecode;


    public AmostrasFavoritasDB(){

    }

    public AmostrasFavoritasDB(String titulo, String url, String caminho, String categoria,
                               String nome_estabelecimento, String idestabelecimento, int quantidade) {
        this.titulo = titulo;
        this.url = url;
        this.caminho = caminho;
        this.categoria = categoria;
        this.nome_estabelecimento = nome_estabelecimento;
        this.idestabelecimento = idestabelecimento;
        this.quantidade = quantidade;
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

    public String getTipoestabelecimento() {
        return tipoestabelecimento;
    }

    public void setTipoestabelecimento(String tipoestabelecimento) {
        this.tipoestabelecimento = tipoestabelecimento;
    }

    public String getIdestabelecimento() {
        return idestabelecimento;
    }
    public String getNome_estabelecimento() {
        return nome_estabelecimento;
    }
    public void setIdestabelecimento(String idestabelecimento) {
        this.idestabelecimento = idestabelecimento;
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
