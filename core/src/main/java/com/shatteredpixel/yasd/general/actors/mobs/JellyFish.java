/*
 *
 *   Pixel Dungeon
 *   Copyright (C) 2012-2015 Oleg Dolya
 *
 *   Shattered Pixel Dungeon
 *   Copyright (C) 2014-2019 Evan Debenham
 *
 *   Yet Another Shattered Dungeon
 *   Copyright (C) 2014-2020 Samuel Braithwaite
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.actors.mobs;

import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.Element;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Burning;
import com.shatteredpixel.yasd.general.actors.buffs.Vertigo;
import com.shatteredpixel.yasd.general.sprites.BlueJellyFishSprite;
import com.shatteredpixel.yasd.general.sprites.GreenJellyFishSprite;
import com.shatteredpixel.yasd.general.sprites.PurpleJellyFishSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class JellyFish extends Mob {

	private static final float TIME_TO_ZAP = 1f;
	private static final String PRIVATE_DEPTH = "privateDepth";
	private static final String COLOUR = "colour";

	int privateDepth;
	int colour;

	public static final int BLUE = 0;
	public static final int GREEN = 1;
	public static final int PURPLE = 2;

	{
		EXP = 0;

		//loot = BlueGell.class;
		//lootChance = 1f;

		immunities.add(Burning.class);
		immunities.add(Vertigo.class);
		properties.add(Property.BLOB_IMMUNE);
		properties.add(Property.ELECTRIC);
	}

	@Override
	public Element elementalType() {
		return Element.ELECTRIC;
	}

	public JellyFish(){
		this(Random.Int(3), Dungeon.getScaleFactor());
	}

	public JellyFish(int colour, int depth){
		super();

		this.colour = colour;
		if(colour == BLUE){
			spriteClass = BlueJellyFishSprite.class;
			//loot = BlueGel.class;
		}
		if(colour == GREEN){
			spriteClass = GreenJellyFishSprite.class;
			//loot = GreenGel.class;
		}
		if(colour == PURPLE){
			spriteClass = PurpleJellyFishSprite.class;
			//loot = PurpleGel.class;
		}

		privateDepth = depth;
		HP = HT = 8 + privateDepth * 4;
		defenseSkill = 5 + privateDepth * 4;
	}

	protected boolean act() {
		if(!Dungeon.level.liquid()[pos]){
			die(null);
			sprite.killAndErase();
			return true;
		} else {
			return super.act();
		}
	}

	@Override
	public boolean reset(){
		return true;
	}
	@Override
	protected boolean getCloser(int target){
		if(rooted){
			return false;
		}
		int step = Dungeon.findStep(this, pos, target,
				Dungeon.level.liquid(),
				fieldOfView);
		if(step != -1){
			move(step);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean getFurther(int target){
		int step = Dungeon.flee(this, pos, target,
				Dungeon.level.liquid(),
				fieldOfView);
		if(step != -1){
			move(step);
			return true;
		}else{
			return false;
		}
	}
	@Override
	public void storeInBundle(Bundle bundle){
		super.storeInBundle(bundle);
		bundle.put(PRIVATE_DEPTH, privateDepth);
		bundle.put(COLOUR, colour);
	}
	@Override
	public void restoreFromBundle(Bundle bundle){
		super.restoreFromBundle(bundle);
		privateDepth = bundle.getInt(PRIVATE_DEPTH);
		colour = bundle.getInt(COLOUR);
	}

	@Override
	public boolean canAttack(Char enemy){
		return Dungeon.level.distance(pos, enemy.pos) <= (Dungeon.level.liquid()[enemy.pos] ? 2 : 1);
	}
}