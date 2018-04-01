package rd.com.demofuncionario.item.firebase;

/**
 * Created by Reni on 12/02/2018.
 */

public class Funcionario {
    private String email;
    private String nome;
    private String sobrenome;
    private String estabelecimentonome;
    private String estabelecimentoid;
    private String estabelecimentotipo;
    private String numero;
    private String cidade, cidadecode;
    private String dataadicao;
    private String datacontratacao;
    private String funcao;

    public Funcionario(){}

    public Funcionario(String email, String nome, String sobrenome, String estabelecimentonome, String estabelecimentoid, String estabelecimentotipo, String numero, String cidade, String cidadecode, String dataadicao, String datacontratacao, String funcao) {
        this.email = email;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.estabelecimentonome = estabelecimentonome;
        this.estabelecimentoid = estabelecimentoid;
        this.estabelecimentotipo = estabelecimentotipo;
        this.numero = numero;
        this.cidade = cidade;
        this.cidadecode = cidadecode;
        this.dataadicao = dataadicao;
        this.datacontratacao = datacontratacao;
        this.funcao = funcao;
    }

    public String getEstabelecimentotipo() {
        return estabelecimentotipo;
    }

    public void setEstabelecimentotipo(String estabelecimentotipo) {
        this.estabelecimentotipo = estabelecimentotipo;
    }

    public String getDataadicao() {
        return dataadicao;
    }

    public void setDataadicao(String dataadicao) {
        this.dataadicao = dataadicao;
    }

    public String getDatacontratacao() {
        return datacontratacao;
    }

    public void setDatacontratacao(String datacontratacao) {
        this.datacontratacao = datacontratacao;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEstabelecimentonome() {
        return estabelecimentonome;
    }

    public void setEstabelecimentonome(String estabelecimentonome) {
        this.estabelecimentonome = estabelecimentonome;
    }

    public String getEstabelecimentoid() {
        return estabelecimentoid;
    }

    public void setEstabelecimentoid(String estabelecimentoid) {
        this.estabelecimentoid = estabelecimentoid;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }
}
