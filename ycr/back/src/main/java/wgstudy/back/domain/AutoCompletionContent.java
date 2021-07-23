package wgstudy.back.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoCompletionContent implements Comparable<AutoCompletionContent> {
	private String title;
	private String channelImg;
	private long subscribers;
	
	@Override
	public int compareTo(AutoCompletionContent o) {
		return (int) -(this.subscribers - o.subscribers);
	}
}
