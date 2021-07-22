package wgstudy.back.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoCompletion {
	private List<AutoCompletionContent> channelLists;
	
	public AutoCompletion() {
		channelLists = new ArrayList<AutoCompletionContent>();
	}
}