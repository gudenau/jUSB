package net.gudenau.jusb.internal.libusb;

import net.gudenau.jusb.internal.ForeignUtils;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.invoke.VarHandle;

public record Timeval(MemorySegment segment) {
    static final MemoryLayout LAYOUT = ForeignUtils.structure(
        ForeignUtils.S64.withName("tv_sec"),
        ForeignUtils.S64.withName("tv_usec")
    );
    
    private static final VarHandle tv_sec = ForeignUtils.layoutHandle(LAYOUT, "tv_sec");
    private static final VarHandle tv_usec = ForeignUtils.layoutHandle(LAYOUT, "tv_usec");
    
    public Timeval(SegmentAllocator allocator) {
        this(allocator.allocate(LAYOUT));
    }
    
    public long tv_sec() {
        return (long) tv_sec.get(segment);
    }
    
    public long tv_usec() {
        return (long) tv_usec.get(segment);
    }
    
    public Timeval tv_sec(long value) {
        tv_sec.set(segment, value);
        return this;
    }
    
    public Timeval tv_usec(long value) {
        tv_usec.set(segment, value);
        return this;
    }
}
