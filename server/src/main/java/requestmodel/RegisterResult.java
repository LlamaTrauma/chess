package requestmodel;

import model.ResponseBody;

public record RegisterResult(String username, String authToken) implements ResponseBody {
}
