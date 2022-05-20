package workable.movieRama.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Response {
    OK("OK"),
    REQUEST_FAILED("REQUEST_FAILED"),
    BAD_REQUEST("BAD_REQUEST"),
    INTERNAL_ERROR("UNAVAILABLE");

    private String responseCode;
    private ObjectMapper mapper = new ObjectMapper();

    private Response(String rCode) {
        this.responseCode = rCode;
    }

    private ObjectNode toJson(String message) {
        JsonNode node = mapper.createObjectNode();
        ((ObjectNode) node).put("status", responseCode);
        ((ObjectNode) node).put("message", message);
        ((ObjectNode) node).set("data", mapper.createObjectNode());

        return ((ObjectNode) node);
    }

    private ObjectNode toJson(String message, JsonNode node) {
        ObjectNode on = this.toJson(message);
        on.set("data", node);
        return on;
    }

    public ObjectNode toResult(ResponseCode code) { return toResult(code.name()); }
    public ObjectNode toResult(ResponseCode code, ArrayNode an) { return toResult(code.name(), an); }
    public ObjectNode toResult(ResponseCode code, ObjectNode on) { return toResult(code.name(), on); }
    public ObjectNode toResult(ResponseCode code, JsonNode jn) { return toResult(code.name(), jn); }

    public ObjectNode toResult(String message) { return this.toResultInternal(toJson(message)); }
    public ObjectNode toResult(String message, ArrayNode an) { return this.toResultInternal(toJson(message, an)); }
    public ObjectNode toResult(String message, ObjectNode on) { return this.toResultInternal(toJson(message, on)); }
    public ObjectNode toResult(String message, JsonNode jn) { return this.toResultInternal(toJson(message, jn)); }

    private ObjectNode toResultInternal(ObjectNode on) {
        switch(this.responseCode) {
            case "OK":
                return Result.builder().data(on).build().getData();
            case "BAD_REQUEST":
                return Result.builder().data(on).build().getData();
            case "UNAVAILABLE":
                return Result.builder().data(on).build().getData();
            default:
                log.error("%s : %s", this.responseCode, on.get("message"));
                return Result.builder().data(on).build().getData();
        }
    }
}

