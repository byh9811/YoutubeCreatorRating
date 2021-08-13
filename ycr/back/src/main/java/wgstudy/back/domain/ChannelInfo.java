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
	private String category;
	private long subscribers;
	private int ATK;		// recent averge views
	private int DEF;		// subs
	private int LV;			// (ATK/5) + DEF / penalty * Equip
	private String characterImg;
	private String itemImg;
	private List<String> items;
	
	public ChannelInfo() {
		items = new ArrayList<String>();
	}
}
