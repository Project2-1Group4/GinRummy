package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class CardGame {

    public World world;

    public CardGame(){
        world = new World(new Vector2(0,-10f), true);
    }

}
