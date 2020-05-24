package rd.com.vendedor.adm.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.activity.Adicionar_produtos;
import rd.com.vendedor.adm.activity.GerenciarProdutos;
import rd.com.vendedor.adm.item.Produto;
import rd.com.vendedor.adm.utils.Constants;

import static rd.com.vendedor.adm.activity.GerenciarProdutos.cidadecode;
import static rd.com.vendedor.adm.activity.GerenciarProdutos.idEstabelecimento;
import static rd.com.vendedor.adm.activity.GerenciarProdutos.tipoEstabelecimento;


public class AdapterProdutos extends RecyclerView.Adapter {


    private List<Produto> list;
    public AdapterProdutos(List<Produto> objects) {
        list = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
            viewHolder = new Holder_Produtos(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_produto_, parent, false));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final Holder_Produtos holder = (Holder_Produtos) viewHolder;
        final Produto produto = list.get(position);

        holder.titulo.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog(v, produto.getCodigo(), holder.getAdapterPosition());
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Adicionar_produtos.class);
                intent.putExtra("produto", produto);
                intent.setAction("edit");
                intent.putExtra("quantidade", GerenciarProdutos.amostra.getQuantidade());
                v.getContext().startActivity(intent);
            }
        });
    }
    private void dialog(final View view, final String ca, final int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) view.getContext(), R.style.dialog);
        AlertDialog alerta;
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance()//Todo nao esta apagando
                        .collection("estabelecimentos")
                        .document(Constants.cidade)
                        .collection(cidadecode)
                        .document(tipoEstabelecimento)
                        .collection(idEstabelecimento)
                        .document("produtos")
                        .collection("produtos")
                        .document(ca)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(view, "Amostra removida com sucesso!", Snackbar.LENGTH_SHORT).show();
                        notifyItemRemoved(adapterPosition);
                        atualizar_amostra();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        if (e.getMessage().equals("PERMISSION_DENIED: Missing or insufficient permissions.")){
                            Snackbar.make(view, "Parece que você não tem permissão de remover essa amostra", Snackbar.LENGTH_SHORT).show();
                        } else
                            Snackbar.make(view, "Ocorreu uma falha ao remover a amostra", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alerta = builder.create();
        alerta.setTitle("Confirme a remoção");
        alerta.setMessage("Deseja excluir o produto?");
        alerta.show();
    }
    private void atualizar_amostra() {
        int quant = GerenciarProdutos.amostra.getQuantidade();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(tipoEstabelecimento)
                .collection("amostras")
                .document(GerenciarProdutos.amostra.getCaminho())
                .update("quantidade", --quant)//remove o produto do registro da amostra
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.v("AdapterProdutos", "Amostra atualizada com sucesso");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder_Produtos extends RecyclerView.ViewHolder {//holder anuncios topo
        private final FloatingActionButton edit, delete;
        private final TextView titulo, descricao, detalhes;
        private final RelativeLayout relativeLayout;
        Holder_Produtos(View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.edit);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            delete = itemView.findViewById(R.id.remove);
            titulo = itemView.findViewById(R.id.nome);
            descricao = itemView.findViewById(R.id.descricao);
            detalhes = itemView.findViewById(R.id.detalhes);

        }
    }
}

