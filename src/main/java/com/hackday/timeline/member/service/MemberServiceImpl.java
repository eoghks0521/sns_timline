package com.hackday.timeline.member.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackday.timeline.member.domain.Member;
import com.hackday.timeline.member.domain.MemberAuth;
import com.hackday.timeline.member.repository.MemberRepository;
import com.hackday.timeline.member.vo.MemberVO;
import com.hackday.timeline.subscription.repository.SubsRepository;
import com.hackday.timeline.subscription.vo.SubsVO;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final SubsRepository subsRepository;

	public MemberServiceImpl(MemberRepository memberRepository, SubsRepository subsRepository) {
		this.memberRepository = memberRepository;
		this.subsRepository = subsRepository;
	}

	@Override
	public void register(Member member) throws Exception {
		Member memberEntity = new Member();
		memberEntity.setUserId(member.getUserId());
		memberEntity.setUserPw(member.getUserPw());
		memberEntity.setUserName(member.getUserName());

		MemberAuth memberAuth = new MemberAuth();
		memberAuth.setAuth("ROLE_MEMBER");
		memberEntity.addAuth(memberAuth);

		memberRepository.save(memberEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public Member read(Long userNo) throws UsernameNotFoundException {
		return memberRepository.getOne(userNo);
	}

	@Override
	public void remove(Long userNo) throws UsernameNotFoundException {
		memberRepository.deleteById(userNo);
	}

	@Override
	public void modify(Member member) throws UsernameNotFoundException {
		Member userEntity = memberRepository.getOne(member.getUserNo());
		userEntity.setUserName(member.getUserName());
		memberRepository.save(userEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MemberVO> listAll(Long userNo) throws Exception {
		List<Member> memberList = memberRepository.findAll();
		List<SubsVO> valueArray = subsRepository.memberSubsList(userNo);
		List<MemberVO> memberVOList = new ArrayList<>();
		Map<Long, Long> map = new HashMap<>();

		for (SubsVO subsVO : valueArray) {
			map.put(subsVO.getUserNo(), subsVO.getSubsNo());
		}

		memberList.stream().forEach(member -> {

			if (map.containsKey(member.getUserNo())) {
				memberVOList.add(MemberVO.builder()
					.userNo(member.getUserNo())
					.userId(member.getUserId())
					.userName(member.getUserName())
					.subsOk(true)
					.subsNo(map.get(member.getUserNo()))
					.build());
			} else {
				memberVOList.add(MemberVO.builder()
					.userNo(member.getUserNo())
					.userId(member.getUserId())
					.userName(member.getUserName())
					.subsOk(false)
					.build());
			}
		});
		return memberVOList;
	}

}
