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

package com.shatteredpixel.yasd.general.effects;

import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.sprites.ItemSprite;
import com.shatteredpixel.yasd.general.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.utils.PointF;

public class WepSprite extends ItemSprite {

	public static final float TIME		= 0.6f;

	private Char source;
	private Char target;

	private float duration;
	private float passed;

	public WepSprite(Item item) {
		super( item.image(), null );
		originToCenter();

		duration = TIME;
		passed = 0;
	}

	public void setChars(Char source, Char target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public void update() {
		super.update();


		if ((passed += Game.elapsed) > duration) {
			kill();
			passed = 0;
		}
	}

	public void rotateEffect() {
		PointF src = DungeonTilemap.tileToWorld(source.pos);
		PointF dest = DungeonTilemap.tileToWorld(target.pos);
		PointF d = PointF.diff(dest, src);
		angle = 135 - (float) (Math.atan2(d.x, d.y) / 3.1415926 * 180);
		angularSpeed = 400;
		if (d.x > 0)
			x += 2 * SIZE / 3;
		else if (d.x < 0)
			x -= 2 * SIZE / 3;

		if (d.y > 0)
			y += 2 * SIZE / 3;
		else if (d.y < 0)
			y -= 2 * SIZE / 3;
		x = source.sprite.x;
		y = source.sprite.y;
	}

	public void stabEffect() {
		PointF src = DungeonTilemap.tileToWorld( source.pos );
		PointF dest = DungeonTilemap.tileToWorld( target.pos );
		PointF d = PointF.diff( dest, src );
		angle = 135 - (float)(Math.atan2( d.x, d.y ) / 3.1415926 * 180);

		x = source.sprite.x;
		y = source.sprite.y;

		PosTweener tweener = new PosTweener( this, dest, TIME );
		source.sprite.parent.add( tweener );
	}

	public static void show(Char ch, Char ch2, Item item ) {

		if (!ch.sprite.visible) {
			return;
		}

		WepSprite sprite = new WepSprite( item );
		sprite.source = ch;
		sprite.target = ch2;
		sprite.rotateEffect();
		ch.sprite.parent.add( sprite );
	}
}
