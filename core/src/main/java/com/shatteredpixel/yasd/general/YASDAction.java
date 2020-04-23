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

package com.shatteredpixel.yasd.general;

import com.badlogic.gdx.Input;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;

public class YASDAction extends GameAction {

	//--Existing actions from GameAction
	public static final int NONE        = GameAction.NONE;

	public static final int BACK        = GameAction.BACK;
	public static final int MENU        = GameAction.MENU;
	//--

	public static final int HERO_INFO   = 3;
	public static final int JOURNAL     = 4;

	public static final int WAIT        = 5;
	public static final int SEARCH      = 6;

	public static final int INVENTORY   = 7;
	public static final int QUICKSLOT_1 = 8;
	public static final int QUICKSLOT_2 = 9;
	public static final int QUICKSLOT_3 = 10;
	public static final int QUICKSLOT_4 = 11;

	public static final int TAG_ATTACK  = 12;
	public static final int TAG_DANGER  = 13;
	public static final int TAG_ACTION  = 14;
	public static final int TAG_LOOT    = 15;
	public static final int TAG_RESUME  = 16;

	public static final int ZOOM_IN     = 17;
	public static final int ZOOM_OUT    = 18;

	public static final int N           = 19;
	public static final int E           = 20;
	public static final int S           = 21;
	public static final int W           = 22;
	public static final int NE          = 23;
	public static final int SE          = 24;
	public static final int SW          = 25;
	public static final int NW          = 26;

	public static final int TOTAL_ACTIONS = 27;

	public static void initDefaults() {

		KeyBindings.addName(NONE,           "none");

		KeyBindings.addName(BACK,           "back");
		KeyBindings.addName(MENU,           "menu");

		KeyBindings.addName(HERO_INFO,      "hero_info");
		KeyBindings.addName(JOURNAL,        "journal");

		KeyBindings.addName(WAIT,           "wait");
		KeyBindings.addName(SEARCH,         "search");

		KeyBindings.addName(INVENTORY,      "inventory");
		KeyBindings.addName(QUICKSLOT_1,    "quickslot_1");
		KeyBindings.addName(QUICKSLOT_2,    "quickslot_2");
		KeyBindings.addName(QUICKSLOT_3,    "quickslot_3");
		KeyBindings.addName(QUICKSLOT_4,    "quickslot_4");

		KeyBindings.addName(TAG_ATTACK,     "tag_attack");
		KeyBindings.addName(TAG_DANGER,     "tag_danger");
		KeyBindings.addName(TAG_ACTION,     "tag_action");
		KeyBindings.addName(TAG_LOOT,       "tag_loot");
		KeyBindings.addName(TAG_RESUME,     "tag_resume");

		KeyBindings.addName(ZOOM_IN,        "zoom_in");
		KeyBindings.addName(ZOOM_OUT,       "zoom_out");

		KeyBindings.addName(N,              "n");
		KeyBindings.addName(E,              "e");
		KeyBindings.addName(S,              "s");
		KeyBindings.addName(W,              "w");
		KeyBindings.addName(NE,             "ne");
		KeyBindings.addName(SE,             "se");
		KeyBindings.addName(SW,             "sw");
		KeyBindings.addName(NW,             "nw");

		//default key bindings
		KeyBindings.addBinding( Input.Keys.BACK, GameAction.BACK );
		KeyBindings.addBinding( Input.Keys.MENU, GameAction.MENU );

		KeyBindings.addBinding( Input.Keys.H, YASDAction.HERO_INFO );
		KeyBindings.addBinding( Input.Keys.J, YASDAction.JOURNAL );

		KeyBindings.addBinding( Input.Keys.SPACE,    YASDAction.WAIT );
		KeyBindings.addBinding( Input.Keys.S,        YASDAction.SEARCH );

		KeyBindings.addBinding( Input.Keys.I,  YASDAction.INVENTORY );
		KeyBindings.addBinding( Input.Keys.Q,  YASDAction.QUICKSLOT_1 );
		KeyBindings.addBinding( Input.Keys.W,  YASDAction.QUICKSLOT_2 );
		KeyBindings.addBinding( Input.Keys.E,  YASDAction.QUICKSLOT_3 );
		KeyBindings.addBinding( Input.Keys.R,  YASDAction.QUICKSLOT_4 );

		KeyBindings.addBinding( Input.Keys.A,     YASDAction.TAG_ATTACK );
		KeyBindings.addBinding( Input.Keys.TAB,   YASDAction.TAG_DANGER );
		KeyBindings.addBinding( Input.Keys.D,     YASDAction.TAG_ACTION );
		KeyBindings.addBinding( Input.Keys.ENTER, YASDAction.TAG_LOOT );
		KeyBindings.addBinding( Input.Keys.T,     YASDAction.TAG_RESUME );

		KeyBindings.addBinding( Input.Keys.PLUS,   YASDAction.ZOOM_IN );
		KeyBindings.addBinding( Input.Keys.EQUALS, YASDAction.ZOOM_IN );
		KeyBindings.addBinding( Input.Keys.MINUS,  YASDAction.ZOOM_OUT );

		KeyBindings.addBinding( Input.Keys.UP,    YASDAction.N );
		KeyBindings.addBinding( Input.Keys.RIGHT, YASDAction.E );
		KeyBindings.addBinding( Input.Keys.DOWN,  YASDAction.S );
		KeyBindings.addBinding( Input.Keys.LEFT,  YASDAction.W );
		KeyBindings.addBinding( Input.Keys.NUMPAD_5,  YASDAction.WAIT );
		KeyBindings.addBinding( Input.Keys.NUMPAD_8,  YASDAction.N );
		KeyBindings.addBinding( Input.Keys.NUMPAD_9,  YASDAction.NE );
		KeyBindings.addBinding( Input.Keys.NUMPAD_6,  YASDAction.E );
		KeyBindings.addBinding( Input.Keys.NUMPAD_3,  YASDAction.SE );
		KeyBindings.addBinding( Input.Keys.NUMPAD_2,  YASDAction.S );
		KeyBindings.addBinding( Input.Keys.NUMPAD_1,  YASDAction.SW );
		KeyBindings.addBinding( Input.Keys.NUMPAD_4,  YASDAction.W );
		KeyBindings.addBinding( Input.Keys.NUMPAD_7,  YASDAction.NW );

	}

	//file name? perhaps

	public static void loadBindings(){

	}

	public static void saveBindings(){

	}
}
