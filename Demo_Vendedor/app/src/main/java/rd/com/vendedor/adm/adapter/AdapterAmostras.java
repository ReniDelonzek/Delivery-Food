package rd.com.vendedor.adm.adapter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.activity.Adicionar_amostras;
import rd.com.vendedor.adm.activity.Adicionar_produtos;
import rd.com.vendedor.adm.activity.GerenciarProdutos;
import rd.com.vendedor.adm.item.Amostras;
import rd.com.vendedor.adm.utils.Constants;


public class AdapterAmostras extends RecyclerView.Adapter {

    private List<Amostras> list;
    public AdapterAmostras(List<Amostras> objects) {
        list = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
            viewHolder = new Holder_Produtos(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_amostra, parent, false));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
         if (viewHolder instanceof Holder_Produtos){//adaptador para as promoções do topo
            final Holder_Produtos holderProdutos = (Holder_Produtos) viewHolder;
            final Amostras amostras = list.get(position);
            holderProdutos.titulo.setText(amostras.getTitulo());
            String s = String.valueOf(amostras.getQuantidade()) + " produto(s)";
            holderProdutos.descricao.setText(s);
             Picasso.with(holderProdutos.imageView.getContext()).
                     load(amostras.getUrl()).into(holderProdutos.imageView);


             holderProdutos.add.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(holderProdutos.add.getContext(), Adicionar_produtos.class);
                     intent.setAction("add_from_amostra");
                     intent.putExtra(Constants.nomeEstabelecimento, amostras.getNome_estabelecimento());
                     intent.putExtra(Constants.idEstabelecimento, amostras.getId_estabelecimento());
                     intent.putExtra(Constants.cidadeCode, amostras.getCidadecode());

                     intent.putExtra("amostracode", amostras.getCaminho());
                     intent.putExtra("amostranome", amostras.getTitulo());
                     intent.putExtra("quantidade", amostras.getQuantidade());
                     intent.putExtra("categoria", amostras.getCategoria());
                     intent.putExtra("url", amostras.getUrl());
                     holderProdutos.add.getContext().startActivity(intent);
                 }
             });
             holderProdutos.edit.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(v.getContext(), Adicionar_amostras.class);
                     intent.setAction("edit");
                     intent.putExtra(Constants.nomeEstabelecimento, amostras.getNome_estabelecimento());
                     intent.putExtra(Constants.idEstabelecimento, amostras.getId_estabelecimento());
                     //intent.putExtra(Constants.tipoEstabelecimento, amostras.getId_estabelecimento());

                     intent.putExtra("url", amostras.getUrl());
                     intent.putExtra("titulo", amostras.getTitulo());
                     intent.putExtra("quantidade", amostras.getQuantidade());
                     intent.putExtra("caminho", amostras.getCaminho());

                     v.getContext().startActivity(intent);

                 }
             });
             holderProdutos.relativeLayout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(v.getContext(), GerenciarProdutos.class);
                     intent.putExtra("amostra", amostras.getCaminho());
                     intent.putExtra("quantamostra", amostras.getQuantidade());
                     intent.putExtra("amostra", amostras);
                     v.getContext().startActivity(intent);
                 }
             });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder_Produtos extends RecyclerView.ViewHolder {//holder anuncios topo
        private final FloatingActionButton edit, add;
        private final TextView titulo, descricao;
        private final ImageView imageView;
        private final RelativeLayout relativeLayout;
        Holder_Produtos(View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.edit);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            add = itemView.findViewById(R.id.remove);
            imageView = itemView.findViewById(R.id.imageView);
            titulo = itemView.findViewById(R.id.nameTextView);
            descricao = itemView.findViewById(R.id.text_quantidade);

        }
    }
}

