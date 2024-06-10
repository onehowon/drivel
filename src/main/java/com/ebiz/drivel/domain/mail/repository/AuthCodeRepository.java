package com.ebiz.drivel.domain.mail.repository;

import com.ebiz.drivel.domain.mail.dto.AuthCodeCheckDTO;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class AuthCodeRepository {
    private final int VERIFICATION_EXPIRE = 60 * 3;
    private final String VERIFICATION_PREFIX = "verificationEmail:";

    private RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    public AuthCodeRepository(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void save(final String email, final String randomCode) {
        valueOperations.set(VERIFICATION_PREFIX + email,
                randomCode);
        redisTemplate.expire(VERIFICATION_PREFIX + email, VERIFICATION_EXPIRE,
                TimeUnit.SECONDS);
    }

    public void delete(String email) {
        redisTemplate.delete(VERIFICATION_PREFIX + email);
    }

    public boolean checkByEmailAndRandomCode(AuthCodeCheckDTO authCodeCheckDTO) {
        String randomCode = valueOperations.get(VERIFICATION_PREFIX + authCodeCheckDTO.getEmail());
        if (!Objects.isNull(randomCode) && authCodeCheckDTO.getRandomCode().equals(randomCode.toString())) {
            delete(authCodeCheckDTO.getEmail());
            return true;
        }
        return false;
    }
}
