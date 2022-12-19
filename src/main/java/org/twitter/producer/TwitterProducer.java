package org.twitter.producer;

//Import classes:
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.JSON;
import com.twitter.clientlib.model.*;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;

import java.io.InputStream;

import com.google.common.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class TwitterProducer {

	private static String TOPIC_NAME = "tweets";

	public static Producer<Long, String> getProducer() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        
        return new KafkaProducer<>(properties);
	}
	
	public static void main(String[] args) {

		TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(TwitterConfig.BEARER_TOKEN);
		TwitterApi apiInstance = new TwitterApi(credentials);

		BlockingQueue<String> queue = new LinkedBlockingDeque<>(50000);
		
		Set<String> tweetFields = new HashSet<>(Arrays.asList("created_at",
				"conversation_id", "public_metrics", "source", "lang"));
		Set<String> expansions = new HashSet<>(Arrays.asList("author_id", "geo.place_id"));
		Set<String> placeFields = new HashSet<>(Arrays.asList("contained_within", "country", "country_code", "geo", "name", "place_type"));
		
		try {
			InputStream result = apiInstance.tweets().searchStream()
					.tweetFields(tweetFields)
					.expansions(expansions)
					.placeFields(placeFields)
					.execute();
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(result));
				String line = reader.readLine();
				Producer<Long, String> producer = getProducer();
				
				while (line != null) {
					try {
						line = reader.readLine();
						System.out.println(line);
						ProducerRecord<Long, String> message = new ProducerRecord<>(TOPIC_NAME, line);
						producer.send(message);
					} catch (NullPointerException e) {
						System.out.println("Null tweet detected");
						line = reader.readLine();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e);
			}
		} catch (ApiException e) {
			System.err.println("Exception when calling TweetsApi#searchStream");
			System.err.println("Status code: " + e.getCode());
			System.err.println("Reason: " + e.getResponseBody());
			System.err.println("Response headers: " + e.getResponseHeaders());
			e.printStackTrace();
		}
	}
}
