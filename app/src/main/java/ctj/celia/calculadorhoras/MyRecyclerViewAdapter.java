package ctj.celia.calculadorhoras;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private static List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private RegistroActivity clickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
      //  return new ViewHolder(view);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String linea = mData.get(position);
        String[] parts = linea.split(";");

            String dia = parts[0];
            String hora = parts[1];

            holder.mydia.setText(dia);
            holder.myhora.setText(hora);
        System.out.println(parts);
        System.out.println(linea);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(RegistroActivity clickListener) {
        this.clickListener = clickListener;
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mydia, myhora, mymin;
        private ItemClickListener mClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            mydia = itemView.findViewById(R.id.mydia);
            myhora = itemView.findViewById(R.id.myhora);
            itemView.setOnClickListener(this);
           // itemView.setOnCreateContextMenuListener((View.OnCreateContextMenuListener) this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }


        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), v.getId(), 0, R.string.app_name);//groupId, itemId, order, title

        }
    }
        String getItem(int id) {
            return mData.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);

        }
    }
