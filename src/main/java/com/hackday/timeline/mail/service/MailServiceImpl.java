package com.hackday.timeline.mail.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackday.timeline.member.domain.Member;
import com.hackday.timeline.member.domain.MemberAuth;
import com.hackday.timeline.member.repository.MemberRepository;

@Transactional
@Service
public class MailServiceImpl implements MailService {

	private JavaMailSender mailSender;
	private MemberRepository memberRepository;
	private RedisTemplate<String, String> redisTemplate;

	public MailServiceImpl(JavaMailSender mailSender, MemberRepository memberRepository,
		RedisTemplate<String, String> redisTemplate) {
		this.mailSender = mailSender;
		this.memberRepository = memberRepository;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public String getKey() throws Exception {
		Random ran = new Random();
		StringBuffer sb = new StringBuffer();
		int num = 0;

		//20글자의 난수 생성
		while (sb.length() < 21) {
			num = ran.nextInt(75) + 48;
			if ((num >= 48 && num <= 57) || (num >= 65 && num <= 90) || (num >= 97 && num <= 122)) {
				sb.append((char)num);
			}
		}

		return sb.toString();
	}

	@Override
	public void mailSendWithMemberKey(String userId) throws Exception, MessagingException {
		String key = getKey();
		String hashKey = String.format("user:%s", userId);
		Map<String, String> map = new HashMap<>();
		map.put("keyValue", key);
		redisTemplate.opsForHash().putAll(hashKey, map);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper mail = new MimeMessageHelper(message, true, "UTF-8");

		String htmlStr = "<h2>안녕하세요!</h2><br><br>"
			+ "<h3>" + userId + "님</h3>" + "<p>인증하기 버튼을 누르시면 로그인을 하실 수 있습니다 : "
			+ "<a href='http://localhost:8080" + "/mail/update?key=" + userId
			+ "&keyValue=" + key + "'>인증하기</a></p>";
		mail.setTo(userId);

		mail.setFrom("hackdaytimeline@gmail.com");
		mail.setText(htmlStr, true);

		mail.setSubject("[본인인증] SNS_TIMELINE님의 인증메일입니다");
		mailSender.send(message);
	}

	@Override
	public void authMemberKey(String key) throws Exception {
		String hashKey = String.format("user:%s", key);
		redisTemplate.opsForHash().delete(hashKey, "keyValue");

		Member member = (Member)memberRepository.findByUserId(key).get(0);
		member.setAuthList(new ArrayList<>());

		MemberAuth auth = new MemberAuth();
		auth.setAuth("ROLE_MEMBER");
		member.addAuth(auth);

		memberRepository.save(member);
	}

	@Override
	public boolean checkKey(String key, String keyValue) throws Exception {
		String hashKey = String.format("user:%s", key);
		String value = (String)redisTemplate.opsForHash().get(hashKey, "keyValue");
		return value.equals(keyValue) ? true : false;
	}

}
