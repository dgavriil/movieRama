package workable.movieRama.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@CompoundIndexes({
        @CompoundIndex(def = "{ 'movieId': 1, 'positive': 1}", background = true, useGeneratedName = true),
        @CompoundIndex(def = "{ 'movieId': 1, 'userId': 1}", background = true, useGeneratedName = true)
})
@Document(collection = "vote")
public class Votes {
    @Id
    private String id;
    private String movieId;
    private String userId;
    private Boolean positive;
    private LocalDateTime publicationDate;
}
