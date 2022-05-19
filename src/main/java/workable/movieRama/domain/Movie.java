package workable.movieRama.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@CompoundIndexes({
    @CompoundIndex(def = "{ 'userId': 1, 'publicationDate': -1 }", background = true, useGeneratedName = true)
})
@Document(collection = "movie")
public class Movie {
    @Id
    private String  id;
    @NotBlank
    private String title;
    private String description;
    private String userId;
    private LocalDateTime publicationDate;
    private int likes;
    private int dislikes;

    @JsonIgnore
    public String getLikes() {
        return likes;
    }

    @JsonProperty
    public void setLikes(String likes) {
        this.likes = likes;
    }

    @JsonIgnore
    public String getDislikes() {
        return dislikes;
    }

    @JsonProperty
    public void setDislikes(String likes) {
        this.dislikes = dislikes;
    }
}