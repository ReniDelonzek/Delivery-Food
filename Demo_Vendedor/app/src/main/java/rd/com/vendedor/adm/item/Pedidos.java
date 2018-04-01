package rd.com.vendedor.adm.item;

/**
 * Created by Reni on 09/02/2018.
 */

public class Pedidos {
    private String titulo;
    private String descricao;
    private String codigo;
    private String idpedido;
    private String status;
    private String nomeEstabelecimento;
    private String idEstabelecimento;
    private String caminho;
    private int quantidade;
    private String userid, username, endereco;
    private boolean levar;

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

    public boolean isLevar() {
        return levar;
    }

    public void setLevar(boolean levar) {
        this.levar = levar;
    }

    public Pedidos(){}

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

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Pedidos(String titulo, String descricao, String codigo, String idpedido,
                   String status, String nomeEstabelecimento, String idEstabelecimento, String caminho, int quantidade,
                   String userid, String username, String endereco, boolean levar) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.codigo = codigo;
        this.idpedido = idpedido;
        this.status = status;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.idEstabelecimento = idEstabelecimento;
        this.caminho = caminho;
        this.quantidade = quantidade;
        this.userid = userid;
        this.username = username;
        this.endereco = endereco;
        this.levar = levar;
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

    public String getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(String idpedido) {
        this.idpedido = idpedido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Pedidos(String titulo, String descricao, String codigo, String idpedido, String status) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.codigo = codigo;
        this.idpedido = idpedido;
        this.status = status;
    }
}
