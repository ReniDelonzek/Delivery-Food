package rd.com.demo.item.firebase;

/**
 * Created by Reni on 10/03/2018.
 */

public class Mensagens {
    private String mensagem, data, hora;

    public Mensagens(String mensagem, String data, String hora) {
        this.mensagem = mensagem;
        this.data = data;
        this.hora = hora;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
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

    public Mensagens(){}
}
