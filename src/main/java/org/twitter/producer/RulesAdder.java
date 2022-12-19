package org.twitter.producer;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.*;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;

import java.util.ArrayList;
import java.util.List;

public class RulesAdder {
	public static String[] WORLDCUP_RULE = {"(#worldcup OR #fifaworldcup OR fifa OR \"world cup\") has:geo", "worldcup related"};

	public static void setRules(TwitterCredentialsBearer credentials) {
		TwitterApi apiInstance = new TwitterApi(credentials);

		AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest();
		AddRulesRequest addRuleRequest = new AddRulesRequest();

		RuleNoId ruleOne = new RuleNoId();
		ruleOne.value(WORLDCUP_RULE[0]);
		ruleOne.tag(WORLDCUP_RULE[1]);

		addRuleRequest.addAddItem(ruleOne);
		// addRuleRequest.addAddItem(Configs.RULE_TWO.getRuleNoId());
		// addRuleRequest.addAddItem(Configs.RULE_THREE.getRuleNoId());
		// addRuleRequest.addAddItem(Configs.RULE_FOUR.getRuleNoId());
		// addRuleRequest.addAddItem(Configs.RULE_FIVE.getRuleNoId());

		addOrDeleteRulesRequest.setActualInstance(addRuleRequest);
		Boolean dryRun = false;

		try {
			AddOrDeleteRulesResponse result = apiInstance.tweets()
					.addOrDeleteRules(addOrDeleteRulesRequest).dryRun(dryRun)
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

	public static void deleteRules(TwitterCredentialsBearer credentials) {
		TwitterApi apiInstance = new TwitterApi(credentials);
		RulesLookupResponse result;
		List<String> ids = new ArrayList<>();

		try {
			result = apiInstance.tweets().getRules().execute();

			if(result.getData() == null) {
				System.out.println("No Rules to delete!");
				return;
			}

			for (Rule rule : result.getData()) {
				ids.add(rule.getId());
			}

			System.out.println(result);
		} catch (ApiException e) {
			System.err.println("Exception when calling TweetsApi#getRules");
			System.err.println("Status code: " + e.getCode());
			System.err.println("Reason: " + e.getResponseBody());
			System.err.println("Response headers: " + e.getResponseHeaders());
			e.printStackTrace();
		}

		AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest();
		DeleteRulesRequest deleteRulesRequest = new DeleteRulesRequest();
		DeleteRulesRequestDelete deleteRules = new DeleteRulesRequestDelete();

		deleteRules.ids(ids);
		deleteRulesRequest.delete(deleteRules);

		addOrDeleteRulesRequest.setActualInstance(deleteRulesRequest);
		Boolean dryRun = false;

		try {
			AddOrDeleteRulesResponse deleteReuslt = apiInstance.tweets()
					.addOrDeleteRules(addOrDeleteRulesRequest).dryRun(dryRun)
					.execute();
			System.out.println(deleteReuslt);
		} catch (ApiException e) {
			System.err.println("Exception when calling TweetsApi#deleteRules");
			System.err.println("Status code: " + e.getCode());
			System.err.println("Reason: " + e.getResponseBody());
			System.err.println("Response headers: " + e.getResponseHeaders());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(TwitterConfig.BEARER_TOKEN);

		setRules(credentials);
//		deleteRules(credentials);
	}
}
