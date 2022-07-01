/*
 * MIT License
 *
 * Copyright (c) 2021 OpeningO Co.,Ltd.
 *
 *    https://openingo.org
 *    contactus(at)openingo.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.openingo.spring.boot.extension.mq.rocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import javax.annotation.Resource;
import java.util.Set;

/**
 * AbstractProducer
 *
 * @author Qicz
 * @since 2021/7/30 17:17
 */
@Slf4j
public abstract class AbstractProducer<T> {

	@Resource
	protected RocketMQTemplate rocketMqTemplate;

	/**
	 * sync send message
	 * @param topic mq destination
	 * @param payload data
	 * @return true is ok else if false
	 */
	public boolean syncSendMessage(String topic, T payload) {
		return this.send(topic, payload);
	}

	/**
	 * sync send message
	 * @param topic mq topic
	 * @param tags mq tags
	 * @param payload data
	 * @return true is ok else if false
	 */
	public boolean syncSendMessage(String topic, String tags, T payload) {
		return this.send(String.format("%s:%s", topic, tags), payload);
	}

	/**
	 * async send message
	 * @param topic mq topic
	 * @param payload data
	 * @param sendCallback callback
	 */
	public void asyncSendMessage(String topic, T payload, SendCallback sendCallback) {
		this.send(topic, payload, sendCallback);
	}

	/**
	 * async send message
	 * @param topic mq topic
	 * @param tags mq tags
	 * @param payload data
	 * @param sendCallback callback
	 */
	public void asyncSendMessage(String topic, String tags, T payload, SendCallback sendCallback) {
		this.send(String.format("%s:%s", topic, tags), payload, sendCallback);
	}

	/**
	 * sync send messages
	 * @param topic mq destination
	 * @param payloads data
	 * @return true is ok else if false
	 */
	public boolean syncSendMessages(String topic, Set<T> payloads) {
		return this.send(topic, payloads);
	}

	/**
	 * sync send messages
	 * @param topic mq topic
	 * @param tags mq tags
	 * @param payloads data
	 * @return true is ok else if false
	 */
	public boolean syncSendMessages(String topic, String tags, Set<T> payloads) {
		return this.send(String.format("%s:%s", topic, tags), payloads);
	}

	/**
	 * async send message
	 * @param topic mq topic
	 * @param payloads data
	 * @param sendCallback callback
	 */
	public void asyncSendMessage(String topic, Set<T> payloads, SendCallback sendCallback) {
		this.send(topic, payloads, sendCallback);
	}

	/**
	 * async send messages
	 * @param topic mq topic
	 * @param tags mq tags
	 * @param payloads data
	 * @param sendCallback callback
	 */
	public void asyncSendMessages(String topic, String tags, Set<T> payloads, SendCallback sendCallback) {
		this.send(String.format("%s:%s", topic, tags), payloads, sendCallback);
	}

	private boolean send(String destination, Object payloads) {
		log.info("sync send destination {} payloads {}", destination, payloads);
		SendResult sendResult = this.rocketMqTemplate.syncSend(destination, payloads);
		boolean ret = SendStatus.SEND_OK.equals(sendResult.getSendStatus());
		log.info("send data to rocketMQ result {} {}!", sendResult, ret ? "success" : "failed");
		return ret;
	}

	private void send(String destination, Object payloads, SendCallback sendCallback) {
		log.info("async send destination {} payloads {}", destination, payloads);
		this.rocketMqTemplate.asyncSend(destination, payloads, sendCallback);
	}
}
