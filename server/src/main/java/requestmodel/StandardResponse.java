package requestmodel;

public class StandardResponse implements ResponseBody {
    public String message;

    public StandardResponse(int code) {
        switch (code) {
            case 400:
                message = "Error: bad request";
                break;
            case 401:
                message = "Error: unauthorized";
                break;
            case 403:
                message = "Error: already taken";
                break;
            default:
                message = "Unhandled Exception";
        }
    }

    public StandardResponse(String message) {
        this.message = message;
    }
}
