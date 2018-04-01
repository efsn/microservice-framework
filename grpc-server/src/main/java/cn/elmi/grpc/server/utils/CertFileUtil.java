package cn.elmi.grpc.server.utils;

import cn.elmi.grpc.server.exception.GrpcServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@Slf4j
public class CertFileUtil {

    public static File getFile(String path) {
        try {
            return new ClassPathResource(path).getFile();
        } catch (IOException e) {
            log.error("load file fail: {}", path, e);
            throw new GrpcServerException("load file fail: " + path, e);
        }
    }

}
