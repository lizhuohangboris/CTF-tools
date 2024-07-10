package org.apache.catalina.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/NetMask.class */
public final class NetMask {
    private static final StringManager sm = StringManager.getManager(NetMask.class);
    private final String expression;
    private final byte[] netaddr;
    private final int nrBytes;
    private final int lastByteShift;

    public NetMask(String input) {
        this.expression = input;
        int idx = input.indexOf("/");
        if (idx == -1) {
            try {
                this.netaddr = InetAddress.getByName(input).getAddress();
                this.nrBytes = this.netaddr.length;
                this.lastByteShift = 0;
                return;
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException(sm.getString("netmask.invalidAddress", input));
            }
        }
        String addressPart = input.substring(0, idx);
        String cidrPart = input.substring(idx + 1);
        try {
            this.netaddr = InetAddress.getByName(addressPart).getAddress();
            int addrlen = this.netaddr.length * 8;
            try {
                int cidr = Integer.parseInt(cidrPart);
                if (cidr < 0) {
                    throw new IllegalArgumentException(sm.getString("netmask.cidrNegative", cidrPart));
                }
                if (cidr > addrlen) {
                    throw new IllegalArgumentException(sm.getString("netmask.cidrTooBig", cidrPart, Integer.valueOf(addrlen)));
                }
                this.nrBytes = cidr / 8;
                int remainder = cidr % 8;
                this.lastByteShift = remainder == 0 ? 0 : 8 - remainder;
            } catch (NumberFormatException e2) {
                throw new IllegalArgumentException(sm.getString("netmask.cidrNotNumeric", cidrPart));
            }
        } catch (UnknownHostException e3) {
            throw new IllegalArgumentException(sm.getString("netmask.invalidAddress", addressPart));
        }
    }

    public boolean matches(InetAddress addr) {
        byte[] candidate = addr.getAddress();
        if (candidate.length != this.netaddr.length) {
            return false;
        }
        int i = 0;
        while (i < this.nrBytes) {
            if (this.netaddr[i] == candidate[i]) {
                i++;
            } else {
                return false;
            }
        }
        if (this.lastByteShift == 0) {
            return true;
        }
        int lastByte = this.netaddr[i] ^ candidate[i];
        return (lastByte >> this.lastByteShift) == 0;
    }

    public String toString() {
        return this.expression;
    }
}