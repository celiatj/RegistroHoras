package ctj.celia.calculadorhoras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MiAdapter extends RecyclerView.Adapter<MiAdapter.MiViewHolder> {
private ArrayList<String> listaNombres;
private OnItemClickListener listener;

public MiAdapter(ArrayList<String> listaNombres) {
        this.listaNombres = listaNombres;
        }

public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        }

public interface OnItemClickListener {
    void onItemClick(int position);
}

    @NonNull
    public MiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MiViewHolder(v);
    }

    public void onBindViewHolder(@NonNull MiViewHolder holder, int position) {
        holder.nombreTextView.setText(listaNombres.get(holder.getAdapterPosition()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    public int getItemCount() {
        return listaNombres.size();
    }

public static class MiViewHolder extends RecyclerView.ViewHolder {
    public TextView nombreTextView;

    public MiViewHolder(View itemView) {
        super(itemView);
        nombreTextView = itemView.findViewById(android.R.id.text1);
    }
}
}