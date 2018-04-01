package rd.com.demo.banco.sugarOs;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Reni on 03/02/2018.
 */

public class Carinho_itemDB extends SugarRecord<Carinho_itemDB> implements Serializable {//nunca usar nenhum item com '_'
    private String titulo;
    private String descricao;
    private String tipoestabelecimento;
    private String estabelecimento;
    private String estabelecimentoid;
    private String codigo;
    private String url;
    private String quantidade;
    private String preco;
    public String data;
    private boolean check1, check2;
    private String cidadecode;
    private String cidade;
    private String nomeamostra;

    public Carinho_itemDB(){}


    public Carinho_itemDB(String titulo, String quantidade, String descricao, String estabelecimento,
                          String estabelecimentoid, String tipoestabelecimento, String codigo, String data, String preco,
                          String url){
        this.titulo = titulo;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.estabelecimento = estabelecimento;
        this.estabelecimentoid = estabelecimentoid;
        this.codigo = codigo;
        this.quantidade = quantidade;
        this.data = data;
        this.preco = preco;
        this.url = url;
        this.tipoestabelecimento = tipoestabelecimento;
    }

    public Carinho_itemDB(String titulo, String descricao, String tipoestabelecimento,
                          String estabelecimento, String estabelecimentoid, String codigo,
                          String url, String quantidade, String preco, String data,
                          boolean check1, boolean check2, String cidadecode, String cidade, String nomeamostra) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipoestabelecimento = tipoestabelecimento;
        this.estabelecimento = estabelecimento;
        this.estabelecimentoid = estabelecimentoid;
        this.codigo = codigo;
        this.url = url;
        this.quantidade = quantidade;
        this.preco = preco;
        this.data = data;
        this.check1 = check1;
        this.check2 = check2;
        this.cidadecode = cidadecode;
        this.cidade = cidade;
        this.nomeamostra = nomeamostra;
    }

    public String getNomeamostra() {
        return nomeamostra;
    }

    public void setNomeamostra(String nomeamostra) {
        this.nomeamostra = nomeamostra;
    }

    public String getCidadecode() {
        return cidadecode;
    }

    public void setCidadecode(String cidadecode) {
        this.cidadecode = cidadecode;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTipoestabelecimento() {
        return tipoestabelecimento;
    }
    public void setTipoestabelecimento(String tipoestabelecimento) {
        this.tipoestabelecimento = tipoestabelecimento;
    }
    public boolean isCheck1() {
        return check1;
    }
    public boolean isCheck2() {
        return check2;
    }
    public void setCheck1(boolean check1) {
        this.check1 = check1;
    }
    public void setCheck2(boolean check2) {
        this.check2 = check2;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    public String getPreco() {
        return preco;
    }
    public void setPreco(String preco) {
        this.preco = preco;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
    public String getEstabelecimento() {
        return estabelecimento;
    }
    public String getid() {
        return estabelecimentoid;
    }
    public void setEstabelecimento(String estabelecimento) {this.estabelecimento = estabelecimento;}
    public void setid(String id) {
        this.estabelecimentoid = id;
    }
    public String getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }
    public String getEstabelecimentoid() {
        return estabelecimentoid;
    }
    public void setEstabelecimentoid(String estabelecimentoid) {
        this.estabelecimentoid = estabelecimentoid;
    }

}
