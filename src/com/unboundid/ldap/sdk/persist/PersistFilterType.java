/*
 * Copyright 2011-2018 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2011-2018 Ping Identity Corporation
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
package com.unboundid.ldap.sdk.persist;



import com.unboundid.util.StaticUtils;



/**
 * This enum defines a set of filter types for filters that may be generated
 * for an object using the LDAP SDK persistence framework.  Classes created by
 * {@link GenerateSourceFromSchema} (including the
 * {@code generate-source-from-schema} command-line tool) will include methods
 * that may be used to generate filters for object contents.
 */
public enum PersistFilterType
{
  /**
   * The filter type that may be used to generate a presence filter, like
   * "(attrName=*)".
   */
  PRESENCE,



  /**
   * The filter type that may be used to generate an equality filter, like
   * "(attrName=value)".
   */
  EQUALITY,



  /**
   * The filter type that may be used to generate a substring filter with a
   * subInitial element, like "(attrName=value*)".
   */
  STARTS_WITH,



  /**
   * The filter type that may be used to generate a substring filter with a
   * subFinal element, like "(attrName=*value)".
   */
  ENDS_WITH,



  /**
   * The filter type that may be used to generate a substring filter with a
   * subAny element, like "(attrName=*value*)".
   */
  CONTAINS,



  /**
   * The filter type that may be used to generate a greater-than-or-equal-to
   * filter, like "(attrName&gt;=value)".
   */
  GREATER_OR_EQUAL,



  /**
   * The filter type that may be used to generate a less-than-or-equal-to
   * filter, like "(attrName&lt;=value)".
   */
  LESS_OR_EQUAL,



  /**
   * The filter type that may be used to generate an approximate match filter,
   * like "(attrName~=value)".
   */
  APPROXIMATELY_EQUAL_TO;



  /**
   * Retrieves the filter type with the specified name.
   *
   * @param  name  The name of the filter type to retrieve.  It must not be
   *               {@code null}.
   *
   * @return  The requested filter type, or {@code null} if no such type is
   *          defined.
   */
  public static PersistFilterType forName(final String name)
  {
    switch (StaticUtils.toLowerCase(name))
    {
      case "presence":
        return PRESENCE;
      case "equality":
        return EQUALITY;
      case "startswith":
      case "starts-with":
      case "starts_with":
        return STARTS_WITH;
      case "endswith":
      case "ends-with":
      case "ends_with":
        return ENDS_WITH;
      case "contains":
        return CONTAINS;
      case "greaterorequal":
      case "greater-or-equal":
      case "greater_or_equal":
        return GREATER_OR_EQUAL;
      case "lessorequal":
      case "less-or-equal":
      case "less_or_equal":
        return LESS_OR_EQUAL;
      case "approximatelyequalto":
      case "approximately-equal-to":
      case "approximately_equal_to":
        return APPROXIMATELY_EQUAL_TO;
      default:
        return null;
    }
  }
}
