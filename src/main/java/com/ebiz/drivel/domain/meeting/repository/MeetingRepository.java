package com.ebiz.drivel.domain.meeting.repository;

import com.ebiz.drivel.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
