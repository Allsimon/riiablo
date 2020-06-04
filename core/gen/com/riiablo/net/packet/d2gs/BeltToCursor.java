// automatically generated by the FlatBuffers compiler, do not modify

package com.riiablo.net.packet.d2gs;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class BeltToCursor extends Table {
  public static BeltToCursor getRootAsBeltToCursor(ByteBuffer _bb) { return getRootAsBeltToCursor(_bb, new BeltToCursor()); }
  public static BeltToCursor getRootAsBeltToCursor(ByteBuffer _bb, BeltToCursor obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public BeltToCursor __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int itemId() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createBeltToCursor(FlatBufferBuilder builder,
      int itemId) {
    builder.startObject(1);
    BeltToCursor.addItemId(builder, itemId);
    return BeltToCursor.endBeltToCursor(builder);
  }

  public static void startBeltToCursor(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addItemId(FlatBufferBuilder builder, int itemId) { builder.addInt(0, itemId, 0); }
  public static int endBeltToCursor(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
