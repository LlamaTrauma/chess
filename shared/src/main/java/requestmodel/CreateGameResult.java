package requestmodel;

import model.ResponseBody;

public record CreateGameResult(int gameID) implements ResponseBody {
}
