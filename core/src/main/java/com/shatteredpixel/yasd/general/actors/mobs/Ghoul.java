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
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.buffs.Corruption;
import com.shatteredpixel.yasd.general.effects.Pushing;
import com.shatteredpixel.yasd.general.levels.features.Chasm;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.sprites.GhoulSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

//TODO some aspects of existing enemy AI make these really wonky. Need to address that.
public class Ghoul extends Mob {

	{
		spriteClass = GhoulSprite.class;

		healthFactor = 0.7f;
		damageFactor = 0.8f;

		EXP = 5;
		maxLvl = 20;

		SLEEPING = new Sleeping();
		WANDERING = new Wandering();
		HUNTING = new Hunting();
		state = SLEEPING;

		properties.add(Property.UNDEAD);
	}

	private int partnerID = -1;
	private int timesDowned = 0;

	private static final String PARTNER_ID = "partner_id";
	private static final String TIMES_DOWNED = "times_downed";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( PARTNER_ID, partnerID );
		bundle.put( TIMES_DOWNED, timesDowned );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		partnerID = bundle.getInt( PARTNER_ID );
		timesDowned = bundle.getInt( TIMES_DOWNED );
	}

	@Override
	public float spawningWeight() {
		return 0.5f;
	}

	@Override
	protected boolean act() {
		//create a child
		if (partnerID == -1){

			ArrayList<Integer> candidates = new ArrayList<>();

			int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
			for (int n : neighbours) {
				if (Dungeon.level.passable(n) && Actor.findChar( n ) == null) {
					candidates.add( n );
				}
			}

			if (!candidates.isEmpty()){
				Ghoul child = new Ghoul();
				child.partnerID = this.id();
				this.partnerID = child.id();
				if (state != SLEEPING) {
					child.state = child.WANDERING;
				}

				child.pos = Random.element( candidates );

				Dungeon.level.occupyCell(child);

				GameScene.add( child );
				if (sprite.visible) {
					Actor.addDelayed( new Pushing( child, pos, child.pos ), -1 );
				}
			}

		}
		return super.act();
	}

	@Override
	public void die(DamageSrc cause) {
		if (cause.getCause() != Chasm.class && cause.getCause() != GhoulLifeLink.class){
			Ghoul nearby = GhoulLifeLink.searchForHost(this);
			if (nearby != null){
				Actor.remove(this);
				Dungeon.level.mobs.remove( this );
				timesDowned++;
				Buff.append(nearby, GhoulLifeLink.class).set(timesDowned*5, this);
				((GhoulSprite)sprite).crumple();
			} else {
				super.die(cause);
			}
		} else {
			super.die(cause);
		}
	}

	@Override
	protected synchronized void onRemove() {
		for (Buff buff : buffs()) {
			//corruption is preserved
			if (!(buff instanceof Corruption)) buff.detach();
		}
	}

	@Override
	protected void onAdd() {
		spend(-cooldown());
		super.onAdd();
	}

	private class Sleeping extends Mob.Sleeping {
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			Ghoul partner = (Ghoul) Actor.findById( partnerID );
			if (partner != null && partner.state != partner.SLEEPING){
				state = WANDERING;
				target = partner.pos;
				return true;
			} else {
				return super.act( enemyInFOV, justAlerted );
			}
		}
	}

	private class Wandering extends Mob.Wandering {

		@Override
		protected boolean continueWandering() {
			enemySeen = false;

			Ghoul partner = (Ghoul) Actor.findById( partnerID );
			if (partner != null && (partner.state != partner.WANDERING || Dungeon.level.distance( pos,  partner.target) > 1)){
				target = partner.pos;
				int oldPos = pos;
				if (getCloser( target )){
					spend( 1 / speed() );
					return moveSprite( oldPos, pos );
				} else {
					spend( TICK );
					return true;
				}
			} else {
				return super.continueWandering();
			}
		}
	}

	//TODO currently very similar to super.Hunting and is largely a stop-gap, need to refactor
	private class Hunting extends Mob.Hunting {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				} else if (enemy == null) {
					state = WANDERING;
					target = Dungeon.level.randomDestination(Ghoul.this);
					return true;
				}

				int oldPos = pos;
				if (target != -1 && getCloser( target )) {

					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {

					Ghoul partner = (Ghoul) Actor.findById( partnerID );
					if (!enemyInFOV) {
						spend( TICK );
						sprite.showLost();
						state = WANDERING;
						target = Dungeon.level.randomDestination(Ghoul.this);

						//try to move closer to partner if they can't move to hero
					} else if (partner != null && getCloser(partner.pos)) {
						spend( 1 / speed() );
						return moveSprite( oldPos,  pos );
					} else {
						spend( TICK );
					}
					return true;
				}
			}
		}
	}

	public static class GhoulLifeLink extends Buff{

		private Ghoul ghoul;
		private int turnsToRevive;

		@Override
		public boolean act() {
			ghoul.sprite.visible = Dungeon.level.heroFOV[ghoul.pos];

			if (!target.fieldOfView[ghoul.pos] && Dungeon.level.distance(ghoul.pos, target.pos) >= 4){
				detach();
				return true;
			}

			turnsToRevive--;
			if (turnsToRevive <= 0){
				ghoul.HP = Math.round(ghoul.HT/10f);
				if (Actor.findChar( ghoul.pos ) != null) {
					ArrayList<Integer> candidates = new ArrayList<>();
					for (int n : PathFinder.NEIGHBOURS8) {
						int cell = ghoul.pos + n;
						if ((Dungeon.level.passable(cell) || Dungeon.level.avoid(cell)) && Actor.findChar( cell ) == null) {
							candidates.add( cell );
						}
					}
					if (candidates.size() > 0) {
						int newPos = Random.element( candidates );
						Actor.addDelayed( new Pushing( ghoul, ghoul.pos, newPos ), -1 );
						ghoul.pos = newPos;

					} else {
						spend(TICK);
						return true;
					}
				}
				Actor.add(ghoul);
				Dungeon.level.mobs.add(ghoul);
				Dungeon.level.occupyCell( ghoul );
				super.detach();
				return true;
			}

			spend(TICK);
			return true;
		}

		public void set(int turns, Ghoul ghoul){
			this.ghoul = ghoul;
			turnsToRevive = turns;
		}

		@Override
		public void fx(boolean on) {
			if (on && ghoul != null && ghoul.sprite == null){
				GameScene.addSprite(ghoul);
				((GhoulSprite)ghoul.sprite).crumple();
			}
		}

		@Override
		public void detach() {
			super.detach();
			Ghoul newHost = searchForHost(ghoul);
			if (newHost != null){
				newHost.add(this);
			} else {
				ghoul.die(new DamageSrc(Element.NATURAL, this));
			}
		}

		private static final String GHOUL = "ghoul";
		private static final String LEFT  = "left";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(GHOUL, ghoul);
			bundle.put(LEFT, turnsToRevive);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			ghoul = (Ghoul) bundle.get(GHOUL);
			turnsToRevive = bundle.getInt(LEFT);
		}

		public static Ghoul searchForHost(Ghoul dieing){

			for (Char ch : Actor.chars()){
				if (ch != dieing && ch instanceof Ghoul && ch.alignment == dieing.alignment){
					if (ch.fieldOfView == null){
						ch.fieldOfView = new boolean[Dungeon.level.length()];
						Dungeon.level.updateFieldOfView( ch, ch.fieldOfView );
					}
					if (ch.fieldOfView[dieing.pos] || Dungeon.level.distance(ch.pos, dieing.pos) < 4){
						return (Ghoul) ch;
					}
				}
			}
			return null;
		}
	}
}