package rd.com.demo.item.firebase;


public class Item {
    private String nome;
    private String urlImagem;
    private int drawable;
    private String codigo;
    private int categoria;
    private String nome_categoria;

    public Item(){}
    public Item(String nome, String urlImagem){
        this.urlImagem = urlImagem;
        this.nome = nome;
    }
	
    public void setNome(String nome) {this.nome = nome;}
    public void setNome_categoria(String nome_categoria) {this.nome_categoria = nome_categoria;}
	public String getNome_categoria() {return nome_categoria;}
    public int getCategoria() {return categoria;}
    public void setCategoria(int categoria) {this.categoria = categoria;}
    public String getCodigo() {return codigo;}
    public void setCodigo(String codigo) {this.codigo = codigo;}
    public void setDrawable(int drawable) {this.drawable = drawable;}
    public int getDrawable() {return drawable;}
    public String getNome() {return nome;}
    public String getUrlImagem() {return urlImagem;}
}
