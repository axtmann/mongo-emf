/*******************************************************************************
 * Copyright (c) 2010 Bryan Hunt & Ed Merks.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt & Ed Merks - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.mongo.emf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.eclipselabs.mongo.IMongoLocator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * This EMF URI handler interfaces to MongoDB. This URI handler can handle URIs with the "mongodb"
 * scheme. The URI path must have exactly 3 segments. The URI path must be of the form
 * /database/collection/{id} where id is optional the first time the EMF object is saved. When
 * building queries, do not specify an id, but make sure path has 3 segments by placing a "/" after
 * the collection.
 * 
 * Note that if the id is not specified when the object is first saved, MongoDB will assign the id
 * and the URI of the EMF Resource will be modified to include the id in the URI. Examples of valid
 * URIs:
 * 
 * mongodb://localhost/data/people/
 * mongodb://localhost/data/people/4d0a3e259095b5b334a59df0
 * 
 * This class has the ability to use a custom converter service, builders, and streams. Each component
 * has a set function for the appropriate factory. Unless you are customizing how EMF objects are
 * serialized and de-serialized in MongoDB, you should not call these functions as default factories
 * are provided.
 * 
 * @author bhunt
 * 
 */
public class MongoURIHandlerImpl extends URIHandlerImpl
{
	/**
	 * 
	 * @param mongoLocator an instance of the mongo locator service
	 * @param queryEngine an instance of the query engine
	 */
	public MongoURIHandlerImpl(IMongoLocator mongoLocator, IInputStreamFactory inputStreamFactory, IOutputStreamFactory outputStreamFactory)
	{

		this.mongoLocator = mongoLocator;
		this.inputStreamFactory = inputStreamFactory;
		this.outputStreamFactory = outputStreamFactory;
	}

	@Override
	public boolean canHandle(URI uri)
	{
		// This handler should only accept URIs with the scheme "mongodb"

		return "mongodb".equalsIgnoreCase(uri.scheme());
	}

	@Override
	public OutputStream createOutputStream(final URI uri, final Map<?, ?> options) throws IOException
	{
		// This function may be called with a URI path with or without an id. If an id is not specified
		// the EMF resource URI will be modified to include the id generated by MongoDB.

		return outputStreamFactory.createOutputStream(uri, options, getCollection(uri, options), getResponse(options));
	}

	@Override
	public InputStream createInputStream(final URI uri, final Map<?, ?> options) throws IOException
	{
		return inputStreamFactory.createInputStream(uri, options, getCollection(uri, options), getResponse(options));
	}

	@Override
	public void delete(URI uri, Map<?, ?> options) throws IOException
	{
		// It is assumed that delete is called with the URI path /database/collection/id

		DBCollection collection = getCollection(uri, options);
		collection.findAndRemove(new BasicDBObject(ID_KEY, getID(uri)));
	}

	@Override
	public boolean exists(URI uri, Map<?, ?> options)
	{
		if (uri.query() != null)
			return false;

		try
		{
			DBCollection collection = getCollection(uri, options);
			return collection.findOne(new BasicDBObject(ID_KEY, getID(uri))) != null;
		}
		catch (Throwable exception)
		{
			return false;
		}
	}

	/**
	 * This function locates the MongoDB collection instance corresponding to the collection identifier extracted from the URI. The URI path must have exactly 3 segments and be of the form
	 * mongodb://host:[port]/database/collection/{id} where id is optional.
	 * 
	 * @param mongoDB the MongoDB service
	 * @param uri the MongoDB collection identifier
	 * @param options the load or save options as appropriate
	 * @return the MongoDB collection corresponding to the URI
	 * @throws UnknownHostException if the host specified in the URI can't be found
	 * @throws IOException if the URI is malformed or the collection could not otherwise be resolved
	 */
	public DBCollection getCollection(URI uri, Map<?, ?> options) throws IOException
	{
		// We assume that the URI path has the form /database/collection/{id} making the
		// collection segment # 1.

		if (uri.segmentCount() != 3)
			throw new IOException("The URI is not of the form 'mongodb:/database/collection/{id}");

		String port = uri.port();
		String mongoURI = "mongodb://" + uri.host() + (port != null ? ":" + port : "");
		DBCollection dbCollection = mongoLocator.getMongo(mongoURI).getDB(uri.segment(0)).getCollection(uri.segment(1));

// FIXME uncomment the 4 lines below when MongoDB properly supports tagged reads
//		@SuppressWarnings("unchecked")
//		Map<String, String> tags = (Map<String, String>) options.get(OPTION_TAGGED_READ_PREFERENCE);

//		if (tags != null)
//			dbCollection.setReadPreference(new ReadPreference.TaggedReadPreference(tags));

		return dbCollection;
	}

	/**
	 * This function extracts the object ID from the given URI. The URI path must have exactly 3 segments and be of the form
	 * mongodb://host:[port]/database/collection/{id} where id is optional.
	 * 
	 * @param uri
	 * @return the object ID from the given URI or null if the id was not specified
	 * @throws IOException if the URI path is not exactly three segments
	 */
	public static Object getID(URI uri) throws IOException
	{
		// Require that the URI path has the form /database/collection/{id} making the id segment # 2.

		if (uri.segmentCount() != 3)
			throw new IOException("The URI is not of the form 'mongo:/database/collection/{id}");

		String id = uri.segment(2);

		// If the ID was specified in the URI, we first attempt to create a MongoDB ObjectId. If
		// that fails, we assume that the client has specified a non ObjectId and return the raw data.

		try
		{
			return id.isEmpty() ? null : new ObjectId(id);
		}
		catch (Throwable t)
		{
			return id;
		}
	}

	/**
	 * This function determines whether or not the given EDataType can be represented natively by MongoDB.
	 * 
	 * @param dataType the EMF data type to check
	 * @return true if the data type can be represneted natively by MongoDB; false otherwise
	 */
	public static boolean isNativeType(EDataType dataType)
	{
		String instanceClassName = dataType.getInstanceClassName();
		//@formatter:off
		return
			instanceClassName == "java.lang.String"  ||
			instanceClassName == "int"               ||
			instanceClassName == "boolean"           ||
			instanceClassName == "float"             ||
			instanceClassName == "long"              ||
			instanceClassName == "double"            ||
			instanceClassName == "java.util.Date"    ||
			instanceClassName == "short"             ||
			instanceClassName == "byte[]"            ||
			instanceClassName == "byte"              ||
			instanceClassName == "java.lang.Integer" ||
			instanceClassName == "java.lang.Boolean" ||
			instanceClassName == "java.lang.Long"    ||
			instanceClassName == "java.lang.Float"   ||
			instanceClassName == "java.lang.Double"  ||
			instanceClassName == "java.lang.Short"   ||
			instanceClassName == "java.lang.Byte";
		//@formatter:on
	}

	/**
	 * MongoDB ID field identifier. Not intended to be used by clients.
	 */
	public static final String ID_KEY = "_id";

	/**
	 * MongoDB eClass field identifier. Not intended to be used by clients.
	 */
	public static final String ECLASS_KEY = "_eClass";

	/**
	 * MongoDB eProxyURI field identifier. Not intended to be used by clients.
	 */
	public static final String PROXY_KEY = "_eProxyURI";

	/**
	 * MongoDB Extrensic ID field identifier. Not intended to be used by clients.
	 */
	public static final String EXTRINSIC_ID_KEY = "_eId";

	/**
	 * MongoDB Timestamp field identifier. Not intended to be used by clients.
	 */
	public static final String TIME_STAMP_KEY = "_timeStamp";

	/**
	 * When you load an object with cross-document references, they will be proxies. When you access the reference, EMF will resolve the proxy and you can then access the attributes. This can cause
	 * performance problems for example when expanding a tree where you only need a name attribute to display the children and then only resolve the next child to be expanded. Setting this option to
	 * Boolean.TRUE will cause the proxy instance to have its attribute values populated so that you can display the child names in the tree without resolving the proxy.
	 * 
	 * Value type: Boolean
	 */
	public static final String OPTION_PROXY_ATTRIBUTES = BinaryResourceImpl.OPTION_STYLE_PROXY_ATTRIBUTES;

	/**
	 * This option may be used when you wish to read from a particular server in a MongoDB replica set that has been tagged. <code>
	 * HashMap<String, String> tags = new HashMap<String, String>(1);
	 * tags.put("locale", "in");
	 * 
	 * resourceSet.getLoadOptions().put(MongoDBURIHandlerImpl.OPTION_TAGGED_READ_PREFERENCE, tags);
	 * </code>
	 * 
	 * Value type: Map<String, String>
	 */
// FIXME uncomment when MongoDB supports tagged reads	
//	public static final String OPTION_TAGGED_READ_PREFERENCE = "TAGGED_READ_PREFERENCE";

	/**
	 * EMF's default serialization is designed to conserve space by not serializing attributes that are set to their default value. This is a problem when attempting to query objects by an attributes
	 * default value. By setting this option to Boolean.TRUE, all attribute values will be stored to MongoDB.
	 * 
	 * Value type: Boolean
	 */
	public static final String OPTION_SERIALIZE_DEFAULT_ATTRIBUTE_VALUES = "SERIALIZE_DEFAULT_ATTRIBUTE_VALUES";

	/**
	 * If it is set to Boolean.TRUE and the ID was not specified in the URI, the value of the ID attribute will be used as the MongoDB _id if it exists.
	 * 
	 * Value type: Boolean
	 */
	public static final String OPTION_USE_ID_ATTRIBUTE_AS_PRIMARY_KEY = "USE_ID_ATTRIBUTE_AS_PRIMARY_KEY";

	/**
	 * If set, the value must be an instance of WriteConcern and will be passed to MongoDB when the object is inserted into the database, or updated.
	 * 
	 * Value type: WriteConcern
	 */
	public static final String OPTION_WRITE_CONCERN = "WRITE_CONCERN";

	/**
	 * If set to Boolean.TRUE, a query will return a MongoCursor instead of a Result
	 * 
	 * Value type: Boolean
	 */
	public static final String OPTION_QUERY_CURSOR = "QUERY_CURSOR";

	private IMongoLocator mongoLocator;
	private IInputStreamFactory inputStreamFactory;
	private IOutputStreamFactory outputStreamFactory;
}
