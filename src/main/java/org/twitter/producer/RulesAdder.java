package org.twitter.producer;

import com.twitter.clientlib.ApiClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.Configuration;
import com.twitter.clientlib.auth.*;
import com.twitter.clientlib.model.*;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;

import com.twitter.clientlib.api.TweetsApi;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.time.OffsetDateTime;

public class RulesAdder {
  public static void main(String[] args) {
    // Set the credentials based on the API's "security" tag values.
    // Check the API definition in https://api.twitter.com/2/openapi.json
    // When multiple options exist, the SDK supports only "OAuth2UserToken" or "BearerToken"

    // Uncomment and set the credentials configuration
      
    // Configure HTTP bearer authorization:
     TwitterCredentialsBearer credentials = new TwitterCredentialsBearer("BEARER_TOKEN");
        TwitterApi apiInstance = new TwitterApi(credentials);

    // Set the params values
    AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest(); // AddOrDeleteRulesRequest | type name = new type(arguments);
    RuleNoId newRule = new RuleNoId();
    newRule.value("cat has:media");
    newRule.tag("cats with media");
    AddRulesRequest addRuleRequest = new AddRulesRequest();
    addRuleRequest.addAddItem(newRule);
    addOrDeleteRulesRequest.setActualInstance(addRuleRequest);
    Boolean dryRun = false; // Boolean | Dry Run can be used with both the add and delete action, with the expected result given, but without actually taking any action in the system (meaning the end state will always be as it was when the request was submitted). This is particularly useful to validate rule changes.
    try {
           AddOrDeleteRulesResponse result = apiInstance.tweets().addOrDeleteRules(addOrDeleteRulesRequest)
            .dryRun(dryRun)
            .execute();
            System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TweetsApi#addOrDeleteRules");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}