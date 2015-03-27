package com.globalways.csscli.view;

import com.globalways.csscli.R;

/**
 * 菜单项实体类
 * 
 * @author James.Fan
 *
 */
public class MenuItemEntity {
	/** 菜单ID **/
	public int menuId;
	/** 菜单显示的文字 **/
	public String menuText;
	/** 字体颜色 **/
	public Color color;

	public MenuItemEntity(int menuId, String menuText, Color color) {
		super();
		this.menuId = menuId;
		this.menuText = menuText;
		this.color = color;
	}

	public enum Color {
		NROMAL(R.color.base_black), ALERT(android.R.color.holo_red_dark);
		private int colorId;

		private Color(int colorId) {
			this.colorId = colorId;
		}

		public int getColorId() {
			return colorId;
		}
	}
	
	public enum Type{
		SINGLE, TOP, BOTTOM, MIDDLE
	}
}
