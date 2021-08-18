package wgstudy.back.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelInfo {
	private String channelImg;
	private String title;
	private String description;
	private long subscribers;
	private int ATK;		// recent averge views
	private int DEF;		// subs / 2
	private int LV;			// (ATK/5) + DEF / penalty * Equip
	private String characterImg;
	private String itemImg;
	private List<String> items;
//	private List<String> categories;
	
	public ChannelInfo() {
		items = new ArrayList<String>();
//		categories = new ArrayList<String>();
	}
}
