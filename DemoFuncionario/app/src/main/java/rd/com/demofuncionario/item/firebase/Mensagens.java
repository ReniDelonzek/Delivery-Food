package rd.com.demofuncionario.item.firebase;

/**
 * Created by Reni on 10/03/2018.
 */

public class Mensagens {
    private String mensagem, data, hora, email;

    public Mensagens(String mensagem, String data, String hora, String email) {
        this.mensagem = mensagem;
        this.data = data;
        this.hora = hora;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
