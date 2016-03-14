/*
 * Copyright 2016 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2016 UnboundID Corp.
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
package com.unboundid.ldap.sdk.unboundidds.extensions;



import java.util.ArrayList;

import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.NotMutable;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;

import static com.unboundid.ldap.sdk.unboundidds.extensions.ExtOpMessages.*;



/**
 * <BLOCKQUOTE>
 *   <B>NOTE:</B>  This class is part of the Commercial Edition of the UnboundID
 *   LDAP SDK for Java.  It is not available for use in applications that
 *   include only the Standard Edition of the LDAP SDK, and is not supported for
 *   use in conjunction with non-UnboundID products.
 * </BLOCKQUOTE>
 * This class provides an implementation of an extended request that may be used
 * to deregister a YubiKey OTP device with the Directory Server so that it may
 * no longer used to authenticate using the UNBOUNDID-YUBIKEY-OTP SASL
 * mechanism.
 * <BR><BR>
 * This extended request has an OID of 1.3.6.1.4.1.30221.2.6.55, and it must
 * include a request value with the following encoding:
 * <BR><BR>
 * <PRE>
 *   DeregisterYubiKeyOTPDeviceRequest ::= SEQUENCE {
 *        authenticationID     [0] OCTET STRING OPTIONAL,
 *        staticPassword       [1] OCTET STRING OPTIONAL,
 *        yubiKeyOTP           [2] OCTET STRING OPTIONAL,
 *        ... }
 * </PRE>
 *
 *
 * @see  RegisterYubiKeyOTPDeviceExtendedRequest
 * @see  com.unboundid.ldap.sdk.unboundidds.UnboundIDYubiKeyOTPBindRequest
 * @see  com.unboundid.ldap.sdk.unboundidds.RegisterYubiKeyOTPDevice
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DeregisterYubiKeyOTPDeviceExtendedRequest
       extends ExtendedRequest
{
  /**
   * The OID (1.3.6.1.4.1.30221.2.6.55) for the deregister YubiKey OTP device
   * extended request.
   */
  public static final String DEREGISTER_YUBIKEY_OTP_DEVICE_REQUEST_OID =
       "1.3.6.1.4.1.30221.2.6.55";



  /**
   * The BER type for the authentication ID element of the request value
   * sequence.
   */
  private static final byte TYPE_AUTHENTICATION_ID = (byte) 0x80;



  /**
   * The BER type for the static password element of the request value sequence.
   */
  private static final byte TYPE_STATIC_PASSWORD = (byte) 0x81;



  /**
   * The BER type for the YubiKey OTP element of the request value sequence.
   */
  private static final byte TYPE_YUBIKEY_OTP = (byte) 0x82;



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -4029230013825076585L;



  // The static password for the request.
  private final ASN1OctetString staticPassword;

  // The authentication ID for the request.
  private final String authenticationID;

  // The YubiKey OTP for the request.
  private final String yubiKeyOTP;



  /**
   * Creates a new deregister YubiKey OTP device extended request with the
   * provided information.
   *
   * @param  authenticationID  The authentication ID that identifies the user
   *                           for whom the YubiKey OTP device is to be
   *                           deregistered.  It may be {@code null} if the
   *                           device is to be deregistered for the user as whom
   *                           the underlying connection is authenticated.
   * @param  yubiKeyOTP        An optional one-time password generated by the
   *                           YubiKey device to be deregistered.  If this is
   *                           {@code null}, then all YubiKey OTP devices
   *                           associated with the target user will be
   *                           deregistered.  If it is non-{@code null}, then
   *                           only the YubiKey device used to generate the OTP
   *                           will be deregistered.
   * @param  controls          The set of controls to include in the request.
   *                           It may be {@code null} or empty if there should
   *                           not be any request controls.
   */
  public DeregisterYubiKeyOTPDeviceExtendedRequest(
              final String authenticationID, final String yubiKeyOTP,
              final Control... controls)
  {
    this(authenticationID, (ASN1OctetString) null, yubiKeyOTP, controls);
  }



  /**
   * Creates a new deregister YubiKey OTP device extended request with the
   * provided information.
   *
   * @param  authenticationID  The authentication ID that identifies the user
   *                           for whom the YubiKey OTP device is to be
   *                           deregistered.  It may be {@code null} if the
   *                           device is to be deregistered for the user as whom
   *                           the underlying connection is authenticated.
   * @param  staticPassword    The static password of the user for whom the
   *                           device is to be deregistered.  It may be
   *                           {@code null} if the server is configured to not
   *                           require a static password when deregistering one
   *                           or more devices.
   * @param  yubiKeyOTP        An optional one-time password generated by the
   *                           YubiKey device to be deregistered.  If this is
   *                           {@code null}, then all YubiKey OTP devices
   *                           associated with the target user will be
   *                           deregistered.  If it is non-{@code null}, then
   *                           only the YubiKey device used to generate the OTP
   *                           will be deregistered.
   * @param  controls          The set of controls to include in the request.
   *                           It may be {@code null} or empty if there should
   *                           not be any request controls.
   */
  public DeregisterYubiKeyOTPDeviceExtendedRequest(
              final String authenticationID, final String staticPassword,
              final String yubiKeyOTP, final Control... controls)
  {
    this(authenticationID,
         RegisterYubiKeyOTPDeviceExtendedRequest.encodePassword(staticPassword),
         yubiKeyOTP, controls);
  }



  /**
   * Creates a new deregister YubiKey OTP device extended request with the
   * provided information.
   *
   * @param  authenticationID  The authentication ID that identifies the user
   *                           for whom the YubiKey OTP device is to be
   *                           deregistered.  It may be {@code null} if the
   *                           device is to be deregistered for the user as whom
   *                           the underlying connection is authenticated.
   * @param  staticPassword    The static password of the user for whom the
   *                           device is to be deregistered.  It may be
   *                           {@code null} if the server is configured to not
   *                           require a static password when deregistering one
   *                           or more devices.
   * @param  yubiKeyOTP        An optional one-time password generated by the
   *                           YubiKey device to be deregistered.  If this is
   *                           {@code null}, then all YubiKey OTP devices
   *                           associated with the target user will be
   *                           deregistered.  If it is non-{@code null}, then
   *                           only the YubiKey device used to generate the OTP
   *                           will be deregistered.
   * @param  controls          The set of controls to include in the request.
   *                           It may be {@code null} or empty if there should
   *                           not be any request controls.
   */
  public DeregisterYubiKeyOTPDeviceExtendedRequest(
              final String authenticationID, final byte[] staticPassword,
              final String yubiKeyOTP, final Control... controls)
  {
    this(authenticationID,
         RegisterYubiKeyOTPDeviceExtendedRequest.encodePassword(staticPassword),
         yubiKeyOTP, controls);
  }



  /**
   * Creates a new deregister YubiKey OTP device extended request with the
   * provided information.
   *
   * @param  authenticationID  The authentication ID that identifies the user
   *                           for whom the YubiKey OTP device is to be
   *                           deregistered.  It may be {@code null} if the
   *                           device is to be deregistered for the user as whom
   *                           the underlying connection is authenticated.
   * @param  staticPassword    The static password of the user for whom the
   *                           device is to be deregistered.  It may be
   *                           {@code null} if the server is configured to not
   *                           require a static password when deregistering one
   *                           or more devices.
   * @param  yubiKeyOTP        An optional one-time password generated by the
   *                           YubiKey device to be deregistered.  If this is
   *                           {@code null}, then all YubiKey OTP devices
   *                           associated with the target user will be
   *                           deregistered.  If it is non-{@code null}, then
   *                           only the YubiKey device used to generate the OTP
   *                           will be deregistered.
   * @param  controls          The set of controls to include in the request.
   *                           It may be {@code null} or empty if there should
   *                           not be any request controls.
   */
  private DeregisterYubiKeyOTPDeviceExtendedRequest(
               final String authenticationID,
               final ASN1OctetString staticPassword, final String yubiKeyOTP,
               final Control... controls)
  {
    super(DEREGISTER_YUBIKEY_OTP_DEVICE_REQUEST_OID,
         encodeValue(authenticationID, staticPassword, yubiKeyOTP), controls);

    this.authenticationID = authenticationID;
    this.staticPassword   = staticPassword;
    this.yubiKeyOTP       = yubiKeyOTP;
  }



  /**
   * Creates a new deregister YubiKey OTP device extended request that is
   * decoded from the provided generic extended request.
   *
   * @param  request  The generic extended request to decode as a deregister
   *                  YubiKey OTP device request.
   *
   * @throws  LDAPException  If a problem is encountered while attempting to
   *                         decode the provided request.
   */
  public DeregisterYubiKeyOTPDeviceExtendedRequest(
              final ExtendedRequest request)
         throws LDAPException
  {
    super(request);

    final ASN1OctetString value = request.getValue();
    if (value == null)
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_DEREGISTER_YUBIKEY_OTP_REQUEST_NO_VALUE.get());
    }

    try
    {
      String authID = null;
      ASN1OctetString staticPW = null;
      String otp = null;
      for (final ASN1Element e :
           ASN1Sequence.decodeAsSequence(value.getValue()).elements())
      {
        switch (e.getType())
        {
          case TYPE_AUTHENTICATION_ID:
            authID = ASN1OctetString.decodeAsOctetString(e).stringValue();
            break;
          case TYPE_STATIC_PASSWORD:
            staticPW = ASN1OctetString.decodeAsOctetString(e);
            break;
          case TYPE_YUBIKEY_OTP:
            otp = ASN1OctetString.decodeAsOctetString(e).stringValue();
            break;
          default:
            throw new LDAPException(ResultCode.DECODING_ERROR,
                 ERR_DEREGISTER_YUBIKEY_OTP_REQUEST_UNRECOGNIZED_TYPE.get(
                      StaticUtils.toHex(e.getType())));
        }
      }

      authenticationID = authID;
      staticPassword   = staticPW;
      yubiKeyOTP       = otp;
    }
    catch (final LDAPException le)
    {
      Debug.debugException(le);
      throw le;
    }
    catch (final Exception e)
    {
      Debug.debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_DEREGISTER_YUBIKEY_OTP_REQUEST_ERROR_DECODING_VALUE.get(
                StaticUtils.getExceptionMessage(e)),
           e);
    }
  }



  /**
   * Encodes the provided information into an ASN.1 octet string suitable for
   * use as the value of this extended request.
   *
   * @param  authenticationID  The authentication ID that identifies the user
   *                           for whom the YubiKey OTP device is to be
   *                           deregistered.  It may be {@code null} if the
   *                           device is to be deregistered for the user as whom
   *                           the underlying connection is authenticated.
   * @param  staticPassword    The static password of the user for whom the
   *                           device is to be deregistered.  It may be
   *                           {@code null} if the server is configured to not
   *                           require a static password when deregistering one
   *                           or more devices.
   * @param  yubiKeyOTP        An optional one-time password generated by the
   *                           YubiKey device to be deregistered.  If this is
   *                           {@code null}, then all YubiKey OTP devices
   *                           associated with the target user will be
   *                           deregistered.  If it is non-{@code null}, then
   *                           only the YubiKey device used to generate the OTP
   *                           will be deregistered.
   *
   * @return  The ASN.1 octet string containing the encoded request value.
   */
  private static ASN1OctetString encodeValue(final String authenticationID,
                                      final ASN1OctetString staticPassword,
                                      final String yubiKeyOTP)
  {
    final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);

    if (authenticationID != null)
    {
      elements.add(
           new ASN1OctetString(TYPE_AUTHENTICATION_ID, authenticationID));
    }

    if (staticPassword != null)
    {
      elements.add(staticPassword);
    }

    if (yubiKeyOTP != null)
    {
      elements.add(new ASN1OctetString(TYPE_YUBIKEY_OTP, yubiKeyOTP));
    }

    return new ASN1OctetString(new ASN1Sequence(elements).encode());
  }



  /**
   * Retrieves the authentication ID that identifies the user from whom the
   * YubiKey OTP device is to be deregistered, if provided.
   *
   * @return  The authentication ID that identifies the target user, or
   *          {@code null} if the device is to be deregistered for the user as
   *          whom the underlying connection is authenticated.
   */
  public String getAuthenticationID()
  {
    return authenticationID;
  }



  /**
   * Retrieves the string representation of the static password for the target
   * user, if provided.
   *
   * @return  The string representation of the static password for the target
   *          user, or {@code null} if no static password was provided.
   */
  public String getStaticPasswordString()
  {
    if (staticPassword == null)
    {
      return null;
    }
    else
    {
      return staticPassword.stringValue();
    }
  }



  /**
   * Retrieves the bytes that comprise the static password for the target user,
   * if provided.
   *
   * @return  The bytes that comprise the static password for the target user,
   *          or {@code null} if no static password was provided.
   */
  public byte[] getStaticPasswordBytes()
  {
    if (staticPassword == null)
    {
      return null;
    }
    else
    {
      return staticPassword.getValue();
    }
  }



  /**
   * Retrieves a one-time password generated by the YubiKey device to be
   * deregistered, if provided.
   *
   * @return  A one-time password generated by the YubiKey device to be
   *          deregistered, or {@code null} if all devices associated with the
   *          target user should be deregistered.
   */
  public String getYubiKeyOTP()
  {
    return yubiKeyOTP;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public DeregisterYubiKeyOTPDeviceExtendedRequest duplicate()
  {
    return duplicate(getControls());
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public DeregisterYubiKeyOTPDeviceExtendedRequest
              duplicate(final Control[] controls)
  {
    final DeregisterYubiKeyOTPDeviceExtendedRequest r =
         new DeregisterYubiKeyOTPDeviceExtendedRequest(authenticationID,
              staticPassword, yubiKeyOTP, controls);
    r.setResponseTimeoutMillis(getResponseTimeoutMillis(null));
    return r;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getExtendedRequestName()
  {
    return INFO_DEREGISTER_YUBIKEY_OTP_REQUEST_NAME.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("DeregisterYubiKeyOTPDeviceExtendedRequest(");

    if (authenticationID != null)
    {
      buffer.append("authenticationID='");
      buffer.append(authenticationID);
      buffer.append("', ");
    }

    buffer.append("staticPasswordProvided=");
    buffer.append(staticPassword != null);
    buffer.append(", otpProvided=");
    buffer.append(yubiKeyOTP != null);

    final Control[] controls = getControls();
    if (controls.length > 0)
    {
      buffer.append(", controls={");
      for (int i=0; i < controls.length; i++)
      {
        if (i > 0)
        {
          buffer.append(", ");
        }

        buffer.append(controls[i]);
      }
      buffer.append('}');
    }

    buffer.append(')');
  }
}
