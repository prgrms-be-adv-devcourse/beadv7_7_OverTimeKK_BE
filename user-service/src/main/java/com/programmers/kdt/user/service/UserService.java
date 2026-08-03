package com.programmers.kdt.user.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.user.dto.LoginRequest;
import com.programmers.kdt.user.dto.LoginResponse;
import com.programmers.kdt.user.dto.RefreshTokenRequest;
import com.programmers.kdt.user.dto.SignUpBusinessRequest;
import com.programmers.kdt.user.dto.SignUpIndividualRequest;
import com.programmers.kdt.user.dto.SignUpUserResponse;
import com.programmers.kdt.user.dto.WithdrawRequest;
import com.programmers.kdt.user.entity.User;
import com.programmers.kdt.user.exception.UserErrorCode;
import com.programmers.kdt.user.jwt.JwtProvider;
import com.programmers.kdt.user.jwt.RefreshTokenStore;
import com.programmers.kdt.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 인터페이스 없이 클래스 하나로 (팀 요청: MVC 단순화)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    @Transactional
    public SignUpUserResponse signUpIndividual(SignUpIndividualRequest request) {
        validateDuplicate(request.username(), request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.signUpIndividual(request.username(), request.email(), encodedPassword);
        User saved = userRepository.save(user);

        return SignUpUserResponse.from(saved);
    }

    @Transactional
    public SignUpUserResponse signUpBusiness(SignUpBusinessRequest request) {
        validateDuplicate(request.username(), request.email());
        validateDuplicateBusinessNumber(request.businessNumber());

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.signUpBusiness(request.username(), request.email(), encodedPassword,
                request.businessName(), request.businessNumber());
        User saved = userRepository.save(user);

        return SignUpUserResponse.from(saved);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_PASSWORD);
        }
        if (user.isWithdrawn()) {
            throw new BusinessException(UserErrorCode.WITHDRAWN_USER);
        }

        return issueTokens(user);
    }

    /**
     * refresh token 로테이션: 사용된 토큰은 즉시 폐기하고 새 access/refresh token 쌍을 발급한다.
     * 이미 폐기된(예전) 토큰이 다시 들어오면 탈취로 간주해 해당 사용자의 세션을 강제 무효화한다.
     */
    public LoginResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        try {
            jwtProvider.validateToken(token);
        } catch (JwtException e) {
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }
        if (!jwtProvider.isRefreshToken(token)) {
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getUserId(token);
        String presentedTokenId = jwtProvider.getTokenId(token);
        String storedTokenId = refreshTokenStore.find(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN));

        if (!storedTokenId.equals(presentedTokenId)) {
            refreshTokenStore.delete(userId);
            log.warn("탈취 의심: userId={}의 폐기된 refresh token이 재사용되어 세션을 강제 무효화했습니다.", userId);
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        if (user.isWithdrawn()) {
            refreshTokenStore.delete(userId);
            throw new BusinessException(UserErrorCode.WITHDRAWN_USER);
        }

        return issueTokens(user);
    }

    public void logout(Long userId) {
        refreshTokenStore.delete(userId);
    }

    private LoginResponse issueTokens(User user) {
        String accessToken = jwtProvider.createToken(user.getUserId(), user.getUsername());
        String tokenId = UUID.randomUUID().toString();
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getUsername(), tokenId);
        refreshTokenStore.save(user.getUserId(), tokenId);

        return new LoginResponse(accessToken, refreshToken);
    }

    private void validateDuplicate(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateDuplicateBusinessNumber(String businessNumber) {
        if (userRepository.existsByBusinessNumber(businessNumber)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }
    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.isWithdrawn()) {
            throw new BusinessException(UserErrorCode.WITHDRAWN_USER);
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_PASSWORD);
        }

        user.withdraw();
        refreshTokenStore.delete(userId);
    }

    // TODO: 조회 기능 추가
}
