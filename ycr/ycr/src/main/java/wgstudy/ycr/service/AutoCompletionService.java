package wgstudy.ycr.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;

import wgstudy.ycr.domain.AutoCompletion;

@Service
public class AutoCompletionService implements AutoCompletionProvider {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final long NUMBER_OF_VIDEOS_RETURNED = 8;
	private static YouTube youtube;
	
	@Override
	public AutoCompletion run(String name) {
		AutoCompletion ac = new AutoCompletion();
		
		try {
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException { }
			}).setApplicationName("youtube-video-duration-get").build();
			
			YouTube.Channels.List channels = youtube.channels().list("snippet");
			channels.setKey("AIzaSyCjOlrNkkzNNTkA8ZqkKXfY7n9OA-CkLIE");
			channels.setForUsername(name);
			channels.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			List<Channel> ChannelList = channels.execute().getItems();
			
			if (ChannelList != null) {
				prettyPrint(ChannelList.iterator(), ac);
			}
		}
		catch (GoogleJsonResponseException e) {
			System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return ac;
	}

	private void prettyPrint(Iterator<Channel> iterator, AutoCompletion ac) {
		System.out.println("\n=============================================================");
	    System.out.println("=============================================================\n");

	    if (!iterator.hasNext()) {
	      System.out.println(" There aren't any results for your query.");
	    }

	    while (iterator.hasNext()) {
	    	Channel singleChannel = iterator.next();

	    	if (singleChannel.getKind().equals("youtube#channel")) {
	    		ChannelSnippet snippet = singleChannel.getSnippet();
	    		String title = snippet.getTitle();
	    		String profile = snippet.getThumbnails().getDefault().getUrl();

	    		System.out.println(" Title: " + title);
	    		System.out.println(" Profile: " + profile);
	    		System.out.println("\n-------------------------------------------------------------\n");

	    		JSONObject jo = new JSONObject();
	    		jo.put("title", title);
	    		jo.put("profile", profile);
	    		
	    		ac.getChannelLists().put(jo);
	    	}
	    }
	}
}
