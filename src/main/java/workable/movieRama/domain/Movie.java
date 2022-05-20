package workable.movieRama.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    @Builder.Default
    private Long likes = 0L;
    @Builder.Default
    private Long dislikes = 0L;

    public ObjectNode toJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resp = mapper.createObjectNode();

        resp.put("title", this.title);
        resp.put("description", this.description);
        resp.put("likes", this.likes);
        resp.put("dislikes", this.dislikes);

        return resp;
    }
}