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

package org.openingo.spring.boot.extension.websocket;

import org.openingo.jdkits.json.JacksonKit;
import org.openingo.jdkits.validate.ValidateKit;

import javax.websocket.EncodeException;
import javax.websocket.SendHandler;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * InstantManager
 *
 * @author Qicz
 */
public class InstantManager {

    private ConcurrentHashMap<Integer, List<WebsocketServerEndpoint>> connections = new ConcurrentHashMap<>();

    private InstantManager() {

    }

    void addConnection(Integer connId, WebsocketServerEndpoint endpoint) {
        listEndpoint(connId).add(endpoint);
    }

    void removeConnection(Integer connId, WebsocketServerEndpoint endpoint) {
        listEndpoint(connId).remove(endpoint);
    }

    List<WebsocketServerEndpoint> listEndpoint(Integer connId) {
        List<WebsocketServerEndpoint> endpointList = this.connections.get(connId);
        if (ValidateKit.isNull(endpointList)) {
            endpointList = new CopyOnWriteArrayList<>();
            List<WebsocketServerEndpoint> oldEndpointList = this.connections.putIfAbsent(connId, endpointList);
            if (ValidateKit.isNotNull(oldEndpointList)) {
                endpointList = oldEndpointList;
            }
        }
        return endpointList;
    }

    /**
     * Send the message, blocking until the message is sent.
     * @param connId connect id
     * @param text  The text message to send.
     * @throws IllegalArgumentException if {@code text} is {@code null}.
     * @throws IOException if an I/O error occurs during the sending of the
     *                     message.
     */
    public static void sendText(Integer connId, String text) throws IOException {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getBasicRemote().sendText(text);
        }
    }

    /**
     * Send the message, blocking until the message is sent.
     * @param connId connect id
     * @param data  The binary message to send
     * @throws IllegalArgumentException if {@code data} is {@code null}.
     * @throws IOException if an I/O error occurs during the sending of the
     *                     message.
     */
    public static void sendBinary(Integer connId, ByteBuffer data) throws IOException {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getBasicRemote().sendBinary(data);
        }
    }

    /**
     * Sends part of a text message to the remote endpoint. Once the first part
     * of a message has been sent, no other text or binary messages may be sent
     * until all remaining parts of this message have been sent.
     *
     * @param connId connect id
     * @param fragment  The partial message to send
     * @param isLast    <code>true</code> if this is the last part of the
     *                  message, otherwise <code>false</code>
     * @throws IllegalArgumentException if {@code fragment} is {@code null}.
     * @throws IOException if an I/O error occurs during the sending of the
     *                     message.
     */
    public static void sendText(Integer connId, String fragment, boolean isLast) throws IOException {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getBasicRemote().sendText(fragment, isLast);
        }
    }

    /**
     * Sends part of a binary message to the remote endpoint. Once the first
     * part of a message has been sent, no other text or binary messages may be
     * sent until all remaining parts of this message have been sent.
     *
     * @param connId connect id
     * @param partialByte   The partial message to send
     * @param isLast        <code>true</code> if this is the last part of the
     *                      message, otherwise <code>false</code>
     * @throws IllegalArgumentException if {@code partialByte} is
     *                     {@code null}.
     * @throws IOException if an I/O error occurs during the sending of the
     *                     message.
     */
    public void sendBinary(Integer connId, ByteBuffer partialByte, boolean isLast) throws IOException {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getBasicRemote().sendBinary(partialByte, isLast);
        }
    }

    /**
     * Encodes object as a message and sends it to the remote endpoint.
     * @param connId connect id
     * @param data  The object to be sent.
     * @throws EncodeException if there was a problem encoding the
     *                     {@code data} object as a websocket message.
     * @throws IllegalArgumentException if {@code data} is {@code null}.
     * @throws IOException if an I/O error occurs during the sending of the
     *                     message.
     */
    public void sendObject(Integer connId, Object data) throws IOException, EncodeException {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getBasicRemote().sendObject(data);
        }
    }

    /**
     * Obtain the timeout (in milliseconds) for sending a message
     * asynchronously. The default value is determined by
     * {@link WebSocketContainer#getDefaultAsyncSendTimeout()}.
     *  @param connId connect id
     * @return  The current send timeout in milliseconds. A non-positive
     *          value means an infinite timeout.
     */
    public static long getSessionAsyncSendTimeout(Integer connId) {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        if (ValidateKit.isNotNull(websocketServerEndpoints)) {
            return websocketServerEndpoints.get(0).getAsyncRemote().getSendTimeout();
        }
        return -1;
    }

    /**
     * Set the timeout (in milliseconds) for sending a message
     * asynchronously. The default value is determined by
     * {@link WebSocketContainer#getDefaultAsyncSendTimeout()}.
     * @param connId connect id
     * @param timeout   The new timeout for sending messages asynchronously
     *                  in milliseconds. A non-positive value means an
     *                  infinite timeout.
     */
    public static void setAsyncSendTimeout(Integer connId, long timeout) {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getAsyncRemote().setSendTimeout(timeout);
        }
    }

    /**
     * Send the message asynchronously, using the SendHandler to signal to the
     * client when the message has been sent.
     * @param connId connect id
     * @param text          The text message to send
     * @param completion    Used to signal to the client when the message has
     *                      been sent
     */
    public void asyncSendText(Integer connId, String text, SendHandler completion) {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getAsyncRemote().sendText(text, completion);
        }
    }

    /**
     * Send the message asynchronously, using the Future to signal to the
     * client when the message has been sent.
     * @param connId connect id
     * @param text          The text message to send
     * @return A Future that signals when the message has been sent.
     */
    public static List<Future<Void>> asyncSendText(Integer connId, String text) {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        List<Future<Void>> futures = new ArrayList<>(websocketServerEndpoints.size());
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            futures.add(websocketServerEndpoint.getAsyncRemote().sendText(text));
        }
        return futures;
    }

    /**
     * Send the message asynchronously, using the Future to signal to the client
     * when the message has been sent.
     * @param connId connect id
     * @param data          The text message to send
     * @return A Future that signals when the message has been sent.
     * @throws IllegalArgumentException if {@code data} is {@code null}.
     */
    public static List<Future<Void>> asyncSendBinary(Integer connId, ByteBuffer data) {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        List<Future<Void>> futures = new ArrayList<>(websocketServerEndpoints.size());
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            futures.add(websocketServerEndpoint.getAsyncRemote().sendBinary(data));
        }
        return futures;
    }

    /**
     * Send the message asynchronously, using the SendHandler to signal to the
     * client when the message has been sent.
     * @param connId connect id
     * @param data          The text message to send
     * @param completion    Used to signal to the client when the message has
     *                      been sent
     * @throws IllegalArgumentException if {@code data} or {@code completion}
     *                      is {@code null}.
     */
    public static void asyncSendBinary(Integer connId, ByteBuffer data, SendHandler completion) {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getAsyncRemote().sendBinary(data, completion);
        }
    }

    /**
     * Encodes object as a message and sends it asynchronously, using the
     * Future to signal to the client when the message has been sent.
     * @param connId connect id
     * @param obj           The object to be sent.
     * @return A Future that signals when the message has been sent.
     * @throws IllegalArgumentException if {@code obj} is {@code null}.
     */
    public static List<Future<Void>> asyncSendObject(Integer connId, Object obj) {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        List<Future<Void>> futures = new ArrayList<>(websocketServerEndpoints.size());
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            futures.add(websocketServerEndpoint.getAsyncRemote().sendObject(obj));
        }
        return futures;
    }

    /**
     * Encodes object as a message and sends it asynchronously, using the
     * SendHandler to signal to the client when the message has been sent.
     * @param connId connect id
     * @param obj           The object to be sent.
     * @param completion    Used to signal to the client when the message has
     *                      been sent
     * @throws IllegalArgumentException if {@code obj} or
     *                      {@code completion} is {@code null}.
     */
    public static void asyncSendObject(Integer connId, Object obj, SendHandler completion) {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getAsyncRemote().sendObject(obj, completion);
        }
    }

    /**
     * Send the message, blocking until the message is sent.
     * @param connId connect id
     * @param obj  The obj to send.
     * @throws IllegalArgumentException if {@code text} is {@code null}.
     * @throws IOException if an I/O error occurs during the sending of the
     *                     message.
     */
    public static void sendJson(Integer connId, Object obj) throws IOException {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getBasicRemote().sendText(JacksonKit.toJson(obj));
        }
    }

    /**
     * Send the message, blocking until the message is sent.
     * @param connId connect id
     * @param obj  The obj to send.
     * @throws IllegalArgumentException if {@code text} is {@code null}.
     * @throws IOException if an I/O error occurs during the sending of the
     *                     message.
     */
    public static void asyncSendJson(Integer connId, Object obj) throws IOException {
        List<WebsocketServerEndpoint> websocketServerEndpoints = manager.listEndpoint(connId);
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            websocketServerEndpoint.getAsyncRemote().sendText(JacksonKit.toJson(obj));
        }
    }

    static InstantManager manager = new InstantManager();

    static InstantManager getInstance() {
        return manager;
    }
}
