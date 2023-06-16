package net.gudenau.jusb.internal;

import java.lang.foreign.Arena;

final class LazyArena implements AutoCloseable {
    private volatile Arena arena;
    
    public Arena get() {
        if(arena == null) {
            synchronized(this) {
                if(arena == null) {
                    arena = Arena.openShared();
                }
            }
        }
        return arena;
    }
    
    @Override
    public void close() {
        synchronized(this) {
            if(arena != null) {
                arena.close();
            }
        }
    }
}
