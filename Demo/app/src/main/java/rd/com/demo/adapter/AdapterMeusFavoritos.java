package rd.com.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import rd.com.demo.R;
import rd.com.demo.activity.ConfirmacaoCompra;
import rd.com.demo.activity.DetalhesAmostra;
import rd.com.demo.activity.DetalhesProduto;
import rd.com.demo.activity.MainActivity;
import rd.com.demo.banco.sugarOs.AmostrasFavoritasDB;
import rd.com.demo.banco.sugarOs.Carinho_itemDB;
import rd.com.demo.banco.sugarOs.ProdutoFavoritosDB;
import rd.com.demo.item.firebase.Amostras;


public class AdapterMeusFavoritos extends RecyclerView.Adapter {
    private List<Object> list;

    public AdapterMeusFavoritos(List<Object> list){
        this.list = list;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) instanceof AmostrasFavoritasDB
                ? 0 : 1;//caso seja do tipo amostras retorna 0, caso contrario retorna 1
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new HolderAmostras(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_amostras, parent, false));
        } else {
            return new HolderProduto(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_produto_, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
           if (viewHolder instanceof HolderAmostras){
               final AmostrasFavoritasDB amostrasFavoritasDB = (AmostrasFavoritasDB) list.get(position);
               final HolderAmostras holder = (HolderAmostras) viewHolder;
               holder.titulo.setText(amostrasFavoritasDB.getTitulo());
               String string;
               if (amostrasFavoritasDB.getQuantidade() == 1){
                   string = amostrasFavoritasDB.getQuantidade() + " opção";
               } else {
                   string = amostrasFavoritasDB.getQuantidade() + " opções";
               }
               holder.descricao.setText(string);
               Picasso.with(holder.imageView.getContext()).
                       load(amostrasFavoritasDB.getUrl()).into(holder.imageView);
               holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(v.getContext(), DetalhesAmostra.class);
                       Amostras amostras = new Amostras(amostrasFavoritasDB.getTitulo(), amostrasFavoritasDB.getUrl(), amostrasFavoritasDB.getCaminho(),
                               amostrasFavoritasDB.getCategoria(), amostrasFavoritasDB.getNome_estabelecimento(), amostrasFavoritasDB.getIdestabelecimento(),
                               amostrasFavoritasDB.getTipoestabelecimento(), 1);
                       intent.putExtra("amostra", amostrasFavoritasDB);
                       v.getContext().startActivity(intent);
                   }
               });
               holder.preco.setVisibility(View.GONE);
               if (MainActivity.amostrasFavoritasDB.contains(amostrasFavoritasDB.getCaminho())){
                   holder.favoritar.setImageResource(R.drawable.ic_favorite_white_36dp);
               }
               holder.preco.setVisibility(View.GONE);
               holder.favoritar.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (MainActivity.amostrasFavoritasDB.contains(amostrasFavoritasDB.getCaminho())){
                           int pos = MainActivity.amostrasFavoritasDB.indexOf(amostrasFavoritasDB.getCaminho());
                           AmostrasFavoritasDB.deleteAll(AmostrasFavoritasDB.class, "caminho = ?",
                                   amostrasFavoritasDB.getCaminho());
                           holder.favoritar.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                           Snackbar.make(v, "Amostra removida das favoritas", Snackbar.LENGTH_SHORT).show();
                           MainActivity.amostrasFavoritasDB.remove(pos);
                       } else {
                           AmostrasFavoritasDB favoritasDB = new AmostrasFavoritasDB(amostrasFavoritasDB.getTitulo(), amostrasFavoritasDB.getUrl(),
                                   amostrasFavoritasDB.getCaminho(), amostrasFavoritasDB.getCategoria(), amostrasFavoritasDB.getNome_estabelecimento(), amostrasFavoritasDB.getIdestabelecimento(),
                                   amostrasFavoritasDB.getQuantidade());
                           favoritasDB.save();
                           MainActivity.amostrasFavoritasDB.add(amostrasFavoritasDB.getCaminho());
                           Snackbar.make(holder.imageView, "Amostra adiciona aos favoritos", Snackbar.LENGTH_SHORT).show();
                           holder.favoritar.setImageResource(R.drawable.ic_favorite_white_36dp);
                       }
                   }
               });
           } else if (viewHolder instanceof HolderProduto){
               final ProdutoFavoritosDB produto = (ProdutoFavoritosDB) list.get(position);
               HolderProduto holder = (HolderProduto) viewHolder;
               final String preco = "R$" + String.format(Locale.getDefault(), "%.2f", produto.getPreco());
               holder.preco.setText(preco);
               holder.descricao.setText(produto.getDescricao());
               holder.nome.setText(produto.getNome());
               holder.bt.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Carinho_itemDB carinho_itemDB = new Carinho_itemDB(produto.getNome(), "1", produto.getDescricao(),
                               produto.getEstabelecimento(), produto.getEstabelecimentoid(), produto.getTipoestabelecimento(),
                               produto.getCodigo(), "11/02/2018", String.valueOf(produto.getPreco()),
                               produto.getUrl());
                       Intent intent = new Intent(v.getContext(), ConfirmacaoCompra.class);
                       intent.setAction("comprar");
                       intent.putExtra("type", "buySimple");
                       intent.putExtra("pedido", carinho_itemDB);
                       v.getContext().startActivity(intent);
                   }
               });
               holder.layout.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(v.getContext(), DetalhesProduto.class);
                       intent.putExtra("produto", produto);
                       v.getContext().startActivity(intent);
                   }
               });
           }

    }


    public int color(@ColorRes int resId, Context context) {
        return ContextCompat.getColor(
                context, resId);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private class HolderAmostras extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        final TextView titulo;
        private final TextView descricao;
        private final TextView preco;
        private final RelativeLayout relativeLayout;
        private final FloatingActionButton favoritar;

        HolderAmostras(View itemView) {
            super(itemView);
            favoritar = itemView.findViewById(R.id.edit);
            imageView = itemView.findViewById(R.id.imageView);
            titulo = itemView.findViewById(R.id.titulo);
            descricao = itemView.findViewById(R.id.text_quantidade);
            preco = itemView.findViewById(R.id.preco);
            relativeLayout = itemView.findViewById(R.id.linearLayout);

        }

    }
    private class HolderProduto extends RecyclerView.ViewHolder {//
        private final TextView nome, descricao, preco;
        private final Button bt;
        private final RelativeLayout layout;

        HolderProduto(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.nome);
            layout = itemView.findViewById(R.id.relativeLayout);
            descricao = itemView.findViewById(R.id.descricao);
            preco = itemView.findViewById(R.id.preco);
            bt = itemView.findViewById(R.id.button2);



        }
    }
}
