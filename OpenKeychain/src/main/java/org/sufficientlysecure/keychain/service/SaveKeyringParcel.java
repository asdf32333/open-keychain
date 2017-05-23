/*
 * Copyright (C) 2014 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2014 Vincent Breitmoser <v.breitmoser@mugenguild.com>
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

package org.sufficientlysecure.keychain.service;


import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import org.sufficientlysecure.keychain.keyimport.ParcelableHkpKeyserver;
import org.sufficientlysecure.keychain.pgp.WrappedUserAttribute;
import org.sufficientlysecure.keychain.util.Passphrase;

/**
 * This class is a a transferable representation for a collection of changes
 * to be done on a keyring.
 * <p/>
 * This class should include all types of operations supported in the backend.
 * <p/>
 * All changes are done in a differential manner. Besides the two key
 * identification attributes, all attributes may be null, which indicates no
 * change to the keyring. This is also the reason why boxed values are used
 * instead of primitives in the subclasses.
 * <p/>
 * Application of operations in the backend should be fail-fast, which means an
 * error in any included operation (for example revocation of a non-existent
 * subkey) will cause the operation as a whole to fail.
 */
@AutoValue
public abstract class SaveKeyringParcel implements Parcelable {

    // the master key id to be edited. if this is null, a new one will be created
    @Nullable
    public abstract Long getMasterKeyId();
    // the key fingerprint, for safety. MUST be null for a new key.
    @Nullable
    public abstract byte[] getFingerprint();

    public abstract ArrayList<String> getAddUserIds();
    public abstract ArrayList<WrappedUserAttribute> getAddUserAttribute();
    public abstract ArrayList<SubkeyAdd> getAddSubKeys();

    public abstract ArrayList<SubkeyChange> getChangeSubKeys();
    @Nullable
    public abstract String getChangePrimaryUserId();

    public abstract ArrayList<String> getRevokeUserIds();
    public abstract ArrayList<Long> getRevokeSubKeys();

    // if these are non-null, PINs will be changed on the token
    @Nullable
    public abstract Passphrase getSecurityTokenPin();
    @Nullable
    public abstract Passphrase getSecurityTokenAdminPin();

    public abstract boolean isShouldUpload();
    public abstract boolean isShouldUploadAtomic();
    @Nullable
    public abstract ParcelableHkpKeyserver getUploadKeyserver();

    @Nullable
    public abstract ChangeUnlockParcel getNewUnlock();

    public static Builder buildNewKeyringParcel() {
        return new AutoValue_SaveKeyringParcel.Builder()
                .setAddUserAttribute(new ArrayList<WrappedUserAttribute>())
                .setRevokeSubKeys(new ArrayList<Long>())
                .setRevokeUserIds(new ArrayList<String>())
                .setAddSubKeys(new ArrayList<SubkeyAdd>())
                .setAddUserIds(new ArrayList<String>())
                .setChangeSubKeys(new ArrayList<SubkeyChange>())
                .setShouldUpload(false)
                .setShouldUploadAtomic(false);
    }

    public static Builder buildChangeKeyringParcel(long masterKeyId, byte[] fingerprint) {
        return buildNewKeyringParcel()
                .setMasterKeyId(masterKeyId)
                .setFingerprint(fingerprint);
    }

    public static Builder buildUpon(SaveKeyringParcel saveKeyringParcel) {
        return new AutoValue_SaveKeyringParcel.Builder(saveKeyringParcel);
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder setChangePrimaryUserId(String changePrimaryUserId);
        public abstract Builder setSecurityTokenPin(Passphrase securityTokenPin);
        public abstract Builder setSecurityTokenAdminPin(Passphrase securityTokenAdminPin);
        public abstract Builder setNewUnlock(ChangeUnlockParcel newUnlock);

        public abstract Long getMasterKeyId();
        public abstract byte[] getFingerprint();

        abstract ArrayList<SubkeyChange> getChangeSubKeys();
        public abstract String getChangePrimaryUserId();
        public abstract ArrayList<SubkeyAdd> getAddSubKeys();
        public abstract ArrayList<String> getAddUserIds();
        public abstract ArrayList<Long> getRevokeSubKeys();
        public abstract ArrayList<String> getRevokeUserIds();
        public abstract ArrayList<WrappedUserAttribute> getAddUserAttribute();

        abstract Builder setMasterKeyId(Long masterKeyId);
        abstract Builder setFingerprint(byte[] fingerprint);
        abstract Builder setAddUserIds(ArrayList<String> addUserIds);
        abstract Builder setAddUserAttribute(ArrayList<WrappedUserAttribute> addUserAttribute);
        abstract Builder setAddSubKeys(ArrayList<SubkeyAdd> addSubKeys);
        abstract Builder setChangeSubKeys(ArrayList<SubkeyChange> changeSubKeys);
        abstract Builder setRevokeUserIds(ArrayList<String> revokeUserIds);
        abstract Builder setRevokeSubKeys(ArrayList<Long> revokeSubKeys);
        abstract Builder setShouldUpload(boolean upload);
        abstract Builder setShouldUploadAtomic(boolean uploadAtomic);
        abstract Builder setUploadKeyserver(ParcelableHkpKeyserver keyserver);

        public void addOrReplaceSubkeyChange(SubkeyChange newChange) {
            SubkeyChange foundSubkeyChange = getSubkeyChange(newChange.getSubKeyId());

            ArrayList<SubkeyChange> changeSubKeys = getChangeSubKeys();
            if (foundSubkeyChange != null) {
                changeSubKeys.remove(foundSubkeyChange);
            }
            changeSubKeys.add(newChange);
        }

        public void removeSubkeyChange(SubkeyChange change) {
            getChangeSubKeys().remove(change);
        }

        public void setUpdateOptions(boolean upload, boolean uploadAtomic, ParcelableHkpKeyserver keyserver) {
            setShouldUpload(upload);
            setShouldUploadAtomic(uploadAtomic);
            setUploadKeyserver(keyserver);
        }

        public abstract SaveKeyringParcel build();

        public void addSubkeyAdd(SubkeyAdd subkeyAdd) {
            getAddSubKeys().add(subkeyAdd);
        }

        public void addUserId(String userId) {
            getAddUserIds().add(userId);
        }

        public void addRevokeSubkey(long masterKeyId) {
            getRevokeSubKeys().add(masterKeyId);
        }

        public void removeRevokeSubkey(long keyId) {
            getRevokeSubKeys().remove(keyId);
        }

        public void addRevokeUserId(String userId) {
            getRevokeUserIds().add(userId);
        }

        public void removeRevokeUserId(String userId) {
            getRevokeUserIds().remove(userId);
        }

        public SubkeyChange getSubkeyChange(long keyId) {
            ArrayList<SubkeyChange> changeSubKeys = getChangeSubKeys();
            if (changeSubKeys == null) {
                return null;
            }

            for (SubkeyChange subkeyChange : changeSubKeys) {
                if (subkeyChange.getSubKeyId() == keyId) {
                    return subkeyChange;
                }
            }
            return null;
        }

        public void addUserAttribute(WrappedUserAttribute ua) {
            getAddUserAttribute().add(ua);
        }
    }

    // performance gain for using Parcelable here would probably be negligible,
    // use Serializable instead.
    @AutoValue
    public abstract static class SubkeyAdd implements Serializable {
        public abstract Algorithm getAlgorithm();
        @Nullable
        public abstract Integer getKeySize();
        @Nullable
        public abstract Curve getCurve();
        public abstract int getFlags();
        @Nullable
        public abstract Long getExpiry();

        public static SubkeyAdd createSubkeyAdd(Algorithm algorithm, Integer keySize, Curve curve, int flags,
                Long expiry) {
            return new AutoValue_SaveKeyringParcel_SubkeyAdd(algorithm, keySize, curve, flags, expiry);
        }
    }

    @AutoValue
    public abstract static class SubkeyChange implements Serializable {
        public abstract long getSubKeyId();
        @Nullable
        public abstract Integer getFlags();
        // this is a long unix timestamp, in seconds (NOT MILLISECONDS!)
        @Nullable
        public abstract Long getExpiry();
        // if this flag is true, the key will be recertified even if all above
        // values are no-ops
        public abstract boolean getRecertify();
        // if this flag is true, the subkey should be changed to a stripped key
        public abstract boolean getDummyStrip();
        // if this flag is true, the subkey should be moved to a security token
        public abstract boolean getMoveKeyToSecurityToken();
        // if this is non-null, the subkey will be changed to a divert-to-card
        // (security token) key for the given serial number
        @Nullable
        public abstract byte[] getSecurityTokenSerialNo();

        public static SubkeyChange createRecertifyChange(long keyId, boolean recertify) {
            return new AutoValue_SaveKeyringParcel_SubkeyChange(keyId, null, null, recertify, false, false, null);
        }

        public static SubkeyChange createFlagsOrExpiryChange(long keyId, Integer flags, Long expiry) {
            return new AutoValue_SaveKeyringParcel_SubkeyChange(keyId, flags, expiry, false, false, false, null);
        }

        public static SubkeyChange createStripChange(long keyId) {
            return new AutoValue_SaveKeyringParcel_SubkeyChange(keyId, null, null, false, true, false, null);
        }

        public static SubkeyChange createMoveToSecurityTokenChange(long keyId) {
            return new AutoValue_SaveKeyringParcel_SubkeyChange(keyId, null, null, false, false, true, null);
        }

        public static SubkeyChange createSecurityTokenSerialNo(long keyId, byte[] securityTokenSerialNo) {
            return new AutoValue_SaveKeyringParcel_SubkeyChange(keyId, null, null, false, false, false, securityTokenSerialNo);
        }
    }

    // All supported algorithms
    public enum Algorithm {
        RSA, DSA, ELGAMAL, ECDSA, ECDH
    }

    // All curves defined in the standard
    // http://www.bouncycastle.org/wiki/pages/viewpage.action?pageId=362269
    public enum Curve {
        NIST_P256, NIST_P384, NIST_P521,

        // these are supported by gpg, but they are not in rfc6637 and not supported by BouncyCastle yet
        // (adding support would be trivial though -> JcaPGPKeyConverter.java:190)
        // BRAINPOOL_P256, BRAINPOOL_P384, BRAINPOOL_P512
    }
}
