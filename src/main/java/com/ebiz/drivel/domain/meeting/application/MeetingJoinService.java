package com.ebiz.drivel.domain.meeting.application;

import static com.ebiz.drivel.domain.meeting.MeetingConstants.MEETING_NOT_FOUND_EXCEPTION_MESSAGE;

import com.ebiz.drivel.domain.auth.application.UserDetailsServiceImpl;
import com.ebiz.drivel.domain.meeting.dto.JoinRequestDecisionDTO;
import com.ebiz.drivel.domain.meeting.dto.MeetingJoinRequestDTO;
import com.ebiz.drivel.domain.meeting.entity.Meeting;
import com.ebiz.drivel.domain.meeting.entity.Meeting.MeetingStatus;
import com.ebiz.drivel.domain.meeting.entity.MeetingJoinRequest;
import com.ebiz.drivel.domain.meeting.entity.MeetingJoinRequest.Status;
import com.ebiz.drivel.domain.meeting.exception.AlreadyRequestedJoinMeetingException;
import com.ebiz.drivel.domain.meeting.exception.MeetingJoinRequestNotFoundException;
import com.ebiz.drivel.domain.meeting.exception.MeetingNotFoundException;
import com.ebiz.drivel.domain.meeting.repository.MeetingJoinRequestRepository;
import com.ebiz.drivel.domain.meeting.repository.MeetingRepository;
import com.ebiz.drivel.domain.member.entity.Member;
import com.ebiz.drivel.domain.sse.Alert;
import com.ebiz.drivel.domain.sse.Alert.AlertCategory;
import com.ebiz.drivel.domain.sse.AlertDTO;
import com.ebiz.drivel.domain.sse.AlertRepository;
import com.ebiz.drivel.domain.sse.AlertService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingJoinService {

    private final MeetingRepository meetingRepository;
    private final MeetingMemberService meetingMemberService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JPAQueryFactory queryFactory;
    private final AlertService alertService;
    private final AlertRepository alertRepository;
    private final MeetingJoinRequestRepository meetingJoinRequestRepository;

    @Transactional
    public void requestJoinMeeting(Long id) {
        Member member = userDetailsService.getMemberByContextHolder();
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException(MEETING_NOT_FOUND_EXCEPTION_MESSAGE));

        //이미 가입 요청했는지 확인
        if (meeting.isWaitingRequestMember(member)) {
            throw new MeetingJoinRequestNotFoundException("이미 가입 신청한 모임입니다");
        }

        // 이미 가입된 멤버인지 확인
        if (meeting.isAlreadyJoinedMember(member)) {
            throw new AlreadyRequestedJoinMeetingException("이미 가입된 모임입니다");
        }
        
        saveMeetingJoinRequest(member, meeting);
    }

    @Transactional
    public void cancelJoinMeeting(Long id) {
        // 요청이 없으면 예외처리
        MeetingJoinRequest meetingJoinRequest = meetingJoinRequestRepository.findById(id)
                .orElseThrow(() -> new MeetingJoinRequestNotFoundException("찾을 수 없는 요청입니다"));

        // 이미 결정된 요청이면 예외처리
        if (!meetingJoinRequest.isWaitingRequest()) {
            throw new MeetingJoinRequestNotFoundException("이미 처리된 요청입니다");
        }
        meetingJoinRequest.cancel();
    }

    @Transactional
    public void acceptJoinMeeting(JoinRequestDecisionDTO request) {
        MeetingJoinRequest meetingJoinRequest = meetingJoinRequestRepository.findById(request.getId())
                .orElseThrow(() -> new MeetingJoinRequestNotFoundException("찾을 수 없는 요청입니다"));
        if (meetingJoinRequest.isAlreadyDecidedRequest()) {
            throw new MeetingJoinRequestNotFoundException("이미 처리가 된 요청입니다");
        }

        Meeting meeting = meetingJoinRequest.getMeeting();
        Member member = meetingJoinRequest.getMember();
        if (request.isAccepted()) {
            meetingMemberService.insertMeetingMember(meeting, member);
            meetingJoinRequest.accept();
            Alert alert = Alert.builder()
                    .meetingId(meeting.getId())
                    .receiverId(member.getId())
                    .alertCategory(AlertCategory.ACCEPTED)
                    .title("수락")
                    .content("가입되었습니다")
                    .build();
            alertRepository.save(alert);
            AlertDTO alertDTO = AlertDTO.from(alert);
            alertService.sendToClient(member.getId(), AlertCategory.ACCEPTED.toString(), alertDTO);
        } else {
            meetingJoinRequest.reject();
            Alert alert = Alert.builder()
                    .meetingId(meeting.getId())
                    .receiverId(member.getId())
                    .alertCategory(AlertCategory.ACCEPTED)
                    .title("거절")
                    .content("가입 신청이 거절되었습니다")
                    .build();
            alertRepository.save(alert);
            AlertDTO alertDTO = AlertDTO.from(alert);
            alertService.sendToClient(member.getId(), AlertCategory.REJECTED.toString(), alertDTO);
        }
    }

    public List<MeetingJoinRequestDTO> getJoinRequests() {
        Member member = userDetailsService.getMemberByContextHolder();
        List<Meeting> meetings = meetingRepository.findByMasterMemberAndStatus(member, MeetingStatus.ACTIVE);
        List<MeetingJoinRequestDTO> requests = new ArrayList<>();
        meetings.forEach(meeting -> {
            List<MeetingJoinRequest> joinRequests = meeting.getJoinRequests().stream()
                    .filter(request -> !request.isAlreadyDecidedRequest()).toList();
            if (!joinRequests.isEmpty()) {
                requests.add(MeetingJoinRequestDTO.from(meeting, joinRequests));
            }
        });
        return requests;
    }

    private void saveMeetingJoinRequest(Member member, Meeting meeting) {
        MeetingJoinRequest meetingJoinRequest = MeetingJoinRequest.builder()
                .meeting(meeting)
                .member(member)
                .status(Status.WAITING)
                .build();
        meetingJoinRequestRepository.save(meetingJoinRequest);
    }

}