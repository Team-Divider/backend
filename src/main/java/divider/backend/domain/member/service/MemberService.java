package divider.backend.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import divider.backend.domain.member.entity.Member;
import divider.backend.domain.member.dto.MemberSimpleResponseDto;
import divider.backend.exception.situation.MemberNotFoundException;
import divider.backend.domain.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<MemberSimpleResponseDto> findAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberSimpleResponseDto::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberSimpleResponseDto findMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        return MemberSimpleResponseDto.toDto(member);
    }
}
