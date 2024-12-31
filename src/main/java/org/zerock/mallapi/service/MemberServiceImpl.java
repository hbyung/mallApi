package org.zerock.mallapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.MemberRole;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.dto.MemberModifyDTO;
import org.zerock.mallapi.repository.MemberRepository;

import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {

        //accessToken 이용해서 사용자 정보 가져오기
        String nickname = getNameFromKakaoAccessToken(accessToken);

        Optional<Member> result = memberRepository.findById(nickname);

        //값이 존재하면 그래도 정보 반환
        if (result.isPresent()) {

            MemberDTO memberDTO = entityToDTO(result.get());

            log.info("exist=============="+memberDTO);

            return memberDTO;
        }

        //기존 회원정보 db / 없는 경우
        Member socialMember = makeSocialMember(nickname);

        memberRepository.save(socialMember);

        MemberDTO memberDTO = entityToDTO(socialMember);

        return memberDTO;
    }

    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {

        Optional<Member> result = memberRepository.findById(memberModifyDTO.getEmail());

        Member member = result.orElseThrow();

        member.changeNickname(memberModifyDTO.getNickname());
        member.changeSocial(false);
        member.changePw(passwordEncoder.encode(memberModifyDTO.getPw()));

        memberRepository.save(member);

    }

    //db에 정보 없을경우 소셜 로그인 정보 만들기
    private Member makeSocialMember(String email) {

        String tempPassword = makeTempPassword();

        log.info("tempPassword : " + tempPassword);

        Member member = Member.builder()
                .email(email)
                .pw(passwordEncoder.encode(tempPassword))
                .nickname("socialMember")
                .social(true)
                .build();
        member.AddRole(MemberRole.USER);

        return member;
    }

    //access 토큰으로 정보 받아오기 (nickname)
    private String getNameFromKakaoAccessToken(String accessToken) {

        String kakaoGetURI = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer "+ accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetURI).build();

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uriBuilder.toUri(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info(response);

        LinkedHashMap<String, LinkedHashMap> bodyMap= response.getBody();

        log.info("------------------------------------");
        log.info(bodyMap);

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("properties");

        log.info("kakaoAccount: " + kakaoAccount);

        String nickname = kakaoAccount.get("nickname");

        return nickname;
    }

    //패스워드 자동생성
    private String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();

        for(int i = 0;  i < 10; i++){
            buffer.append(  (char) ( (int)(Math.random()*55) + 65  ));
        }
        return buffer.toString();
    }

}
