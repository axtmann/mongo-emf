/*******************************************************************************
 * Copyright (c) 2012 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.mongo.emf.developer.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipselabs.mongo.emf.developer.junit.bundle.Activator;
import org.junit.rules.ExternalResource;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author bhunt
 * 
 */
public class ServiceLocator<T> extends ExternalResource
{
	public ServiceLocator(Class<T> type)
	{
		this(type, 1000);
	}

	public ServiceLocator(Class<T> type, long timeout)
	{
		this.timeout = timeout;
		serviceTracker = new ServiceTracker<T, T>(Activator.getBundleContext(), type, null);
		serviceTracker.open();
	}

	public T getService()
	{
		return service;
	}

	@Override
	protected void before() throws Throwable
	{
		service = serviceTracker.waitForService(timeout);
		assertThat(service, is(notNullValue()));
		super.before();
	}

	private long timeout;
	private ServiceTracker<T, T> serviceTracker;
	private T service;
}
