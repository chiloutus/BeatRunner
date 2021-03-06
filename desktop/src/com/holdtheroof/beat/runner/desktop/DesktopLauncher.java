package com.holdtheroof.beat.runner.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.holdtheroof.beat.runner.Beat;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Beat Runner";
		config.height = 480;
		config.width = 800;
		new LwjglApplication(new Beat(), config);
	}
}
