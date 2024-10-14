package com.ebiz.drivel.domain.course.service;

import static com.ebiz.drivel.domain.course.entity.QCourseStyle.courseStyle;
import static com.ebiz.drivel.domain.course.entity.QCourseTheme.courseTheme;
import static com.ebiz.drivel.domain.course.entity.QCourseTogether.courseTogether;
import static com.ebiz.drivel.domain.member.entity.QMemberStyle.memberStyle;
import static com.ebiz.drivel.domain.member.entity.QMemberTheme.memberTheme;
import static com.ebiz.drivel.domain.member.entity.QMemberTogether.memberTogether;

import com.ebiz.drivel.domain.course.entity.QCourse;
import com.ebiz.drivel.domain.member.entity.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;

public class CourseQueryHelper {

    public static BooleanBuilder createQueryFilter(List<Long> themeIds, List<Long> styleIds, List<Long> togetherIds) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        QCourse course = QCourse.course;

        addThemeFilter(themeIds, course, filterBuilder);
        addStyleFilter(styleIds, course, filterBuilder);
        addTogetherFilter(togetherIds, course, filterBuilder);

        return filterBuilder;
    }

    public static void addRegionFilter(List<Long> regionIds, QCourse course, BooleanBuilder filterBuilder) {
        if (regionIds != null && !regionIds.isEmpty()) {
            BooleanBuilder regionBuilder = new BooleanBuilder();
            for (Long regionId : regionIds) {
                regionBuilder.or(course.regionId.eq(regionId));
            }
            filterBuilder.and(regionBuilder);
        }
    }

    public static void addThemeFilter(List<Long> themeIds, QCourse course, BooleanBuilder filterBuilder) {
        if (themeIds != null && !themeIds.isEmpty()) {
            BooleanBuilder themeBuilder = new BooleanBuilder();
            for (Long themeId : themeIds) {
                themeBuilder.or(course.courseThemes.any().theme.id.eq(themeId));
            }
            filterBuilder.and(themeBuilder);
        }
    }

    public static void addStyleFilter(List<Long> styleIds, QCourse course, BooleanBuilder filterBuilder) {
        if (styleIds != null && !styleIds.isEmpty()) {
            BooleanBuilder styleBuilder = new BooleanBuilder();
            for (Long styleId : styleIds) {
                styleBuilder.or(course.courseStyles.any().style.id.eq(styleId));
            }
            filterBuilder.and(styleBuilder);
        }
    }

    public static void addTogetherFilter(List<Long> togetherIds, QCourse course, BooleanBuilder filterBuilder) {
        if (togetherIds != null && !togetherIds.isEmpty()) {
            BooleanBuilder togetherBuilder = new BooleanBuilder();
            for (Long togetherId : togetherIds) {
                togetherBuilder.or(course.courseTogethers.any().together.id.eq(togetherId));
            }
            filterBuilder.and(togetherBuilder);
        }
    }

    public static OrderSpecifier<?> getOrderSpecifier(OrderBy orderBy, QCourse course, Member member) {
        if (orderBy == null) {
            return course.id2.asc();
        }
        switch (orderBy) {
            case LATEST:
                return course.id2.asc();
            case RECOMMEND:
                JPQLQuery<Integer> totalMatchCountQuery = JPAExpressions
                        .select(
                                courseTogether.count().intValue()
                                        .add(courseTheme.count().intValue())
                                        .add(courseStyle.count().intValue())
                        )
                        .from(course)
                        .leftJoin(courseTogether).on(courseTogether.course.eq(course))
                        .leftJoin(courseTheme).on(courseTheme.course.eq(course))
                        .leftJoin(courseStyle).on(courseStyle.course.eq(course))
                        .where(
                                courseTogether.together.id.in(
                                                JPAExpressions.select(memberTogether.together.id)
                                                        .from(memberTogether)
                                                        .where(memberTogether.member.eq(member))
                                        )
                                        .or(courseTheme.theme.id.in(
                                                JPAExpressions.select(memberTheme.theme.id)
                                                        .from(memberTheme)
                                                        .where(memberTheme.member.eq(member))
                                        ))
                                        .or(courseStyle.style.id.in(
                                                JPAExpressions.select(memberStyle.style.id)
                                                        .from(memberStyle)
                                                        .where(memberStyle.member.eq(member))
                                        ))
                        );

                // 서브쿼리 결과를 기반으로 코스 정렬
                return new OrderSpecifier<>(Order.DESC, totalMatchCountQuery);

            default:
                return course.id.desc();
        }
    }

    public enum OrderBy {
        LATEST, RECOMMEND;
    }

}
