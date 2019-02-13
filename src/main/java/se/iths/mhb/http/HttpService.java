package se.iths.mhb.http;

import java.io.IOException;

public interface HttpService {

    HttpResponse serve(HttpRequest httpRequest) throws IOException;

}
