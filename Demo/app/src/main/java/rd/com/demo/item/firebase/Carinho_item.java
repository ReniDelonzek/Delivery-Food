package rd.com.demo.item.firebase;

import java.io.Serializable;

/**
 * Created by Reni on 03/02/2018.
 */

public class Carinho_item implements Serializable {//nunca usar nenhum item com '_'
    private String titulo, descricao, estabelecimento, estabebelecimentoid, codigo;
    private int quantidade;
    private double preco;
    private String data, pedidoid, status;
    private String cidadecode;
    private String cidade;
    private String nomeamostra;


    public Carinho_item(){}

    public Carinho_item(String titulo, int quantidade, String descricao, String estabelecimento,
                        String estabelecimento_id, String codigo, String data, double preco,
                        String pedidoid, String status, String nomeamostra){
        this.titulo = titulo;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.estabelecimento = estabelecimento;
        this.estabebelecimentoid = estabelecimento_id;
        this.codigo = codigo;
        this.quantidade = quantidade;
        this.data = data;
        this.preco = preco;
        this.pedidoid = pedidoid;
        this.status = status;
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

    public String getEstabebelecimentoid() {
        return estabebelecimentoid;
    }

    public void setEstabebelecimentoid(String estabebelecimentoid) {
        this.estabebelecimentoid = estabebelecimentoid;
    }

    public String getPedidoid() {
        return pedidoid;
    }

    public void setPedidoid(String pedidoid) {
        this.pedidoid = pedidoid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPreco() {
        return preco;
    }
    public void setPreco(double preco) {
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
        return estabebelecimentoid;
    }
    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }
    public void setid(String id) {
        this.estabebelecimentoid = id;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
