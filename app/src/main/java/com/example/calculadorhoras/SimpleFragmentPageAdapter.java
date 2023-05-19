package com.example.calculadorhoras;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SimpleFragmentPageAdapter extends FragmentPagerAdapter {
    public SimpleFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Retorna el fragmento correspondiente a la posición actual
        switch (position) {
            case 0:
                return new MainActivity3.MiFragmento();
            case 1:
                return new RegistroUsuarios.MiFragmento2();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Retorna el número total de fragmentos
        return 2; // Cantidad de fragmentos que deseas mostrar en el ViewPager
    }
}