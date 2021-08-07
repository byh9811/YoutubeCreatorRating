package wgstudy.back.domain;

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
	private int ATK;		// total & recent views
	private int DEF;		// subs per views
	private int LV;			// ??
	private int EXP;		// ??
	private String characterImg;
	private String itemImg;
	private String itemInfo;
}
