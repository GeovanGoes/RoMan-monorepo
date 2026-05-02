package com.roman.infrastructure.security;

import com.roman.domain.exception.UrlProibidaException;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class SsrfValidationUtil {

    private SsrfValidationUtil() {}

    public static void validarUrl(String url) {
        try {
            URL parsed = new URL(url);
            String host = parsed.getHost();
            InetAddress address = InetAddress.getByName(host);
            if (isPrivateAddress(address)) {
                throw new UrlProibidaException(url);
            }
        } catch (MalformedURLException | UnknownHostException e) {
            throw new UrlProibidaException(url);
        }
    }

    private static boolean isPrivateAddress(InetAddress address) {
        return address.isLoopbackAddress()
                || address.isSiteLocalAddress()
                || address.isLinkLocalAddress()
                || address.isMulticastAddress()
                || address.isAnyLocalAddress();
    }
}
