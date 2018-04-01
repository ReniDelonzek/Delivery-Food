package rd.com.demofuncionario.item.firebase;

/**
 * Created by Reni on 04/02/2018.
 */

public class Estabelecimento {
    private String nome;
    private String caminho;
    private String url;
    private String atendimento;
    private String endereco;
    private String slogan;
    private String coordenadas;
    private String estabelecimentoid;
    private String telefone;
    private String cidade;
    private String email;
    private int cidadecode;

    public int getCidadecode() {
        return cidadecode;
    }

    public void setCidadecode(int cidadecode) {
        this.cidadecode = cidadecode;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(String atendimento) {
        this.atendimento = atendimento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public Estabelecimento(String nome, String caminho, String url, String atendimento,
                           String endereco, String slogan, String id, String coordenadas) {
        this.nome = nome;
        this.caminho = caminho;
        this.url = url;
        this.atendimento = atendimento;
        this.endereco = endereco;
        this.slogan = slogan;
        this.estabelecimentoid = id;
        this.coordenadas = coordenadas;
    }


    public String getEstabelecimentoid() {
        return estabelecimentoid;
    }

    public void setEstabelecimentoid(String estabelecimentoid) {
        this.estabelecimentoid = estabelecimentoid;
    }
    public Estabelecimento(){

    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNome() {
        return nome;
    }

    public String getUrl() {
        return url;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }
}
