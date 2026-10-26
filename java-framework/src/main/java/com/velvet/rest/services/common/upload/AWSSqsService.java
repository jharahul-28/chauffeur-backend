package com.velvet.rest.services.common.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service("awsSqsService")
public class AWSSqsService {

	@Autowired
	private SqsClient amazonSQS;

	public void sendMessage(String queueUrl, String messageBody, Integer delayInSeconds) {
		SendMessageRequest sendMsgRequest = SendMessageRequest.builder().queueUrl(queueUrl).messageBody(messageBody)
				.build();
		if(delayInSeconds != null){
			sendMsgRequest = sendMsgRequest.toBuilder().delaySeconds(delayInSeconds).build();
		}
		amazonSQS.sendMessage(sendMsgRequest);
	}
}
