package model;

import java.util.ArrayList;

public record ListGamesResult(ArrayList<GameMetadata> games) implements ResponseBody {
}
