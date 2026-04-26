package com.example.nexusbooking.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestor de tokens JWT para manejar revocations i invalidacions
 * Prevé que tokens stolines es puguin usar si son revocats
 */
@Component
public class TokenRevocationService {
    
    private static final Logger log = LoggerFactory.getLogger(TokenRevocationService.class);
    
    // Set de tokens revocats (en producció usar Redis)
    private final Set<String> revocatedTokens = ConcurrentHashMap.newKeySet();
    
    /**
     * Revocar un token (per logout o canvi de contrasenya)
     */
    public void revokeToken(String token) {
        revocatedTokens.add(token);
        log.info("Token revoked. Revoked tokens count: {}", revocatedTokens.size());
    }
    
    /**
     * Comprovar si un token está revocatrevoked
     */
    public boolean isTokenRevoked(String token) {
        return revocatedTokens.contains(token);
    }
    
    /**
     * Netejar tokens antics (podria ser cridat amb scheduler)
     */
    public void cleanupOldTokens() {
        revocatedTokens.clear();
        log.info("Token cache cleared");
    }
    
    /**
     * Obtenir número de tokens revocats
     */
    public int getRevokedTokenCount() {
        return revocatedTokens.size();
    }
}
