package rd.com.demo.item.firebase;

import java.io.Serializable;

public class Produto implements Serializable{
    private String codigo;//caminho
    private String nome;
    private String descricao;
    private double preco;
    private String time;//momento que o produto foi adicionado
    private String categoria;
    private String amostra;//caminho amosta ao qual o produto esta vinculado
    private String estabelecimento, estabelecimento_id, tipo_estabelecimento;
    private String url;
    private String cidade;
    private String cidadecode;
    private String nomeamostra;
    private int caminho;//caminho local da imagem em resources

    public Produto(){}

    public Produto(String codigo, String nome, String descricao, double preco, String time, String categoria, String amostra, String estabelecimento, String estabelecimento_id, String tipo_estabelecimento, String url, String cidade, String cidadecode, String nomeamostra, int caminho) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.time = time;
        this.categoria = categoria;
        this.amostra = amostra;
        this.estabelecimento = estabelecimento;
        this.estabelecimento_id = estabelecimento_id;
        this.tipo_estabelecimento = tipo_estabelecimento;
        this.url = url;
        this.cidade = cidade;
        this.cidadecode = cidadecode;
        this.nomeamostra = nomeamostra;
        this.caminho = caminho;
    }

    public String getNomeamostra() {
        return nomeamostra;
    }

    public void setNomeamostra(String nomeamostra) {
        this.nomeamostra = nomeamostra;
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

    public String getTipo_estabelecimento() {
        return tipo_estabelecimento;
    }

    public void setTipo_estabelecimento(String tipo_estabelecimento) {
        this.tipo_estabelecimento = tipo_estabelecimento;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setEstabelecimento_id(String estabelecimento_id) {
        this.estabelecimento_id = estabelecimento_id;
    }
    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }
    public String getEstabelecimento_id() {
        return estabelecimento_id;
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

    public int getCaminho() {
        return caminho;
    }

    public void setCaminho(int caminho) {
        this.caminho = caminho;
    }
}

