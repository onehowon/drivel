package com.ebiz.drivel.domain.review.dto;

import com.ebiz.drivel.domain.review.entity.Review;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewDTO {
    private Long id;
    private LocalDateTime reviewDate;
    private Long courseId;
    private int rating;
    private String comment;

    public static ReviewDTO from(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .reviewDate(review.getReviewDate())
                .courseId(review.getCourse().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }
}
