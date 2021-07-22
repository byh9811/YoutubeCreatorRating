package wgstudy.back.domain;

import org.json.JSONArray;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoCompletion {
	JSONArray channelLists;
	
	public AutoCompletion() {
		channelLists = new JSONArray();
	}
}