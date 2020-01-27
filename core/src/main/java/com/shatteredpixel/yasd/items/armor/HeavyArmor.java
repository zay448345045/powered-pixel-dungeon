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

package com.shatteredpixel.yasd.items.armor;

import com.shatteredpixel.yasd.messages.Messages;
import com.shatteredpixel.yasd.sprites.ItemSpriteSheet;

public class HeavyArmor extends Armor {

	{
		image = ItemSpriteSheet.ARMOR_PLATE;

		EVA = 0.6f;
		magicalDRFactor = 0.5f;
		DRfactor = 1.2f;
		speedFactor = 2/3f;
	}

	@Override
	public int image() {
		if (tier < 4) {
			return ItemSpriteSheet.ARMOR_PLATE;
		} else  {
			return ItemSpriteSheet.ARMOR_BANDED;
		}
	}

	@Override
	public String desc() {
		if (tier < 4) {
			return Messages.get(Plate.class, "desc");
		} else {
			return Messages.get(Lead.class, "desc");
		}
	}

	@Override
	public String name() {
		if (tier < 4) {
			return Messages.get(Plate.class, "name");
		} else  {
			return Messages.get(Lead.class, "name");
		}
	}

	private static class Plate extends Armor {}
	private static class Lead extends Armor {}

	@Override
	public int appearance() {
		return 5;
	}
}