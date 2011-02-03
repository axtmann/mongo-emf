/*******************************************************************************
 * Copyright (c) 2010 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.mongo.emf.junit.model.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipselabs.mongo.emf.junit.model.Book;
import org.eclipselabs.mongo.emf.junit.model.ETypes;
import org.eclipselabs.mongo.emf.junit.model.Library;
import org.eclipselabs.mongo.emf.junit.model.Location;
import org.eclipselabs.mongo.emf.junit.model.MappedLibrary;
import org.eclipselabs.mongo.emf.junit.model.ModelPackage;
import org.eclipselabs.mongo.emf.junit.model.Person;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipselabs.mongo.emf.junit.model.ModelPackage
 * @generated
 */
public class ModelAdapterFactory extends AdapterFactoryImpl
{
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ModelPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelAdapterFactory()
	{
		if (modelPackage == null)
		{
			modelPackage = ModelPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object)
	{
		if (object == modelPackage)
		{
			return true;
		}
		if (object instanceof EObject)
		{
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelSwitch<Adapter> modelSwitch =
		new ModelSwitch<Adapter>()
		{
			@Override
			public Adapter casePerson(Person object)
			{
				return createPersonAdapter();
			}
			@Override
			public Adapter caseBook(Book object)
			{
				return createBookAdapter();
			}
			@Override
			public Adapter caseLibrary(Library object)
			{
				return createLibraryAdapter();
			}
			@Override
			public Adapter caseLocation(Location object)
			{
				return createLocationAdapter();
			}
			@Override
			public Adapter caseETypes(ETypes object)
			{
				return createETypesAdapter();
			}
			@Override
			public Adapter caseMappedLibrary(MappedLibrary object)
			{
				return createMappedLibraryAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object)
			{
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target)
	{
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.eclipselabs.mongo.emf.junit.model.Person <em>Person</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipselabs.mongo.emf.junit.model.Person
	 * @generated
	 */
	public Adapter createPersonAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipselabs.mongo.emf.junit.model.Book <em>Book</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipselabs.mongo.emf.junit.model.Book
	 * @generated
	 */
	public Adapter createBookAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipselabs.mongo.emf.junit.model.Library <em>Library</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipselabs.mongo.emf.junit.model.Library
	 * @generated
	 */
	public Adapter createLibraryAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipselabs.mongo.emf.junit.model.Location <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipselabs.mongo.emf.junit.model.Location
	 * @generated
	 */
	public Adapter createLocationAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipselabs.mongo.emf.junit.model.ETypes <em>ETypes</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipselabs.mongo.emf.junit.model.ETypes
	 * @generated
	 */
	public Adapter createETypesAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipselabs.mongo.emf.junit.model.MappedLibrary <em>Mapped Library</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipselabs.mongo.emf.junit.model.MappedLibrary
	 * @generated
	 */
	public Adapter createMappedLibraryAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter()
	{
		return null;
	}

} //ModelAdapterFactory
