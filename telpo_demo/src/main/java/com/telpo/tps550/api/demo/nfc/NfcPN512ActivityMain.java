package com.telpo.tps550.api.demo.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

public class NfcPN512ActivityMain extends BaseActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_pn512nfc_main);

	}

	public void onClick(View view) {
		if (view.getId() == R.id.cpu_btn) {
			Intent intentCPU = new Intent(NfcPN512ActivityMain.this, NfcActivity_tps900.class);
			startActivity(intentCPU);
		} else if (view.getId() == R.id.mifare_desfire_btn) {
			Intent intentMifareDefire = new Intent(NfcPN512ActivityMain.this, MifareDesfireActivity.class);
			startActivity(intentMifareDefire);
		} else if (view.getId() == R.id.felica_btn) {
			Intent intentFelica = new Intent(NfcPN512ActivityMain.this, FelicaActivity.class);
			startActivity(intentFelica);
		}

	}
}
