/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Yet Another Shattered Dungeon
 *  * Copyright (C) 2014-2020 Samuel Braithwaite
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.actors.mobs;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.buffs.Charm;
import com.shatteredpixel.yasd.general.actors.buffs.Light;
import com.shatteredpixel.yasd.general.effects.Speck;
import com.shatteredpixel.yasd.general.items.Generator;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.items.scrolls.Scroll;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.yasd.general.mechanics.Ballistica;
import com.shatteredpixel.yasd.general.sprites.SuccubusSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Succubus extends Mob {

	private int blinkCooldown = 0;
	
	{
		spriteClass = SuccubusSprite.class;

		healthFactor = 0.8f;
		damageFactor = 1.2f;
		accuracyFactor = 1.5f;
		perceptionFactor = 1.5f;
		viewDistance = Light.DISTANCE;
		
		EXP = 12;
		maxLvl = 25;

		loot = Generator.Category.SCROLL;
		lootChance = 0.33f;

		properties.add(Property.DEMONIC);
	}

	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		
		if (enemy.buff(Charm.class) != null ){
			/*int shield = (HP - HT) + (5 + damage);
			if (shield > 0){
				HP = HT;
				Buff.affect(this, Barrier.class).setShield(shield);
			} else {
				HP += 5 + damage;
			}*/
			heal(damage, true, true);
			sprite.emitter().burst( Speck.factory( Speck.HEALING ), 2 );
			Sample.INSTANCE.play( Assets.SND_CHARMS );
		} else if (Random.Int( 3 ) == 0) {
			//attack will reduce by 5 turns, so effectively DURATION-5 turns
			Buff.affect( enemy, Charm.class, Charm.DURATION ).object = id();
			enemy.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
			Sample.INSTANCE.play( Assets.SND_CHARMS );
		}
		
		return damage;
	}

	@Override
	protected Item createLoot() {
		Class<?extends Scroll> loot;
		do{
			loot = (Class<? extends Scroll>) Random.oneOf(Generator.Category.SCROLL.classes);
		} while (loot == ScrollOfIdentify.class);

		return Reflection.newInstance(loot);
	}
	
	@Override
	protected boolean getCloser( int target ) {
		if (fieldOfView[target] && Dungeon.level.distance( pos, target ) > 2 && blinkCooldown <= 0) {
			
			blink( target );
			spend( -1 / speed() );
			return true;
			
		} else {

			blinkCooldown--;
			return super.getCloser( target );
			
		}
	}
	
	private void blink( int target ) {
		
		Ballistica route = new  Ballistica( pos, target, Ballistica.PROJECTILE);
		int cell = route.collisionPos;

		//can't occupy the same cell as another char, so move back one.
		if (Actor.findChar( cell ) != null && cell != this.pos)
			cell = route.path.get(route.dist-1);

		if (Dungeon.level.avoid( cell )){
			ArrayList<Integer> candidates = new  ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				cell = route.collisionPos + n;
				if (Dungeon.level.passable(cell) && Actor.findChar( cell ) == null) {
					candidates.add( cell );
				}
			}
			if (candidates.size() > 0)
				cell = Random.element(candidates);
			else {
				blinkCooldown = Random.IntRange(4, 6);
				return;
			}
		}
		
		ScrollOfTeleportation.appear( this, cell );

		blinkCooldown = Random.IntRange(4, 6);
	}

	private static final String BLINK_CD = "blink_cd";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BLINK_CD, blinkCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		blinkCooldown = bundle.getInt(BLINK_CD);
	}
	
	{
		immunities.add( Charm.class );
	}
}
