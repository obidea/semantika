/*
 * Copyright (c) 2013-2014 Josef Hardi <josef.hardi@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.obidea.semantika.expression.base;

public interface ITermList extends IExpressionObject
{
   /**
    * Checks if this list is an empty list.
    * 
    * @return True if this list is empty, or false otherwise.
    */
   public boolean isEmpty();

   /**
    * Gets the length (number of elements) of this list.
    * 
    * @return The length of this list.
    */
   public int getLength();

   /**
    * Gets the first element of this list.
    * 
    * @return The first element of this list.
    */
   public ITerm getFirst();

   /**
    * Gets the next element of this list.
    * 
    * @return The next element of this list.
    */
   public ITermList getNext();

   /**
    * Gets the last element of this list.
    * 
    * @return The last element of this list.
    */
   public ITerm getLast();

   /**
    * Gets the empty list associated to this list.
    * 
    * @return The empty list.
    */
   public ITermList getEmpty();

   /**
    * Gets the index of the first occurrence of a term in this list. Lookup
    * starts at a given index (0 being the first element).
    * 
    * @param target
    *           The target term to look for.
    * @param start
    *           The starting position of the lookup.
    * @return The index of the first occurrence of the term in this list, or -1
    *         if the term does not occur.
    * @throws IndexOutOfBoundsException
    *            When <b>start</b> &gt; <code>getLength()</code>.
    */
   public int indexOf(ITerm target, int start) throws IndexOutOfBoundsException;

   /**
    * Gets the last occurrence of a term in this list. Lookup starts at a given
    * index (0 being the first element).
    * 
    * @param target
    *           The target term to look for.
    * @param start
    *           The starting position of the lookup.
    * @return The index of the last occurrence of the term in this list, or -1
    *         if the term does not occur.
    * @throws IndexOutOfBoundsException
    *            When <b>start</b> &gt; <code>getLength()</code>.
    */
   public int lastIndexOf(ITerm target, int start) throws IndexOutOfBoundsException;

   /**
    * Concatenates a list to this list.
    * 
    * @param list
    *           The list to concatenate to this list.
    * @return The concatenation of the two lists.
    */
   public ITermList concat(ITermList list);

   /**
    * Appends a term to this list.
    * 
    * @param input
    *           The term to append to this list.
    * @return a list with the input term appended to it.
    */
   public ITermList append(ITerm input);

   /**
    * Gets the term at a specific index of this list.
    * 
    * @param i
    *           The index number.
    * @return The ith element of this list.
    * @throws IndexOutOfBoundsException
    *            If <b>i</b> does not refer to a position in this list.
    */
   public ITerm elementAt(int i) throws IndexOutOfBoundsException;

   /**
    * Removes the first occurrence of the target term from this list.
    * 
    * @param target
    *           The target term to be removed.
    * @return This list with the target term has been removed (only its first
    *         occurrence).
    */
   public ITermList remove(ITerm target);

   /**
    * Removes a term at a specific index in this list.
    * 
    * @param i
    *           The index number.
    * @return A list with the ith element removed.
    * @throws IndexOutOfBoundsException
    *            If <b>i</b> does not refer to a position in this list.
    */
   public ITermList removeElementAt(int i) throws IndexOutOfBoundsException;

   /**
    * Removes all occurrences of a target term in this list.
    * 
    * @param target
    *           The term to be removed.
    * @return This list with all occurrences of target term removed.
    */
   public ITermList removeAll(ITerm target);

   /**
    * Inserts a term in front of this list.
    * 
    * @param input
    *           The input term to be inserted.
    * @return A list with the input term inserted.
    */
   public ITermList insert(ITerm input);

   /**
    * Inserts a term at a specific position in this list.
    * 
    * @param input
    *           The input term to be inserted.
    * @param i
    *           The index number.
    * @return A list with the input term inserted as ith element.
    * @throws IndexOutOfBoundsException
    *            If <b>i</b> does not refer to a position within this list.
    */
   public ITermList insertAt(ITerm input, int i) throws IndexOutOfBoundsException;

   /**
    * Gets a portion (or a slice) of this list.
    * 
    * @param start
    *           The start of the slice (included).
    * @param end
    *           The end of the slice (excluded).
    * @return A list between <b>start</b> and <b>end</b>.
    * @throws IndexOutOfBoundsException
    *            If <b>start</b> or <b>end</b> do not refer to a position in
    *            this list.
    */
   public ITermList getSlice(int start, int end) throws IndexOutOfBoundsException;

   /**
    * Replaces a specific term in this list with another term.
    * 
    * @param another
    *           A replacement term to be put into this list.
    * @param i
    *           The index of a term in this list to be replaced.
    * @return The list with the ith element replaced by <b>another</b>.
    * @throws IndexOutOfBoundsException
    *            If <b>i</b> does not refer to a position in this list.
    */
   public ITermList replace(ITerm another, int i) throws IndexOutOfBoundsException;

   /**
    * Reverses the order of terms in this list.
    * 
    * @return A copy of reverse order list.
    */
   public ITermList reverse();
}
