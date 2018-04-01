package rd.com.demofuncionario.item.Sugar;

import com.orm.SugarRecord;

/**
 * Created by Reni on 12/02/2018.
 */


public class Funcionario extends SugarRecord<Funcionario>{
    private String email;
    private String nome;
    private String sobrenome;
    private String estabelecimentonome;


    public Funcionario(){}

    public Funcionario(String email, String nome, String sobrenome,
                       String estabelecimentonome, String estabelecimentoid,
                       String numero, String funcao) {
        this.email = email;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.estabelecimentonome = estabelecimentonome;
        this.estabelecimentoid = estabelecimentoid;
        this.numero = numero;
        this.funcao = funcao;
    }

    private String estabelecimentoid;
    private String numero;

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

    private String funcao;
}
