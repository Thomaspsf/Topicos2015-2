package br.grupointegrado.Spaceinvaders;


import com.badlogic.gdx.Game;


public class MainGame extends Game {

	@Override
	public void create() {
		setScreen(new TelaMenu(this));
	}
}

