package rd.com.demo.item.firebase;

import java.io.Serializable;

/**
 * Created by Reni on 09/02/2018.
 */

public class Pedidos implements Serializable{
    private String titulo;
    private String descricao;
    private String codigo;
    private String status;
    private int status_code;
    private String nomeEstabelecimento;
    private String idEstabelecimento, tipoestabelecimento;
    private String quantidade;
    private String userid, username, endereco;
    private String tipoEntrega;
    private String data;
    private String hora;
    private String datacompleta;
    private String mensagem;
    private String userToken;
    private String preco;
    private String apelidoendereco;
    private String cidade, cidadecode;
    private String id;
    private String precofrete;


    public Pedidos(){}
    public Pedidos(String titulo, String descricao, String codigo,
                   String status, String nomeEstabelecimento, String idEstabelecimento, String tipoestabelecimento, String quantidade,
                   String userid, String username, String endereco, String levar, String data, String hora, String mensagem,
                   int status_code, String userToken, String preco, String apelidoendereco, String cidadecode, String cidade, String precofrete) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.codigo = codigo;
        this.status = status;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.idEstabelecimento = idEstabelecimento;
        this.tipoestabelecimento = tipoestabelecimento;
        this.quantidade = quantidade;
        this.userid = userid;
        this.username = username;
        this.endereco = endereco;
        this.tipoEntrega = levar;
        this.data = data;
        this.hora = hora;
        this.mensagem = mensagem;
        this.status_code = status_code;
        this.userToken = userToken;
        this.preco = preco;
        this.apelidoendereco = apelidoendereco;
        this.cidadecode = cidadecode;
        this.cidade = cidade;
        this.precofrete = precofrete;
    }

    public String getTipoestabelecimento() {
        return tipoestabelecimento;
    }

    public void setTipoestabelecimento(String tipoestabelecimento) {
        this.tipoestabelecimento = tipoestabelecimento;
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

    public String getApelidoendereco() {
        return apelidoendereco;
    }

    public void setApelidoendereco(String apelidoendereco) {
        this.apelidoendereco = apelidoendereco;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getStatus_code() {
        return status_code;
    }
    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }
    public void setNomeEstabelecimento(String nomeEstabelecimento) {
        this.nomeEstabelecimento = nomeEstabelecimento;
    }
    public String getIdEstabelecimento() {
        return idEstabelecimento;
    }
    public void setIdEstabelecimento(String idEstabelecimento) {
        this.idEstabelecimento = idEstabelecimento;
    }
    public String getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDatacompleta() {
        return datacompleta;
    }
    public void setDatacompleta(String datacompleta) {
        this.datacompleta = datacompleta;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public String getTipoEntrega() {
        return tipoEntrega;
    }
    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrecofrete() {
        return precofrete;
    }

    public void setPrecofrete(String precofrete) {
        this.precofrete = precofrete;
    }
}
