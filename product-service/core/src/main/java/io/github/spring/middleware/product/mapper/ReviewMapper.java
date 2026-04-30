package io.github.spring.middleware.product.mapper;

import io.github.spring.middleware.product.domain.Review;
import io.github.spring.middleware.review.dto.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "offsetDateTimeToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToInstant")
    Review toDomain(ReviewDto dto);

    @Named("offsetDateTimeToInstant")
    default java.time.Instant offsetDateTimeToInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toInstant() : null;
    }

}
