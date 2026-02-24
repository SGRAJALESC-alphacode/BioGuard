package org.BioGuard.io.reader;

import org.BioGuard.exception.FileReadException;
import java.util.List;

public interface IFileReader<T> {
    List<T> leer(String ruta) throws FileReadException;
}