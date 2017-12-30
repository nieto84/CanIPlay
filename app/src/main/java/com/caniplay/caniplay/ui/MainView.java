package com.caniplay.caniplay.ui;

import android.content.Context;

import com.caniplay.caniplay.Evento;

import java.util.List;

/**
 * Created by A on 28/12/2017.
 */

public interface MainView {

Context getContext();
    void noGPSfound();
    void noDataFound();
    void refreshDataset(List<Evento> arrayEventos );
    void noLogin();
}
