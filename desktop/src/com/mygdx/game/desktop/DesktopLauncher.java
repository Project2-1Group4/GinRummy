
package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.GinRummy;

public class DesktopLauncher {


	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GinRummy(), config);
	}

	/*
	public static void main(String[] args) {
		SetOfCards deck = new SetOfCards(true);
		deck.shuffleCards();

		Player aPlayer = new Player("player",SetOfCards.handOutCard(10, deck));
		//aPlayer.hand.sortBySuitAndValue();
		//System.out.println(aPlayer.hand);

		List<List<Card>> runs = aPlayer.findRuns();
		System.out.println("runs: \n " + runs);

		List<List<Card>> sets = aPlayer.findSets();
		System.out.println("sets: \n" + sets);

		System.out.println("hand: "+ aPlayer.hand + "\n");

		aPlayer.bestCombination();

		System.out.println("deadwood: "+aPlayer.findDeadwood());

		int deadWoodValue = Player.valueInList(aPlayer.deadWood);

		System.out.println("Value of deadwood set: "+deadWoodValue);

	}*/

}
