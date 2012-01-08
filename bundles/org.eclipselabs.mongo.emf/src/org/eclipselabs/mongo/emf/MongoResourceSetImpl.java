/*******************************************************************************
 * Copyright (c) 2011 Bryan Hunt and Ed Merks.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt and Ed Merks - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.mongo.emf;

import java.util.HashMap;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * This implementation of ResourceSet gives better performance when loading many
 * EMF objects in conjunction with URI normalization. If your URI converter
 * normalizes the URI to another form, then this resource set will load resources
 * with O(n) complexity whereas the default ResourceSetImpl will load resources
 * with O(n^2) complexity.
 * 
 * @author bhunt
 * 
 */
public class MongoResourceSetImpl extends ResourceSetImpl
{
	public MongoResourceSetImpl()
	{
		setURIResourceMap(new HashMap<URI, Resource>());
		getURIConverter();

		eAdapters().add(new EContentAdapter()
		{
			@Override
			protected void setTarget(Resource target)
			{
				URI uri = target.getURI();

				if (uri != null)
					addCachedResource(uriConverter.normalize(uri), target);
			}

			@Override
			protected void unsetTarget(Resource target)
			{
				URI uri = target.getURI();

				if (uri != null)
					removeCachedResource(uriConverter.normalize(uri));
			}

			@Override
			protected void selfAdapt(Notification notification)
			{
				Object notifier = notification.getNotifier();

				if (notifier instanceof ResourceSet)
				{
					if (notification.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES)
						handleContainment(notification);
				}
				else if (notifier instanceof Resource)
				{
					if (notification.getFeatureID(Resource.class) == Resource.RESOURCE__URI)
					{
						Object oldURI = notification.getOldValue();

						if (oldURI != null)
							removeCachedResource(uriConverter.normalize((URI) oldURI));

						Object newURI = notification.getNewValue();

						if (newURI != null)
							addCachedResource(uriConverter.normalize((URI) newURI), (Resource) notifier);
					}
				}
			}
		});
	}

	@Override
	public Resource getResource(URI uri, boolean loadOnDemand)
	{
		URI normalizedURI = uriConverter.normalize(uri);
		Resource resource = getCachedResource(normalizedURI);

		if (resource != null)
		{
			if (loadOnDemand && !resource.isLoaded())
				demandLoadHelper(resource);
		}
		else
		{
			resource = delegatedGetResource(uri, loadOnDemand);

			if (resource == null && loadOnDemand)
			{
				resource = demandCreateResource(uri);

				if (resource == null)
					throw new RuntimeException("Cannot create a resource for '" + uri + "'; a registered resource factory is needed");

				demandLoadHelper(resource);
			}
		}

		return resource;
	}

	protected void addCachedResource(URI uri, Resource resource)
	{
		uriResourceMap.put(uri, resource);
	}

	protected Resource getCachedResource(URI uri)
	{
		return uriResourceMap.get(uri);
	}

	protected void removeCachedResource(URI uri)
	{
		uriResourceMap.remove(uri);
	}
}
