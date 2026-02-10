package com.android.study.douyin;

/* renamed from: X.0tPy, reason: invalid class name and case insensitive filesystem */
/* loaded from: classes29.dex */
public final class C1851880tPy<A, B> {
    public final A LIZ;
    public final B LIZIZ;

    public final int hashCode() {
        int iHashCode;
        A a = this.LIZ;
        int iHashCode2 = 0;
        if (a == null) {
            iHashCode = 0;
        } else {
            iHashCode = a.hashCode();
        }
        int i = (iHashCode + 31) * 31;
        B b = this.LIZIZ;
        if (b != null) {
            iHashCode2 = b.hashCode();
        }
        return i + iHashCode2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || C1851880tPy.class != obj.getClass()) {
            return false;
        }
        C1851880tPy c1851880tPy = (C1851880tPy) obj;
        A a = this.LIZ;
        if (a == null) {
            if (c1851880tPy.LIZ != null) {
                return false;
            }
        } else if (!a.equals(c1851880tPy.LIZ)) {
            return false;
        }
        B b = this.LIZIZ;
        if (b == null) {
            if (c1851880tPy.LIZIZ != null) {
                return false;
            }
        } else if (!b.equals(c1851880tPy.LIZIZ)) {
            return false;
        }
        return true;
    }

    public C1851880tPy(A a, B b) {
        this.LIZ = a;
        this.LIZIZ = b;
    }
}
