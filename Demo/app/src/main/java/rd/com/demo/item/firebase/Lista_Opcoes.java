package rd.com.demo.item.firebase;


public class Lista_Opcoes {
    private String name;
    private String url;
    private int drawable;
    private int cod;

    public Lista_Opcoes(){}

    public Lista_Opcoes(String name, int drawable, String url, int cod){
        this.name = name;
        this.drawable = drawable;
        this.url = url;
        this.cod = cod;
    }

    public String getName() {
        return name;
    }
    public int getDrawable() {
        return drawable;
    }
    public String getUrl() {
        return url;
    }
    public int getCod() {
        return cod;
    }
}
