package requestmodel;

public record LoginResult(String username, String authToken) implements ResponseBody {
}
