package ctj.celia.calculadorhoras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdaptadorRegistro extends RecyclerView.Adapter<AdaptadorRegistro.ViewHolder> {
    private List<String> horasTrabajadasList;

    public AdaptadorRegistro(List<String> horasTrabajadasList) {
        this.horasTrabajadasList = horasTrabajadasList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.registrosdiariosview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String horasTrabajadas = horasTrabajadasList.get(position);
        holder.textViewHorasTrabajadas.setText(horasTrabajadas);
    }

    @Override
    public int getItemCount() {
        return horasTrabajadasList != null ? horasTrabajadasList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHorasTrabajadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHorasTrabajadas = itemView.findViewById(R.id.textViewHorasTrabajadas);
        }
    }
}
