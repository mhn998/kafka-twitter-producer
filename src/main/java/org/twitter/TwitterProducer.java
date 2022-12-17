package org.twitter;

//Import classes:
import com.twitter.clientlib.ApiClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.Configuration;
import com.twitter.clientlib.JSON;
import com.twitter.clientlib.auth.*;
import com.twitter.clientlib.model.*;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.api.TweetsApi;

import java.io.InputStream;

import com.google.common.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.time.OffsetDateTime;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class TwitterProducer {
	public static Producer<Long, String> getProducer() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // username and password
        
        return new KafkaProducer<>(properties);
	}
	
	public static void main(String[] args) {
		// Set the credentials based on the API's "security" tag values.
		// Check the API definition in https://api.twitter.com/2/openapi.json
		// When multiple options exist, the SDK supports only "OAuth2UserToken"
		// or "BearerToken"

		// Uncomment and set the credentials configuration

		// Configure HTTP bearer authorization:
		TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(
				"AAAAAAAAAAAAAAAAAAAAAErhkQEAAAAA6hI1GFWoTinlRHvKNnjeFnwZs6E%3DeVQjkumZSp5aelRkUo0OSZOhtsZqVIG47xc7RRgYJr7U4wyuvE");
		TwitterApi apiInstance = new TwitterApi(credentials);

		// Set the params values
		Set<String> expansions = new HashSet<>(Arrays.asList("author_id"));

		// Set<String> | A comma separated list of Tweet fields to display.
		Set<String> tweetFields = new HashSet<>(Arrays.asList("created_at",
				"conversation_id"));

		// Integer | The number of minutes of backfill requested
		Integer backfillMinutes = 5;
		BlockingQueue<String> queue = new LinkedBlockingDeque<>(50000);
		try {
			InputStream result = apiInstance.tweets().sampleStream()
//					.backfillMinutes(backfillMinutes)
					// .startTime(startTime)
					// .endTime(endTime)
//					.tweetFields(tweetFields).expansions(expansions)
					// .mediaFields(mediaFields)
					// .pollFields(pollFields)
					// .userFields(userFields)
					// .placeFields(placeFields)
					.execute();
			try {
				JSON json = new JSON();
				Type localVarReturnType = new TypeToken<StreamingTweetResponse>() {
				}.getType();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(result));
				String line = reader.readLine();
				Producer<Long, String> producer = getProducer();
				
				while (line != null) {
					TimeUnit.SECONDS.sleep(5);
//					System.out.println(json.getGson()
//							.fromJson(line, localVarReturnType).toString());
					line = reader.readLine();
					System.out.println(line);
					ProducerRecord<Long, String> message = new ProducerRecord<Long, String>("first-topic", line);
					producer.send(message);

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
