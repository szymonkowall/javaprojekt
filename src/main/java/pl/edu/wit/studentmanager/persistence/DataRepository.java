package pl.edu.wit.studentmanager.persistence;

import java.io.IOException;
import java.nio.file.Path;

import pl.edu.wit.studentmanager.model.AppData;


public interface DataRepository {


    void save(Path path, AppData data) throws IOException;


    AppData load(Path path) throws IOException;
}
