/*
 * Copyright (C) 2013-2015 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2015 Vincent Breitmoser <v.breitmoser@mugenguild.com>
 * Copyright (C) 2015 Adithya Abraham Philip <adithyaphilip@gmail.com>
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


import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import org.sufficientlysecure.keychain.keyimport.ParcelableHkpKeyserver;

@AutoValue
public abstract class RevokeKeyringParcel implements Parcelable {
    public abstract long getMasterKeyId();
    public abstract boolean isShouldUpload();
    @Nullable
    public abstract ParcelableHkpKeyserver getKeyserver();

    public static RevokeKeyringParcel createRevokeKeyringParcel(long masterKeyId, boolean upload,
            ParcelableHkpKeyserver keyserver) {
        return new AutoValue_RevokeKeyringParcel(masterKeyId, upload, keyserver);
    }
}