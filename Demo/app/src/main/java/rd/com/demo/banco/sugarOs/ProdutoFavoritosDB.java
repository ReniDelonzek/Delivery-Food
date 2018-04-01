package rd.com.demo.banco.sugarOs;

import com.orm.SugarRecord;

import java.io.Serializable;

public class ProdutoFavoritosDB extends SugarRecord<ProdutoFavoritosDB> implements Serializable{
    private String codigo;//caminho
    private String nome;
    private String descricao;
    private double preco;
    private String time;//momento que o produto foi adicionado
    private String categoria;
    private String amostra;//caminho amosta ao qual o produto esta vinculado
    private String estabelecimento, estabelecimentoid, tipoestabelecimento;
    private String url;
    private String cidade;
    private String cidadecode;


    public ProdutoFavoritosDB(){}


    public ProdutoFavoritosDB(String codigo, String nome, String descricao, double preco, String time, String categoria, String amostra, String estabelecimento, String estabelecimentoid, String tipoestabelecimento, String url) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.time = time;
        this.categoria = categoria;
        this.amostra = amostra;
        this.estabelecimento = estabelecimento;
        this.estabelecimentoid = estabelecimentoid;
        this.tipoestabelecimento = tipoestabelecimento;
        this.url = url;
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

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setEstabelecimentoid(String estabelecimentoid) {
        this.estabelecimentoid = estabelecimentoid;
    }
    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }
    public String getEstabelecimentoid() {
        return estabelecimentoid;
    }
    public String getEstabelecimento() {
        return estabelecimento;
    }
    public String getAmostra() {
        return amostra;
    }
    public void setAmostra(String amostra) {
        this.amostra = amostra;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getNome() {
        return nome;
    }
    public String getCodigo() {
        return codigo;
    }
    public String getDescricao() {
        return descricao;
    }
    public double getPreco() {
        return preco;
    }
    public void setPreco(double preco) {
        this.preco = preco;
    }
}

