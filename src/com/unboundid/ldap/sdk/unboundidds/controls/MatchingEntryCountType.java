/*
 * Copyright 2014-2015 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2015 UnboundID Corp.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.unboundid.ldap.sdk.unboundidds.controls;



import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;



/**
 * <BLOCKQUOTE>
 *   <B>NOTE:</B>  This class is part of the Commercial Edition of the UnboundID
 *   LDAP SDK for Java.  It is not available for use in applications that
 *   include only the Standard Edition of the LDAP SDK, and is not supported for
 *   use in conjunction with non-UnboundID products.
 * </BLOCKQUOTE>
 * This enum defines the set of count types that may be used in a matching entry
 * count response control.
 */
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum MatchingEntryCountType
{
  /**
   * The count type that indicates that the server was able to determine the
   * exact number of entries matching the search criteria and examined them to
   * exclude any entries that would not be returned to the client in the course
   * of processing a normal search with the same criteria.
   */
  EXAMINED_COUNT((byte) 0x80),



  /**
   * The count type that indicates that the server was able to determine the
   * exact number of entries matching the search criteria, but did not examine
   * them to exclude any entries that might not actually be returned to the
   * client in the course of processing a normal search with the same criteria
   * (e.g., entries that the requester doesn't have permission to access, or
   * entries like LDAP subentries, replication conflict entries, or soft-deleted
   * entries that are returned only for special types of requests).
   */
  UNEXAMINED_COUNT((byte) 0x81),



  /**
   * The count type that indicates that the server was unable to determine the
   * exact number of entries matching the search criteria, but was able to
   * determine an upper bound for the number of matching entries.
   */
  UPPER_BOUND((byte) 0x82),



  /**
   * The count type that indicates that the server was unable to make any
   * meaningful determination about the number of entries matching the search
   * criteria.
   */
  UNKNOWN((byte) 0x83);



  // The BER type that corresponds to this enum value.
  private final byte berType;



  /**
   * Creates a new count type value with the provided information.
   *
   * @param  berType  The BER type that corresponds to this enum value.
   */
  MatchingEntryCountType(final byte berType)
  {
    this.berType = berType;
  }



  /**
   * Retrieves the BER type for this count type value.
   *
   * @return  The BER type for this count type value.
   */
  public byte getBERType()
  {
    return berType;
  }



  /**
   * Indicates whether this matching entry count type is considered more
   * specific than the provided count type.  The following order of precedence,
   * from most specific to least specific, will be used:
   * <OL>
   *   <LI>EXAMINED_COUNT</LI>
   *   <LI>UNEXAMINED_COUNT</LI>
   *   <LI>UPPER_BOUND</LI>
   *   <LI>UNKNOWN</LI>
   * </OL>
   *
   * @param  t  The matching entry count type value to compare against this
   *            matching entry count type.  It must not be {@code null}.
   *
   * @return  {@code true} if the provided matching entry count type value is
   *          considered more specific than this matching entry count type, or
   *          {@code false} if the provided count type is the same as or less
   *          specific than this count type.
   */
  public boolean isMoreSpecificThan(final MatchingEntryCountType t)
  {
    switch (this)
    {
      case EXAMINED_COUNT:
        return (t != EXAMINED_COUNT);

      case UNEXAMINED_COUNT:
        return ((t != EXAMINED_COUNT) && (t != UNEXAMINED_COUNT));

      case UPPER_BOUND:
        return ((t != EXAMINED_COUNT) && (t != UNEXAMINED_COUNT) &&
                (t != UPPER_BOUND));

      case UNKNOWN:
      default:
        return false;
    }
  }



  /**
   * Indicates whether this matching entry count type is considered less
   * specific than the provided count type.  The following order of precedence,
   * from most specific to least specific, will be used:
   * <OL>
   *   <LI>EXAMINED_COUNT</LI>
   *   <LI>UNEXAMINED_COUNT</LI>
   *   <LI>UPPER_BOUND</LI>
   *   <LI>UNKNOWN</LI>
   * </OL>
   *
   * @param  t  The matching entry count type value to compare against this
   *            matching entry count type.  It must not be {@code null}.
   *
   * @return  {@code true} if the provided matching entry count type value is
   *          considered less specific than this matching entry count type, or
   *          {@code false} if the provided count type is the same as or more
   *          specific than this count type.
   */
  public boolean isLessSpecificThan(final MatchingEntryCountType t)
  {
    switch (this)
    {
      case UNKNOWN:
        return (t != UNKNOWN);

      case UPPER_BOUND:
        return ((t != UNKNOWN) && (t != UPPER_BOUND));

      case UNEXAMINED_COUNT:
        return ((t != UNKNOWN) && (t != UPPER_BOUND) &&
                (t != UNEXAMINED_COUNT));

      case EXAMINED_COUNT:
      default:
        return false;
    }
  }



  /**
   * Retrieves the count type value for the provided BER type.
   *
   * @param  berType  The BER type for the count type value to retrieve.
   *
   * @return  The count type value that corresponds to the provided BER type, or
   *          {@code null} if there is no corresponding count type value.
   */
  public static MatchingEntryCountType valueOf(final byte berType)
  {
    for (final MatchingEntryCountType t : values())
    {
      if (t.berType == berType)
      {
        return t;
      }
    }

    return null;
  }
}