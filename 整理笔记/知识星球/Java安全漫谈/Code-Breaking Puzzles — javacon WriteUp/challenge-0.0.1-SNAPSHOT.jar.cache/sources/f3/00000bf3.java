package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Status.class */
public class Status {
    public static final int APR_OS_START_ERROR = 20000;
    public static final int APR_OS_ERRSPACE_SIZE = 50000;
    public static final int APR_OS_START_STATUS = 70000;
    public static final int APR_OS_START_USERERR = 120000;
    public static final int APR_OS_START_USEERR = 120000;
    public static final int APR_OS_START_CANONERR = 620000;
    public static final int APR_OS_START_EAIERR = 670000;
    public static final int APR_OS_START_SYSERR = 720000;
    public static final int APR_SUCCESS = 0;
    public static final int APR_ENOSTAT = 20001;
    public static final int APR_ENOPOOL = 20002;
    public static final int APR_EBADDATE = 20004;
    public static final int APR_EINVALSOCK = 20005;
    public static final int APR_ENOPROC = 20006;
    public static final int APR_ENOTIME = 20007;
    public static final int APR_ENODIR = 20008;
    public static final int APR_ENOLOCK = 20009;
    public static final int APR_ENOPOLL = 20010;
    public static final int APR_ENOSOCKET = 20011;
    public static final int APR_ENOTHREAD = 20012;
    public static final int APR_ENOTHDKEY = 20013;
    public static final int APR_EGENERAL = 20014;
    public static final int APR_ENOSHMAVAIL = 20015;
    public static final int APR_EBADIP = 20016;
    public static final int APR_EBADMASK = 20017;
    public static final int APR_EDSOOPEN = 20019;
    public static final int APR_EABSOLUTE = 20020;
    public static final int APR_ERELATIVE = 20021;
    public static final int APR_EINCOMPLETE = 20022;
    public static final int APR_EABOVEROOT = 20023;
    public static final int APR_EBADPATH = 20024;
    public static final int APR_EPATHWILD = 20025;
    public static final int APR_ESYMNOTFOUND = 20026;
    public static final int APR_EPROC_UNKNOWN = 20027;
    public static final int APR_ENOTENOUGHENTROPY = 20028;
    public static final int APR_INCHILD = 70001;
    public static final int APR_INPARENT = 70002;
    public static final int APR_DETACH = 70003;
    public static final int APR_NOTDETACH = 70004;
    public static final int APR_CHILD_DONE = 70005;
    public static final int APR_CHILD_NOTDONE = 70006;
    public static final int APR_TIMEUP = 70007;
    public static final int APR_INCOMPLETE = 70008;
    public static final int APR_BADCH = 70012;
    public static final int APR_BADARG = 70013;
    public static final int APR_EOF = 70014;
    public static final int APR_NOTFOUND = 70015;
    public static final int APR_ANONYMOUS = 70019;
    public static final int APR_FILEBASED = 70020;
    public static final int APR_KEYBASED = 70021;
    public static final int APR_EINIT = 70022;
    public static final int APR_ENOTIMPL = 70023;
    public static final int APR_EMISMATCH = 70024;
    public static final int APR_EBUSY = 70025;
    public static final int TIMEUP = 120001;
    public static final int EAGAIN = 120002;
    public static final int EINTR = 120003;
    public static final int EINPROGRESS = 120004;
    public static final int ETIMEDOUT = 120005;

    private static native boolean is(int i, int i2);

    public static final boolean APR_STATUS_IS_ENOSTAT(int s) {
        return is(s, 1);
    }

    public static final boolean APR_STATUS_IS_ENOPOOL(int s) {
        return is(s, 2);
    }

    public static final boolean APR_STATUS_IS_EBADDATE(int s) {
        return is(s, 4);
    }

    public static final boolean APR_STATUS_IS_EINVALSOCK(int s) {
        return is(s, 5);
    }

    public static final boolean APR_STATUS_IS_ENOPROC(int s) {
        return is(s, 6);
    }

    public static final boolean APR_STATUS_IS_ENOTIME(int s) {
        return is(s, 7);
    }

    public static final boolean APR_STATUS_IS_ENODIR(int s) {
        return is(s, 8);
    }

    public static final boolean APR_STATUS_IS_ENOLOCK(int s) {
        return is(s, 9);
    }

    public static final boolean APR_STATUS_IS_ENOPOLL(int s) {
        return is(s, 10);
    }

    public static final boolean APR_STATUS_IS_ENOSOCKET(int s) {
        return is(s, 11);
    }

    public static final boolean APR_STATUS_IS_ENOTHREAD(int s) {
        return is(s, 12);
    }

    public static final boolean APR_STATUS_IS_ENOTHDKEY(int s) {
        return is(s, 13);
    }

    public static final boolean APR_STATUS_IS_EGENERAL(int s) {
        return is(s, 14);
    }

    public static final boolean APR_STATUS_IS_ENOSHMAVAIL(int s) {
        return is(s, 15);
    }

    public static final boolean APR_STATUS_IS_EBADIP(int s) {
        return is(s, 16);
    }

    public static final boolean APR_STATUS_IS_EBADMASK(int s) {
        return is(s, 17);
    }

    public static final boolean APR_STATUS_IS_EDSOPEN(int s) {
        return is(s, 19);
    }

    public static final boolean APR_STATUS_IS_EABSOLUTE(int s) {
        return is(s, 20);
    }

    public static final boolean APR_STATUS_IS_ERELATIVE(int s) {
        return is(s, 21);
    }

    public static final boolean APR_STATUS_IS_EINCOMPLETE(int s) {
        return is(s, 22);
    }

    public static final boolean APR_STATUS_IS_EABOVEROOT(int s) {
        return is(s, 23);
    }

    public static final boolean APR_STATUS_IS_EBADPATH(int s) {
        return is(s, 24);
    }

    public static final boolean APR_STATUS_IS_EPATHWILD(int s) {
        return is(s, 25);
    }

    public static final boolean APR_STATUS_IS_ESYMNOTFOUND(int s) {
        return is(s, 26);
    }

    public static final boolean APR_STATUS_IS_EPROC_UNKNOWN(int s) {
        return is(s, 27);
    }

    public static final boolean APR_STATUS_IS_ENOTENOUGHENTROPY(int s) {
        return is(s, 28);
    }

    public static final boolean APR_STATUS_IS_INCHILD(int s) {
        return is(s, 51);
    }

    public static final boolean APR_STATUS_IS_INPARENT(int s) {
        return is(s, 52);
    }

    public static final boolean APR_STATUS_IS_DETACH(int s) {
        return is(s, 53);
    }

    public static final boolean APR_STATUS_IS_NOTDETACH(int s) {
        return is(s, 54);
    }

    public static final boolean APR_STATUS_IS_CHILD_DONE(int s) {
        return is(s, 55);
    }

    public static final boolean APR_STATUS_IS_CHILD_NOTDONE(int s) {
        return is(s, 56);
    }

    public static final boolean APR_STATUS_IS_TIMEUP(int s) {
        return is(s, 57);
    }

    public static final boolean APR_STATUS_IS_INCOMPLETE(int s) {
        return is(s, 58);
    }

    public static final boolean APR_STATUS_IS_BADCH(int s) {
        return is(s, 62);
    }

    public static final boolean APR_STATUS_IS_BADARG(int s) {
        return is(s, 63);
    }

    public static final boolean APR_STATUS_IS_EOF(int s) {
        return is(s, 64);
    }

    public static final boolean APR_STATUS_IS_NOTFOUND(int s) {
        return is(s, 65);
    }

    public static final boolean APR_STATUS_IS_ANONYMOUS(int s) {
        return is(s, 69);
    }

    public static final boolean APR_STATUS_IS_FILEBASED(int s) {
        return is(s, 70);
    }

    public static final boolean APR_STATUS_IS_KEYBASED(int s) {
        return is(s, 71);
    }

    public static final boolean APR_STATUS_IS_EINIT(int s) {
        return is(s, 72);
    }

    public static final boolean APR_STATUS_IS_ENOTIMPL(int s) {
        return is(s, 73);
    }

    public static final boolean APR_STATUS_IS_EMISMATCH(int s) {
        return is(s, 74);
    }

    public static final boolean APR_STATUS_IS_EBUSY(int s) {
        return is(s, 75);
    }

    public static final boolean APR_STATUS_IS_EAGAIN(int s) {
        return is(s, 90);
    }

    public static final boolean APR_STATUS_IS_ETIMEDOUT(int s) {
        return is(s, 91);
    }

    public static final boolean APR_STATUS_IS_ECONNABORTED(int s) {
        return is(s, 92);
    }

    public static final boolean APR_STATUS_IS_ECONNRESET(int s) {
        return is(s, 93);
    }

    public static final boolean APR_STATUS_IS_EINPROGRESS(int s) {
        return is(s, 94);
    }

    public static final boolean APR_STATUS_IS_EINTR(int s) {
        return is(s, 95);
    }

    public static final boolean APR_STATUS_IS_ENOTSOCK(int s) {
        return is(s, 96);
    }

    public static final boolean APR_STATUS_IS_EINVAL(int s) {
        return is(s, 97);
    }
}