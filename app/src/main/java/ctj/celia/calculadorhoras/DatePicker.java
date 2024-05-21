package ctj.celia.calculadorhoras;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DatePicker extends DialogFragment {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar calendario = Calendar.getInstance();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
            int anyo = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);
           // int dia= Integer.parseInt(String.format("%02d", dia1));
          //  int mes= Integer.parseInt(String.format("%02d", mes1));
        return new DatePickerDialog(
                getContext(),
                (DatePickerDialog.OnDateSetListener) getParentFragment(), anyo, mes, dia);
    }


}
