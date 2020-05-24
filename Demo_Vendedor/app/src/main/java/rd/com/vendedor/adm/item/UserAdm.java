package rd.com.vendedor.adm.item;

public class UserAdm {
    private String nome, sobrenome, id, cargo,
            estabelecimentonome, estabelecimentotipo, estabelecimentoid, cidadecode,
            cidade, codigopostal, caminhodatabase;


    public UserAdm(){}

    public UserAdm(String nome, String sobrenome, String id, String cargo, String estabelecimentonome, String estabelecimentotipo,
                   String estabelecimentoid, String cidadecode, String cidade, String codigopostal, String caminhodatabase) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.id = id;
        this.cargo = cargo;
        this.estabelecimentonome = estabelecimentonome;
        this.estabelecimentotipo = estabelecimentotipo;
        this.estabelecimentoid = estabelecimentoid;
        this.cidadecode = cidadecode;
        this.cidade = cidade;
        this.codigopostal = codigopostal;
        this.caminhodatabase = caminhodatabase;
    }

    public String getCaminhodatabase() {
        return caminhodatabase;
    }

    public void setCaminhodatabase(String caminhodatabase) {
        this.caminhodatabase = caminhodatabase;
    }

    public String getCodigopostal() {
        return codigopostal;
    }

    public void setCodigopostal(String codigopostal) {
        this.codigopostal = codigopostal;
    }

    public String getCidadecode() {
        return cidadecode;
    }

    public void setCidadecode(String cidadecode) {
        this.cidadecode = cidadecode;
    }

    public String getEstabelecimentonome() {
        return estabelecimentonome;
    }

    public void setEstabelecimentonome(String estabelecimentonome) {
        this.estabelecimentonome = estabelecimentonome;
    }

    public String getEstabelecimentotipo() {
        return estabelecimentotipo;
    }

    public void setEstabelecimentotipo(String estabelecimentotipo) {
        this.estabelecimentotipo = estabelecimentotipo;
    }

    public String getEstabelecimentoid() {
        return estabelecimentoid;
    }

    public void setEstabelecimentoid(String estabelecimentoid) {
        this.estabelecimentoid = estabelecimentoid;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
