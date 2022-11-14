package net.gudenau.jusb.internal;

import java.lang.foreign.MemorySession;

final class OptionalSession implements AutoCloseable {
    private volatile MemorySession session;
    
    public MemorySession get() {
        if(session == null) {
            synchronized(this) {
                if(session == null) {
                    session = MemorySession.openShared();
                }
            }
        }
        return session;
    }
    
    @Override
    public void close() {
        synchronized(this) {
            if(session != null) {
                session.close();
            }
        }
    }
}
