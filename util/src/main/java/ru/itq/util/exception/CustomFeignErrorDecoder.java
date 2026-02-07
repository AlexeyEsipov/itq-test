package ru.itq.util.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        if (status >= 400 && status < 500) {
            return new ClientErrorException(
                    "Client error " + status + " from " + methodKey
            );
        }
        if (status >= 500) {
            return new ServerErrorException(
                    "Server error " + status + " from " + methodKey
            );
        }
        return defaultDecoder.decode(methodKey, response);
    }
}

