package requestmodel;

import model.ResponseBody;

public record HandlerResponse(int status, ResponseBody response) {
}
