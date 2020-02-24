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

package com.shatteredpixel.yasd.general.actors.mobs;

import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.buffs.Terror;
import com.shatteredpixel.yasd.general.actors.buffs.Weakness;
import com.shatteredpixel.yasd.general.effects.CellEmitter;
import com.shatteredpixel.yasd.general.effects.Speck;
import com.shatteredpixel.yasd.general.effects.particles.ShadowParticle;
import com.shatteredpixel.yasd.general.items.KindOfWeapon;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.yasd.general.items.weapon.enchantments.Grim;
import com.shatteredpixel.yasd.general.levels.Level;
import com.shatteredpixel.yasd.general.levels.PrisonLevel;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.sprites.WraithSprite;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Wraith extends RangedMob {

	private static final float SPAWN_DELAY	= 2f;


	private static final float BLINK_CHANCE	= 0.125f;

	private int level;
	
	{
		spriteClass = WraithSprite.class;
		
		HP = HT = 1;
		EXP = 0;

		maxLvl = -2;
		
		flying = true;

		properties.add(Property.UNDEAD);
		properties.add(Property.BLOB_IMMUNE);
		//Resistant to physical weapons.
		resistances.add(KindOfWeapon.class);
		resistances.add(Char.class);
	}
	
	private static final String LEVEL = "level";

	public Wraith() {
		this(Dungeon.depth);
	}

	public Wraith(int level) {
		this.level = level;
		adjustStats(level);
	}

	@Override
	public void add(Buff buff) {
		if (!(buff.type == Buff.buffType.NEGATIVE)) {
			super.add( buff );
		}
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getInt( LEVEL );
		adjustStats( level );
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2 + (int) (level*0.67), 3 + level );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10 + level;
	}
	
	private void adjustStats(int level) {
		this.level = level;
		defenseSkill = attackSkill(null) - 3;
		HP = HT = 5 + Math.round(level * 1.5f);
		enemySeen = true;
	}

	@Override
	public boolean reset() {
		state = WANDERING;
		return true;
	}
	
	private static Wraith spawnNeighbor(int pos) {
		ArrayList<Integer> locations = new  ArrayList<>();
		for (int n : PathFinder.NEIGHBOURS8) {
			int cell = pos + n;
			if (Dungeon.level.passable[cell] && Actor.findChar( cell ) == null) {
				locations.add(cell);
			}
		}
		if (locations.size() > 0) {
			return spawnAt(Random.element(locations), false);
		} else {
			return null;
		}
	}

	public static Wraith spawnAt( int pos ) {
		return spawnAt(pos, true);
	}
	
	public static Wraith spawnAt( int pos, boolean useNeighbors ) {
		if (Dungeon.level.passable[pos] && Actor.findChar( pos ) == null) {
			
			Wraith w = new  Wraith();
			w.adjustStats( Dungeon.depth );
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add( w, SPAWN_DELAY );
			
			w.sprite.alpha( 0 );
			w.sprite.parent.add( new  AlphaTweener( w.sprite, 1, 0.5f ) );
			
			w.sprite.emitter().burst( ShadowParticle.CURSE, 5 );
			
			return w;
		} else {
			if (useNeighbors) {
				return spawnNeighbor(pos);
			} else {
				return null;
			}
		}
	}
	
	{
		immunities.add( Grim.class );
		immunities.add( Terror.class );
	}

	private void blink() {

		ArrayList<Integer> cells = new  ArrayList<>();

		for( Integer cell : Dungeon.level.getPassableCellsList() ){
			if( pos != cell && Dungeon.hero.fieldOfView[ cell ] ) {
				cells.add( cell );
			}
		}

		int newPos = !cells.isEmpty() ? Random.element( cells ) : pos ;

		if (Dungeon.hero.fieldOfView[pos]) {
			CellEmitter.get(pos).start( ShadowParticle.UP, 0.01f, Random.IntRange(5, 10) );
		}

		if (Dungeon.hero.fieldOfView[newPos]) {
			CellEmitter.get(newPos).start(ShadowParticle.MISSILE, 0.01f, Random.IntRange(5, 10));
		}

		ScrollOfTeleportation.appear( this, newPos );

		move( newPos );
	}

	@Override
	public int magicalDamageRoll() {
		return damageRoll()/2;
	}

	@Override
	public int magicalAttackProc(Char enemy, int damage) {
		if (Random.Int(3) == 0) {
			Buff.prolong(enemy, Weakness.class, Weakness.DURATION/4f);
		}
		return super.magicalAttackProc(enemy, damage);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {

		if ( distance( enemy ) <= 1 && isAlive() ) {

			int healed = damage/2;

			if (healed > 0) {

				HP += Math.min(missingHP(), healed);

				if( sprite.visible ) {
					sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				}
			}
		}

		return damage;
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		if (!rooted && Random.Float() < BLINK_CHANCE) {
			blink();
		}

		return super.doAttack(enemy);
	}

	@Override
	public boolean fleesAtMelee() {
		return false;
	}
}