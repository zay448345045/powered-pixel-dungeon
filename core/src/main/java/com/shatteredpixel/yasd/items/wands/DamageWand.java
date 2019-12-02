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

package com.shatteredpixel.yasd.items.wands;

import com.shatteredpixel.yasd.actors.hero.Hero;
import com.shatteredpixel.yasd.messages.Messages;
import com.watabou.utils.Random;

//for wands that directly damage a target
//wands with AOE effects count here (e.g. fireblast), but wands with indrect damage do not (e.g. venom, transfusion)
public abstract class DamageWand extends Wand{

	public int min() {
		if (curUser instanceof Hero) {
			return min(Math.round(effectiveness((Hero) curUser)));
		} else {
			return min(level() * 2);
		}
	}


	public abstract int min(int lvl);

	public int max(){
		if (curUser instanceof Hero) {
			return  max(Math.round(effectiveness((Hero)curUser)));
		} else {
			return max(level()*2);
		}
	}

	public abstract int max(int lvl);

	@Override
	protected int initialCharges() {
		return 3;
	}

	public int damageRoll(){
		return Random.NormalIntRange(min(), max());
	}

	public int damageRoll(int lvl){
		return Random.NormalIntRange(min(lvl), max(lvl));
	}

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", min(), max());
		else
			return Messages.get(this, "stats_desc", min(0), max(0));
	}
}
