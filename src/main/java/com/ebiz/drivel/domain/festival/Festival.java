package com.ebiz.drivel.domain.festival;

import com.ebiz.drivel.domain.festival.FestivalApiResponse.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Table(name = "festival")
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_address")
    private String firstAddress;

    @Column(name = "second_address")
    private String secondAddress;

    @Column(name = "title")
    private String title;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "first_image_path")
    private String firstImagePath;

    @Column(name = "second_image_path")
    private String secondImagePath;

    @Column(name = "latitude", nullable = false, precision = 8, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    public static Festival from(Item item) {
        return Festival.builder()
                .firstAddress(item.getAddr1())
                .secondAddress(item.getAddr2())
                .title(item.getTitle())
                .startDate(item.getEventstartdate())
                .endDate(item.getEventenddate())
                .firstImagePath(item.getFirstimage())
                .secondImagePath(item.getFirstimage2())
                .latitude(convertCoordinate(item.getMapy()))
                .longitude(convertCoordinate(item.getMapx()))
                .build();
    }

    public static BigDecimal convertCoordinate(String decimal) {
        return BigDecimal.valueOf(Double.valueOf(decimal));
    }

}
