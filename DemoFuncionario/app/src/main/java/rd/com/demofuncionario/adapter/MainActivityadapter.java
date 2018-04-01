package rd.com.demofuncionario.adapter;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.activity.ConfirmarPedido;
import rd.com.demofuncionario.activity.Detalhes_pedido;
import rd.com.demofuncionario.auxiliar.Constants;
import rd.com.demofuncionario.item.firebase.Pedidos;


public class MainActivityadapter extends RecyclerView.Adapter {
    private List<Pedidos> list;

    public MainActivityadapter(List<Pedidos> objects) {
        list = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new Holder_Pedidos(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof Holder_Pedidos){//adaptador para as promoções do topo
            final Holder_Pedidos holder_pedidos = (Holder_Pedidos) viewHolder;
            final Pedidos pedidos = list.get(position);
            String[] p = pedidos.getCodigo().split("¨");


            if (p.length == 1){
                holder_pedidos.titulo.setText(String.format(
                        "%s item", String.valueOf(p.length)));
            } else {
                holder_pedidos.titulo.setText(String.format(
                        "%s itens diferentes", String.valueOf(p.length)));
            }
            holder_pedidos.status.setText(pedidos.getStatus());
            holder_pedidos.entregar.setText(pedidos.getTipoEntrega());
            definir_horario(pedidos, obter_data(), holder_pedidos);
            holder_pedidos.descricao.setText(pedidos.getTitulo().replace("¨", ", "));
            holder_pedidos.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pedidos.getStatus_code() < Constants.pronto_code) {
                        Intent intent = new Intent(v.getContext(), Detalhes_pedido.class);
                        intent.putExtra("pedido", pedidos);
                        intent.putExtra("time", holder_pedidos.hora.getText().toString());
                        v.getContext().startActivity(intent);
                    } else if (pedidos.getStatus_code() >= Constants.pronto_code
                            && pedidos.getStatus_code() < Constants.concluido_code){
                        Intent intent = new Intent(v.getContext(), ConfirmarPedido.class);
                        intent.putExtra("pedido", pedidos);
                        intent.putExtra("time", holder_pedidos.hora.getText().toString());
                        v.getContext().startActivity(intent);
                    } else if (pedidos.getStatus_code() == Constants.concluido_code){
                        Snackbar.make(holder_pedidos.itemView, "O pedido foi concluído com sucesso!", Snackbar.LENGTH_SHORT).show();
                    } else if (pedidos.getStatus_code() == Constants.cancelado_code){
                        Snackbar.make(holder_pedidos.itemView, "O pedido foi cancelado!", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }
    private void definir_horario(Pedidos pedidos, Map<String, String> data, Holder_Pedidos holder_pedidos){
        int minpedido = Integer.parseInt(pedidos.getHora().substring(3, 5));
        int minatual = Integer.parseInt(data.get("hora").substring(3, 5));
        int horapedido = Integer.parseInt(pedidos.getHora().substring(0, 2));
        int horaatual = Integer.parseInt(data.get("hora").substring(0, 2));

        int diaatual = Integer.parseInt(data.get("data").substring(0, 2));
        int diapedido = Integer.parseInt(pedidos.getData().substring(0, 2));

        if (pedidos.getData().equals(data.get("data"))){
            if (horaatual == horapedido){//o pedido foi feito ainda nessa hora
                holder_pedidos.hora.setText(String.format("A %s minutos atrás", String.valueOf(minatual - minpedido)));
            } else if (1 + horapedido == horaatual &&  //verifica se o pedido foi feito a menos de uma hora
                    minatual < minpedido){//e se ja nao se passou mais de 60 minutos
                holder_pedidos.hora.setText(String.format(Locale.getDefault(),
                        "A %s minutos atrás",(60 - minpedido) + minatual));
            } else {//pedido foi feito a mais de 1 hr
                int ho = horaatual - horapedido;
                if (ho == 1) {
                    holder_pedidos.hora.setText(String.format("A %s Hora atrás", String.valueOf(ho)));
                } else {
                    holder_pedidos.hora.setText(String.format("A %s Horas atrás", String.valueOf(ho)));
                }
            }
        } else if (diaatual - 1 == diapedido){//pedido foi feito outro dia
            holder_pedidos.hora.setText(String.format("Ontem às %s", pedidos.getHora()));
        } else {
            holder_pedidos.hora.setText(String.format("%s %s", pedidos.getData(),
                    pedidos.getHora().substring(0, pedidos.getHora().length() - 3)));
        }
    }
    private Map<String, String> obter_data(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
        SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        Map<String, String> map = new HashMap<>();
        map.put("data", dateFormat.format(data_atual));
        map.put("hora", hora.format(data_atual));
        return map;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder_Pedidos extends RecyclerView.ViewHolder {//holder anuncios topo
       private final TextView status, entregar, titulo, hora, descricao;
       private final RelativeLayout relativeLayout;


        Holder_Pedidos(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            entregar = itemView.findViewById(R.id.entregar);
            descricao = itemView.findViewById(R.id.descricao);
            status = itemView.findViewById(R.id.status);
            titulo = itemView.findViewById(R.id.titulo);
            hora = itemView.findViewById(R.id.tempo);

        }
    }
}

