/*
 * Copyright (C) 2015 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.keychain.service.input;


import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.ryanharter.auto.value.parcel.ParcelAdapter;
import org.sufficientlysecure.keychain.util.ByteMapParcelAdapter;
import org.sufficientlysecure.keychain.util.ParcelableProxy;
import org.sufficientlysecure.keychain.util.Passphrase;

/**
 * This is a base class for the input of crypto operations.
 */
@AutoValue
public abstract class CryptoInputParcel implements Parcelable {

    public static CryptoInputParcel createCryptoInputParcel() {
        return new AutoValue_CryptoInputParcel(null, new HashMap<ByteBuffer,byte[]>(), null, null, true);
    }

    public static CryptoInputParcel createCryptoInputParcel(Date signatureTime, Passphrase passphrase) {
        if (signatureTime == null) {
            signatureTime = new Date();
        }
        return new AutoValue_CryptoInputParcel(null, new HashMap<ByteBuffer,byte[]>(), signatureTime, passphrase, true);
    }

    public static CryptoInputParcel createCryptoInputParcel(Passphrase passphrase) {
        return new AutoValue_CryptoInputParcel(null, new HashMap<ByteBuffer,byte[]>(), null, passphrase, true);
    }

    public static CryptoInputParcel createCryptoInputParcel(Date signatureTime) {
        if (signatureTime == null) {
            signatureTime = new Date();
        }
        return new AutoValue_CryptoInputParcel(null, new HashMap<ByteBuffer,byte[]>(), signatureTime, null, true);
    }

    public static CryptoInputParcel createCryptoInputParcel(ParcelableProxy parcelableProxy) {
        return new AutoValue_CryptoInputParcel(parcelableProxy, new HashMap<ByteBuffer,byte[]>(), null, null, true);
    }

    public static CryptoInputParcel createCryptoInputParcel(Date signatureTime, boolean cachePassphrase) {
        if (signatureTime == null) {
            signatureTime = new Date();
        }
        return new AutoValue_CryptoInputParcel(null, new HashMap<ByteBuffer,byte[]>(), signatureTime, null, cachePassphrase);
    }

    public static CryptoInputParcel createCryptoInputParcel(boolean cachePassphrase) {
        return new AutoValue_CryptoInputParcel(null, new HashMap<ByteBuffer,byte[]>(), null, null, cachePassphrase);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void addCryptoData(byte[] hash, byte[] signedHash) {
        getCryptoData().put(ByteBuffer.wrap(hash), signedHash);
    }
    public void addCryptoData(Map<ByteBuffer, byte[]> cachedSessionKeys) {
        getCryptoData().putAll(cachedSessionKeys);
    }

    public CryptoInputParcel withPassphrase(Passphrase passphrase) {
        return new AutoValue_CryptoInputParcel(
                getParcelableProxy(), getCryptoData(), getSignatureTime(), passphrase, isCachePassphrase());
    }

    public CryptoInputParcel withoutPassphrase() {
        return new AutoValue_CryptoInputParcel(
                getParcelableProxy(), getCryptoData(), getSignatureTime(), null, isCachePassphrase());
    }

    public CryptoInputParcel withSignatureTime(Date signatureTime) {
        return new AutoValue_CryptoInputParcel(
                getParcelableProxy(), getCryptoData(), signatureTime, getPassphrase(), isCachePassphrase());
    }

    public CryptoInputParcel withParcelableProxy(ParcelableProxy parcelableProxy) {
        return new AutoValue_CryptoInputParcel(
                parcelableProxy, getCryptoData(), getSignatureTime(), getPassphrase(), isCachePassphrase());
    }

    // used to supply an explicit proxy to operations that require it
    // this is not final so it can be added to an existing CryptoInputParcel
    // (e.g) CertifyOperation with upload might require both passphrase and orbot to be enabled
    @Nullable
    public abstract ParcelableProxy getParcelableProxy();

    // this map contains both decrypted session keys and signed hashes to be
    // used in the crypto operation described by this parcel.
    @ParcelAdapter(ByteMapParcelAdapter.class) public abstract HashMap<ByteBuffer, byte[]> getCryptoData();

    @Nullable
    public abstract Date getSignatureTime();
    @Nullable
    public abstract Passphrase getPassphrase();
    public abstract boolean isCachePassphrase();

    public boolean hasPassphrase() {
        return getPassphrase() != null;
    }
}
