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

package com.shatteredpixel.yasd.items.food;

import com.shatteredpixel.yasd.Holiday;
import com.shatteredpixel.yasd.actors.buffs.Buff;
import com.shatteredpixel.yasd.actors.buffs.Hunger;
import com.shatteredpixel.yasd.actors.buffs.Recharging;
import com.shatteredpixel.yasd.actors.hero.Hero;
import com.shatteredpixel.yasd.effects.Speck;
import com.shatteredpixel.yasd.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.yasd.messages.Messages;
import com.shatteredpixel.yasd.sprites.ItemSpriteSheet;

import java.util.Calendar;

public class Pasty extends Food {

	private static Holiday holiday;

	static{
		holiday = Holiday.getHoliday();
	}

	{
		reset();

		energy = Hunger.STARVING;

		bones = true;
	}
	
	@Override
	public void reset() {
		super.reset();
		switch(holiday){
			case NONE:
				name = Messages.get(this, "pasty");
				image = ItemSpriteSheet.PASTY;
				break;
			case HWEEN:
				name = Messages.get(this, "pie");
				image = ItemSpriteSheet.PUMPKIN_PIE;
				break;
			case XMAS:
				name = Messages.get(this, "cane");
				image = ItemSpriteSheet.CANDY_CANE;
				break;
		}
	}
	
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		
		switch(holiday){
			case NONE:
				break; //do nothing extra
			case HWEEN:
				//heals for 10% max hp
				hero.HP = Math.min(hero.HP + hero.HT/10, hero.HT);
				hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
				break;
			case XMAS:
				Buff.affect( hero, Recharging.class, 2f ); //half of a charge
				ScrollOfRecharging.charge( hero );
				break;
		}
	}

	@Override
	public String info() {
		switch(holiday){
			case NONE: default:
				return Messages.get(this, "pasty_desc");
			case HWEEN:
				return Messages.get(this, "pie_desc");
			case XMAS:
				return Messages.get(this, "cane_desc");
		}
	}
	
	@Override
	public int price() {
		return 20 * quantity;
	}
}
