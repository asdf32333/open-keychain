package org.sufficientlysecure.keychain.util;


import java.nio.ByteBuffer;
import java.util.HashMap;

import android.os.Parcel;

import com.ryanharter.auto.value.parcel.TypeAdapter;


public class ByteMapParcelAdapter implements TypeAdapter<HashMap<ByteBuffer,byte[]>> {
    @Override
    public HashMap<ByteBuffer, byte[]> fromParcel(Parcel source) {
        int count = source.readInt();
        HashMap<ByteBuffer,byte[]> result = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            byte[] key = source.createByteArray();
            byte[] value = source.createByteArray();
            result.put(ByteBuffer.wrap(key), value);
        }
        return result;
    }

    @Override
    public void toParcel(HashMap<ByteBuffer, byte[]> value, Parcel dest) {
        dest.writeInt(value.size());
        for (HashMap.Entry<ByteBuffer, byte[]> entry : value.entrySet()) {
            dest.writeByteArray(entry.getKey().array());
            dest.writeByteArray(entry.getValue());
        }
    }
}
