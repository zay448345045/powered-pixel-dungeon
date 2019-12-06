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
//wands with AOE effects count here (e.g. fireblast, blast wave), but wands with indrect damage do not (e.g. corrosion, transfusion)
public abstract class DamageWand extends Wand{

	public int min() {
		return (int) min(actualLevel());
	}


	public abstract float min(float lvl);

	public int max(){
		return (int) max(actualLevel());
	}

	public abstract float max(float lvl);

	@Override
	protected int initialCharges() {
		return 3;
	}

	public int damageRoll(){
		return damageRoll(actualLevel());
	}

	public int damageRoll(float lvl){
		return Random.NormalIntRange((int)min(lvl), (int)max(lvl));
	}

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc",  min(), max());
		else
			return Messages.get(this, "stats_desc", (int) min(0), (int) max(0));
	}
}
