package wgstudy.back.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import com.google.api.services.youtube.model.ChannelStatistics;
import com.google.api.services.youtube.model.SearchResult;

import wgstudy.back.domain.AutoCompletion;
import wgstudy.back.domain.AutoCompletionContent;

@Service
public class AutoCompletionService implements AutoCompletionProvider {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final long NUMBER_OF_VIDEOS_RETURNED_BY_NAME = 8;
	private static YouTube youtube;
	
	@Override
	public AutoCompletion run(String name) {
		AutoCompletion ac = new AutoCompletion();
		
		try {
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException { }
			}).setApplicationName("youtube-video-duration-get").build();
			
			List<SearchResult> searchResult = getChannelIdByName(name);
			
			if(searchResult.size()==0)
				return null;
			
			Iterator<SearchResult> iterator = searchResult.iterator();
			StringBuilder sb = new StringBuilder();
			while(true) {
				sb.append(iterator.next().getId().getChannelId().toString());
				if(iterator.hasNext())
					sb.append(",");
				else
					break;
			}
			System.out.println("ChannelIds: " + sb);

			List<Channel> channelList = getChannelSnippetByChannelId(sb.toString());
			prettyPrint(channelList.iterator(), ac);
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

	private List<SearchResult> getChannelIdByName(String name) throws IOException {
		YouTube.Search.List search = youtube.search().list("id");
		search.setKey("AIzaSyCjOlrNkkzNNTkA8ZqkKXfY7n9OA-CkLIE");
		search.setQ(name);
		search.setType("channel");
		search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED_BY_NAME);

		// To search in specific country
		Locale locale = Locale.KOREA;
		search.setRegionCode(locale.getCountry());
		search.setRelevanceLanguage(locale.getLanguage());
		
		// To sort search results [ relevance(default), rating, viewCount... ], But inaccurate method.
		// search.setOrder("videoCount");
		
		return search.execute().getItems();
	}

	private List<Channel> getChannelSnippetByChannelId(String id) throws IOException {
		YouTube.Channels.List channels = youtube.channels().list("id, snippet, statistics");
		channels.setKey("AIzaSyCjOlrNkkzNNTkA8ZqkKXfY7n9OA-CkLIE");
		channels.setId(id);
		return channels.execute().getItems();
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
	    		String channelId = singleChannel.getId();
	    		ChannelSnippet snippet = singleChannel.getSnippet();
	    		String title = snippet.getTitle();
	    		String chImg = snippet.getThumbnails().getDefault().getUrl();
	    		
	    		ChannelStatistics statistics = singleChannel.getStatistics();
	    		long subs;
	    		try {
		    		subs = statistics.getSubscriberCount().longValue();
	    		}
	    		catch (NullPointerException e) {
	    			subs = 0;
	    		}
	    		
	    		System.out.println(" Title: " + title);
	    		System.out.println(" Profile: " + chImg);
	    		System.out.println(" Subs: " + subs);
	    		System.out.println("\n-------------------------------------------------------------\n");
	    		
	    		AutoCompletionContent acc = new AutoCompletionContent();
	    		acc.setId(channelId);
	    		acc.setTitle(title);
	    		acc.setChannelImg(chImg);
	    		acc.setSubscribers(subs);
	    		
	    		ac.getChannelLists().add(acc);
	    	}
	    }
	    ac.getChannelLists().sort(null);
	}
}
