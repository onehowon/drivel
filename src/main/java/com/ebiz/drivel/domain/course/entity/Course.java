package com.ebiz.drivel.domain.course.entity;

import com.ebiz.drivel.domain.meeting.entity.Meeting;
import com.ebiz.drivel.domain.review.entity.CourseReview;
import com.ebiz.drivel.domain.waypoint.entity.Waypoint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer id2;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "latitude", nullable = false, precision = 8, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "description", length = 50)
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "region_id")
    private Long regionId;

    @Column(name = "region_name")
    private String regionName;

    @Column(name = "region_description")
    private String regionDescription;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<CourseReview> courseReviews;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Waypoint> waypoints;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<CourseLike> courseLikes;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<CourseTheme> courseThemes;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Meeting> meetings;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<CourseStyle> courseStyles;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<CourseTogether> courseTogethers;

    public double calculateAverageRating() {
        double average = courseReviews.stream()
                .filter(courseReview -> !courseReview.isDeleted())
                .mapToLong(CourseReview::getRating).average().orElse(0);
        DecimalFormat df = new DecimalFormat("#.#");
        return Double.parseDouble(df.format(average));
    }

    public int countReviews() {
        return (int) courseReviews.stream().filter(courseReview -> !courseReview.isDeleted()).count();
    }

    public String generateWaypointString() {
        return String.join("-",
                waypoints.stream()
                        .map(waypoint -> waypoint.getName())
                        .collect(Collectors.toList()));
    }

}
