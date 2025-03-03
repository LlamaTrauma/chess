package server;

import model.GameMetadata;

import java.util.ArrayList;

public record ListGamesResult(ArrayList<GameMetadata> games) implements ResponseBody {
}
