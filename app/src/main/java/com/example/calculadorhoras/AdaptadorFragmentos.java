package com.example.calculadorhoras;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorFragmentos extends FragmentStateAdapter {
    private List<Fragment> fragmentList = new ArrayList<>();

    public AdaptadorFragmentos(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

  /*  public AdaptadorFragmentos(FragmentManager fragmentManager) {
        super(fragmentManager, getLifecycle()); // Aquí se pasa el FragmentManager y el ciclo de vida del Fragmento actual
        // Agrega tus fragmentos a la lista
        fragmentList.add(new MainFragment());
        fragmentList.add(new SecondFragment());*/


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}




