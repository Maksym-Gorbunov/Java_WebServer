package se.iths.mhb.http;

import java.io.IOException;

public interface HttpService {

    String defaultMapping();
    HttpResponse serve(HttpRequest httpRequest) throws IOException;

}
