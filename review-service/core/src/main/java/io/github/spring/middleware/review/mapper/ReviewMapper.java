package io.github.spring.middleware.review.mapper;

import io.github.spring.middleware.review.domain.Review;
import io.github.spring.middleware.review.dto.ReviewCreateItemDto;
import io.github.spring.middleware.review.dto.ReviewCreateRequestDto;
import io.github.spring.middleware.review.dto.ReviewDto;
import io.github.spring.middleware.review.dto.ReviewPatchRequestDto;
import io.github.spring.middleware.review.dto.ReviewReplaceItemDto;
import io.github.spring.middleware.review.dto.ReviewUpdateRequestDto;
import io.github.spring.middleware.review.dto.graphql.ReviewInput;
import io.github.spring.middleware.review.entity.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {

    Review toDomain(ReviewEntity entity);

    ReviewEntity toEntity(Review domain);

    Review toDomain(ReviewCreateRequestDto request);

    Review toDomain(ReviewCreateItemDto request);

    Review toDomain(ReviewReplaceItemDto request);

    Review toDomain(ReviewUpdateRequestDto request);

    Review toDomain(ReviewPatchRequestDto request);

    Review toDomain(ReviewInput input);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    ReviewDto toDto(Review domain);

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(OffsetDateTime.now().getOffset());
    }
}
