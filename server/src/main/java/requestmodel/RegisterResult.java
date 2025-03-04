package requestmodel;

public record RegisterResult(String username, String authToken) implements ResponseBody {
}
