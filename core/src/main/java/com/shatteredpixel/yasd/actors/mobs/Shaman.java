/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.yasd.actors.mobs;

import com.shatteredpixel.yasd.Dungeon;
import com.shatteredpixel.yasd.actors.Char;
import com.shatteredpixel.yasd.effects.particles.SparkParticle;
import com.shatteredpixel.yasd.items.Generator;
import com.shatteredpixel.yasd.mechanics.Ballistica;
import com.shatteredpixel.yasd.messages.Messages;
import com.shatteredpixel.yasd.sprites.CharSprite;
import com.shatteredpixel.yasd.sprites.ShamanSprite;
import com.shatteredpixel.yasd.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Shaman extends Mob implements Callback {

	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = ShamanSprite.class;
		
		HP = HT = 20;
		defenseSkill = 8;
		
		EXP = 6;
		maxLvl = 14;
		
		loot = Generator.Category.SCROLL;
		lootChance = 0.33f;
		
		properties.add(Property.ELECTRIC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 12 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return Dungeon.depth*2;
	}//Finding Shaman later are still hard.
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class LightningBolt{}
	
	@Override
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.distance( pos, enemy.pos ) <= 1) {
			
			return super.doAttack( enemy );
			
		} else {
			
			if (sprite != null && sprite.visible) {
				sprite.zap( enemy.pos );
			}
			
			spend( TIME_TO_ZAP );
			
			if (hit( this, enemy, true )) {
				int dmg = Random.NormalIntRange(4, 13);
				if (Dungeon.level.water[enemy.pos] && !enemy.flying) {
					dmg *= 2f;
				}
				enemy.damage( dmg, new LightningBolt() );
				
				enemy.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
				enemy.sprite.flash();
				
				if (enemy == Dungeon.hero) {
					
					Camera.main.shake( 2, 0.3f );
					
					if (!enemy.isAlive()) {
						Dungeon.fail( getClass() );
						GLog.n( Messages.get(this, "zap_kill") );
					}
				}
			} else {
				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}
			
			if (sprite != null && sprite.visible) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				return true;
			}
		}
	}
	
	@Override
	public void call() {
		next();
	}
	
}
