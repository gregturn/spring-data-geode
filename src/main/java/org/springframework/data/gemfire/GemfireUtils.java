/*
 * Copyright 2010-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.gemfire;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.internal.GemFireVersion;
import org.springframework.data.gemfire.config.support.GemfireFeature;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

/**
 * {@link GemfireUtils} is an abstract utility class encapsulating common functionality to access features
 * and capabilities of GemFire based on version and other configuration meta-data.
 *
 * @author John Blum
 * @see org.apache.geode.cache.CacheFactory
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.util.RegionUtils
 * @since 1.3.3
 */
@SuppressWarnings("unused")
public abstract class GemfireUtils extends RegionUtils {

	public final static String APACHE_GEODE_NAME = "Aache Geode";
	public final static String GEMFIRE_NAME = apacheGeodeProductName();
	public final static String GEMFIRE_VERSION = apacheGeodeVersion();
	public final static String UNKNOWN = "unknown";

	private static final String ASYNC_EVENT_QUEUE_ELEMENT_NAME = "async-event-queue";
	private static final String ASYNC_EVENT_QUEUE_TYPE_NAME = "org.apache.geode.cache.asyncqueue.AsyncEventQueue";
	private static final String CQ_ELEMENT_NAME = "cq-listener-container";
	private static final String CQ_TYPE_NAME = "org.apache.geode.cache.query.internal.cq.CqServiceFactoryImpl";
	private static final String GATEWAY_RECEIVER_ELEMENT_NAME = "gateway-receiver";
	private static final String GATEWAY_RECEIVER_TYPE_NAME = "org.apache.geode.internal.cache.wan.GatewayReceiverFactoryImpl";
	private static final String GATEWAY_SENDER_ELEMENT_NAME = "gateway-sender";
	private static final String GATEWAY_SENDER_TYPE_NAME = "org.apache.geode.internal.cache.wan.GatewaySenderFactoryImpl";

	/* (non-Javadoc) */
	public static String apacheGeodeProductName() {

		try {
			return GemFireVersion.getProductName();
		}
		catch (Throwable ignore) {
			return APACHE_GEODE_NAME;
		}
	}

	/* (non-Javadoc) */
	public static String apacheGeodeVersion() {

		try {
			return CacheFactory.getVersion();
		}
		catch (Throwable ignore) {
			return UNKNOWN;
		}
	}

	/* (non-Javadoc) */
	public static boolean isClassAvailable(String fullyQualifiedClassName) {
		return ClassUtils.isPresent(fullyQualifiedClassName, GemfireUtils.class.getClassLoader());
	}

	/* (non-Javadoc) */
	public static boolean isGemfireFeatureAvailable(GemfireFeature feature) {

		boolean featureAvailable = (!GemfireFeature.AEQ.equals(feature) || isAsyncEventQueueAvailable());

		featureAvailable &= (!GemfireFeature.CONTINUOUS_QUERY.equals(feature) || isContinuousQueryAvailable());
		featureAvailable &= (!GemfireFeature.WAN.equals(feature) || isGatewayAvailable());

		return featureAvailable;
	}

	/* (non-Javadoc) */
	public static boolean isGemfireFeatureAvailable(Element element) {

		boolean featureAvailable = (!isAsyncEventQueue(element) || isAsyncEventQueueAvailable());

		featureAvailable &= (!isContinuousQuery(element) || isContinuousQueryAvailable());
		featureAvailable &= (!isGateway(element) || isGatewayAvailable());

		return featureAvailable;
	}

	/* (non-Javadoc) */
	public static boolean isGemfireFeatureUnavailable(GemfireFeature feature) {
		return !isGemfireFeatureAvailable(feature);
	}

	/* (non-Javadoc) */
	public static boolean isGemfireFeatureUnavailable(Element element) {
		return !isGemfireFeatureAvailable(element);
	}

	/* (non-Javadoc) */
	private static boolean isAsyncEventQueue(Element element) {
		return ASYNC_EVENT_QUEUE_ELEMENT_NAME.equals(element.getLocalName());
	}

	/* (non-Javadoc) */
	private static boolean isAsyncEventQueueAvailable() {
		return isClassAvailable(ASYNC_EVENT_QUEUE_TYPE_NAME);
	}

	/* (non-Javadoc) */
	private static boolean isContinuousQuery(Element element) {
		return CQ_ELEMENT_NAME.equals(element.getLocalName());
	}

	/* (non-Javadoc) */
	private static boolean isContinuousQueryAvailable() {
		return isClassAvailable(CQ_TYPE_NAME);
	}

	/* (non-Javadoc) */
	private static boolean isGateway(Element element) {

		String elementLocalName = element.getLocalName();

		return (GATEWAY_RECEIVER_ELEMENT_NAME.equals(elementLocalName)
			|| GATEWAY_SENDER_ELEMENT_NAME.equals(elementLocalName));
	}

	/* (non-Javadoc) */
	private static boolean isGatewayAvailable() {
		return isClassAvailable(GATEWAY_SENDER_TYPE_NAME);
	}

	public static void main(final String... args) {
		System.out.printf("GemFire Product Name (%1$s) Version (%2$s)%n", GEMFIRE_NAME, GEMFIRE_VERSION);
		//System.out.printf("Is GemFire Version 6.5 of Above? %1$s%n", isGemfireVersion65OrAbove());
		//System.out.printf("Is GemFire Version 7.0 of Above? %1$s%n", isGemfireVersion7OrAbove());
	}
}
